package com.example.kwoncheolhyeok.core.Util;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2018-02-21.
 */

public class FirebaseSendPushMsg {

    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAAosxzawM:APA91bHueqSnwuxBizof90IP8CtUPYo9WS8tjScyi0wNX0aoysNil8z-pinrUFtZOO5lFyk5BTY7qC0Uod1JfBipng5RfyvgkzpzI7VmMgt4XO8C7ST97agxHEWR2_Cg7TbPQxVnntiZ";


    public static void sendPostToFCM(final String type, final String targetUuid, final String currentUserNick, final String message,final String room) {

        DataContainer.getInstance().getUsersRef().child(targetUuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // FMC 메시지 생성 start
                            JSONObject root = new JSONObject();
                            //JSONObject notification = new JSONObject();
                            JSONObject data = new JSONObject();
                            //notification.put("body", message);
                            //notification.put("title", currentUserNick);
                            data.put("message", message);
                            data.put("type",type);
                            data.put("nick", currentUserNick);
                            data.put("room",room);
                            root.put("data", data);
                            //root.put("notification", notification);
                            root.put("to", user.getToken());
                            // FMC 메시지 생성 end

                            URL Url = new URL(FCM_MESSAGE_URL);
                            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setDoOutput(true);
                            conn.setDoInput(true);
                            conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                            conn.setRequestProperty("Accept", "application/json");
                            conn.setRequestProperty("Content-type", "application/json");
                            OutputStream os = conn.getOutputStream();
                            os.write(root.toString().getBytes("utf-8"));
                            os.flush();
                            conn.getResponseCode();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void sendPostToFCM(final String type, final String targetUuid, final String currentUserNick, final String message) {

        DataContainer.getInstance().getUsersRef().child(targetUuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // FMC 메시지 생성 start
                            JSONObject root = new JSONObject();
                            //JSONObject notification = new JSONObject();
                            JSONObject data = new JSONObject();
                            //notification.put("body", message);
                            //notification.put("title", currentUserNick);
                            data.put("message", message);
                            data.put("type",type);
                            data.put("nick", currentUserNick);
                            root.put("data", data);
                            //root.put("notification", notification);
                            root.put("to", user.getToken());
                            // FMC 메시지 생성 end

                            URL Url = new URL(FCM_MESSAGE_URL);
                            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setDoOutput(true);
                            conn.setDoInput(true);
                            conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                            conn.setRequestProperty("Accept", "application/json");
                            conn.setRequestProperty("Content-type", "application/json");
                            OutputStream os = conn.getOutputStream();
                            os.write(root.toString().getBytes("utf-8"));
                            os.flush();
                            conn.getResponseCode();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
