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

package com.nexmo.sdk;

import android.test.mock.MockContext;

import com.nexmo.sdk.core.client.ClientBuilderException;
import com.nexmo.sdk.core.config.Config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NexmoClientTest {
    private static final String TAG = NexmoClientTest.class.getSimpleName();
    private NexmoClient nexmoClient;

    @Before
    public void setUp() throws Exception {
        nexmoClient = new NexmoClient.NexmoClientBuilder()
                 .context(new MockContext())
                .applicationId("app_dummy")
                .sharedSecretKey("secret_dummy")
                .environmentHost(NexmoClient.ENVIRONMENT_HOST.PRODUCTION)
                .build();
    }

    @After
    public void tearDown() throws Exception {
        nexmoClient = null;
    }

    @Test
    public void testGetSharedSecretKey() throws Exception {
        assertEquals(TAG + " testGetSharedSecretKey" , nexmoClient.getSharedSecretKey(), "secret_dummy");
    }

    @Test
    public void testGetAppId() throws Exception {
        assertEquals(TAG + " testGetAppId", nexmoClient.getApplicationId(), "app_dummy");
    }

    @Test
    public void testGetEnvironment() throws Exception {
        assertEquals(TAG + " testGetEnvironment", nexmoClient.getEnvironmentHost(), NexmoClient.ENVIRONMENT_HOST.PRODUCTION);
    }

    @Test
    public void expectedConfigNoAppId() throws Exception {
        try {
            new NexmoClient.NexmoClientBuilder()
                    .context(new MockContext())
                    .sharedSecretKey("xyz")
                    .environmentHost(NexmoClient.ENVIRONMENT_HOST.PRODUCTION)
                    .build();
            assertTrue(TAG + " expectedConfigNoAppId, did not throw exception.", false);
        } catch (ClientBuilderException exception) {
            assertTrue(TAG + " expectedConfigNoAppId", true);
        }
    }

    @Test
    public void expectedConfigNoSecretKey() throws Exception {
        try {
            new NexmoClient.NexmoClientBuilder().context(new MockContext())
                    .applicationId("app_dummy")
                    .environmentHost(NexmoClient.ENVIRONMENT_HOST.PRODUCTION)
                    .build();
            assertTrue(TAG + " expectedConfigNoSecretKey, did not throw exception.", false);
        } catch (ClientBuilderException exception) {
            assertTrue(TAG + " expectedConfigNoSecretKey", true);
        }
    }

    @Test
    public void testGetVersion() throws Exception {
        assertEquals(TAG + " testGetVersion", nexmoClient.getVersion(), Config.SDK_REVISION_CODE);
    }

}