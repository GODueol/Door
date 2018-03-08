package com.example.kwoncheolhyeok.core.Util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.kwoncheolhyeok.core.Activity.MainActivity;
import com.example.kwoncheolhyeok.core.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by godueol on 2018. 2. 24..
 */

public class FirebaseRcevPushMsg extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private boolean isCheck;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.alarm), MODE_PRIVATE);
        editor = sharedPref.edit();

        isCheck = sharedPref.getBoolean(getString(R.string.alertUnlockPic), true);
        isCheck = sharedPref.getBoolean(getString(R.string.alertPost), true);
        isCheck = sharedPref.getBoolean(getString(R.string.alertLike), true);


        // Check if message contains a data payload.

        if (remoteMessage.getData().size() > 0) {

            switch (remoteMessage.getData().get("type")) {
                case "chat":
                    isCheck = sharedPref.getBoolean(getString(R.string.alertChat), true);
                    // 트랜젝션 문제 없는지?
                    int i = sharedPref.getInt("badgeChat", 0);
                    editor.putInt("badgeChat", ++i).apply();
                    if (isCheck) {
                        sendNotification(remoteMessage.getData().get("nick"), remoteMessage.getData().get("message"));
                    }
                    break;
                case "follow":
                    isCheck = sharedPref.getBoolean(getString(R.string.alertFolow), true);
                    if (isCheck) {
                        sendNotification(remoteMessage.getData().get("nick"), remoteMessage.getData().get("message"));
                    }
                    break;
                case "friend":
                    isCheck = sharedPref.getBoolean(getString(R.string.alertFriend), true);
                    if (isCheck) {
                        sendNotification(remoteMessage.getData().get("nick"), remoteMessage.getData().get("message"));
                    }
                    break;

            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "notification";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(NotificationID.getID() /* ID of notification */, notificationBuilder.build());
    }
}
