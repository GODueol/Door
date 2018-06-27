package com.teamcore.android.core.Util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
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

    private static final int DEFUALT = 1000;
    private static final int CHATING_FALG = 2000;
    private static final int FRIENDS_FALG = 3000;
    private static final int COREPOST_FALG = 4000;

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

        boolean isCheck = SPUtil.getSwitchState(getString(R.string.alertUnlockPic));
        isCheck = SPUtil.getSwitchState(getString(R.string.alertPost));
        isCheck = SPUtil.getSwitchState(getString(R.string.alertLike));


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

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title, String messageBody, int flag) {
        // 기본 엑티비티 메인으로
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
        pendingIntent = stackBuilder.getPendingIntent(0, FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "Core channel")
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);


        boolean isCheck = SPUtil.getSwitchState(getString(R.string.set_vibrate));
        switch (flag) {
            case CHATING_FALG:
                // 1000L == 1초 , 100L == 0.1초
                // 대기 , 진동, 대기, 진동
                // new long[]{대기,진동,대기,진동} 숫자뒤에 L은 꼭써줘야되요
                // 여긴 채팅
                notificationBuilder.setVibrate(new long[]{0L,100L,100L,200L});
                break;
            case FRIENDS_FALG:
                // 여긴 프렌즈
                notificationBuilder.setVibrate(new long[]{0L,200L,100L,100L});
                break;
            case COREPOST_FALG:
                // 여긴 코어
                notificationBuilder.setVibrate(new long[]{0L,200L,100L,200L});
                break;
        }

        // 진동 제거
        if (!isCheck) {
            notificationBuilder.setVibrate(new long[]{0L});
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        switch (flag) {
            case CHATING_FALG:
                notificationManager.notify(CHATING_FALG/* ID of notification */, notificationBuilder.build());
                break;
            case FRIENDS_FALG:
                notificationManager.notify(FRIENDS_FALG/* ID of notification */, notificationBuilder.build());
                break;
            case COREPOST_FALG:
                notificationManager.notify(COREPOST_FALG/* ID of notification */, notificationBuilder.build());
                break;
            default :
                notificationManager.notify(DEFUALT/* ID of notification */, notificationBuilder.build());
                break;
        }
    }

}
