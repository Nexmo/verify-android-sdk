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

package com.nexmo.sdk.verify.core.event;

import java.io.IOException;

import com.nexmo.sdk.verify.event.*;
import com.nexmo.sdk.verify.event.VerifyError;

/**
 * Base interface that notifies the VerifyClient on incoming events from all the services.
 */
public interface BaseClientListener {

    /**
     * Handle the error result code.
     * Notify all listeners on response codes.
     * @param resultCode The response code.
     */
    public void handleErrorResult(final int resultCode);

    /**
     * Notify all VerifyClientListeners on error events.
     * @param verifyError The verify error code.
     */
    public void notifyErrorListeners(final VerifyError verifyError);

    /**
     * Notify all listeners of a user status change.
     * @param userStatus The new user status.
     */
    public void handleUserStateChanged(final UserStatus userStatus);

    /**
     * Notify all listeners of a network connectivity exception.
     * @param exception The exception.
     */
    public void handleNetworkException(final IOException exception);

}
