package com.teamcore.android.core.Util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.teamcore.android.core.Activity.MainActivity;
import com.teamcore.android.core.FriendsActivity.FriendsActivity;
import com.teamcore.android.core.MessageActivity.MessageActivity;
import com.teamcore.android.core.R;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Created by godueol on 2018. 2. 24..
 */

public class FirebaseRcevPushMsg extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";


    private static final int CHATING_FALG = 0;
    private static final int FRIENDS_FALG = 1;
    private static final int COREPOST_FALG = 2;
    private static final int DEFUALT = 3;
    private static final long Vibration[][] = new long[][]{{0L, 100L, 100L, 200L}, // chat vibration
                                                            {0L, 200L, 100L, 100L}, // friends vibration
                                                            {0L, 200L, 100L, 200L}}; // core vibration
    private SharedPreferencesUtil SPUtil;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        SPUtil = new SharedPreferencesUtil(getApplicationContext());
        boolean isCheck;


        // Check if message contains a data payload.

        if (remoteMessage.getData().size() > 0) {

            switch (remoteMessage.getData().get("type")) {
                case "chat":
                    String cRoom = SPUtil.getCurrentChat();
                    String room = remoteMessage.getData().get("room");
                    if (!cRoom.equals(remoteMessage.getData().get("room"))) {
                        isCheck = SPUtil.getSwitchState(getString(R.string.alertChat));
                        SPUtil.increaseChatRoomBadge(room);

                        if (isCheck) {
                            sendNotification(remoteMessage.getData().get("nick"), remoteMessage.getData().get("message"), CHATING_FALG);
                        }
                    }
                    break;
                case "follow":
                    isCheck = SPUtil.getSwitchState(getString(R.string.alertFolow));
                    SPUtil.increaseBadgeCount(getString(R.string.badgeFollow));

                    if (isCheck) {
                        sendNotification(remoteMessage.getData().get("nick"), remoteMessage.getData().get("message"), FRIENDS_FALG);
                    }
                    break;
                case "friend":
                    isCheck = SPUtil.getSwitchState(getString(R.string.alertFriend));
                    SPUtil.increaseBadgeCount(getString(R.string.badgeFriend));

                    if (isCheck) {
                        sendNotification(remoteMessage.getData().get("nick"), remoteMessage.getData().get("message"), FRIENDS_FALG);
                    }
                    break;
                case "Like":
                    isCheck = SPUtil.getSwitchState(getString(R.string.alertLike));
                    SPUtil.setMainIcon(getString(R.string.mainAlarm), true);
                    SPUtil.setAlarmIcon(getString(R.string.navAlarm), true);
                    if (isCheck) {
                        sendNotification(remoteMessage.getData().get("nick"), remoteMessage.getData().get("message"), COREPOST_FALG);
                    }
                    break;
                case "Post":
                    isCheck = SPUtil.getSwitchState(getString(R.string.alertPost));
                    SPUtil.increaseBadgeCount(getString(R.string.badgePost));
                    SPUtil.setAlarmIcon(getString(R.string.navAlarm), true);
                    if (isCheck) {
                        sendNotification(remoteMessage.getData().get("nick"), remoteMessage.getData().get("message"), COREPOST_FALG);
                    }
                    break;
                case "Answer":
                    isCheck = SPUtil.getSwitchState(getString(R.string.alertAnswer));
                    SPUtil.setMainIcon(getString(R.string.mainAlarm), true);
                    SPUtil.setAlarmIcon(getString(R.string.navAlarm), true);
                    if (isCheck) {
                        sendNotification(remoteMessage.getData().get("nick"), remoteMessage.getData().get("message"), COREPOST_FALG);
                    }
                    break;
                case "View":
                    SPUtil.switchBadgeState(getString(R.string.badgeView), true);
                    break;

            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }


    public PendingIntent setPendingIntent(int flag) {
        Intent intent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent;
        // 플레그에 따른 뷰 스택 조절
        switch (flag) {
            case CHATING_FALG:
                Intent ChattingView = new Intent(this, MessageActivity.class);
                stackBuilder.addNextIntent(ChattingView);
                break;
            case FRIENDS_FALG:
                Intent FriendsView = new Intent(this, FriendsActivity.class);
                stackBuilder.addNextIntent(FriendsView);
                break;
        }

        return stackBuilder.getPendingIntent(0, FLAG_UPDATE_CURRENT);
    }

    public void showNotification(int flag, NotificationCompat.Builder notificationBuilder, NotificationManager notificationManager) {

        switch (flag) {
            case CHATING_FALG:
                notificationManager.notify(CHATING_FALG, notificationBuilder.build());
                break;
            case FRIENDS_FALG:
                notificationManager.notify(FRIENDS_FALG, notificationBuilder.build());
                break;
            case COREPOST_FALG:
                notificationManager.notify(COREPOST_FALG, notificationBuilder.build());
                break;
            default:
                notificationManager.notify(DEFUALT, notificationBuilder.build());
                break;
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody, int flag) {
        // 기본 엑티비티 메인으로

        PendingIntent pendingIntent = setPendingIntent(flag);
        String channelId = "Core channel";
        String chatChannelId = "Chat channel";
        String friendsChannelId = "Friends channel";
        String CoreChannelId = "Core channel";
        String channelId_none = "none alert channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        boolean isCheck = SPUtil.getSwitchState(getString(R.string.set_vibrate));
        NotificationCompat.Builder notificationBuilder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Since android Oreo notification channel is needed.
            NotificationChannel channel;
            if (!isCheck) {
                // 채크되있으면 노알림
                channel = notificationManager.getNotificationChannel(channelId_none);
                if (channel == null) {
                    channel = new NotificationChannel(channelId_none, channelId_none, NotificationManager.IMPORTANCE_LOW);
                }
                notificationBuilder =
                        new NotificationCompat.Builder(this, channelId_none)
                                .setSmallIcon(R.drawable.icon)
                                .setContentTitle(title)
                                .setContentText(messageBody)
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setContentIntent(pendingIntent);

            } else {
                switch (flag) {
                    case CHATING_FALG:
                        // 여긴 채팅
                        // 체크 안되있을경우
                        channel = notificationManager.getNotificationChannel(chatChannelId);
                        if (channel == null) {
                            channel = new NotificationChannel(chatChannelId, chatChannelId, NotificationManager.IMPORTANCE_DEFAULT);
                            channel.setVibrationPattern(Vibration[CHATING_FALG]);
                        }
                        notificationBuilder =
                                new NotificationCompat.Builder(this, chatChannelId)
                                        .setSmallIcon(R.drawable.icon)
                                        .setContentTitle(title)
                                        .setContentText(messageBody)
                                        .setAutoCancel(true)
                                        .setSound(defaultSoundUri)
                                        .setContentIntent(pendingIntent);
                        break;
                    case FRIENDS_FALG:
                        // 여긴 프렌즈
                        // 체크 안되있을경우
                        channel = notificationManager.getNotificationChannel(friendsChannelId);
                        if (channel == null) {
                            channel = new NotificationChannel(friendsChannelId, friendsChannelId, NotificationManager.IMPORTANCE_DEFAULT);
                            channel.setVibrationPattern(Vibration[FRIENDS_FALG]);
                        }
                        notificationBuilder =
                                new NotificationCompat.Builder(this, friendsChannelId)
                                        .setSmallIcon(R.drawable.icon)
                                        .setContentTitle(title)
                                        .setContentText(messageBody)
                                        .setAutoCancel(true)
                                        .setSound(defaultSoundUri)
                                        .setContentIntent(pendingIntent);
                        break;
                    case COREPOST_FALG:
                        // 여긴 코어
                        // 체크 안되있을경우
                        channel = notificationManager.getNotificationChannel(CoreChannelId);
                        if (channel == null) {
                            channel = new NotificationChannel(CoreChannelId, CoreChannelId, NotificationManager.IMPORTANCE_DEFAULT);
                            channel.setVibrationPattern(Vibration[COREPOST_FALG]);
                        }
                        notificationBuilder =
                                new NotificationCompat.Builder(this, CoreChannelId)
                                        .setSmallIcon(R.drawable.icon)
                                        .setContentTitle(title)
                                        .setContentText(messageBody)
                                        .setAutoCancel(true)
                                        .setSound(defaultSoundUri)
                                        .setContentIntent(pendingIntent);
                        break;
                    default:                // 체크 안되있을경우
                        channel = notificationManager.getNotificationChannel(channelId);
                        if (channel == null) {
                            channel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT);
                        }
                        notificationBuilder =
                                new NotificationCompat.Builder(this, channelId)
                                        .setSmallIcon(R.drawable.icon)
                                        .setContentTitle(title)
                                        .setContentText(messageBody)
                                        .setAutoCancel(true)
                                        .setSound(defaultSoundUri)
                                        .setContentIntent(pendingIntent);
                        break;
                }

            }
            notificationManager.createNotificationChannel(channel);
            showNotification(flag, notificationBuilder, notificationManager);

        } else {
            // 오레오 미만 버전
            notificationBuilder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            switch (flag) {
                case CHATING_FALG:
                    // 여긴 채팅
                    notificationBuilder.setVibrate(Vibration[CHATING_FALG]);
                    break;
                case FRIENDS_FALG:
                    // 여긴 프렌즈
                    notificationBuilder.setVibrate(Vibration[FRIENDS_FALG]);
                    break;
                case COREPOST_FALG:
                    // 여긴 코어
                    notificationBuilder.setVibrate(Vibration[COREPOST_FALG]);
                    break;
            }

            // 진동 제거
            if (!isCheck) {
                notificationBuilder.setSound(null);
                notificationBuilder.setVibrate(new long[]{0L});
            }

            showNotification(flag, notificationBuilder, notificationManager);
        }
    }

}
