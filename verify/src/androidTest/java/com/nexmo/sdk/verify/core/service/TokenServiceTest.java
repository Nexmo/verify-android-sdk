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

import com.nexmo.sdk.verify.client.MockNexmoClient;
import com.nexmo.sdk.verify.core.event.token.BaseTokenServiceListener;
import com.nexmo.sdk.verify.event.VerifyError;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class TokenServiceTest {

    private static final String TAG = TokenServiceTest.class.getSimpleName();
    private MockNexmoClient mockNexmoClient;
    private TokenService mockServiceInstance = TokenService.getInstance();

    @Before
    public void setUp() throws Exception {
        mockNexmoClient = new MockNexmoClient();
    }

    @Test
    public void testNullParamsStart() {
        assertFalse(TAG + " Service able to start without params.",
                    mockServiceInstance.start(null, null));
    }

    @Test
    public void testNullListenerStart() {
        assertFalse(TAG + " Service able to start without params.",
                mockServiceInstance.start(mockNexmoClient.getClient(), null));
    }

    @Test
    public void testInvalidCredentialsStart() {
        mockServiceInstance.start(mockNexmoClient.getClient(),
                                   new BaseTokenServiceListener() {
                @Override
                public void onToken(String token) {
                    fail(TAG + " Invalid appId and sharedSecret received a Token response.");
                }

                @Override
                public void onTokenError(com.nexmo.sdk.verify.event.VerifyError errorCode, String errorMessage) {
                    assertEquals(TAG + " Dummy appId and sharedSecret triggered different error(" + errorCode.toString() + ")", errorCode, VerifyError.INVALID_CREDENTIALS);
                }

                @Override
                public void onException(IOException exception) {
                }
            });
    }

}
