/*
 * Copyright (c) 2015 Nexmo Inc
 * All rights reserved.
 *
 * Licensed only under the Nexmo Verify SDK License Agreement located at
 *
 * https://www.nexmo.com/terms-use/verify-sdk/ (the “License”)
 *
 * You may not use, exercise any rights with respect to or exploit this SDK,
 * or any modifications or derivative works thereof, except in accordance
 * with the License.
 */

package com.nexmo.sdk.verify.core.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.nexmo.sdk.BuildConfig;
import com.nexmo.sdk.NexmoClient;

import com.nexmo.sdk.core.client.Client;
import com.nexmo.sdk.core.client.Request;
import com.nexmo.sdk.core.client.Response;
import com.nexmo.sdk.core.device.DeviceProperties;
import com.nexmo.sdk.core.event.ServiceListener;

import com.nexmo.sdk.verify.client.InternalNetworkException;
import com.nexmo.sdk.verify.core.event.token.BaseTokenServiceListener;
import com.nexmo.sdk.verify.core.request.SearchRequest;
import com.nexmo.sdk.verify.core.response.SearchResponse;
import com.nexmo.sdk.verify.event.VerifyError;

/**
 * Service that checks the current state of the user, without initiating any verification
 * triggered in the background.
 */
public class SearchService extends BaseService<SearchResponse>
                           implements BaseTokenServiceListener {

    private static final String TAG = SearchService.class.getSimpleName();
    private static SearchService instance = new SearchService();
    private SearchRequest searchRequest;

    private SearchService() {}

    public static SearchService getInstance() {
        return instance;
    }
    @Override
    SearchResponse parseJson(String input) throws JsonSyntaxException {
        return gson.fromJson(input, SearchResponse.class);
    }

    public void init(final SearchRequest searchRequest) {
        synchronized(this) {
            this.searchRequest = searchRequest;
        }
    }

    @Override
    public boolean start(final NexmoClient nexmoClient, final ServiceListener<SearchResponse> listener) {
        if (nexmoClient == null || listener == null || this.searchRequest == null) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Cannot start request, missing params.");
            return false;
        }
        setNexmoClient(nexmoClient);
        setServiceListener(listener);

        // Do not reuse tokens anymore, always generate a new one to ensure it's valid.
        TokenService.getInstance().start(nexmoClient, this);
        return true;
    }

    /**
     * Update the service of a new token.
     *
     * @param token The new token.
     */
    @Override
    public void updateToken(final String token) {
        synchronized(this){
            this.searchRequest.setToken(token);
        }
    }

    /**
     * Indicates there is a new token received.
     *
     * @param token The new token response.
     */
    @Override
    public void onToken(final String token) {
        updateToken(token);

        new SearchTask(getNexmoClient(), getServiceListener()).execute(this.searchRequest);
    }

    /**
     * The token request has been rejected.
     *
     * @param errorCode    The {@link com.nexmo.sdk.verify.event.VerifyError} codes to describe the error.
     * @param errorMessage The message that describes the {@link com.nexmo.sdk.verify.event.VerifyError}.
     */
    @Override
    public void onTokenError(VerifyError errorCode, String errorMessage) {
        getServiceListener().onFail(errorCode,
                                    errorMessage);
    }

    /**
     * A request was timed out because of network connectivity exception.
     * Triggered in case of network error, such as UnknownHostException or SocketTimeout exception.
     *
     * @param exception The exception.
     */
    @Override
    public void onException(final IOException exception) {
        // Network exception while getting a token.
        getServiceListener().onException(exception);
    }

    /**
     * Search Task requests the current state of the user.
     */
    private class SearchTask extends AsyncTask<SearchRequest, Void, Response> {
        private ServiceListener<SearchResponse> listener;
        private InternalNetworkException internalException;
        private IOException networkException;
        private NexmoClient nexmoClient;

        public SearchTask(final NexmoClient nexmoClient,
                          final ServiceListener<SearchResponse> listener) {
            this.nexmoClient = nexmoClient;
            this.listener = listener;
        }

        @Override
        protected Response doInBackground(SearchRequest... params) {
            SearchRequest searchRequest = params[0];
            try {
                return searchRequest(searchRequest);
            } catch (InternalNetworkException e) {
                this.internalException = e;
            } catch (IOException e) {
                this.networkException = e;
            }
            return null;
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         *
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param result The result of the operation computed by {@link #doInBackground}.
         *
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @SuppressWarnings({"UnusedDeclaration"})
        protected void onPostExecute(Response result) {
            if (result != null) {
                SearchResponse searchResponse = parseJson(result.getBody());
                // Check if the signature is set on the response header.
                if (isSignatureInvalid(this.nexmoClient, searchResponse, result))
                    this.listener.onFail(VerifyError.INVALID_CREDENTIALS, "Invalid credentials.");
                else
                    this.listener.onResponse(searchResponse);
            }
            else if (this.internalException != null)
                this.listener.onFail(VerifyError.INTERNAL_ERR, this.internalException.getMessage());
            else if (this.networkException != null)
                this.listener.onException(this.networkException);
            else
                this.listener.onFail(VerifyError.INTERNAL_ERR, TAG + "No response found.");
        }

        /**
         * Search enables you to check the current user status for an SDK user.
         *
         * @param searchRequest The current verify request object that contains: <p>
         *                      <li>
         *                      <ul>The authorization token.</ul>
         *                      <ul>The country code of the user.</ul>
         *                      <ul>The phone number of the user.</ul>
         *                      </li>
         *
         * @return request response.
         * @throws InternalNetworkException If an internal sdk error occurs while parsing the response.
         */
        private Response searchRequest(final SearchRequest searchRequest) throws IOException {
            Context appContext = nexmoClient.getContext();
            Map<String, String> requestParams = new TreeMap<>();
            requestParams.put(TokenService.PARAM_TOKEN, searchRequest.getToken());
            requestParams.put(VerifyService.PARAM_COUNTRY_CODE, searchRequest.getCountryCode());
            requestParams.put(BaseService.PARAM_NUMBER, searchRequest.getPhoneNumber());
            requestParams.put(BaseService.PARAM_APP_ID, nexmoClient.getApplicationId());
            requestParams.put(BaseService.PARAM_DEVICE_ID, DeviceProperties.getDeviceId(appContext));
            requestParams.put(BaseService.PARAM_SOURCE_IP, DeviceProperties.getIPAddress(appContext));

            Client client = new Client();
            try {
                HttpURLConnection connection = client.initConnection(new Request(nexmoClient.getEnvironmentHost(),
                        nexmoClient.getSharedSecretKey(),
                        BaseService.METHOD_SEARCH,
                        requestParams));
                Response response = client.execute(connection);
                Log.d(TAG, "SEARCH raw response: " + response);
                return response;
            } catch (JsonIOException | JsonSyntaxException e) {
                Log.d(TAG, " Error parsing " + e);
                throw new InternalNetworkException(TAG + " Error parsing response " + e);
            } catch (IOException e) {
                Log.d(TAG, " Error network issue " + e);
                throw new IOException(TAG + " Error establishing connection " + e);
            }
        }
    }

}
