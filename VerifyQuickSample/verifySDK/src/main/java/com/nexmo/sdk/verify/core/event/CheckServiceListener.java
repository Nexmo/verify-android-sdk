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

import com.nexmo.sdk.verify.core.response.CheckResponse;
import com.nexmo.sdk.verify.core.service.CheckService;

public class CheckServiceListener extends ServiceListener<CheckResponse> {

    public CheckServiceListener(final BaseClientListener clientListener){
        super(clientListener);
    }

    @Override
    public void onResponse(CheckResponse response) {
        switch (response.getResultCode()) {
            case ResultCodes.RESULT_CODE_OK: {
                getClientListener().handleUserStateChanged(response.getUserStatus());
                break;
            }
            case ResultCodes.INVALID_TOKEN: {
                // Restart check and request for a new token.
                CheckService.getInstance().start(CheckService.getInstance().getNexmoClient(), this);
                break;
            }
            default: {
                getClientListener().handleErrorResult(response.getResultCode());
                break;
            }
        }
    }

    @Override
    public void onFail(com.nexmo.sdk.verify.event.VerifyError errorCode, String reasonMessage) {
        getClientListener().notifyErrorListeners(errorCode);
    }

}
