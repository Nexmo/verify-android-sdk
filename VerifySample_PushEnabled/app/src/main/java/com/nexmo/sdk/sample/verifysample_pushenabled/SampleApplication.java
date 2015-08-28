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

package com.nexmo.sdk.sample.verifysample_pushenabled;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.nexmo.sdk.NexmoClient;
import com.nexmo.sdk.core.client.ClientBuilderException;
import com.nexmo.sdk.sample.verifysample_pushenabled.gcm.GcmRegistrationIntentService;
import com.nexmo.sdk.verify.client.VerifyClient;

/**
 * Application entry-point used for maintaining global verifyClient instance state.
 * Listens for GCM registration tokens and updates the NexmoClient accordingly.
 *
*/
public class SampleApplication extends Application {

    public static final String TAG = SampleApplication.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
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
        if (TextUtils.isEmpty(Config.NexmoAppId) || TextUtils.isEmpty(Config.NexmoSharedSecretKey)) {
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
                    .environmentHost(NexmoClient.ENVIRONMENT_HOST.PRODUCTION)
                    .build();
        } catch (ClientBuilderException e) {
            e.printStackTrace();
            return;
        }
        this.verifyClient = new VerifyClient(nexmoClient);

        if (TextUtils.isEmpty(Config.PushSenderID))
            Log.e(TAG, "You haven't provided a valid SENDER_ID for GCM to be enabled.");
        else {
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.hasExtra(GcmRegistrationIntentService.INTENT_EXTRA_PUSH_TOKEN))
                        nexmoClient.setGcmRegistrationToken(intent.getStringExtra(GcmRegistrationIntentService.INTENT_EXTRA_PUSH_TOKEN));
                }
            };
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    mRegistrationBroadcastReceiver, new IntentFilter(GcmRegistrationIntentService.REGISTRATION_COMPLETE));
        }
    }

}
