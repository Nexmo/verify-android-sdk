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

import com.nexmo.sdk.verify.event.VerifyError;

/**
 * SDK internal network response callbacks.
 * @param <T> expected response type.
 */
public abstract class ServiceListener<T> {

    /** Successful http response. */
    public abstract void onResponse(final T response);

    /** Unsuccessful http response. The reason code is given by a {@link VerifyError} and is explained by the reasonMessage. */
    public abstract void onFail(final VerifyError errorCode, final String reasonMessage);

}
