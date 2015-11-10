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

import com.nexmo.sdk.core.event.ServiceListener;
import com.nexmo.sdk.verify.core.response.VerifyResponse;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class VerifyServiceTest {

    private static final String TAG = VerifyServiceTest.class.getSimpleName();

    @Test
    public void testNullParamsStart() {
        assertFalse(TAG + " Service able to start without params.",
                    VerifyService.getInstance().start(null, null));
    }

    @Test
    public void testMissingRequestObjectStart() {
        VerifyService verifyService = VerifyService.getInstance();
        verifyService.init(null);
        assertFalse(TAG + " Service able to start without params.",
                    verifyService.start(null, new ServiceListener<VerifyResponse>() {
            @Override
            public void onResponse(VerifyResponse response) {
            }

            @Override
            public void onFail(com.nexmo.sdk.verify.event.VerifyError errorCode, String reasonMessage) {
            }
        }));
    }

}
