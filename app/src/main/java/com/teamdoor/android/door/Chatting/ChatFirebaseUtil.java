package com.teamdoor.android.door.Chatting;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamdoor.android.door.Entity.MessageVO;
import com.teamdoor.android.door.Entity.RoomVO;
import com.teamdoor.android.door.Exception.NotSetAutoTimeException;
import com.teamdoor.android.door.Util.FirebaseSendPushMsg;
import com.teamdoor.android.door.Util.UiUtil;

import java.util.HashMap;
import java.util.Map;

public class ChatFirebaseUtil {
    private final static String chatRoomList = "chatRoomList";
    private final static String chat = "chat";

    // static 메세지
    public static void sendEventMessage(final String mUuid, final String nickName, final String oUuid, final String message, final Context context) {
        final DatabaseReference chatRoomRef = FirebaseDatabase.getInstance().getReference(chatRoomList);
        final String[] RoomName = new String[1];
        chatRoomRef.child(mUuid).child(oUuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final RoomVO checkRoomVO = dataSnapshot.getValue(RoomVO.class);
                long currentTime = 0;
                try {
                    currentTime = UiUtil.getInstance().getCurrentTime(context);
                } catch (NotSetAutoTimeException e) {
                    e.printStackTrace();
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    ActivityCompat.finishAffinity((Activity) context);
                }

                if (dataSnapshot.exists()) {
                    RoomName[0] = checkRoomVO.getChatRoomid();
                } else {
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                    RoomName[0] = FirebaseDatabase.getInstance().getReference(chat).push().getKey();
                    Map<String, Object> childUpdates = new HashMap<>();
                    RoomVO roomVO = new RoomVO(RoomName[0], null, mUuid, currentTime);
                    childUpdates.put("/" + chatRoomList + "/" + oUuid + "/" + mUuid, roomVO);
                    roomVO = new RoomVO(RoomName[0], null, oUuid, currentTime);
                    childUpdates.put("/" + chatRoomList + "/" + mUuid + "/" + oUuid, roomVO);
                    databaseRef.updateChildren(childUpdates);
                    chatRoomRef.child(mUuid).child(oUuid).child("lastViewTime").setValue(currentTime);
                }

                sendMessage(RoomName[0], mUuid, nickName, oUuid, message);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // 메세지 보내기
    private static void sendMessage(final String room, final String mUuid, final String nickName, final String oUuid, final String message) {
        Long currentTime = System.currentTimeMillis();
        MessageVO messageVO = new MessageVO(null, mUuid, nickName, message, currentTime, 1);
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        String key = databaseRef.child(chat).child(room).push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();


        if (messageVO.getImage() == null) {        // 메세지일 경우
            childUpdates.put("/" + chat + "/" + room + "/" + key, messageVO);

            childUpdates.put("/" + chatRoomList + "/" + oUuid + "/" + mUuid + "/" + "lastChatTime", currentTime);
            childUpdates.put("/" + chatRoomList + "/" + oUuid + "/" + mUuid + "/" + "lastChat", messageVO.getContent());

            childUpdates.put("/" + chatRoomList + "/" + mUuid + "/" + oUuid + "/" + "lastViewTime", currentTime);
            childUpdates.put("/" + chatRoomList + "/" + mUuid + "/" + oUuid + "/" + "lastChat", messageVO.getContent());
        } else if (messageVO.getContent() == null) {          // 이미지일 경우

            childUpdates.put("/" + chat + "/" + room + "/" + key, messageVO);

            childUpdates.put("/" + chatRoomList + "/" + oUuid + "/" + mUuid + "/" + "lastChatTime", currentTime);
            childUpdates.put("/" + chatRoomList + "/" + oUuid + "/" + mUuid + "/" + "lastChat", "사진");
            childUpdates.put("/" + chatRoomList + "/" + mUuid + "/" + oUuid + "/" + "lastViewTime", currentTime);
            childUpdates.put("/" + chatRoomList + "/" + mUuid + "/" + oUuid + "/" + "lastChat", "사진");
        }
        databaseRef.updateChildren(childUpdates).addOnSuccessListener(aVoid ->
                FirebaseSendPushMsg.sendPostToFCM("chat", oUuid, nickName, "비공개 사진을 열었습니다!", room));
    }

}

