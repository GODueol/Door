package com.teamdoor.android.door.MessageList;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.teamdoor.android.door.Entity.MessageVO;
import com.teamdoor.android.door.Entity.RoomVO;
import com.teamdoor.android.door.Entity.User;
import com.teamdoor.android.door.Util.FireBaseUtil;
import com.teamdoor.android.door.Util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class MessagePresenter implements MessageContract.Presenter, SharedPreferences.OnSharedPreferenceChangeListener {


    private MessageContract.View mMeesageView;
    private List<RoomVO> listrowItem;
    private List<String> uuidList;
    private DatabaseReference chatRoomListRef;
    private String userId;
    private SharedPreferencesUtil SPUtil;
    int BadgeCount = 0;

    MessagePresenter(MessageContract.View MessageView, SharedPreferencesUtil SPUtil) {
        chatRoomListRef = FirebaseDatabase.getInstance().getReference("chatRoomList");

        this.SPUtil = SPUtil;
        SPUtil.getChatListPreferences().registerOnSharedPreferenceChangeListener(this);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();

        uuidList = new ArrayList<>();
        mMeesageView = MessageView;
        mMeesageView.setPresenter(this);
    }

    @Override
    public void start() {
    }

    @Override
    public void setListItem(List<RoomVO> listrowItem) {
        this.listrowItem = listrowItem;
    }

    @Override
    public void enterChatRoom(RoomVO item) {

        FireBaseUtil.getInstance().queryBlockWithMe(item.getTargetUuid(), isBlockWithMe -> {
            //블럭이 아니면
            if (!isBlockWithMe) {
                FirebaseDatabase.getInstance().getReference("users").child(item.getTargetUuid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user", user);
                        bundle.putString("userUuid", item.getTargetUuid());
                        SPUtil.removeChatRoomBadge(item.getChatRoomid());
                        mMeesageView.startChattingActivity(bundle);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            } else {// 블럭이면
                chatRoomListRef.child(userId).child(item.getTargetUuid()).removeValue();
                mMeesageView.refreshMessageListView();
                // 채팅방 이미지 전체 삭제 및 채팅 로그 삭제
                final String roomId = item.getChatRoomid();
                SPUtil.removeChatRoomBadge(roomId);
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
        });
    }

    @Override
    public void setMessageList() {
        chatRoomListRef.child(userId).orderByChild("lastChatTime").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final RoomVO roomList = dataSnapshot.getValue(RoomVO.class);
                try {
                    // 뱃지 수 동기화
                    int c = SPUtil.getChatRoomBadge(roomList.getChatRoomid());
                    roomList.setBadgeCount(c);
                    BadgeCount += c;
                    SPUtil.setBadgeCount(mMeesageView.getResourceBadge(), BadgeCount);
                } catch (Exception e) {
                    roomList.setBadgeCount(0);
                }
                if (roomList.getLastChat() != null) {
                    if (roomList.getTargetUuid() != null) {
                        uuidList.add(roomList.getTargetUuid());
                        listrowItem.add(roomList);
                        FirebaseDatabase.getInstance().getReference("users").child(roomList.getTargetUuid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.getKey().equals(mMeesageView.getResourceTeamCore())) {
                                    realTimeMessageListChange(dataSnapshot.getValue(User.class), roomList);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    } else {
                        FirebaseDatabase.getInstance().getReference("chatRoomList").child(userId).child(dataSnapshot.getKey()).removeValue();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final RoomVO roomList = dataSnapshot.getValue(RoomVO.class);
                try {
                    roomList.setBadgeCount(SPUtil.getChatRoomBadge(roomList.getChatRoomid()));
                } catch (Exception e) {
                    roomList.setBadgeCount(0);
                }
                if (roomList.getLastChat() != null) {
                    try {
                        FirebaseDatabase.getInstance().getReference("users").child(roomList.getTargetUuid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    roomList.setBadgeCount(SPUtil.getChatRoomBadge(roomList.getChatRoomid()));
                                    realTimeMessageListChange(dataSnapshot.getValue(User.class), roomList, true);
                                } catch (Exception e) {
                                    uuidList.add(roomList.getTargetUuid());
                                    listrowItem.add(roomList);
                                    realTimeMessageListChange(dataSnapshot.getValue(User.class), roomList);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    } catch (Exception e) {
                        FirebaseDatabase.getInstance().getReference("chatRoomList").child(userId).child(dataSnapshot.getKey()).removeValue();
                    }
                } else if (roomList.getLastChat() == null) {
                    try {
                        int key = uuidList.indexOf(roomList.getTargetUuid());
                        listrowItem.remove(key);
                        uuidList.remove(key);
                        mMeesageView.refreshMessageListView();
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                RoomVO roomList = dataSnapshot.getValue(RoomVO.class);
                try {
                    int key = uuidList.indexOf(roomList.getTargetUuid());
                    listrowItem.remove(key);
                    uuidList.remove(key);
                } catch (Exception e) {
                }
                mMeesageView.refreshMessageListView();
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
    public void realTimeMessageListChange(User target, RoomVO roomList, boolean changeFlag) {
        int key = uuidList.indexOf(roomList.getTargetUuid());
        Long startTime = listrowItem.get(key).getLastChatTime();
        Long endTime = roomList.getLastChatTime();
        listrowItem.remove(key);

        if (startTime.equals(endTime)) {
            // 새로고침
            listrowItem.add(key, roomList);
            realTimeMessageListChange(target, roomList);
        } else {      // 맨위로 올림
            uuidList.remove(key);
            uuidList.add(roomList.getTargetUuid());
            listrowItem.add(roomList);
            realTimeMessageListChange(target, roomList);
        }
    }

    @Override
    public void removeMessageList(String target) {
        chatRoomListRef.child(userId).child(target).child("lastChat").removeValue();
    }

    @Override
    public void realTimeMessageListChange(User target, RoomVO roomList) {

        try {
            Log.d("test", target.getId());

            if (target.getId() != null && !target.getId().equals(roomList.getTargetNickName())) {
                roomList.setTargetNickName(target.getId());
                chatRoomListRef.child(userId).child(roomList.getTargetUuid()).child("targetNickName").setValue(target.getId());
            }

            if (target.getTotalProfile() != null && !target.getTotalProfile().equals(roomList.getTargetProfile())) {
                roomList.setTargetProfile(target.getTotalProfile());
                chatRoomListRef.child(userId).child(roomList.getTargetUuid()).child("targetProfile").setValue(target.getTotalProfile());
            }

            if (target.getPicUrls() != null && target.getPicUrls().getThumbNail_picUrl1() != null && !target.getPicUrls().getThumbNail_picUrl1().equals(roomList.getTargetUrl())) {
                roomList.setTargetUrl(target.getPicUrls().getThumbNail_picUrl1());
                chatRoomListRef.child(userId).child(roomList.getTargetUuid()).child("targetUrl").setValue(target.getPicUrls().getThumbNail_picUrl1());
            }
        } catch (Exception ignore) {
        }

        mMeesageView.refreshMessageListView();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        int num = sharedPreferences.getInt(key, 0);
        //리스트 아이템 뱃지 동기화
        for (RoomVO r : listrowItem) {
            if (r.getChatRoomid().equals(key)) {
                int i = listrowItem.indexOf(r);
                listrowItem.get(i).setBadgeCount(num);
                mMeesageView.refreshMessageListView();
                break;
            }
        }
    }
}
