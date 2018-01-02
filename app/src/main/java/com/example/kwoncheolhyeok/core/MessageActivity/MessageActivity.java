package com.example.kwoncheolhyeok.core.MessageActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.RoomVO;
import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.messageRecyclerAdapter;
import com.example.kwoncheolhyeok.core.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    Toolbar toolbar = null;

    messageRecyclerAdapter messageRecyclerAdapter;
    RecyclerView messageList;
    List<RoomVO> listrowItem;
    int i;
    HashMap<String, Integer> listItemNum;

    private DatabaseReference chatRoomListRef;
    private FirebaseAuth mAuth;
    private String userId;
    private RoomVO roomList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.message_activity);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        listrowItem = new ArrayList<RoomVO>();
        listItemNum = new HashMap<String, Integer>();
        i = 0;
        // preparing list data
        com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.messageRecyclerAdapter.RecyclerViewClickListener listener = new messageRecyclerAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                final RoomVO item = messageRecyclerAdapter.getItemRoomVO(position);
                FirebaseDatabase.getInstance().getReference("users").child(item.getUserUuid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
                        intent.putExtra("user", user);
                        intent.putExtra("userUuid", item.getUserUuid());
                        intent.putExtra("userPicuri", item.getTargetUri());
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        messageRecyclerAdapter = new messageRecyclerAdapter(listrowItem, R.layout.messagelist_row, listener);
        messageList = (RecyclerView) findViewById(R.id.messagelist);
        messageList.setAdapter(messageRecyclerAdapter);
        messageList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        messageList.setItemAnimator(new DefaultItemAnimator());
        setMessageData();


        /*****************************************************************/
    }


    public void setMessageData() {
        chatRoomListRef = FirebaseDatabase.getInstance().getReference("chatRoomList");
        chatRoomListRef.child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                roomList = dataSnapshot.getValue(RoomVO.class);
                if (roomList.getLastChat() != null) {
                    FirebaseDatabase.getInstance().getReference("users").child(roomList.getUserUuid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User target = dataSnapshot.getValue(User.class);
                            listItemNum.put(roomList.getUserUuid(), i);
                            i++;
                            roomList.setTargetNickName(target.getId());
                            roomList.setTargetProfile(target.getTotalProfile());

                            listrowItem.add(roomList);
                            messageRecyclerAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                roomList = dataSnapshot.getValue(RoomVO.class);
                FirebaseDatabase.getInstance().getReference("users").child(roomList.getUserUuid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User target = dataSnapshot.getValue(User.class);
                        int listNum = listItemNum.get(roomList.getUserUuid());
                        listrowItem.remove(listNum);
                        roomList.setTargetNickName(target.getId());
                        roomList.setTargetProfile(target.getTotalProfile());
                        listrowItem.add(listNum, roomList);
                        messageRecyclerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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


    // 뒤로가기 버튼 기능
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ;


}