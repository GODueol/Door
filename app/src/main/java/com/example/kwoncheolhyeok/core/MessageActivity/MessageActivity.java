package com.example.kwoncheolhyeok.core.MessageActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.MessageVO;
import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.messageRecyclerAdapter;
import com.example.kwoncheolhyeok.core.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    Toolbar toolbar = null;

    messageRecyclerAdapter messageRecyclerAdapter;
    RecyclerView messageList;
    List<MessageVO> listrowItem;

    private DatabaseReference chatRoomListRef;
    private FirebaseAuth mAuth;
    private String userId;

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
        // preparing list data
        messageList = (RecyclerView) findViewById(R.id.messagelist);
        setMessageData();
        messageList.setAdapter(new messageRecyclerAdapter(listrowItem,R.layout.messagelist_row));
        messageList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        messageList.setItemAnimator(new DefaultItemAnimator());
        /*****************************************************************/
    }


    public void setMessageData(){
        listrowItem = new ArrayList<MessageVO>();
        chatRoomListRef = FirebaseDatabase.getInstance().getReference("chatRoomList");
        chatRoomListRef.child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MessageVO chatList = dataSnapshot.getValue(MessageVO.class);
                Log.d("123",dataSnapshot.getKey());
                listrowItem.add(chatList);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
    };


}