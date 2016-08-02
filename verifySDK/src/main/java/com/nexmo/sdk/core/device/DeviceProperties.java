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

package com.nexmo.sdk.core.device;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.nexmo.sdk.util.DeviceUtil;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;

/**
 * Utility class for accessing device properties.
 */
public class DeviceProperties {

    /** Log tag. */
    private static final String TAG = DeviceProperties.class.getSimpleName();
    private static final String SERIAL_NO_RESET_TO_INVALID = "00000000000000";

    /**
     * Get the Android API version.
     *
     * @return The Android API version.
     */
    public static String getApiLevel() {
        return Build.VERSION.RELEASE;
    }

    /**
     * Get the IP address of the current device.
     * @param context The context of the sender activity.
     *
     * @return The IP address of the current device, or null if 'android.permission.ACCESS_WIFI_STATE" is denied.
     */
    public static String getIPAddress(Context context) {
        if (context != null && DeviceUtil.isAccessWifiStateGranted(context)) {
            // Use the Application context to prevent memory leaks when referencing activities that are being killed.
            Context appContext = context.getApplicationContext();

            WifiManager manager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
            // Get the WiFi or cellular network IP address.
            if (manager.isWifiEnabled()) {
                int ipAddress = manager.getConnectionInfo().getIpAddress();
                // Format the integer IP address to the numeric representation.
                return ((ipAddress>>24) & 0xFF) + "." + ((ipAddress>>16) & 0xFF) + "." + ((ipAddress>>8) & 0xFF) + "." + ((ipAddress & 0xFF));
            } else {
                try {
                    for (Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces(); networks.hasMoreElements();) {
                        NetworkInterface networkInterface = networks.nextElement();
                        for (Enumeration<InetAddress> ipAddresses = networkInterface.getInetAddresses(); ipAddresses.hasMoreElements();) {
                            InetAddress inetAddress = ipAddresses.nextElement();
                            // Ignore the loopback address.
                            // // If only the loopback is available, it is not possible to do any requests to the service.
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    Log.e(TAG, "Error getting the IP address." + e.getMessage());
                }
            }
        }
        return  null;
    }

    /**
     * Get the user's preferred locale language. The format is language code and country code, separated by dash.
     * <p> Since the user's locale changes dynamically, avoid caching this value.
     *
     * @return The user's preferred language.
     */
    public static String getLanguage() {
        String language = Locale.getDefault().toString();
        if (!TextUtils.isEmpty(language) && language.indexOf("_") > 1) {
            return language.replace("_", "-");
        }
        return null;
    }

    /**
     * Get a unique deviceId for the current handset, even for SIM-less devices.
     *
     * First attempt is to get a serial number, and if this cannot be retrieved
     * we fallback to IMEI or ANDROID_ID.
     * @param context The context of the sender activity.
     *
     * @return The unique Id of the device.
     */
    public static String getDeviceId(Context context) throws NoDeviceIdException {
        if (context != null) {
            String serialNo = getSerialNumber();
            if(TextUtils.isEmpty(serialNo)) {
                String android_ID = getAndroid_ID(context);
                if(TextUtils.isEmpty(android_ID)) {
                    String IMEI = getIMEI(context);
                    if(!TextUtils.isEmpty(IMEI))
                        return IMEI;
                    throw new NoDeviceIdException(TAG + " Device ID is not available.");
                }
                return android_ID;
            }
            return serialNo;
        }
        return null;
    }

    /**
     * Get the Android unique IMEI of the current handset, depending on the network technology.
     * For example, the IMEI for GSM and the MEID or ESN for CDMA phones.
     * @param context The context of the sender activity.
     *
     * @return The unique IMEI of the device,
     *         or null if the context is not supplied, device id is not available due to DENIED permission.
     */
    private static String getIMEI(Context context) {
        if (context != null && DeviceUtil.isAccessWifiStateGranted(context)) {
            // Use the Application context to prevent memory leaks when referencing activities that are being killed.
            Context appContext = context.getApplicationContext();
            TelephonyManager manager  = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
            return manager.getDeviceId();
        }
        //if not telephony return null and use serial.
        return null;
    }

    /**
     * Get the unique hardware serial number for any telephony device with API level >=9, no permission is required.
     *
     * Serial number can be identified for the devices such as MIDs (Mobile Internet Devices)
     * or PMPs (Portable Media Players) which are not having telephony services.
     *
     * Note: Known bug during some Android upgrades, it can be reset to trailing zeros:
     * https://code.google.com/p/android/issues/detail?id=35193
     *
     * @return The serial number of the device(Alphanumeric only, case-insensitive),
     *         or null if it is not available.
     */
    private static String getSerialNumber() {
        return (!Build.SERIAL.equals(SERIAL_NO_RESET_TO_INVALID) ? Build.SERIAL : null);
    }

    /**
     * A randomly generated 64-bit number on the device's first boot that does not
     * survive factory resets.
     *
     * @return The device ANDROID_ID, or null if the context is not supplied.
     */
    private static String getAndroid_ID(Context context) {
        if (context != null)
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return null;
    }

}