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

package com.nexmo.sdk.sample.verifyquicksample;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
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
    private NexmoClient nexmoClient;

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
        if(TextUtils.isEmpty(Config.NexmoAppId) || TextUtils.isEmpty(Config.NexmoSharedSecretKey)) {
            Log.e(TAG, "You must supply valid appId and sharedSecretKey, provided by Nexmo");
            return;
        }

        // Acquire the NexmoClient with all the necessary parameters.
        Context context = getApplicationContext();
        try {
            this.nexmoClient = new NexmoClient.NexmoClientBuilder()
                    .context(context)
                    .applicationId(Config.NexmoAppId)
                    .sharedSecretKey(Config.NexmoSharedSecretKey)
                    .environmentHost("https://test.nexmoinc.net/sdk/")
                    .build();
        } catch (ClientBuilderException e) {
            e.printStackTrace();
            return;
        }
        this.verifyClient = new VerifyClient(nexmoClient);
    }

}