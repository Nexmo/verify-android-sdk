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

import com.nexmo.sdk.verify.core.event.token.BaseTokenServiceListener;
import com.nexmo.sdk.verify.core.request.CommandRequest;
import com.nexmo.sdk.verify.core.response.VerifyResponse;

import com.nexmo.sdk.verify.event.VerifyError;
import com.nexmo.sdk.verify.client.InternalNetworkException;

/**
 * Command service that triggers actions for a user.
 * The available commands are {@link com.nexmo.sdk.verify.event.Command}.
 */
public class CommandService extends BaseService<VerifyResponse>
        implements BaseTokenServiceListener {

    private static final String TAG = CommandService.class.getSimpleName();
    private static CommandService instance = new CommandService();
    private CommandRequest commandRequest;

    public static CommandService getInstance(){
        return instance;
    }

    private CommandService(){
    }

    /**
     * Deserialize the specified Json into an object of the specified class.
     *
     * @param input The raw response body in string format.
     * @return An object of type {@link}T from the string. Returns {@code null} if {@link @param input} is {@code null}.
     * @throws com.google.gson.JsonSyntaxException If {@param input} is not a valid representation for an object of type
     * {@link com.nexmo.sdk.verify.core.response.VerifyResponse}.
     */
    @Override
    VerifyResponse parseJson(String input) throws JsonSyntaxException {
        return gson.fromJson(input, VerifyResponse.class);
    }

    public void init(final CommandRequest commandRequest) {
        synchronized(this) {
            this.commandRequest = commandRequest;
        }
    }

    /**
     * Initiate the task that triggers the http request.
     *
     * @param nexmoClient The NexmoClient object that sends the request.
     * @param listener    The internal listener.
     * @return True if the request has been initiated, False otherwise.
     */
    @Override
    public boolean start(NexmoClient nexmoClient, ServiceListener<VerifyResponse> listener) {
        if (nexmoClient == null || listener == null || this.commandRequest == null) {
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
    void updateToken(String token) {
        synchronized(this){
            this.commandRequest.setToken(token);
        }
    }

    /**
     * Indicates there is a new token received.
     *
     * @param token The new token response.
     */
    @Override
    public void onToken(String token) {
        updateToken(token);
        new CommandTask(getNexmoClient(), getServiceListener()).execute(this.commandRequest);
    }

    /**
     * The token request has been rejected.
     *
     * @param errorCode    The {@link com.nexmo.sdk.verify.event.VerifyError} codes to describe the error.
     * @param errorMessage The message that describes the {@link com.nexmo.sdk.verify.event.VerifyError}.
     */
    @Override
    public void onTokenError(com.nexmo.sdk.verify.event.VerifyError errorCode, String errorMessage) {
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
     * Async task that is making a request for a command action.
     */
    private class CommandTask extends AsyncTask<CommandRequest, Void, Response> {
        private ServiceListener<VerifyResponse> listener;
        private InternalNetworkException internalException;
        private IOException networkException;
        private NexmoClient nexmoClient;

        public CommandTask(final NexmoClient nexmoClient,
                           final ServiceListener<VerifyResponse> listener) {
            this.nexmoClient = nexmoClient;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Response doInBackground(CommandRequest... params) {
            CommandRequest commandRequest = params[0];
            try {
                return commandRequest(commandRequest);
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
            else if(this.internalException != null)
                this.listener.onFail(com.nexmo.sdk.verify.event.VerifyError.INTERNAL_ERR, this.internalException.getMessage());
            else if (this.networkException != null)
                this.listener.onException(this.networkException);
            else
                this.listener.onFail(VerifyError.INTERNAL_ERR, TAG + "No response found.");
        }

        /**
         * Request a command action: one of the {@link com.nexmo.sdk.verify.event.Command} actions.
         *
         * @param commandRequest The current verify request object that contains: <p>
         *                      <li>
         *                      <ul>The authorization token.</ul>
         *                      <ul>The country code of the current SIM card.</ul>
         *                      <ul>The phone number that was verified or not.</ul>
         *                      <ul>The command action.</ul>
         *                      </li>
         *
         * @return request response.
         * @throws InternalNetworkException If an internal sdk error occurs while parsing the response.
         */
        private Response commandRequest(final CommandRequest commandRequest) throws IOException {
            Context appContext = nexmoClient.getContext();
            String method = null;
            Map<String, String> requestParams = new TreeMap<>();
            requestParams.put(TokenService.PARAM_TOKEN, commandRequest.getToken());
            requestParams.put(VerifyService.PARAM_NUMBER, commandRequest.getPhoneNumber());
            requestParams.put(VerifyService.PARAM_COUNTRY_CODE, commandRequest.getCountryCode());
            requestParams.put(BaseService.PARAM_APP_ID, nexmoClient.getApplicationId());
            requestParams.put(BaseService.PARAM_DEVICE_ID, DeviceProperties.getDeviceId(appContext));
            requestParams.put(BaseService.PARAM_SOURCE_IP, DeviceProperties.getIPAddress(appContext));

            switch(commandRequest.getCommand()) {
                case LOGOUT:{
                    method = BaseService.METHOD_LOGOUT;
                    break;
                }
                case CANCEL: {
                    method = BaseService.METHOD_COMMAND;
                    requestParams.put(BaseService.PARAM_COMMAND,
                                      BaseService.PARAM_COMMAND_CANCEL);
                    break;
                }
                case TRIGGER_NEXT_EVENT: {
                    method = BaseService.METHOD_COMMAND;
                    requestParams.put(BaseService.PARAM_COMMAND,
                                      BaseService.PARAM_COMMAND_SKIP);
                    break;
                }
            }

            Client client = new Client();
            try {
                HttpURLConnection connection = client.initConnection(new Request(nexmoClient.getEnvironmentHost(),
                                                                                 nexmoClient.getSharedSecretKey(),
                                                                                 method,
                                                                                 requestParams));
                Response response = client.execute(connection);
                Log.d(TAG, "COMMAND raw response: " + response);
                return response;
            } catch (JsonIOException | JsonSyntaxException e ) {
                Log.d(TAG, " Error parsing " + e);
                throw new InternalNetworkException(TAG + "Error parsing response " + e);
            } catch (IOException e) {
                Log.d(TAG, " Error network issue " + e);
                throw new IOException(TAG + " Error establishing connection " + e);
            }
        }

    }

}
