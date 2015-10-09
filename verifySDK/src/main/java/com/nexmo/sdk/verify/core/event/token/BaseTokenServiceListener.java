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

package com.nexmo.sdk.verify.core.event.token;

import com.nexmo.sdk.verify.core.event.GenericExceptionListener;

public interface BaseTokenServiceListener extends GenericExceptionListener {

    /**
     * Indicates there is a new token received.
     * @param token The new token response.
     */
    public void onToken(final String token);

    /**
     * The token request has been rejected.
     * @param errorCode The {@link com.nexmo.sdk.verify.event.VerifyError} codes to describe the error.
     * @param errorMessage The message that describes the {@link com.nexmo.sdk.verify.event.VerifyError}.
     */
    public void onTokenError(final com.nexmo.sdk.verify.event.VerifyError errorCode, final String errorMessage);

}
