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

package com.nexmo.sdk.verify.client;

import com.nexmo.sdk.verify.event.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class VerifyClientTest {

    private static final String TAG = VerifyClientTest.class.getSimpleName();
    private VerifyClient verifyClient;
    private final VerifyClientListener listener = new VerifyClientListener() {
        @Override
        public void onVerifyInProgress(VerifyClient verifyClient) {
        }

        @Override
        public void onUserVerified(VerifyClient verifyClient) {
        }

        @Override
        public void onError(VerifyClient verifyClient, com.nexmo.sdk.verify.event.VerifyError errorCode) {
        }

        @Override
        public void onException(IOException exception) {

        }
    };

    @Before
    public void setUp() throws Exception {
        verifyClient = new VerifyClient(new MockNexmoClient().getClient());
    }

    @After
    public void tearDown() throws Exception {
        verifyClient = null;
    }

    @Test
    public void testRemoveVerifyListener() throws Exception {
        verifyClient.addVerifyListener(listener);
        assertTrue(TAG + " attached listener cannot be removed.",
                   verifyClient.removeVerifyListener(listener));
    }

    @Test
    public void testRemoveAllVerifyListeners() throws Exception {
        verifyClient.removeVerifyListeners();
        assertFalse(TAG + " listener still attached after removal.",
                    verifyClient.removeVerifyListener(listener));
    }

}