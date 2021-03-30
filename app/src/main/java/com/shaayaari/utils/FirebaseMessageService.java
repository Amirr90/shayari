package com.shaayaari.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.shaayaari.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

public class FirebaseMessageService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessageService";
    final String CHANNEL_ID = "com.example.playludo";
    private NotificationManager mManager;
    Bitmap bitmap;
    Context context;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceivedData: " + remoteMessage.getData());

        try {
            remoteMessage.getData();
            showNotification(remoteMessage.getData());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "onMessageReceived: error " + e.getLocalizedMessage());
        }

    }

    private void showNotification(Map<String, String> data) throws JSONException {

        JSONObject json = new JSONObject(data);
        try {
            String title = json.getString("title");
            String msg = json.getString("body");
            String msgId = json.getString("msgId");

            Bundle bundle = new Bundle();
            bundle.putString("id", msgId);


            //if body || title null or empty return
            if (null == msg && msg.isEmpty() && null == title && title.isEmpty())
                return;
            PendingIntent pendingIntent = new NavDeepLinkBuilder(App.context)
                    .setGraph(R.navigation.nav_host)
                    .setDestination(R.id.dataFragment)
                    .setArguments(bundle)
                    .createPendingIntent();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel androidChannel = new NotificationChannel(CHANNEL_ID,
                        title, NotificationManager.IMPORTANCE_HIGH);
                androidChannel.enableLights(true);
                androidChannel.enableVibration(true);
                androidChannel.setLightColor(Color.GREEN);

                androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                getManager().createNotificationChannel(androidChannel);

                NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

                if (bitmap != null) {
                    notification.setLargeIcon(bitmap);
                }

                //int timestamp = 1000;
                int timestamp = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

                getManager().notify(timestamp, notification.build());

                playNotificationSound();

            } else {
                try {

                    playNotificationSound();
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher_foreground)
                            .setContentTitle(title)
                            .setContentText(msg)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                            .setContentIntent(pendingIntent)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setLights(0xFF760193, 300, 1000)
                            .setVibrate(new long[]{200, 400});

                    if (bitmap != null) {
                        notificationBuilder.setLargeIcon(bitmap);
                    }
                    int timestamp = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(timestamp, notificationBuilder.build());
                } catch (SecurityException se) {
                    se.printStackTrace();
                    Log.d(TAG, "createNotification: " + se.getLocalizedMessage());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d(TAG, "onNewToken: " + s);
    }

    public void playNotificationSound() {
        try {
            Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(this, notificationSoundUri);


            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
}
