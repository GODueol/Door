package com.teamdoor.android.door.Chatting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.teamdoor.android.door.Chatting.util.CryptoImeageName;
import com.teamdoor.android.door.ChattingRoomList.ChattingRoomListActivity;
import com.teamdoor.android.door.Entity.ChatMessage;
import com.teamdoor.android.door.Entity.MessageVO;
import com.teamdoor.android.door.Entity.RoomVO;
import com.teamdoor.android.door.Entity.User;
import com.teamdoor.android.door.Exception.NotSetAutoTimeException;
import com.teamdoor.android.door.PeopleFragment.GridItem;
import com.teamdoor.android.door.R;
import com.teamdoor.android.door.Util.FireBaseUtil;
import com.teamdoor.android.door.Util.FirebaseSendPushMsg;
import com.teamdoor.android.door.Util.GPSInfo;
import com.teamdoor.android.door.Util.GalleryPick;
import com.teamdoor.android.door.Util.RemoteConfig;
import com.teamdoor.android.door.Util.SharedPreferencesUtil;
import com.teamdoor.android.door.Util.UiUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChatFirebaseUtil {


    private final static String chatRoomList = "chatRoomList";
    private final static String chat = "chat";
    private final static String image = "image";
    private final static String strDelete = "DELETE";

    private Context context;
    private String childChatKey;
    private DatabaseReference databaseRef;
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

    private SharedPreferencesUtil SPUtil;

    public ChatFirebaseUtil(Context context, LinearLayout overlay, TextView hideText) {
        databaseRef = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        childKeyList = new ArrayList<String>();
        chatMessageKeyList = new ArrayList<String>();
        uncheckMessageList = new ArrayList<ChatMessage>();
        this.context = context;
        this.hideText = hideText;
        this.overlay = overlay;
        SPUtil = new SharedPreferencesUtil(context);
    }

    // 방을 본 시간을 업데이트
    public void setLastChatView(String userUuid, String targetUuid) {
        Long currentTime = null;
        try {
            currentTime = getTime();
        } catch (NotSetAutoTimeException e) {
            e.printStackTrace();
        }
        databaseRef.child(chatRoomList).child(userUuid).child(targetUuid).child("lastViewTime").setValue(currentTime);
    }

    // 채팅방 삭제
    public void clearChatLog(String Room, String userUuid, String targetUuid) {

        final String roomId = Room;
        SPUtil.removeChatRoomBadge(roomId);

        // 방제거
        FirebaseDatabase.getInstance().getReference().child("chatRoomList").child(targetUuid).child(userUuid).removeValue();
        FirebaseDatabase.getInstance().getReference().child("chatRoomList").child(userUuid).child(targetUuid).removeValue();

        // 채팅방 이미지 전체 삭제 및 채팅 로그 삭제
        FirebaseDatabase.getInstance().getReference("chat").child(roomId).orderByChild("isImage").equalTo(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {//마찬가지로 중복 유무 확인
                    MessageVO message = ds.getValue(MessageVO.class);
                    FirebaseStorage.getInstance().getReferenceFromUrl(message.getImage()).delete();
                }
                FirebaseDatabase.getInstance().getReference("chat").child(roomId).removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    // 메세지 보내기
    public void sendMessage(String Room, String id, String userUuid, String targetUuid, MessageVO message) throws NotSetAutoTimeException {

        String room = Room;
        String key = databaseRef.child(chat).child(room).push().getKey();

        Long currentTime = getTime();
        Map<String, Object> childUpdates = new HashMap<>();

        if (message.getImage() == null) {        // 메세지일 경우
            childUpdates.put("/" + chat + "/" + room + "/" + key, message);
            childUpdates.put("/" + chatRoomList + "/" + targetUuid + "/" + userUuid + "/" + "lastChatTime", currentTime);
            childUpdates.put("/" + chatRoomList + "/" + targetUuid + "/" + userUuid + "/" + "lastChat", message.getContent());
            childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid + "/" + "lastViewTime", currentTime);
            childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid + "/" + "lastChat", message.getContent());
        } else {          // 이미지일 경우
            childUpdates.put("/" + chat + "/" + room + "/" + key, message);
            childUpdates.put("/" + chatRoomList + "/" + targetUuid + "/" + userUuid + "/" + "lastChatTime", currentTime);
            childUpdates.put("/" + chatRoomList + "/" + targetUuid + "/" + userUuid + "/" + "lastChat", "사진");
            childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid + "/" + "lastViewTime", currentTime);
            childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid + "/" + "lastChat", "사진");
        }
        databaseRef.updateChildren(childUpdates).addOnSuccessListener(aVoid -> {
            // 상대방에게 내정보를 담아서 메세지를 보냄
            FirebaseSendPushMsg.sendPostToFCM(chat, targetUuid, id, context.getString(R.string.alertChat), room);
        });
    }

    // 메세지 보내기 (이미지)
    public void sendImageMessage(String Room, String id, String userUuid, String targetUuid, GalleryPick galleryPick) throws NotSetAutoTimeException {

        StorageReference imagesRef = storage.getReference().child(chat);
        long currentTime = UiUtil.getInstance().getCurrentTime(context);
        final String imageName = CryptoImeageName.md5(Long.toString(currentTime) + userUuid);
        final StorageReference imageMessageRef = imagesRef.child(image + "/" + Room + "/" + imageName);

        try {
            StorageTask<UploadTask.TaskSnapshot> uploadTask = galleryPick.upload(imageMessageRef);
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
                            long currentTime = 0;
                            try {
                                currentTime = getTime();
                                MessageVO message = new MessageVO(uri.toString(), userUuid, id, imageName, currentTime, 1, 1);
                                sendMessage(Room, id, userUuid, targetUuid, message);
                            } catch (NotSetAutoTimeException e) {
                                e.printStackTrace();
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                ActivityCompat.finishAffinity((Activity) context);
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    // 처음접속시 채팅방 설정
    public void setchatRoom(String userUuid, String targetUuid, RecyclerView chattingRecyclerview, List<ChatMessage> chatMessageList) {

        final String[] Room = {null};
        final DatabaseReference chatRoomRef = FirebaseDatabase.getInstance().getReference(chatRoomList);
        this.chattingRecyclerview = chattingRecyclerview;
        chattingMessageAdapter = (ChattingMessageAdapter) chattingRecyclerview.getAdapter();
        this.chatMessageList = chatMessageList;
        chattingRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView absListView, int i) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.computeVerticalScrollOffset() == 0) {
                    addChatLog(Room[0], userUuid);
                }
            }
        });

        long currentTime = System.currentTimeMillis();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/" + chatRoomList + "/" + targetUuid + "/" + userUuid + "/" + "targetUuid", userUuid);
        childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid + "/" + "targetUuid", targetUuid);
        childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid + "/" + "lastViewTime", currentTime);
        databaseRef.updateChildren(childUpdates).addOnCompleteListener(task -> {
            // 채팅방 세팅
            chatRoomRef.child(userUuid).child(targetUuid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final RoomVO checkRoomVO = dataSnapshot.getValue(RoomVO.class);
                    if (checkRoomVO.getChatRoomid() != null) {
                        Room[0] = checkRoomVO.getChatRoomid();
                    } else {
                        Room[0] = FirebaseDatabase.getInstance().getReference(chat).push().getKey();
                        childUpdates.put("/" + chatRoomList + "/" + targetUuid + "/" + userUuid + "/" + "chatRoomid", Room[0]);
                        childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid + "/" + "chatRoomid", Room[0]);
                        databaseRef.updateChildren(childUpdates);
                    }
                    //Room 보내줘야함
                    SPUtil.setCurrentChat(context.getString(R.string.currentRoom), Room[0]);
                    // 처음 모든 메세지 읽음처리
                    databaseRef.child(chat).child(Room[0]).orderByChild("check").equalTo(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {//마찬가지로 중복 유무 확인
                                MessageVO message = ds.getValue(MessageVO.class);
                                message.setParent(ds.getKey());
                                if (message != null && !message.getWriter().equals(userUuid)) {
                                    databaseRef.child(chat).child(Room[0]).child(ds.getKey()).child("check").setValue(0);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    // 그후 메세지 통신
                    databaseRef.child(chat).child(Room[0]).limitToLast(RemoteConfig.MessageCount).addChildEventListener(new ChildEventListener() {
                        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                            childChatKey = dataSnapshot.getKey();
                            if (!childKeyList.contains(childChatKey)) {
                                childKeyList.add(childChatKey);
                                MessageVO message = dataSnapshot.getValue(MessageVO.class);
                                message.setParent(childChatKey);
                                initMessage(Room[0], userUuid, message, childChatKey);
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                            MessageVO messageVO = dataSnapshot.getValue(MessageVO.class);
                            if (messageVO.getImage() != null && messageVO.getImage().equals(strDelete) && !messageVO.getWriter().equals(userUuid)) {
                                try {
                                    String key = dataSnapshot.getKey();
                                    int position = chatMessageKeyList.indexOf(key);
                                    chatMessageList.get(position).setImage(strDelete);
                                    chattingRecyclerview.getRecycledViewPool().clear();
                                    chattingMessageAdapter.notifyDataSetChanged();
                                } catch (Exception e) {

                                }
                            } else if (messageVO.getCheck() == 0) {
                                checkRefreshChatLog();
                            }
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        });

    }


    // 상대방 프로필 정보 셋팅
    void setUserInfo(User targetUser, String targetUuid) {
        try {
            item = new GridItem(0, targetUuid, targetUser.getSummaryUser(), targetUser.getPicUrls().getThumbNail_picUrl1());
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
        } catch (Exception e) {
            item = new GridItem(0, null, null, null);
            item.setUuid(targetUuid);
        }
    }

    public Long getTime() throws NotSetAutoTimeException {
        return UiUtil.getInstance().getCurrentTime(context);
    }

    // 채팅 초기화
    public void initMessage(String Room, String userUuid, MessageVO message, String key) {

        ChatMessage chatMessage;
        int check = message.getCheck();

        // 체크버튼
        if (check != 0 && !message.getWriter().equals(userUuid)) {
            message.setCheck(0);
            databaseRef.child(chat).child(Room).child(key).child("check").setValue(check - 1);
        }

        if (message.getWriter().equals(userUuid) && message.getImage() == null) {
            chatMessage = new ChatMessage(message, true, false);
            if (chatMessage.getCheck() == 1) {
                uncheckMessageList.add(chatMessage);
            }
        } else if (!message.getWriter().equals(userUuid) && message.getImage() == null) {
            chatMessage = new ChatMessage(message, false, false, item);
        } else if (message.getWriter().equals(userUuid)) {
            chatMessage = new ChatMessage(message, true, true);
            if (chatMessage.getCheck() == 1) {
                uncheckMessageList.add(chatMessage);
            }
        } else {
            chatMessage = new ChatMessage(message, false, true, item);
        }


        //데이터 추가
        chatMessageList.add(chatMessage);
        chatMessageKeyList.add(key);
        chattingRecyclerview.getRecycledViewPool().clear();
        chattingMessageAdapter.notifyDataSetChanged();

        // 스크롤 다루기
        chattingRecyclerview.post(() -> {
            int pos = chattingMessageAdapter.getItemCount() - 1;
            LinearLayoutManager lm = (LinearLayoutManager) chattingRecyclerview.getLayoutManager();
            int visiblieCompLastPosition = lm.findLastVisibleItemPosition();

            if (pos <= visiblieCompLastPosition) {
                //맨마지막에서 2이내에 있을경우
                chattingRecyclerview.scrollToPosition(pos);
            } else if (!chatMessage.isMine() && !chatMessage.isImage()) {
                // 내것이 아니고 텍스트
                hideText.setText(message.getContent());
                overlay.setVisibility(View.VISIBLE);
            } else if (!chatMessage.isMine()) {
                // 내것이 아니고 이미지
                hideText.setText("사진");
                overlay.setVisibility(View.VISIBLE);
            } else {
                // 내 메세지일경우
                chattingRecyclerview.scrollToPosition(chattingMessageAdapter.getItemCount() - 1);
            }
        });

    }

    // 채팅 로딩
    public void loadMessage(String Room, String userUuid,Iterator<DataSnapshot> child) {

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
//        if (detectTopPosition != null) {
            chattingRecyclerview.getRecycledViewPool().clear();
            chattingMessageAdapter.notifyDataSetChanged();
            int messageViewCount = childKeyList.size() + (visiblieCompLastPosition - visilbeCompFirstPosition) - 2;
            chattingRecyclerview.scrollToPosition(messageViewCount);
//            chattingRecyclerview.addOnScrollListener(detectTopPosition);
//        }
        databaseRef.child(chat).child(Room).removeEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                loadMessage(Room,userUuid,child);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // 채팅 로딩 발생 메소드
    public void addChatLog(String Room, String userUuid) {
        if (!childKeyList.isEmpty() && childKeyList.size() != 1) {
//            chattingRecyclerview.removeOnScrollListener(detectTopPosition);
            if (childKeyList.size() < RemoteConfig.MessageCount) {
//                detectTopPosition = null;
                return;
            }
            // 마지막으로 불러온 채팅 아이디
            String lastChildChatKey = childKeyList.get(0);
            childKeyList.clear();
            // 맨 위 아이템 제거(중복발생)
            try {
                chatMessageList.remove(0);
                chattingRecyclerview.getRecycledViewPool().clear();
                chattingMessageAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Intent p = new Intent(context.getApplicationContext(), ChattingRoomListActivity.class);
                p.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.getApplicationContext().startActivity(p);
            }
            messageWeight *= 1;
            databaseRef.child(chat).child(Room).orderByKey().endAt(lastChildChatKey).limitToLast(RemoteConfig.MessageCount * messageWeight).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();
                    loadMessage(Room,userUuid,child);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    // 채팅 로딩 리스너 (상단 포지션 탐지)
//    RecyclerView.OnScrollListener detectTopPosition =


    // 이미지 제거 루팅
    public void removeImeageMessage(String Room,final String parent, final int position) {
        String url = chatMessageList.get(position).getImage();
        FirebaseStorage.getInstance().getReferenceFromUrl(url).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseDatabase.getInstance().getReference("chat").child(Room).child(parent).child("image").setValue(strDelete);
                FirebaseDatabase.getInstance().getReference("chat").child(Room).child(parent).child("isImage").setValue(0);
                chatMessageList.get(position).setImage(strDelete);
                chattingRecyclerview.getRecycledViewPool().clear();
                chattingMessageAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "이미지 삭제에 실패했습니다", Toast.LENGTH_SHORT).show();
            }
        });

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
        //databaseRef.child(chat).child(roomName).removeEventListener(chatInitListener);
        //databaseRef.child(chat).child(roomName).removeEventListener(chatLoadListener);
    }

    public GridItem getItem() {
        return item;
    }


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
        databaseRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseSendPushMsg.sendPostToFCM("chat", oUuid, nickName, "비공개 사진을 열었습니다!", room);
            }
        });
    }

    public void Pause() {
        SPUtil.removeCurrentChat(context.getString(R.string.currentRoom));
    }

    public void Resume() {
        SPUtil.setCurrentChat(context.getString(R.string.currentRoom), "-LJnZGVGTUK2GiyawCb5");
    }

}

