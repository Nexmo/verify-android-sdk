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

import java.util.Map;
import java.util.TreeMap;

import com.nexmo.sdk.NexmoClient;

/**
 * Wrapper class to encapsulate all needed Http request data.
 */
public class Request {

    private final NexmoClient.ENVIRONMENT_HOST url;
    private final String secretKey;
    /** The method name, appended to the ENDPOINT: production/sandbox. **/
    private final String method;
    private final TreeMap<String, String> params;

    public Request(NexmoClient.ENVIRONMENT_HOST url,
                   String secretKey,
                   String method,
                   Map<String,
                   String> params) {
        this.url = url;
        this.secretKey = secretKey;
        this.method = method;
        this.params = new TreeMap<>(params);
    }

    public NexmoClient.ENVIRONMENT_HOST getUrl() {
        return this.url;
    }

    public String getSecretKey(){
        return this.secretKey;
    }

    public String getMethod() {
        return this.method;
    }

    public TreeMap<String, String> getParams() {
        return this.params;
    }

}
