package com.nexmo.sdk.core.push;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nexmo.sdk.BuildConfig;
import com.nexmo.sdk.R;

import java.util.Map;

/**
 * Firebase Push support for receiving PIN code.
 * A message can also contains both notification and data payload.
 * When these kind of messages are sent, it will be handled in two scenarios depending
 * upon app state (background / foreground).
 * For these message we can use both notification and data keys.
 * When in the background
 * <ul>
 *     <li>- Apps receive the notification payload in the notification tray,
 *     and only handle the data payload when the user taps on the notification.
 *     </li>
 * </ul>
 * When in the foreground
 * <ul>
 *     <li>- App receives a message object with both payloads available.
 *     </li>
 * </ul><
 *
 * Created by emma tresanszki on 15/11/2016.
 */
public class NexmoFirebaseMessagingService  extends FirebaseMessagingService {
    private static final String TAG = NexmoFirebaseMessagingService.class.getSimpleName();
    public static final String MESSAGE_KEY_PIN = "pin";
    public static final String MESSAGE_KEY_TITLE = "title";
    public static final String NOTIFICATION_DEFAULT_TITLE = "Nexmo Verify";
    public static final String ACTION_BROADCAST_PIN = "com.nexmo.sdk.push.BROADCAST_PIN";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        Log.d(TAG, "onMessageReceived");
        String from = message.getFrom();
        Map data = message.getData();

        String notificationTitle = NOTIFICATION_DEFAULT_TITLE;
        String notificationBody = null, pinCode = null;
        String notificationIcon = null;
        RemoteMessage.Notification notification = message.getNotification();
        if (notification != null) {
            notificationTitle = notification.getTitle();
            notificationBody = notification.getBody();
            notificationIcon = notification.getIcon();
            Log.d(TAG, "onMessageReceived notification:title " + notificationTitle);
            Log.d(TAG, "onMessageReceived notification:body " + notificationBody);
            Log.d(TAG, "onMessageReceived notification:icon " + notificationIcon);
        }

        // Identify the app server that is permitted to send messages to this client.
        if (data.containsKey(MESSAGE_KEY_PIN)) {
            pinCode = (String) data.get(MESSAGE_KEY_PIN);
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onMessageReceived From: " + from);
                Log.d(TAG, "onMessageReceived Pin: " + pinCode);
            }
            if (data.containsKey(MESSAGE_KEY_TITLE))
                notificationTitle = (String) data.get(MESSAGE_KEY_TITLE);

            if (!TextUtils.isEmpty(notificationTitle) && !TextUtils.isEmpty(notificationBody))
                showNotification(notificationTitle, notificationBody, notificationIcon);
            else
                // Do a silent check.
                // but for now, display the notification as well.
                broadcastPushPINCode(pinCode);
        }
    }

    private void broadcastPushPINCode(final String code) {
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
     * @param icon              The notification drawable icon identifier. Common would be R.drawable.ic_launcher
     */
    private void showNotification(final String notificationTitle, final String payload, final String icon) {
        int notificationIcon;

        if (!TextUtils.isEmpty(icon)) {
            notificationIcon =  getResourceIdForName(icon);
            if (notificationIcon == 0)
                notificationIcon = getApplicationIconId();
        }
        else
            notificationIcon = getApplicationIconId();

        Log.d(TAG, "notification icon used: " + notificationIcon);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(notificationTitle)
                .setContentText(payload)
                .setSmallIcon(notificationIcon)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Build and issue the notification. All pending notifications with same id will be canceled.
        notificationManager.notify(0, notificationBuilder.build());
    }

    private int getApplicationIconId() {
        PackageManager packageManager = getApplicationContext().getPackageManager();
        int notificationIcon  = 0;
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getApplication().getPackageName(), 0);
            notificationIcon = packageInfo.applicationInfo.icon;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "cannot retrieve application icon id for notification icon");
            e.printStackTrace();
        }

        return notificationIcon;
    }

    private int getResourceIdForName(String resName) {
        String defType = resName.subSequence(resName.indexOf(".") + 1, resName.lastIndexOf(".")).toString();
        String iconName = resName.substring(resName.lastIndexOf(".") + 1);
        Log.d(TAG, "Resource id: " + resName + " .notification icon type: " + defType + ".name: " + iconName);
        Log.d(TAG, "Resource id for sample should be " + "2130837576");

        return getApplicationContext().getResources().getIdentifier(
                iconName,
                defType,
                getApplicationContext().getPackageName());
    }
}
