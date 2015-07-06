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

package com.nexmo.sdk.core.event;

import com.nexmo.sdk.core.client.ResultCodes;
import com.nexmo.sdk.verify.core.event.BaseClientListener;
import com.nexmo.sdk.verify.core.event.GenericExceptionListener;
import com.nexmo.sdk.verify.event.VerifyError;

import java.io.IOException;

/**
 * SDK internal network response callbacks.
 * @param <T> expected response type.
 */
public abstract class ServiceListener<T> implements GenericExceptionListener {

    private static BaseClientListener clientListener;

    public ServiceListener(final BaseClientListener clientListener) {
        ServiceListener.clientListener = clientListener;
    }

    protected ServiceListener() {}

    public BaseClientListener getClientListener() {
        return this.clientListener;
    }

    /** Successful http response. */
    public abstract void onResponse(final T response);

    /** Unsuccessful http response. The reason code is given by a {@link VerifyError} and is explained by the reasonMessage. */
    public abstract void onFail(final VerifyError errorCode, final String reasonMessage);

    public static VerifyError formatResultCode(final int resultCode) {
        switch(resultCode) {
            case ResultCodes.INVALID_NUMBER:
                return VerifyError.INVALID_NUMBER;
            case ResultCodes.INVALID_CREDENTIALS:
                return VerifyError.INVALID_CREDENTIALS;
            case ResultCodes.OS_NOT_SUPPORTED:
                return VerifyError.OS_NOT_SUPPORTED;
            case ResultCodes.SDK_NOT_SUPPORTED:
                return VerifyError.SDK_REVISION_NOT_SUPPORTED;
            case ResultCodes.QUOTA_EXCEEDED:
                return VerifyError.QUOTA_EXCEEDED;
            case ResultCodes.INVALID_USER_STATUS_FOR_COMMAND:
            case ResultCodes.INVALID_USER_STATUS_FOR_LOGOUT:
                return VerifyError.INVALID_USER_STATUS_FOR_COMMAND;
            case ResultCodes.COMMAND_NOT_SUPPORTED:
                return VerifyError.COMMAND_NOT_SUPPORTED;
            default:
                return VerifyError.INTERNAL_ERR;
        }
    }

    /**
     * A request was timed out because of network connectivity exception.
     * Triggered in case of network error, such as UnknownHostException or SocketTimeout exception.
     *
     * @param exception The exception.
     */
    @Override
    public void onException(final IOException exception) {
        getClientListener().handleNetworkException(exception);
    }

}
