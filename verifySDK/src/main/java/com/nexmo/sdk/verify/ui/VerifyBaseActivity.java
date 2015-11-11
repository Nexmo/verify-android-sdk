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

package com.nexmo.sdk.verify.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nexmo.sdk.R;
import com.nexmo.sdk.verify.ui.response.ManagedVerifyResponse;

/**
 * Will define some common logic for all screens, such as onDestroy, onResume etc.
 */
public class VerifyBaseActivity extends Activity {

    public static final String TAG = VerifyBaseActivity.class.getSimpleName();
    public static final String ACTION_BROADCAST_MANAGED_EVENT = "com.nexmo.sdk.verify.ui.EVENT";
    public static final int TRY_AGAIN_RESULT_CODE = 1;
    public static final int VERIFIED_RESULT_CODE = 2;
    public static final int FAILED_RESULT_CODE = 3;
    public static final int CANCELED_RESULT_CODE = 4;
    public static final int CANCEL_FAILED_RESULT_CODE = 5;
    public static final int GO_TO_CHECK_REQUEST_CODE = 1;
    /**
     * Notify {@link com.nexmo.sdk.verify.client.VerifyClient} on the event.
     * @param response The response object passed to the calling {@@link com.nexmo.sdk.verify.client.VerifyClient}
     */
    public void broadcastManagedVerifyUpdate(final ManagedVerifyResponse response) {
        Intent intent = new Intent(ACTION_BROADCAST_MANAGED_EVENT);
        Bundle verifyResponseBundle = new Bundle();
        verifyResponseBundle.putParcelable(ManagedVerifyResponse.class.getSimpleName(), response);
        intent.putExtras(verifyResponseBundle);
        LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(intent);
    }

    // Cancel on-going verification, or even before it is started.
    public void onCancelVerification(View view) {
        Log.d(TAG, "cancel pressed");
        finish();
    }

    /**
     * Display long toast  to notify the user if verify is Done or Failed.
     * @param message The toast message.
     */
    public void displayToast(final String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

}
