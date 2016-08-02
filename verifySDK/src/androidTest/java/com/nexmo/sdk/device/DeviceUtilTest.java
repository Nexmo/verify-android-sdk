package com.nexmo.sdk.device;

import com.nexmo.sdk.util.DeviceUtil;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class DeviceUtilTest {
    private static final String TAG = DeviceUtilTest.class.getSimpleName();

    @Test
    public void testIsNetworkAvailableNullContext() {
        assertFalse(TAG + "isNetworkAvailable does not fail for null context.",
                DeviceUtil.isNetworkAvailable(null));
    }

    @Test
    public void testIsNetworkAvailableDeniedPermission() {
        DeviceUtil.isNetworkAvailable(null);
    }
}
