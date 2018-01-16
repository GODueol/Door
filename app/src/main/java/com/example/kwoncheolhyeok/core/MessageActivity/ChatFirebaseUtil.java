package com.example.kwoncheolhyeok.core.MessageActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.MessageActivity.util.CryptoImeageName;
import com.example.kwoncheolhyeok.core.MessageActivity.util.MessageVO;
import com.example.kwoncheolhyeok.core.MessageActivity.util.RoomVO;
import com.example.kwoncheolhyeok.core.PeopleFragment.ImageAdapter;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.example.kwoncheolhyeok.core.Util.GPSInfo;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018-01-08.
 */

public class ChatFirebaseUtil {


    final static String chatRoomList = "chatRoomList";
    final static String chat = "chat";
    final static String image = "image";
    public final static int MessageCount = 30;

    private Context context;
    private String userUuid, targetUuid;
    private User currentUser, targetUser;
    private String userPickuri, targetPicuri;
    private String roomName;
    private String childChatKey;
    private DatabaseReference databaseRef, chatDatabaseRef;
    private FirebaseStorage storage;

    ///////////////////////
    private int messageWeight = 1;
    private boolean destroy = false;
    private ImageAdapter.Item item;
    private ChattingMessageAdapter chattingMessageAdapter;
    private RecyclerView chattingRecyclerview;
    private List<String> childKeyList;
    private List<ChatMessage> chatMessageList;
    private List<ChatMessage> uncheckMessageList;

    public ChatFirebaseUtil(Context context, User currentUser, User targetUser, String userUuid, String targetUuid) {
        databaseRef = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        childKeyList = new ArrayList<String>();
        uncheckMessageList = new ArrayList<ChatMessage>();
        this.context = context;
        this.currentUser = currentUser;
        this.targetUser = targetUser;
        this.userUuid = userUuid;
        this.targetUuid = targetUuid;
        userPickuri = currentUser.getPicUrls().getPicUrl1();
        targetPicuri = targetUser.getPicUrls().getPicUrl1();
    }

    public void setLastChatView() {
        if (!destroy) {
            Long currentTime = getTime();
            databaseRef.child(chatRoomList).child(userUuid).child(targetUuid).child("lastViewTime").setValue(currentTime);
        }
    }

    public void sendMessage(MessageVO message) {

        String room = roomName;
        String key = databaseRef.child(chat).child(room).push().getKey();

        Long currentTime = getTime();
        Map<String, Object> childUpdates = new HashMap<>();

        if (message.getImage() == null) {
            childUpdates.put("/" + chat + "/" + room + "/" + key, message);

            childUpdates.put("/" + chatRoomList + "/" + targetUuid + "/" + userUuid + "/" + "lastChatTime", currentTime);
            childUpdates.put("/" + chatRoomList + "/" + targetUuid + "/" + userUuid + "/" + "lastChat", message.getContent());

            childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid + "/" + "lastViewTime", currentTime);
            childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid + "/" + "lastChat", message.getContent());
        } else if (message.getContent() == null) {
            childUpdates.put("/" + chat + "/" + room + "/" + key, message);

            childUpdates.put("/" + chatRoomList + "/" + targetUuid + "/" + userUuid + "/" + "lastChatTime", currentTime);
            childUpdates.put("/" + chatRoomList + "/" + targetUuid + "/" + userUuid + "/" + "lastChat", "사진");
            childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid + "/" + "lastViewTime", currentTime);
            childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid + "/" + "lastChat", "사진");
        }
        databaseRef.updateChildren(childUpdates);
    }

    public void sendImageMessage(Uri outputFileUri) {

        StorageReference imagesRef = storage.getReference().child(chat);
        long currentTime = System.currentTimeMillis();
        final String imageName = CryptoImeageName.md5(Long.toString(currentTime));
        final StorageReference imageMessageRef = imagesRef.child(image + "/" + roomName + "/" + userUuid + "/" + imageName);
        UploadTask uploadTask = imageMessageRef.putFile(outputFileUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("chatError", e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUri = taskSnapshot.getDownloadUrl();
                imageMessageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        long currentTime = getTime();
                        MessageVO message = new MessageVO(uri.toString(), userUuid, currentUser.getId(), null, currentTime, 1);
                        sendMessage(message);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

            }
        });
    }

    public void setchatRoom(RecyclerView chattingRecyclerview, List<ChatMessage> chatMessageList) {
        final DatabaseReference chatRoomRef = FirebaseDatabase.getInstance().getReference(chatRoomList);
        this.chattingRecyclerview = chattingRecyclerview;
        chattingMessageAdapter = (ChattingMessageAdapter) chattingRecyclerview.getAdapter();
        this.chatMessageList = chatMessageList;
        // 채팅방 세팅
        chatRoomRef.child(userUuid).child(targetUuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final RoomVO checkRoomVO = dataSnapshot.getValue(RoomVO.class);
                long currentTime = System.currentTimeMillis();
                try {
                    roomName = checkRoomVO.getChatRoomid();
                    chatRoomRef.child(userUuid).child(targetUuid).child("lastViewTime").setValue(currentTime);
                } catch (Exception e) {
                    // 채팅방 이름 설정
                    roomName = FirebaseDatabase.getInstance().getReference(chat).push().getKey();

                    Map<String, Object> childUpdates = new HashMap<>();
                    RoomVO roomVO = new RoomVO(roomName, null, userUuid, currentTime, userPickuri);
                    childUpdates.put("/" + chatRoomList + "/" + targetUuid + "/" + userUuid, roomVO);
                    roomVO = new RoomVO(roomName, null, targetUuid, currentTime, targetPicuri);
                    childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid, roomVO);
                    databaseRef.updateChildren(childUpdates);
                    chatRoomRef.child(userUuid).child(targetUuid).child("lastViewTime").setValue(currentTime);
                }
                item = new ImageAdapter.Item(0, targetUuid, targetUser, targetPicuri);
                // 상대방과의 거리 셋팅
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(FireBaseUtil.currentLocationPath);
                GeoFire geoFire = new GeoFire(ref);
                final Location location = GPSInfo.getmInstance(context).getGPSLocation();
                geoFire.getLocation(targetUuid, new LocationCallback() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onLocationResult(String s, GeoLocation geoLocation) {
                        Location targetLocation = new Location("");//provider name is unnecessary
                        targetLocation.setLatitude(geoLocation.latitude);//your coords of course
                        targetLocation.setLongitude(geoLocation.longitude);
                        final float distance = location.distanceTo(targetLocation);
                        item.setDistance(distance);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                chatDatabaseRef = FirebaseDatabase.getInstance().getReference().child(chat).child(roomName);
                // 처음 모든 메세지 읽음처리
                chatDatabaseRef.orderByChild("check").equalTo(1).addListenerForSingleValueEvent(checkChatListener);
                chatDatabaseRef.removeEventListener(checkChatListener);
                // 그후 메세지 통신
                chatDatabaseRef.orderByKey().limitToLast(MessageCount).addChildEventListener(chatInitListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // 첫 채팅방 초기화
    ChildEventListener chatInitListener = new ChildEventListener() {
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            childChatKey = dataSnapshot.getKey();
            childKeyList.add(childChatKey);

            MessageVO message = dataSnapshot.getValue(MessageVO.class);
            initMessage(message, childChatKey);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            checkRefreshChatLog();
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            destroy = true;
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    // 처음 메세지 읽음 처리
    ValueEventListener checkChatListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
            while (child.hasNext()) {//마찬가지로 중복 유무 확인
                DataSnapshot ds = child.next();
                MessageVO message = ds.getValue(MessageVO.class);
                if (message != null && !message.getWriter().equals(userUuid)) {
                    databaseRef.child(chat).child(roomName).child(ds.getKey()).child("check").setValue(0);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };
    // 채팅로그 로딩
    ValueEventListener chatLoadListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
            loadMessage(child);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };

    public Long getTime() {
        return System.currentTimeMillis();
    }

    public void deleteFirebaseRef() {
        chatDatabaseRef.removeEventListener(chatInitListener);
        chatDatabaseRef.removeEventListener(chatLoadListener);
    }

    public void initMessage(MessageVO message, String key) {

        ChatMessage chatMessage;
        int check = message.getCheck();

        if (check != 0 && !message.getWriter().equals(userUuid)) {
            message.setCheck(0);
            databaseRef.child(chat).child(roomName).child(key).child("check").setValue(check - 1);
        }

        if (message.getWriter().equals(userUuid) && message.getImage() == null) {
            chatMessage = new ChatMessage(message, true, false);
            if (chatMessage.getCheck() == 1) {
                uncheckMessageList.add(chatMessage);
            }
        } else if (!message.getWriter().equals(userUuid) && message.getImage() == null) {
            chatMessage = new ChatMessage(message, false, false, item);
        } else if (message.getWriter().equals(userUuid) && message.getContent() == null) {
            chattingMessageAdapter.setRequestListener();
            chatMessage = new ChatMessage(message, true, true);
            if (chatMessage.getCheck() == 1) {
                uncheckMessageList.add(chatMessage);
            }
        } else {
            chattingMessageAdapter.setRequestListener();
            chatMessage = new ChatMessage(message, false, true, item);
        }
        chatMessageList.add(chatMessage);
        chattingRecyclerview.getRecycledViewPool().clear();
        chattingMessageAdapter.notifyDataSetChanged();
        chattingRecyclerview.smoothScrollToPosition(chattingMessageAdapter.getItemCount());
    }

    public void loadMessage(Iterator<DataSnapshot> child) {

        ChatMessage chatMessage;

        while (child.hasNext()) {//마찬가지로 중복 유무 확인
            DataSnapshot ds = child.next();
            childChatKey = ds.getKey();
            childKeyList.add(childChatKey);
            MessageVO message = ds.getValue(MessageVO.class);

            if (message.getWriter().equals(userUuid) && message.getImage() == null) {
                chatMessage = new ChatMessage(message, true, false);
            } else if (!message.getWriter().equals(userUuid) && message.getImage() == null) {
                chatMessage = new ChatMessage(message, false, false, item);
            } else if (message.getWriter().equals(userUuid) && message.getContent() == null) {
                chatMessage = new ChatMessage(message, true, true);
            } else {
                chatMessage = new ChatMessage(message, false, true, item);
            }

            int size = childKeyList.size() - 1;
            chatMessageList.add(size, chatMessage);
        }

        chattingRecyclerview.getRecycledViewPool().clear();
        chattingMessageAdapter.notifyDataSetChanged();
        //로딩 후 제거
        chatDatabaseRef.removeEventListener(chatLoadListener);
    }

    public void addChatLog() {
        if(!childKeyList.isEmpty()) {
            // 마지막으로 불러온 채팅 아이디
            String lastChildChatKey = childKeyList.get(0);
            childKeyList.clear();
            // 맨 위 아이템 제거(중복발생)
            chatMessageList.remove(0);
            messageWeight *= 1;
            chatDatabaseRef.orderByKey().endAt(lastChildChatKey).limitToLast(MessageCount * messageWeight).addValueEventListener(chatLoadListener);
        }
    }

    public void checkRefreshChatLog() {
        for (ChatMessage chatMessage : uncheckMessageList) {
            chatMessage.setCheck(0);
        }
        uncheckMessageList.clear();
        chattingRecyclerview.getRecycledViewPool().clear();
        chattingMessageAdapter.notifyDataSetChanged();
    }


    public ImageAdapter.Item getItem() {
        return item;
    }
}

