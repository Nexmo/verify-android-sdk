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

import com.nexmo.sdk.NexmoClient;

import com.nexmo.sdk.core.client.Client;
import com.nexmo.sdk.core.event.ServiceListener;
import com.nexmo.sdk.verify.core.request.VerifyRequest;
import com.nexmo.sdk.core.client.Request;
import com.nexmo.sdk.core.device.DeviceProperties;
import com.nexmo.sdk.core.client.Response;
import com.nexmo.sdk.verify.core.response.TokenResponse;

import com.nexmo.sdk.verify.client.InternalNetworkException;
import com.nexmo.sdk.verify.event.VerifyError;

/**
 * Service that triggers a new token request against the SDK Service.
 */
public class TokenService extends BaseService<TokenResponse> {

    /** Log tag, apps may override it. */
    private static final String TAG = TokenService.class.getSimpleName();

    /** HTTP response parameters. */
    public static final String PARAM_TOKEN = "token";
    private static TokenService instance = new TokenService();

    public static TokenService getInstance() {
        return instance;
    }

    private TokenService(){
    }

    @Override
    public void start(final NexmoClient nexmoClient,
                      final VerifyRequest request,
                      final ServiceListener<TokenResponse> listener) {
        new TokenTask(nexmoClient, listener).execute();
    }

    @Override
    TokenResponse parseJson(final String input) throws JsonSyntaxException {
        return gson.fromJson(input, TokenResponse.class);
    }

    /**
     * Token Task requests a new token generation.
     */
    private class TokenTask extends AsyncTask<Void, Void, Response> {
        private ServiceListener<TokenResponse> listener;
        private InternalNetworkException exception;
        private NexmoClient nexmoClient;

        public TokenTask(final NexmoClient nexmoClient, final ServiceListener<TokenResponse> listener) {
            this.nexmoClient = nexmoClient;
            this.listener = listener;
        }

        /**
         * Runs on the UI thread before {@link #doInBackground}.
         *
         * @see #onPostExecute
         * @see #doInBackground
         */
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Response doInBackground(Void... params) {
            try {
                return getTokenRequest();
            } catch (InternalNetworkException e) {
                exception = e;
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
            if (this.exception != null)
                this.listener.onFail(VerifyError.INTERNAL_ERR, "IO Internal error.");
            else if (result != null) {
                TokenResponse newToken = parseJson(result.getBody());
                // Check if the signature is set on the response header.
                if (isSignatureInvalid(this.nexmoClient, newToken, result))
                    this.listener.onFail(VerifyError.INVALID_CREDENTIALS, "Invalid credentials.");
                else
                    this.listener.onResponse(newToken);
            }
        }

        /**
         * Generate an access token for the current device used to sign all communications from your
         * Android application to Nexmo.
         * <p>
         * The token has a limited lifetime to protect your Nexmo account from abuse.
         * The token expiration time can be configured from the Dashboard.
         * <p>
         * For the security of your Nexmo account, you should not embed your sharedSecretKey or your
         * Nexmo authorization token as strings in the app you submit to the Google Play Store.
         *
         * @return Request response, that also contains a short-lived new authorization token.
         * @throws InternalNetworkException If an internal sdk error occurs while parsing the response.
         */
        private Response getTokenRequest() throws InternalNetworkException {
            Context appContext = nexmoClient.getContext();

            Map<String, String> requestParams = new TreeMap<>();
            requestParams.put(BaseService.PARAM_APP_ID, nexmoClient.getApplicationId());
            requestParams.put(BaseService.PARAM_DEVICE_ID, DeviceProperties.getIMEI(appContext));
            requestParams.put(BaseService.PARAM_SOURCE_IP, DeviceProperties.getIPAddress(appContext));

            Client client = new Client();
            try {
                HttpURLConnection connection = client.initConnection(new Request(nexmoClient.getEnvironmentHost(),
                                                                                nexmoClient.getSharedSecretKey(),
                                                                                BaseService.METHOD_TOKEN,
                                                                                requestParams));
                Response response = client.execute(connection);
                Log.d(TAG, " --getToken raw response-- " + response);
                return response;
            } catch (JsonIOException | JsonSyntaxException | IOException e) {
                Log.d(TAG, " Error parsing " + e);
                throw new InternalNetworkException(" Error parsing response " + e);
            }
        }
    }

}
