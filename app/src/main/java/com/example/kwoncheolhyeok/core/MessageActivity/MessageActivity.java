package com.example.kwoncheolhyeok.core.MessageActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.MessageActivity.messageRecyclerAdapter.OnRemoveChattingListCallback;
import com.example.kwoncheolhyeok.core.MessageActivity.util.MessageVO;
import com.example.kwoncheolhyeok.core.MessageActivity.util.RoomVO;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.example.kwoncheolhyeok.core.Util.SharedPreferencesUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    Toolbar toolbar = null;

    messageRecyclerAdapter messageRecyclerAdapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView messageList;
    List<RoomVO> listrowItem;
    List<String> uuidList;
    private DatabaseReference chatRoomListRef;
    private FirebaseAuth mAuth;
    private String userId;

    private SharedPreferencesUtil SPUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.chatting_list_activity);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        SPUtil = new SharedPreferencesUtil(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        listrowItem = new ArrayList<RoomVO>();
        // preparing list data
        com.example.kwoncheolhyeok.core.MessageActivity.messageRecyclerAdapter.RecyclerViewClickListener listener = new messageRecyclerAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, final int position) {
                final RoomVO item = messageRecyclerAdapter.getItemRoomVO(position);
                FireBaseUtil.getInstance().queryBlockWithMe(item.getTargetUuid(), new FireBaseUtil.BlockListener() {
                    @Override
                    public void isBlockCallback(boolean isBlockWithMe) {
                        //블럭이 아니면
                        if (!isBlockWithMe) {
                            FirebaseDatabase.getInstance().getReference("users").child(item.getTargetUuid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User user = dataSnapshot.getValue(User.class);
                                    Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
                                    intent.putExtra("user", user);
                                    intent.putExtra("userUuid", item.getTargetUuid());
                                    SPUtil.removeChatRoomBadge(item.getChatRoomid());
                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        } else {// 블럭이면
                            chatRoomListRef.child(userId).child(item.getTargetUuid()).removeValue();
                            messageRecyclerAdapter.notifyDataSetChanged();
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
                    }
                });


            }
        };
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        messageRecyclerAdapter = new messageRecyclerAdapter(MessageActivity.this, listrowItem, R.layout.chatting_list_row, listener, chatlistener);
        messageList = (RecyclerView) findViewById(R.id.messagelist);
        messageList.setAdapter(messageRecyclerAdapter);
        messageList.setLayoutManager(linearLayoutManager);
        messageList.addItemDecoration(new DividerItemDecoration(MessageActivity.this, DividerItemDecoration.VERTICAL)); //리사이클뷰 구분선
        messageList.setItemAnimator(new DefaultItemAnimator());
        setMessageData();
        SPUtil.getChatListPreferences().registerOnSharedPreferenceChangeListener(this);
        /*****************************************************************/
    }


    public void setMessageData() {
        chatRoomListRef = FirebaseDatabase.getInstance().getReference("chatRoomList");
        uuidList = new ArrayList<>();
        chatRoomListRef.child(userId).orderByChild("lastChatTime").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final RoomVO roomList = dataSnapshot.getValue(RoomVO.class);
                try {
                    roomList.setBadgeCount(SPUtil.getChatRoomBadge(roomList.getChatRoomid()));
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
                                refreshChatRoomList(dataSnapshot.getValue(User.class), roomList);
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
                                    refreshChatRoomList(dataSnapshot.getValue(User.class), roomList, true);
                                } catch (Exception e) {
                                    uuidList.add(roomList.getTargetUuid());
                                    listrowItem.add(roomList);
                                    refreshChatRoomList(dataSnapshot.getValue(User.class), roomList);
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
                        messageRecyclerAdapter.notifyDataSetChanged();
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
                messageRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    OnRemoveChattingListCallback chatlistener = new OnRemoveChattingListCallback() {

        @Override
        public void onRemove(String target) {
            chatRoomListRef.child(userId).child(target).child("lastChat").removeValue();
        }
    };

    // 뒤로가기 버튼 기능
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshChatRoomList(User target, RoomVO roomList) {
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

        messageRecyclerAdapter.notifyDataSetChanged();
    }

    public void refreshChatRoomList(User target, RoomVO roomList, boolean changeFlag) {
        int key = uuidList.indexOf(roomList.getTargetUuid());
        Long startTime = listrowItem.get(key).getLastChatTime();
        Long endTime = roomList.getLastChatTime();
        listrowItem.remove(key);

        if (startTime.equals(endTime)) {
            // 새로고침
            listrowItem.add(key, roomList);
            refreshChatRoomList(target, roomList);
        } else {      // 맨위로 올림
            uuidList.remove(key);
            uuidList.add(roomList.getTargetUuid());
            listrowItem.add(roomList);
            refreshChatRoomList(target, roomList);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        int num = sharedPreferences.getInt(key, 0);
        //리스트 아이템 뱃지 동기화
        for (RoomVO r : listrowItem) {
            if (r.getChatRoomid().equals(key)) {
                int i = listrowItem.indexOf(r);
                listrowItem.get(i).setBadgeCount(num);
                messageRecyclerAdapter.notifyDataSetChanged();
                break;
            }
        }
    }
}