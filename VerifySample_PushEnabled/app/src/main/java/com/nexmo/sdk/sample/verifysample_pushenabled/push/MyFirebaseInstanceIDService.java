package com.nexmo.sdk.sample.verifysample_pushenabled.push;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Handle Firebase refresh tokens.
 *
 * Created by emma tresanszki on 10/11/2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    public static final String REGISTRATION_COMPLETE = "com.nexmo.sdk.sample.verifysample_pushenabled.push.registrationComplete";
    public static final String INTENT_EXTRA_PUSH_TOKEN = "com.nexmo.sdk.sample.verifysample_pushenabled.push.PUSH_TOKEN";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        // TODO: Implement this method to send any registration to your app's servers.

       // Notify Application class that registration has completed, so NexmoClient can update the PushRegistrationToken.
        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        registrationComplete.putExtra(INTENT_EXTRA_PUSH_TOKEN, refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}
