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

import com.nexmo.sdk.NexmoClient;

import com.nexmo.sdk.core.client.Client;
import com.nexmo.sdk.core.client.Request;
import com.nexmo.sdk.core.device.DeviceProperties;
import com.nexmo.sdk.core.event.ServiceListener;
import com.nexmo.sdk.core.client.Response;
import com.nexmo.sdk.verify.core.response.VerifyResponse;
import com.nexmo.sdk.verify.core.request.VerifyRequest;

import com.nexmo.sdk.verify.client.InternalNetworkException;
import com.nexmo.sdk.verify.event.VerifyError;

/**
 * Verification Service that enables you to request Nexmo to kick off the verification process for the number you/the user has provided.
 */
public class VerifyService extends BaseService<VerifyResponse> {

    private static final String TAG = VerifyService.class.getSimpleName();
    private static VerifyService instance = new VerifyService();

    public static VerifyService getInstance(){
        return instance;
    }

    private VerifyService(){
    }

    @Override
    public void start(final NexmoClient nexmoClient,
                      final VerifyRequest request,
                      final ServiceListener<VerifyResponse> listener) {
        new VerifyTask(nexmoClient, listener).execute(request);
    }


    @Override
    VerifyResponse parseJson(final String input) throws JsonSyntaxException {
        return gson.fromJson(input, VerifyResponse.class);
    }

    /**
     * Async task that is making a request for a new verify.
     */
    private class VerifyTask extends AsyncTask<VerifyRequest, Void, Response> {
        private ServiceListener<VerifyResponse> listener;
        private InternalNetworkException exception;
        private NexmoClient nexmoClient;

        public VerifyTask(final NexmoClient nexmoClient, final ServiceListener<VerifyResponse> listener) {
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
                VerifyResponse verifyResponse = parseJson(result.getBody());
                // Check if the signature is set on the response header.
                if (isSignatureInvalid(this.nexmoClient, verifyResponse, result))
                    this.listener.onFail(VerifyError.INVALID_CREDENTIALS, "Invalid credentials.");
                else
                    this.listener.onResponse(verifyResponse);
            }
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
        private Response startVerifyRequest(final VerifyRequest verifyRequest) throws InternalNetworkException {
            Context appContext = nexmoClient.getContext();
            Map<String, String> requestParams = new TreeMap<>();
            requestParams.put(TokenService.PARAM_TOKEN, verifyRequest.getToken());
            requestParams.put(VerifyService.PARAM_NUMBER, verifyRequest.getPhoneNumber());
            requestParams.put(VerifyService.PARAM_COUNTRY_CODE, verifyRequest.getCountryCode());
            requestParams.put(BaseService.PARAM_APP_ID, nexmoClient.getApplicationId());
            requestParams.put(BaseService.PARAM_DEVICE_ID, DeviceProperties.getIMEI(appContext));
            requestParams.put(BaseService.PARAM_SOURCE_IP, DeviceProperties.getIPAddress(appContext));
            String deviceLanguage = DeviceProperties.getLanguage();
            if (!TextUtils.isEmpty(deviceLanguage))
                requestParams.put(BaseService.PARAM_LANGUAGE, deviceLanguage);

            Client client = new Client();
            try {
                HttpURLConnection connection = client.initConnection(new Request(nexmoClient.getEnvironmentHost(),
                                                                                nexmoClient.getSharedSecretKey(),
                                                                                BaseService.METHOD_VERIFY,
                                                                                requestParams));
                Response response = client.execute(connection);
                Log.d(TAG, " --VERIFY raw response-- " + response);
                return response;
            } catch (JsonIOException | JsonSyntaxException | IOException e ) {
                Log.d(TAG, " Error parsing " + e);
                throw new InternalNetworkException("VerifyService error parsing response " + e);
            }
        }
    }

}
