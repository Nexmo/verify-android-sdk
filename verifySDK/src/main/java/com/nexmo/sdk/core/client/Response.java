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

/**
* Wrapper class that encapsulates the signature header and content of raw http responses.
*/
public class Response {

    private final String signature;
    private final String body;

    public Response(String body, String signature) {
        this.signature = signature;
        this.body = body;
    }

    public String getSignature() {
        return signature;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Signature: " + (this.signature != null ? this.signature : "") + ". Content: " + (this.body != null ? this.body : "");
    }

}
