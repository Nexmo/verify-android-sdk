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
import com.nexmo.sdk.verify.core.response.CheckResponse;
import com.nexmo.sdk.core.client.Response;

import com.nexmo.sdk.verify.event.VerifyError;
import com.nexmo.sdk.verify.client.InternalNetworkException;

/**
 * Service that checks whether the PIN code received from your end user matches the one Nexmo has sent.
 */
public class CheckService extends BaseService<CheckResponse> {

    private static final String TAG = VerifyService.class.getSimpleName();

    /** HTTP request parameters. */
    private static final String PARAM_CODE = "code";
    private static CheckService instance = new CheckService();

    public static CheckService getInstance() {
        return instance;
    }

    private CheckService() {
    }

    @Override
    public void start(final NexmoClient nexmoClient,
                      final VerifyRequest request,
                      final ServiceListener<CheckResponse> listener) {
        new CheckTask(nexmoClient, listener).execute(request);
    }

    @Override
    CheckResponse parseJson(final String input) throws JsonSyntaxException {
        return gson.fromJson(input, CheckResponse.class);
    }

    /**
     * Token Task requests a new PIN code check.
     */
    private class CheckTask extends AsyncTask<VerifyRequest, Void, Response> {
        private ServiceListener<CheckResponse> listener;
        private InternalNetworkException exception;
        private NexmoClient nexmoClient;

        public CheckTask(final NexmoClient nexmoClient, final ServiceListener<CheckResponse> listener) {
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
        protected Response doInBackground(VerifyRequest... params) {
            VerifyRequest verifyRequest = params[0];
            try {
                return checkRequest(verifyRequest);
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
            if(this.exception != null)
                this.listener.onFail(VerifyError.INTERNAL_ERR, "IO Internal error.");
            else if (result != null) {
                CheckResponse checkResponse = parseJson(result.getBody());
                // Check if the signature is set on the response header.
                if (isSignatureInvalid(this.nexmoClient, checkResponse, result))
                    this.listener.onFail(VerifyError.INVALID_CREDENTIALS, "Invalid credentials.");
                else
                    this.listener.onResponse(checkResponse);
            }
        }

        /**
         * Check verification enables you to check whether the PIN code you got from the end user matches
         * the one Nexmo has sent.
         *
         * @param verifyRequest The current verify request object that contains: <p>
         *                      <li>
         *                      <ul>The authorization token.</ul>
         *                      <ul>The country code of the current SIM card.</ul>
         *                      <ul>The phone number that is under verification process.</ul>
         *                      <ul>PIN code the end user provided to you (min 4 digits).</ul>
         *                      </li>
         *
         * @return request response.
         * @throws InternalNetworkException If an internal sdk error occurs while parsing the response.
         */
        private Response checkRequest(final VerifyRequest verifyRequest) throws InternalNetworkException {
            Context appContext = nexmoClient.getContext();
            Map<String, String> requestParams = new TreeMap<>();
            requestParams.put(TokenService.PARAM_TOKEN, verifyRequest.getToken());
            requestParams.put(CheckService.PARAM_CODE, verifyRequest.getPinCode());
            requestParams.put(VerifyService.PARAM_COUNTRY_CODE, verifyRequest.getCountryCode());
            requestParams.put(BaseService.PARAM_NUMBER, verifyRequest.getPhoneNumber());
            requestParams.put(BaseService.PARAM_APP_ID, nexmoClient.getApplicationId());
            requestParams.put(BaseService.PARAM_DEVICE_ID, DeviceProperties.getIMEI(appContext));
            requestParams.put(BaseService.PARAM_SOURCE_IP, DeviceProperties.getIPAddress(appContext));

            Client client = new Client();
            try {
                HttpURLConnection connection = client.initConnection(new Request(nexmoClient.getEnvironmentHost(),
                                                                                nexmoClient.getSharedSecretKey(),
                                                                                BaseService.METHOD_CHECK,
                                                                                requestParams));
                Response response = client.execute(connection);
                Log.d(TAG, " --CHECK raw response-- " + response);
                return response;
            } catch (JsonIOException | JsonSyntaxException | IOException e) {
                Log.d(TAG, " Error parsing " + e);
                throw new InternalNetworkException(TAG + " Error parsing response " + e);
            }
        }
    }

}
