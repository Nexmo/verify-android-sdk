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

package com.nexmo.sdk.device;

import android.test.mock.MockContext;

import com.nexmo.sdk.core.device.DeviceProperties;
import com.nexmo.sdk.core.device.NoDeviceIdException;

import org.junit.Test;
import org.junit.internal.runners.statements.Fail;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DevicePropertiesTest {
    private static final String TAG = DevicePropertiesTest.class.getSimpleName();

    @Test
    public void testGetDeviceId(){
        try {
            DeviceProperties.getDeviceId(new MockContext());
           assertTrue(TAG + "getDeviceId did not throw any exception.", true);
        } catch (NoDeviceIdException e) {
            assertTrue(TAG + " getDeviceId failed with no context.", false);
        }
    }

    @Test
    public void testGetDeviceIdNoContext(){
        try {
            DeviceProperties.getDeviceId(null);
            assertTrue(TAG + "getDeviceId did not throw any exception.", false);
        } catch (NoDeviceIdException e) {
            assertTrue(TAG + " getDeviceId no device ID.", true);
        }
    }

    @Test
    public void testGetLanguage() {
        assertNotNull(TAG + " Device locale not found.", DeviceProperties.getLanguage());
    }

    @Test
    public void testGetApiLevel() {
        assertNotNull(TAG + " Android API version not found.", DeviceProperties.getApiLevel());
    }

}
