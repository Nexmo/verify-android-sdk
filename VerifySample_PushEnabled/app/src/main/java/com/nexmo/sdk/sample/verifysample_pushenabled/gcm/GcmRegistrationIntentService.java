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

package com.nexmo.sdk.sample.verifysample_pushenabled.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import java.io.IOException;

/**
 * <p>An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 * Handles GCM token registration.
 * Notifies
 */
public class GcmRegistrationIntentService extends IntentService {

    public static final String ACTION_REGISTER = "com.nexmo.sdk.sample.verifysample_pushenabled.gcm.action.REGISTER";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String INTENT_EXTRA_SENDER_ID = "SENDER_ID";
    public static final String INTENT_EXTRA_PUSH_TOKEN = "PUSH_TOKEN";
    // Please provide here your SENDER_ID:
    public static final String SENDER_ID = "919459387407";
    private static final String TAG = GcmRegistrationIntentService.class.getSimpleName();

    /**
     * Creates an IntentService. Invoked by your subclass's constructor.
     *
     */
    public GcmRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Synchronize this part in case multiple refresh operations occur.
        if(intent != null && ACTION_REGISTER.equals(intent.getAction()) && intent.hasExtra(INTENT_EXTRA_SENDER_ID))
            synchronized(TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                String sender_id = intent.getExtras().getString(INTENT_EXTRA_SENDER_ID);
                String token = null;

                try {
                    token = instanceID.getToken(sender_id, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                    Log.d(TAG, "GCM registration token: " + token);
                } catch (IOException e) {
                    Log.e(TAG, "GCM registration token failed", e);
                }
                // Notify Application class that registration has completed, so NexmoClient can update the GcmRegistrationToken.
                Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
                intent.putExtra(INTENT_EXTRA_PUSH_TOKEN, token);
                LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
            }
    }

}
