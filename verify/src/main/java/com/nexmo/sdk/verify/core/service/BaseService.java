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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import com.nexmo.sdk.NexmoClient;

import com.nexmo.sdk.core.client.Response;
import com.nexmo.sdk.core.client.ResultCodes;

import com.nexmo.sdk.core.event.ServiceListener;
import com.nexmo.sdk.core.request.RequestSigning;
import com.nexmo.sdk.verify.core.response.BaseResponse;
import com.nexmo.sdk.verify.core.request.VerifyRequest;

/**
 * Wrapper class used for constructing and sending Http requests to Nexmo services.
 *
 * @param <T> expected response type.
 */
public abstract class BaseService<T extends BaseResponse> extends Service {

    /** Log tag, apps may override it. */
    private static final String TAG = BaseService.class.getSimpleName();

    /** HTTP request methods. */
    public static final String METHOD_TOKEN = "token/json?";
    public static final String METHOD_VERIFY = "verify/json?";
    public static final String METHOD_CHECK = "verify/check/json?";

    /** Custom HTTP header fields. */
    public static final String OS_FAMILY = "X-NEXMO-SDK-OS-FAMILY";
    public static final String OS_REVISION = "X-NEXMO-SDK-OS-REVISION";
    public static final String SDK_REVISION = "X-NEXMO-SDK-REVISION";
    public static final String RESPONSE_SIG = "X-NEXMO-RESPONSE-SIGNATURE";

    /** HTTP request parameters. */
    public static final String PARAM_DEVICE_ID = "device_id";
    public static final String PARAM_SOURCE_IP = "source_ip_address";
    public static final String PARAM_APP_ID = "app_id";
    public static final String PARAM_NUMBER = "number";
    public static final String PARAM_COUNTRY_CODE = "country";
    public static final String PARAM_TIMESTAMP = "timestamp";
    public static final String PARAM_LANGUAGE = "lg";
    public static final String PARAM_SIGNATURE = "sig";

    /** HTTP response parameters. */
    public static final String PARAM_RESULT_CODE = "result_code";
    public static final String PARAM_RESULT_MESSAGE = "result_message";
    public static final String PARAM_RESULT_USER_STATUS = "user_status";

    /** User status */
    public static final String USER_NEW = "new";
    public static final String USER_PENDING = "pending";
    public static final String USER_VERIFIED = "verified";
    public static final String USER_FAILED = "failed";
    public static final String USER_EXPIRED = "expired";
    public static final String USER_BLACKLISTED = "blacklisted";

    public static final Gson gson = new GsonBuilder().create();

    protected BaseService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Validate if the response signature was supplied and if it matches the expected value.
     * @param nexmoClient The NexmoClient object that sends the request.
     * @param response The http response parsed as a {@link BaseResponse}
     * @param result The un-parsed response message.
     *
     * @return true if the signature is invalid, false otherwise.
     */
    public static boolean isSignatureInvalid(final NexmoClient nexmoClient,
                                             final BaseResponse response,
                                             final Response result) {
        if (response.getResultCode() == ResultCodes.INVALID_CREDENTIALS)
            if (TextUtils.isEmpty(result.getSignature()))
                return true;
        if (response.getResultCode() == ResultCodes.RESULT_CODE_OK)
            if (!RequestSigning.verifyRequestSignature(response.getTimestamp(), result, nexmoClient.getSharedSecretKey()))
                return true;
        return false;
    }

    /**
     * Deserialize the specified Json into an object of the specified class.
     * @param input The raw response body in string format.
     *
     * @return An object of type {@link}T from the string. Returns {@code null} if {@link @param input} is {@code null}.
     * @throws JsonSyntaxException If {@param input} is not a valid representation for an object of type {@link T}.
     */
    abstract T parseJson(final String input) throws JsonSyntaxException;

    /**
     * Initiate the task that triggers the http request.
     * @param nexmoClient The NexmoClient object that sends the request.
     * @param request     The request object.
     * @param listener    The internal listener.
     */
    abstract void start(final NexmoClient nexmoClient, final VerifyRequest request, final ServiceListener<T> listener);

}
