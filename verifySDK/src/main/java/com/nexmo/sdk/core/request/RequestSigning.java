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

package com.nexmo.sdk.core.request;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Map;
import java.util.TreeMap;

import android.text.TextUtils;
import android.util.Log;

import com.nexmo.sdk.core.config.Defaults;
import com.nexmo.sdk.core.client.Response;
import com.nexmo.sdk.verify.core.service.BaseService;

import com.nexmo.sdk.BuildConfig;

/**
 * Signing mechanism used for all the SDK service requests.
 * <ul>
 *     <li>Attach all request parameters together in a string.</li>
 *     <li>Append the shared_secret and timestamp and apply an MD5 algorithm to compute the signature.</li>
 *     <li>The signature together with the timestamp will be appended to the request parameters.</li>
 * </ul>
 *
 */
public class RequestSigning {

    private static final String TAG = RequestSigning.class.getName();
    private static final String SIGN_ALGORITHM = "MD5";

    /**
     * Signing mechanism used for all the SDK service requests.
     * <p>
     * Sign a set of request parameters, generating additional parameters to represent the timestamp
     * and generated signature. Uses the supplied pre-shared secret key to generate the signature.</p>
     *
     * @param params Map containing name value pair parameters to be submitted as part of the url.
     * @param secretKey the pre-shared secret key held by the merchant.
     *
     * @return String the fully constructed url complete with signature.
     */
    public static String constructSignatureForRequestParameters(Map<String, String> params, final String secretKey) {
        // Inject a 'timestamp=' parameter containing the current time in seconds since Jan 1st 1970
        params.put(BaseService.PARAM_TIMESTAMP, "" + System.currentTimeMillis() / 1000);

        // Now, append the secret key, and calculate an MD5 signature of the resultant string.
        String requestString = constructRequestParamsString(params, secretKey);
        String md5 = computeMD5Hash(requestString);

        if (BuildConfig.DEBUG)
            Log.i(TAG, "SECURITY-KEY-GENERATION -- String [ " + requestString + " ] Signature [ " + md5 + " ] ");

        params.put(BaseService.PARAM_SIGNATURE, md5);

        return md5;
    }

    public static boolean verifyRequestSignature(final String timestamp,
                                                 final Response response,
                                                 final String secretKey) {
        boolean isSignatureValid = false;
        if (timestampAllowed(timestamp)) {
            String md5 = computeMD5Hash(response.getBody() + secretKey);
            isSignatureValid = (md5.equals(response.getSignature()));
        }

        if (BuildConfig.DEBUG)
            Log.d(TAG, "verifyRequestSignature result: " + isSignatureValid);
        return isSignatureValid;
    }

    /**
     * Append all the parameters in a sorted order.
     * @param params The request parameters.
     * @param secretKey The pre-shared secret key.
     *
     * @return The resulting string.
     */
    private static String constructRequestParamsString(Map<String, String> params, final String secretKey) {
        // construct a string from the sorted params, excluding the signature.
        Map<String, String> sortedParams = new TreeMap<>();
        for (Map.Entry<String, String> param: params.entrySet()) {
            String name = param.getKey();
            String value = param.getValue();
            if (name.equals(BaseService.PARAM_SIGNATURE))
                continue;
            if (!TextUtils.isEmpty(value) && !TextUtils.isEmpty(value.trim()))
                sortedParams.put(name, value);
        }

        // Now, walk through the sorted list of parameters and construct a string
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> param: sortedParams.entrySet()) {
            String name = param.getKey();
            String value = param.getValue();
            sb.append("&").append(clean(name)).append("=").append(clean(value));
        }

        sb.append(secretKey);
        return sb.toString();
    }

    /**
     * Verify if the timestamp is within 5minutes of 'current time'.
     * @param timestamp The timestamp parameter.
     *
     * @return True if the timestamp is valid.
     */
    private static boolean timestampAllowed(final String timestamp) {
        if (!TextUtils.isEmpty(timestamp)){
            long time = -1;
            try {
                time = Long.parseLong(timestamp) * 1000;
            } catch (NumberFormatException e){
                return false;
            }
            long diff = System.currentTimeMillis() - time;
            if (diff > Defaults.MAX_ALLOWABLE_TIME_DELTA || diff < -Defaults.MAX_ALLOWABLE_TIME_DELTA) {
                if (BuildConfig.DEBUG)
                    Log.d(TAG, "SECURITY-KEY-VERIFICATION -- BAD-TIMESTAMP ... Timestamp [ " + time + " ] delta [ " + diff + " ] max allowed delta [ " + -Defaults.MAX_ALLOWABLE_TIME_DELTA + " ] ");
                return false;
            }
        }
        return true;
    }

    private static String computeMD5Hash(final String input) {
        try{
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(SIGN_ALGORITHM);
            digest.update(input.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder MD5Hash = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                MD5Hash.append(h);
            }
            return MD5Hash.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String clean(String str) {
        return str == null ? null : str.replaceAll("[=&]", "_");
    }

}
