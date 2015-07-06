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

package com.nexmo.sdk.sample.verifysample;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.nexmo.sdk.NexmoClient;
import com.nexmo.sdk.core.client.ClientBuilderException;
import com.nexmo.sdk.verify.client.VerifyClient;

/**
 * Used for maintaining global verifyClient instance state.
*/
public class SampleApplication extends Application {

    public static final String TAG = SampleApplication.class.getSimpleName();
    private VerifyClient verifyClient;

    public VerifyClient getVerifyClient() {
        return this.verifyClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        acquireVerifyClient();
    }

    /**
     * Acquire a new verify client.
     * If the user changes the settings shared preferences, a new verify client needs
     * to be created to reflect the new configuration.
     * Storing the credentials: applicationId and sharedSecretKey is up to the developer:
     * you may choose to use SharedPreferences, a file or define meta-data in the AndroidManifest.xml
     */
    public void acquireVerifyClient() {
        String appId = null;
        String sharedSecretKey = null;
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            appId = applicationInfo.metaData.getString("com.nexmo.sdk.applicationId");
            sharedSecretKey = applicationInfo.metaData.get("com.nexmo.sdk.sharedSecretKey").toString();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        }

        // Acquire the NexmoClient with all the necessary parameters.
        Context context = getApplicationContext();
        NexmoClient nexmoClient = null;
        try {
            nexmoClient = new NexmoClient.NexmoClientBuilder()
                    .context(context)
                    .applicationId(appId)
                    .sharedSecretKey(sharedSecretKey)
                    .environmentHost(NexmoClient.ENVIRONMENT_HOST.PRODUCTION)
                    .build();
        } catch (ClientBuilderException e) {
            e.printStackTrace();
        }
        this.verifyClient = new VerifyClient(nexmoClient);
    }

}
