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
import android.text.TextUtils;
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

import com.nexmo.sdk.verify.core.event.token.BaseTokenServiceListener;
import com.nexmo.sdk.verify.core.response.VerifyResponse;
import com.nexmo.sdk.verify.core.request.VerifyRequest;

import com.nexmo.sdk.verify.client.InternalNetworkException;
import com.nexmo.sdk.verify.event.VerifyError;

/**
 * Verification Service that enables you to request Nexmo to kick off the verification process for the number you/the user has provided.
 */
public class VerifyService extends BaseService<VerifyResponse>
                           implements BaseTokenServiceListener {

    private static final String TAG = VerifyService.class.getSimpleName();
    private static VerifyService instance = new VerifyService();
    private VerifyRequest verifyRequest;

    public static VerifyService getInstance(){
        return instance;
    }

    private VerifyService(){
    }

    public void init(final VerifyRequest verifyRequest) {
        synchronized(this) {
            this.verifyRequest = verifyRequest;
        }
    }

    public void updateToken(final String token) {
        synchronized(this){
            this.verifyRequest.setToken(token);
        }
    }

    /**
     * Initiate the task that triggers the http request.
     * @param nexmoClient   The NexmoClient object that sends the request.
     * @param listener      The internal listener.
     * @return              True if the request has been initiated, False otherwise.
     */
    @Override
    public boolean start(final NexmoClient nexmoClient,
                         final ServiceListener<VerifyResponse> listener) {
        if (nexmoClient == null || listener == null || this.verifyRequest == null) {
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


    @Override
    VerifyResponse parseJson(final String input) throws JsonSyntaxException {
        return gson.fromJson(input, VerifyResponse.class);
    }

    @Override
    public void onToken(final String token) {
        updateToken(token);

        // verify can now be initiated.
        new VerifyTask(getNexmoClient(), getServiceListener()).execute(this.verifyRequest);
    }

    /**
     * The token request has been rejected.
     *
     * @param errorCode    The {@link com.nexmo.sdk.verify.event.VerifyError} codes to describe the error.
     * @param errorMessage The message that describes the {@link com.nexmo.sdk.verify.event.VerifyError}.
     */
    @Override
    public void onTokenError(final com.nexmo.sdk.verify.event.VerifyError errorCode,
                             final String errorMessage) {
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
     * Async task that is making a request for a new verify.
     */
    private class VerifyTask extends AsyncTask<VerifyRequest, Void, Response> {
        private ServiceListener<VerifyResponse> listener;
        private InternalNetworkException internalException;
        private IOException networkException;
        private NexmoClient nexmoClient;

        public VerifyTask(final NexmoClient nexmoClient,
                          final ServiceListener<VerifyResponse> listener) {
            this.nexmoClient = nexmoClient;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Response doInBackground(VerifyRequest... params) {
            VerifyRequest verifyRequest = params[0];
            try {
                return startVerifyRequest(verifyRequest);
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
                VerifyResponse verifyResponse = parseJson(result.getBody());
                // Check if the signature is set on the response header.
                if (isSignatureInvalid(this.nexmoClient, verifyResponse, result))
                    this.listener.onFail(VerifyError.INVALID_CREDENTIALS, "Invalid credentials.");
                else
                    this.listener.onResponse(verifyResponse);
            }
            else if (this.internalException != null)
                this.listener.onFail(VerifyError.INTERNAL_ERR, this.internalException.getMessage());
            else if (this.networkException != null)
                this.listener.onException(this.networkException);
            else
                this.listener.onFail(VerifyError.INTERNAL_ERR, TAG + "No response found.");
        }

        /**
         * Start the verify flow.
         *
         * @param verifyRequest The current verify request object that contains: <p>
         *                      <li>
         *                      <ul>The authorization token.</ul>
         *                      <ul>The country code of the current SIM card.</ul>
         *                      <ul>The phone number that is under verification process.</ul>
         *                      </li>
         *
         * @return request response.
         * @throws InternalNetworkException If an internal sdk error occurs while parsing the response.
         */
        private Response startVerifyRequest(final VerifyRequest verifyRequest) throws IOException {
            Context appContext = nexmoClient.getContext();
            String push_token = nexmoClient.getGcmRegistrationToken();
            Map<String, String> requestParams = new TreeMap<>();
            requestParams.put(TokenService.PARAM_TOKEN, verifyRequest.getToken());
            requestParams.put(BaseService.PARAM_NUMBER, verifyRequest.getPhoneNumber());
            requestParams.put(BaseService.PARAM_COUNTRY_CODE, verifyRequest.getCountryCode());
            requestParams.put(BaseService.PARAM_APP_ID, nexmoClient.getApplicationId());
            requestParams.put(BaseService.PARAM_DEVICE_ID, DeviceProperties.getDeviceId(appContext));
            requestParams.put(BaseService.PARAM_SOURCE_IP, DeviceProperties.getIPAddress(appContext));
            if (!TextUtils.isEmpty(push_token))
                requestParams.put(BaseService.PARAM_GCM_REGISTRATION_TOKEN, push_token);
            String deviceLanguage = DeviceProperties.getLanguage();
            if(!TextUtils.isEmpty(deviceLanguage))
                requestParams.put(BaseService.PARAM_LANGUAGE, deviceLanguage);

            Client client = new Client();
            try {
                HttpURLConnection connection = client.initConnection(new Request(nexmoClient.getEnvironmentHost(),
                                                                                 nexmoClient.getSharedSecretKey(),
                                                                                 verifyRequest.isStandalone() ? BaseService.METHOD_VERIFY_STANDALONE : BaseService.METHOD_VERIFY,
                                                                                 requestParams));
                Response response = client.execute(connection);
                Log.d(TAG, "VERIFY raw response: " + response);
                return response;
            } catch (JsonIOException | JsonSyntaxException e) {
                Log.d(TAG, " Error parsing " + e);
                throw new InternalNetworkException(TAG + "Error parsing response " + e);
            } catch (IOException e) {
                Log.d(TAG, " Error network issue " + e);
                throw new IOException(TAG + " Error establishing connection " + e);
            }
        }
    }

}
