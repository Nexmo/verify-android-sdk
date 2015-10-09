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

import java.io.IOException;

import java.net.HttpURLConnection;

import com.nexmo.sdk.verify.client.InternalNetworkException;

/**
 * Generic Connection Client.
 */
public interface ConnectionClient {

    /** Prepare a new connection with necessary custom header fields.
     * @param request The request object.
     *
     * @return A new url connection.
     * @throws IOException If an error occurs while opening the connection.
     */
    public HttpURLConnection initConnection(Request request) throws IOException;

    /**
     * Invokes an http request.
     * @param connection The http connection.
     *
     * @return The response object.
     * @throws IOException If an error occurs while connecting to the resource.
     * @throws InternalNetworkException If an internal sdk error occurs while parsing the response.
     */
    public Response execute(HttpURLConnection connection) throws IOException, InternalNetworkException;

}
