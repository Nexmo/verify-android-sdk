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

package com.nexmo.sdk.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.nexmo.sdk.BuildConfig;

/**
* Utility class for getting the handset properties.
* Used to check whether the handset uses a SIM card, or it is WiiFi only.
*/
public class DeviceUtil {

    /** Log tag. */
    public static final String TAG = DeviceUtil.class.getSimpleName();

    /**
     * Returns the ISO country code equivalent for the SIM provider's country code.
     * @param context The context of the sender activity.
     *
     * @return The country code of the current SIM card, {@code null} if the context supplied was {@code null}.
     */
    public static String getCountryCode(Context context) {
        if (context != null) {
            Context appContext = context.getApplicationContext();
            TelephonyManager manager  = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
            String simCountryCode = manager.getSimCountryIso();
            if (!TextUtils.isEmpty(simCountryCode))
                return simCountryCode.toUpperCase();
        }
        return null;
    }

    /**
     * Check if the provided SIM details match the internal SIM card.
     * @param context The context of the sender activity.
     * @param countryCode The supplied country code.
     * @param phoneNumber The supplied phone number.
     *
     * @return True if the supplied SIM details match the SIM card, False otherwise.
     */
    public static boolean isNumberMatchingSIMCard(Context context, final String countryCode, final String phoneNumber) {
        if (context != null) {
            String simPhoneNumber = DeviceUtil.getPhoneNumber(context);
            String simCountryCode = DeviceUtil.getCountryCode(context);
            return !(!TextUtils.isEmpty(simPhoneNumber) && !TextUtils.isEmpty(simCountryCode)) || (simPhoneNumber.equals(phoneNumber) && simCountryCode.equals(countryCode));
        } else
            return false;
    }

    /**
     * Returns the phone number for the SIM line 1. Only for GSM phones.
     * @param context The context of the sender activity.
     *
     * @return The phone number, or  {@code null} if it is unavailable.
     */
    public static String getPhoneNumber(Context context) {
        if (context != null) {
            Context appContext = context.getApplicationContext();
            TelephonyManager manager = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
            return manager.getLine1Number();
        }
        return null;
    }

    /**
     * Checks if the current GSM handset uses a SIM card.
     * @param context The context of the sender activity.
     *
     * @return True if the handset uses a SIM card,
     *         False if the handset has no SIM or it is WifiOnly.
     */
    public static boolean isSIMAvailable(Context context) {
        if (context != null) {
            Context appContext = context.getApplicationContext();
            TelephonyManager manager  = (TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE);
            return (TelephonyManager.SIM_STATE_READY == manager.getSimState() &&
                    !TextUtils.isEmpty(manager.getLine1Number()) &&
                    !TextUtils.isEmpty(manager.getSimCountryIso()));
        }
        return false;
    }

    /**
     * Check if there are 2 SIM cards available on the handset.
     * @param context The context of the sender activity.
     *
     * @return True if the handset if dualSim, false otherwise.
     */
    public static boolean isPhoneDualSIM(Context context) {
        if (context != null) {
            Context appContext = context.getApplicationContext();
            TelephonyManager telephonyManager = ((TelephonyManager) appContext.getSystemService(Context.TELEPHONY_SERVICE));

            try {
                Class<?> telephonyClass = Class.forName(telephonyManager.getClass().getName());
                Class<?>[] parameter = new Class[1];
                parameter[0] = int.class;

                Method getSimStateMethod = telephonyClass.getMethod("getSimState", parameter);
                Object[] objectParam = new Object[1];
                // Get Sim state for slotId 1.
                objectParam[0] = 1;

                Object objectDevice = getSimStateMethod.invoke(telephonyManager, objectParam);
                if (objectDevice != null && Integer.parseInt(objectDevice.toString()) == TelephonyManager.SIM_STATE_READY)
                    return true;
            } catch(NoClassDefFoundError| ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                if (BuildConfig.DEBUG)
                    Log.i(TAG, "isPhoneDualSIM: not available");
            }
        }
        return false;
    }

    /**
     * Indicates whether a network connectivity exists and if it is possible to establish
     * connections and pass data.
     * <p> This method does not provide notifications in case the connectivity changes, so make
     * sure you call this each time before attempting to send requests.
     * @param context The context of the sender activity.
     *
     * @return {@code true} if network connectivity exists, {@code false} otherwise.
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }
        return false;
    }

}
