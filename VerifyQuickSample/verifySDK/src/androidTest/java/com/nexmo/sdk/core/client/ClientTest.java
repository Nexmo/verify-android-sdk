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

package com.nexmo.sdk.core.client;

import com.nexmo.sdk.core.config.Config;
import com.nexmo.sdk.core.device.DeviceProperties;
import com.nexmo.sdk.verify.core.service.BaseService;
import com.nexmo.sdk.verify.client.InternalNetworkException;
import com.nexmo.sdk.verify.client.MockNexmoClient;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.TreeMap;

public class ClientTest {
    private static final String TAG = ClientTest.class.getSimpleName();
    private Client client;
    private MockNexmoClient mockNexmoClient;
    private HttpURLConnection connection;
    Map<String, String> requestParams = new TreeMap<>();

    @Before
    public void setUp() throws Exception {
        mockNexmoClient = new MockNexmoClient();
        client = new Client();
        connection = client.initConnection(new Request(mockNexmoClient.getClientHost(),
                mockNexmoClient.getSharedSecretKey(),
                BaseService.METHOD_TOKEN,
                requestParams));
    }

    @Test
    public void testInitTokenConnection() throws Exception {
        assertNotNull(connection);
    }

    @Test
    public void testExecuteTokenConnection() throws Exception {
        try {
            client.execute(connection);
            assertTrue(TAG + " testExecuteTokenConnection, did not throw exception.", false);
        } catch (InternalNetworkException e) {
            // runs on JVM, not Android.
            assertTrue(TAG + " testExecuteTokenConnection", true);
        }
    }

    @Test
    public void testOSFamilyHeader() throws Exception{
        assertEquals(BaseService.OS_FAMILY + " invalid", connection.getHeaderField(BaseService.OS_FAMILY), Config.OS_ANDROID);
    }

    @Test
    public void testOSRevisionHeader() throws Exception{
        assertEquals(BaseService.OS_REVISION + " invalid", connection.getHeaderField(BaseService.OS_REVISION), DeviceProperties.getApiLevel());
    }
    @Test
    public void testSDKRevisionHeader() throws Exception{
        assertEquals(BaseService.SDK_REVISION + " invalid", connection.getHeaderField(BaseService.SDK_REVISION), Config.SDK_REVISION_CODE);
    }

}