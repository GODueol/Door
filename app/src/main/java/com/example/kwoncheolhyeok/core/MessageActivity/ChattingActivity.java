package com.example.kwoncheolhyeok.core.MessageActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.MessageVO;
import com.example.kwoncheolhyeok.core.PeopleFragment.ImageAdapter;
import com.example.kwoncheolhyeok.core.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChattingActivity extends AppCompatActivity {

    Toolbar toolbar = null;

    private ListView mListView;
    private ImageButton mButtonSend;
    private EditText mEditTextMessage;
    private ImageButton mImageView;

    private ChatMessageAdapter mAdapter;

    private DatabaseReference mDatabase,userRef;
    private FirebaseAuth mAuth;
    private String userId;
    private String roomName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_chatting_activity);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        userId =  mAuth.getUid();

        mListView = (ListView) findViewById(R.id.listView);
        mButtonSend = (ImageButton) findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mImageView = (ImageButton) findViewById(R.id.iv_image);

        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);

        Intent p = getIntent();
        final ImageAdapter.Item item = (ImageAdapter.Item) p.getSerializableExtra("user");
        String targetuuid = item.getUuid();
        // userId 내아이디 , item.getUuid() 상대방 아이디
       // Toast.makeText(this,        userRef.child(userId).child("chatRoom").child(targetuuid).getKey(),Toast.LENGTH_SHORT).show();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference roomKeyRef = userRef.child(userId).child("chatRoom").child(targetuuid).push();
        roomName = roomKeyRef.getKey();
        userRef.child(userId).child("chatRoom").child(targetuuid).setValue(roomName);
        userRef.child(targetuuid).child("chatRoom").child(userId).setValue(roomName);

        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                writeMessage(roomName,0, userId,"kaikai",message,0);
                mEditTextMessage.setText("");
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


        FirebaseDatabase.getInstance().getReference().child("message").child(roomName)
                .addChildEventListener(childEventListener);
    }

    private void writeMessage(String room , int img,String userId, String nickname, String content, int editimg) {
        MessageVO message = new MessageVO(img,userId ,nickname, content,editimg);
        mDatabase.child("message").child(room).push().setValue(message);
    }
    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
    }

    private void sendMessage() {
        ChatMessage chatMessage = new ChatMessage(null, true, true);
        mAdapter.add(chatMessage);

        mimicOtherMessage();
    }

    private void mimicOtherMessage() {
        ChatMessage chatMessage = new ChatMessage(null, false, true);
        mAdapter.add(chatMessage);
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d("fireabaseUpdate", "onChildAdded:" + dataSnapshot.getKey());

            // A new comment has been added, add it to the displayed list
            MessageVO user = dataSnapshot.getValue(MessageVO.class);
            String message = user.getContent();
            ChatMessage chatMessage = new ChatMessage(message, true, false);
            mAdapter.add(chatMessage);
            // ...
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d("fireabaseUpdate", "onChildChanged:" + dataSnapshot.getKey());

            // A comment has changed, use the key to determine if we are displaying this
            // comment and if so displayed the changed comment.
            MessageVO user = dataSnapshot.getValue(MessageVO.class);
            String commentKey = dataSnapshot.getKey();

            // ...
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.d("fireabaseUpdate", "onChildRemoved:" + dataSnapshot.getKey());

            // A comment has changed, use the key to determine if we are displaying this
            // comment and if so remove it.
            String commentKey = dataSnapshot.getKey();

            // ...
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d("fireabaseUpdate", "onChildMoved:" + dataSnapshot.getKey());

            // A comment has changed position, use the key to determine if we are
            // displaying this comment and if so move it.
            MessageVO user = dataSnapshot.getValue(MessageVO.class);
            String commentKey = dataSnapshot.getKey();

            // ...
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w("fireabaseUpdate", "postComments:onCancelled", databaseError.toException());
            Toast.makeText(getApplicationContext(), "Failed to load comments.",
                    Toast.LENGTH_SHORT).show();
        }
    };
        /*addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information

                        MessageVO user = dataSnapshot.getValue(MessageVO.class);
                        String message = user.getContent();
                        ChatMessage chatMessage = new ChatMessage(message, true, false);
                        mAdapter.add(chatMessage);
                       // sendMessage(user.getContent());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }*/
//    private void scrollMyListViewToBottom() {
//        mListView.post(new Runnable() {
//            @Override
//            public void run() {
//                // Select the last row so it will scroll into view...
//                mListView.setSelection(mAdapter.getCount() - 1);
//            }
//        });
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }

    // 뒤로가기 버튼 기능
    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.find_text) {
            Toast.makeText(this, "흐음..", Toast.LENGTH_SHORT).show();
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.board_menu, menu);

        return super.onPrepareOptionsMenu(menu);
    }

}

