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

package com.nexmo.sdk.core.gcm;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.nexmo.sdk.BuildConfig;
import com.nexmo.sdk.R;

/**
 *  The GCM push messages listener.
 *  Handle GCM messages, displays a notification and silently triggers a verify check request.
 */
public class VerifyGcmListenerService extends GcmListenerService {

    public static final String MESSAGE_KEY_PIN = "pin";
    public static final String MESSAGE_KEY_TITLE = "title";
    public static final String NOTIFICATION_DEFAULT_TITLE = "Nexmo Verify";
    public static final String ACTION_BROADCAST_PIN = "com.nexmo.sdk.core.gcm.BROADCAST_PIN";
    private static final String TAG = VerifyGcmListenerService.class.getSimpleName();

    /**
     * Called when a message is received.
     * @param from The senderId.
     * @param data Data bundle containing key/value pairs.
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG, "onMessageReceived");
        String notificationTitle = NOTIFICATION_DEFAULT_TITLE;
        // Identify the app server that is permitted to send messages to this client.
        if (data.containsKey(MESSAGE_KEY_PIN)) {
            String pinCode = data.getString(MESSAGE_KEY_PIN);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onMessageReceived From: " + from);
                Log.d(TAG, "onMessageReceived Pin: " + pinCode);
            }
            if (data.containsKey(MESSAGE_KEY_TITLE))
                notificationTitle = data.getString(MESSAGE_KEY_TITLE);

            // Do a silent check.
            // but for now, display the notification as well.
            showNotification(notificationTitle, pinCode);
            broadcastGcmPin(pinCode);
        }
    }

    private void broadcastGcmPin(final String code) {
        Intent intent = new Intent(ACTION_BROADCAST_PIN);
        intent.putExtra(MESSAGE_KEY_PIN, code);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * Show a notification indicating the user that a message was received.
     * If a notification with the same id has already been posted by your application and
     * has not yet been canceled, it will be replaced by the updated information.
     * @param notificationTitle The notification title.
     * @param payload           The notification message.
     */
    private void showNotification(final String notificationTitle, final String payload) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(notificationTitle)
                .setContentText(payload)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Build and issue the notification. All pending notifications with same id will be canceled.
        notificationManager.notify(0, notificationBuilder.build());
    }

}
