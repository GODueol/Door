package com.example.kwoncheolhyeok.core.MessageActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.MessageActivity.util.CryptoImeageName;
import com.example.kwoncheolhyeok.core.MessageActivity.util.MessageVO;
import com.example.kwoncheolhyeok.core.MessageActivity.util.RoomVO;
import com.example.kwoncheolhyeok.core.PeopleFragment.GridItem;
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
    private String lastMessage;
    private DatabaseReference databaseRef, chatDatabaseRef;
    private FirebaseStorage storage;

    ///////////////////////
    private int messageWeight = 1;
    private GridItem item;
    private ChattingMessageAdapter chattingMessageAdapter;
    private RecyclerView chattingRecyclerview;
    private TextView hideText;
    LinearLayout overlay;
    private List<String> childKeyList;
    private List<String> chatMessageKeyList;
    private List<ChatMessage> chatMessageList;
    private List<ChatMessage> uncheckMessageList;

    public ChatFirebaseUtil(Context context, User currentUser, User targetUser, String userUuid, String targetUuid, LinearLayout overlay, TextView hideText) {
        databaseRef = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        childKeyList = new ArrayList<String>();
        chatMessageKeyList = new ArrayList<String>();
        uncheckMessageList = new ArrayList<ChatMessage>();
        this.context = context;
        this.currentUser = currentUser;
        this.targetUser = targetUser;
        this.userUuid = userUuid;
        this.targetUuid = targetUuid;
        this.hideText = hideText;
        this.overlay = overlay;
        userPickuri = currentUser.getPicUrls().getThumbNail_picUrl1();
        targetPicuri = targetUser.getPicUrls().getThumbNail_picUrl1();
    }

    public void setLastChatView() {
        final DatabaseReference chatRoomRef = FirebaseDatabase.getInstance().getReference(chatRoomList);
        chatRoomRef.child(userUuid).child(targetUuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Long currentTime = getTime();
                    databaseRef.child(chatRoomList).child(userUuid).child(targetUuid).child("lastViewTime").setValue(currentTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
        chattingRecyclerview.addOnScrollListener(detectTopPosition);
        // 채팅방 세팅
        chatRoomRef.child(userUuid).child(targetUuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final RoomVO checkRoomVO = dataSnapshot.getValue(RoomVO.class);
                long currentTime = System.currentTimeMillis();

                if (dataSnapshot.exists()) {
                    roomName = checkRoomVO.getChatRoomid();
                    chatRoomRef.child(userUuid).child(targetUuid).child("lastViewTime").setValue(currentTime);
                } else {
                    roomName = FirebaseDatabase.getInstance().getReference(chat).push().getKey();
                    Map<String, Object> childUpdates = new HashMap<>();
                    RoomVO roomVO = new RoomVO(roomName, null, userUuid, currentTime, userPickuri);
                    childUpdates.put("/" + chatRoomList + "/" + targetUuid + "/" + userUuid, roomVO);
                    roomVO = new RoomVO(roomName, null, targetUuid, currentTime, targetPicuri);
                    childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid, roomVO);
                    databaseRef.updateChildren(childUpdates);
                    chatRoomRef.child(userUuid).child(targetUuid).child("lastViewTime").setValue(currentTime);
                }

                item = new GridItem(0, targetUuid, targetUser.getSummaryUser(), targetPicuri);
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
                chatDatabaseRef.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                        while (child.hasNext()) {//마찬가지로 중복 유무 확인
                            DataSnapshot ds = child.next();
                            lastMessage = ds.getKey();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                chatDatabaseRef.limitToLast(MessageCount).addChildEventListener(chatInitListener);

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
            if (!childKeyList.contains(childChatKey)) {
                childKeyList.add(childChatKey);
                MessageVO message = dataSnapshot.getValue(MessageVO.class);
                message.setParent(childChatKey);
                initMessage(message, childChatKey);
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            checkRefreshChatLog();
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            MessageVO messageVO = dataSnapshot.getValue(MessageVO.class);
            if (!messageVO.getWriter().equals(userUuid)) {
                try {
                    String key = dataSnapshot.getKey();
                    int position = chatMessageKeyList.indexOf(key);
                    chatMessageList.remove(position);
                    chatMessageKeyList.remove(position);
                    chattingRecyclerview.getRecycledViewPool().clear();
                    chattingMessageAdapter.notifyDataSetChanged();
                } catch (Exception e) {

                }
            }
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
                message.setParent(dataSnapshot.getKey());
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

    // 채팅 초기화
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
            chatMessage = new ChatMessage(message, true, true);
            if (chatMessage.getCheck() == 1) {
                uncheckMessageList.add(chatMessage);
            }
        } else {
            chatMessage = new ChatMessage(message, false, true, item);

        }
        chatMessageList.add(chatMessage);
        chatMessageKeyList.add(key);
        chattingRecyclerview.getRecycledViewPool().clear();
        chattingMessageAdapter.notifyDataSetChanged();


        int pos = chattingMessageAdapter.getItemCount() - 1;
        int visiblieCompLastPosition = ((LinearLayoutManager) chattingRecyclerview.getLayoutManager()).findLastVisibleItemPosition();
        if (pos == visiblieCompLastPosition || key.equals(lastMessage)) {
            chattingRecyclerview.scrollToPosition(chattingMessageAdapter.getItemCount() - 1);
        } else if(!chatMessage.isMine()&&!chatMessage.isImage()){
            hideText.setText(message.getContent());
            overlay.setVisibility(View.VISIBLE);
        }else if(!chatMessage.isMine()){
            hideText.setText("사진");
            overlay.setVisibility(View.VISIBLE);
        } else{
            chattingRecyclerview.scrollToPosition(chattingMessageAdapter.getItemCount() - 1);
        }
    }

    // 채팅 로딩
    public void loadMessage(Iterator<DataSnapshot> child) {

        ChatMessage chatMessage;
        int visilbeCompFirstPosition = ((LinearLayoutManager) chattingRecyclerview.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        int visiblieCompLastPosition = ((LinearLayoutManager) chattingRecyclerview.getLayoutManager()).findLastCompletelyVisibleItemPosition();
        while (child.hasNext()) {//마찬가지로 중복 유무 확인
            DataSnapshot ds = child.next();
            childChatKey = ds.getKey();
            childKeyList.add(childChatKey);
            MessageVO message = ds.getValue(MessageVO.class);
            message.setParent(childChatKey);
            if (message.getWriter().equals(userUuid) && message.getImage() == null) {
                chatMessage = new ChatMessage(message, true, false);
                if (chatMessage.getCheck() == 1) {
                    uncheckMessageList.add(chatMessage);
                }
            } else if (!message.getWriter().equals(userUuid) && message.getImage() == null) {
                chatMessage = new ChatMessage(message, false, false, item);
            } else if (message.getWriter().equals(userUuid) && message.getContent() == null) {
                chatMessage = new ChatMessage(message, true, true);
                if (chatMessage.getCheck() == 1) {
                    uncheckMessageList.add(chatMessage);
                }
            } else {
                chatMessage = new ChatMessage(message, false, true, item);
            }

            int size = childKeyList.size() - 1;
            chatMessageKeyList.add(size, childChatKey);
            chatMessageList.add(size, chatMessage);
        }


        //로딩 후 제거
        if (detectTopPosition != null) {
            chattingRecyclerview.getRecycledViewPool().clear();
            chattingMessageAdapter.notifyDataSetChanged();
            int messageViewCount = childKeyList.size() + (visiblieCompLastPosition - visilbeCompFirstPosition) - 2;
            chattingRecyclerview.scrollToPosition(messageViewCount);
            chattingRecyclerview.addOnScrollListener(detectTopPosition);
        }
        chatDatabaseRef.removeEventListener(chatLoadListener);
    }

    // 채팅 로딩 발생 메소드
    public void addChatLog() {
        if (!childKeyList.isEmpty() && childKeyList.size() != 1) {
            chattingRecyclerview.removeOnScrollListener(detectTopPosition);
            if (childKeyList.size() < MessageCount) {
                detectTopPosition = null;
            }
            // 마지막으로 불러온 채팅 아이디
            String lastChildChatKey = childKeyList.get(0);
            childKeyList.clear();
            // 맨 위 아이템 제거(중복발생)
            try {
                chatMessageList.remove(0);
            } catch (Exception e) {
                Intent p = new Intent(context.getApplicationContext(), MessageActivity.class);
                p.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.getApplicationContext().startActivity(p);
            }
            messageWeight *= 1;
            chatDatabaseRef.orderByKey().endAt(lastChildChatKey).limitToLast(MessageCount * messageWeight).addValueEventListener(chatLoadListener);
        }
    }

    // 채팅 로딩 리스너 (상단 포지션 탐지)
    RecyclerView.OnScrollListener detectTopPosition = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView absListView, int i) {
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (recyclerView.computeVerticalScrollOffset() == 0) {
                addChatLog();
            }
        }
    };


    public void removeImeageMessage(String parent, int position) {
        FirebaseDatabase.getInstance().getReference("chat").child(roomName).child(parent).removeValue();
        chatMessageKeyList.remove(position);
        chatMessageList.remove(position);
        chattingRecyclerview.getRecycledViewPool().clear();
        chattingMessageAdapter.notifyDataSetChanged();
    }

    // 읽음 처리 (클라이언트)
    public void checkRefreshChatLog() {
        for (ChatMessage chatMessage : uncheckMessageList) {
            chatMessage.setCheck(0);
        }
        uncheckMessageList.clear();
        chattingRecyclerview.getRecycledViewPool().clear();
        chattingMessageAdapter.notifyDataSetChanged();
    }

    public void deleteFirebaseRef() {
        chatDatabaseRef.removeEventListener(chatInitListener);
        chatDatabaseRef.removeEventListener(chatLoadListener);
    }

    public GridItem getItem() {
        return item;
    }


}

