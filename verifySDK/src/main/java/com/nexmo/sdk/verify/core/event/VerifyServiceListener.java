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

import com.nexmo.sdk.core.client.ResultCodes;
import com.nexmo.sdk.core.event.ServiceListener;

import com.nexmo.sdk.verify.core.response.VerifyResponse;
import com.nexmo.sdk.verify.core.service.VerifyService;

import com.nexmo.sdk.verify.event.UserStatus;
import com.nexmo.sdk.verify.event.VerifyError;

public class VerifyServiceListener extends ServiceListener<VerifyResponse> {

    public VerifyServiceListener(final BaseClientListener clientListener){
        super(clientListener);
    }

    @Override
    public void onResponse(VerifyResponse response) {
        switch (response.getResultCode()) {
            case ResultCodes.RESULT_CODE_OK:
            case ResultCodes.VERIFICATION_RESTARTED:
            case ResultCodes.VERIFICATION_EXPIRED_RESTARTED: {
                UserStatus userStatus = response.getUserStatus();
                // Trigger onVerifyInProgress for the VerifyClientListener.
                if (userStatus == UserStatus.USER_PENDING)
                    getClientListener().handleUserStateChanged(UserStatus.USER_PENDING);
                else if (userStatus == UserStatus.USER_EXPIRED)
                    getClientListener().notifyErrorListeners(VerifyError.USER_EXPIRED);
                else if (userStatus == UserStatus.USER_BLACKLISTED)
                    getClientListener().notifyErrorListeners(VerifyError.USER_BLACKLISTED);
                    // Trigger onUserVerified event if user was already verified.
                else if(userStatus == UserStatus.USER_VERIFIED)
                    getClientListener().handleUserStateChanged(UserStatus.USER_VERIFIED);
                else
                    // Unknown user status.
                    getClientListener().notifyErrorListeners(VerifyError.INTERNAL_ERR);
                break;
            }
            case ResultCodes.INVALID_USER_STATUS_FOR_STATELESS_VERIFICATION_REQUEST: {
                // todo see if the mapping is not by default.
                getClientListener().notifyErrorListeners(VerifyError.INVALID_USER_STATUS_FOR_STATELESS_VERIFICATION);
                break;
            }
            case ResultCodes.INVALID_TOKEN: {
                // Restart verify and request for a new token.
                // If token continues to expire the service will send back a throttled error.
                VerifyService.getInstance().start(VerifyService.getInstance().getNexmoClient(), this);
                break;
            }
            default: {
                getClientListener().handleErrorResult(response.getResultCode());
                break;
            }
        }
    }

    @Override
    public void onFail(final com.nexmo.sdk.verify.event.VerifyError errorCode,
                       final String reasonMessage) {
        getClientListener().notifyErrorListeners(errorCode);
    }

}
