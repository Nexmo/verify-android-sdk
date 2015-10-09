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

package com.nexmo.sdk.core.client;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;
import android.util.Log;

import com.nexmo.sdk.core.config.Config;
import com.nexmo.sdk.core.config.Defaults;
import com.nexmo.sdk.core.device.DeviceProperties;
import com.nexmo.sdk.core.request.RequestSigning;
import com.nexmo.sdk.verify.core.service.BaseService;

import com.nexmo.sdk.NexmoClient;
import com.nexmo.sdk.verify.client.InternalNetworkException;

import com.nexmo.sdk.BuildConfig;

/**
 * Client that handles network requests.
 */
public class Client implements ConnectionClient {

    /** Log tag, apps may override it. */
    private static final String TAG = Client.class.getSimpleName();

    public Client() {}

    /**
     * Prepare a new connection with necessary custom header fields.
     * @param request The request object.
     *
     * @return A new url connection.
     * @throws IOException if an error occurs while opening the connection.
     */
    public HttpURLConnection initConnection(Request request) throws IOException {
        // Generate signature using pre-shared key.
        RequestSigning.constructSignatureForRequestParameters(request.getParams(), request.getSecretKey());

        // Construct connection with necessary custom headers.
        URL url = constructUrlGetConnection(request.getParams(), request.getMethod(), request.getUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setReadTimeout(Defaults.CONNECTION_READ_TIMEOUT);
        connection.setConnectTimeout(Defaults.CONNECTION_TIMEOUT);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.addRequestProperty(HTTP.CONTENT_ENCODING, Config.PARAMS_ENCODING);
        connection.addRequestProperty(BaseService.OS_FAMILY, Config.OS_ANDROID);
        connection.addRequestProperty(BaseService.OS_REVISION, DeviceProperties.getApiLevel());
        connection.addRequestProperty(BaseService.SDK_REVISION, Config.SDK_REVISION_CODE);

        return connection;
    }

    /**
     * Executes a connection, and validates that the body was supplied.
     *
     * @param connection A prepared HttpUrlConnection.
     *
     * @return The response object.
     * @throws IOException If an error occurs while connecting to the resource.
     * @throws InternalNetworkException If an internal sdk error occurs while parsing the response.
     */
    @Override
    public Response execute(HttpURLConnection connection) throws IOException, InternalNetworkException {
        try{
            connection.connect();

            if (connection.getResponseCode() == HttpStatus.SC_OK) {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, connection.getURL().toString());
                String signatureSupplied = connection.getHeaderField(BaseService.RESPONSE_SIG);
                Response response = new Response(getResponseString(connection.getInputStream()), signatureSupplied);

                if (TextUtils.isEmpty(response.getBody()))
                    throw new InternalNetworkException(TAG + "Internal error. Body response missing.");
                else
                    return response;
            }
            else throw new InternalNetworkException(TAG + " Internal error. Unable to connect to server. " + connection.getResponseCode());
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Construct a Nexmo Url instance.
     *
     * @param requestParams The necessary http params.
     * @param methodName The method name inside the API call.
     *
     * @throws MalformedURLException if an error occurs while opening the connection.
     */
    private static URL constructUrlGetConnection(Map<String, String> requestParams,
                                                 String methodName,
                                                 NexmoClient.ENVIRONMENT_HOST host) throws MalformedURLException {
        List<NameValuePair> getParams = new ArrayList<>();
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            getParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        String paramString = URLEncodedUtils.format(getParams, HTTP.UTF_8);
        return new URL((host == NexmoClient.ENVIRONMENT_HOST.PRODUCTION ? Config.ENDPOINT_PRODUCTION : Config.ENDPOINT_SANDBOX) + methodName + paramString);
    }

    private static String getResponseString(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        Reader reader = new InputStreamReader(inputStream);
        BufferedReader buffReader = new BufferedReader(reader);
        int intChar;

        while ((intChar = buffReader.read()) != -1)
            builder.append((char) intChar);

        return builder.toString();
    }

}
