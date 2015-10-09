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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import com.nexmo.sdk.BuildConfig;
import com.nexmo.sdk.NexmoClient;

import com.nexmo.sdk.core.client.Client;
import com.nexmo.sdk.core.client.Request;
import com.nexmo.sdk.core.client.Response;
import com.nexmo.sdk.core.device.DeviceProperties;
import com.nexmo.sdk.core.device.NoDeviceIdException;
import com.nexmo.sdk.verify.core.event.token.BaseTokenServiceListener;
import com.nexmo.sdk.verify.core.response.TokenResponse;

import com.nexmo.sdk.verify.client.InternalNetworkException;
import com.nexmo.sdk.verify.event.VerifyError;

/**
 * Service that triggers a new token request against the SDK Service.
 */
public class TokenService {

    /** Log tag, apps may override it. */
    private static final String TAG = TokenService.class.getSimpleName();

    /** HTTP response parameters. */
    public static final String PARAM_TOKEN = "token";
    private static TokenService instance = new TokenService();
    private static final Gson gson = new GsonBuilder().create();

    public static TokenService getInstance() {
        return instance;
    }

    private TokenService(){
    }

    public boolean start(final NexmoClient nexmoClient,
                         final BaseTokenServiceListener listener) {
        if (nexmoClient == null || listener == null) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "Cannot start request, missing params.");
            return false;
        }

        new TokenTask(nexmoClient, listener).execute();
        return true;
    }

    private TokenResponse parseJson(final String input) throws JsonSyntaxException {
        return gson.fromJson(input, TokenResponse.class);
    }

    /**
     * Token Task requests a new token generation.
     */
    private class TokenTask extends AsyncTask<Void, Void, Response> {
        private BaseTokenServiceListener tokenListener;
        private IOException network_exception;
        private InternalNetworkException internal_exception;
        private NoDeviceIdException deviceId_exception;
        private NexmoClient nexmoClient;

        public TokenTask(final NexmoClient nexmoClient,
                         final BaseTokenServiceListener listener) {
            this.nexmoClient = nexmoClient;
            this.tokenListener = listener;
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
                this.internal_exception = e;
            }
            catch (NoDeviceIdException e) {
                this.deviceId_exception = e;
            }
            catch (IOException e) {
                this.network_exception = e;
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
                TokenResponse newToken = parseJson(result.getBody());
                // Check if the signature is set on the response header.
                if (BaseService.isSignatureInvalid(this.nexmoClient, newToken, result))
                    this.tokenListener.onTokenError(VerifyError.INVALID_CREDENTIALS, "Invalid credentials.");
                else
                    this.tokenListener.onToken(newToken.getToken());
            }
            else if (this.internal_exception != null)
                this.tokenListener.onTokenError(VerifyError.INTERNAL_ERR, this.internal_exception.getMessage());
            else if (this.network_exception != null)
                this.tokenListener.onException(this.network_exception);
            else if (this.deviceId_exception != null) {
                this.tokenListener.onTokenError(VerifyError.DEVICE_ID_NOT_FOUND, this.deviceId_exception.getMessage());
            } else
                this.tokenListener.onTokenError(VerifyError.INTERNAL_ERR, TAG + "No response found.");
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
        private Response getTokenRequest() throws IOException {
            Context appContext = nexmoClient.getContext();

            Map<String, String> requestParams = new TreeMap<>();
            requestParams.put(BaseService.PARAM_APP_ID, nexmoClient.getApplicationId());
            try {
                requestParams.put(BaseService.PARAM_DEVICE_ID, DeviceProperties.getDeviceId(appContext));
            } catch (NoDeviceIdException e) {
                Log.d(TAG, e.getMessage());
                throw new NoDeviceIdException(TAG + " Error parsing response " + e);
            }
            requestParams.put(BaseService.PARAM_SOURCE_IP, DeviceProperties.getIPAddress(appContext));

            Client client = new Client();
            try {
                HttpURLConnection connection = client.initConnection(new Request(nexmoClient.getEnvironmentHost(),
                                                                                 nexmoClient.getSharedSecretKey(),
                                                                                 BaseService.METHOD_TOKEN,
                                                                                 requestParams));
                Response response = client.execute(connection);
                Log.d(TAG, "Token raw response: " + response);
                return response;
            } catch (JsonIOException | JsonSyntaxException e) {
                Log.d(TAG, " Error parsing " + e);
                throw new InternalNetworkException(TAG + " Error parsing response " + e);
            }
            catch (IOException e) {
                Log.d(TAG, " Error network issue " + e);
                throw new IOException(TAG + " Error establishing connection " + e);
            }
        }
    }

}
