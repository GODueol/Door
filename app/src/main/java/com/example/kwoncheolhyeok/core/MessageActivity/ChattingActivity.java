package com.example.kwoncheolhyeok.core.MessageActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.CryptoImeageName;
import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.MessageVO;
import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.RoomVO;
import com.example.kwoncheolhyeok.core.PeopleFragment.ImageAdapter;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.Camera.LoadPicture;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.example.kwoncheolhyeok.core.Util.GPSInfo;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Map;

public class ChattingActivity extends AppCompatActivity {

    Toolbar toolbar = null;
    private static final int REQUEST_GALLERY = 2;

    private ListView mListView;
    private ImageButton mButtonSend;
    private EditText mEditTextMessage;
    private ImageButton mImageView;

    private ChatMessageAdapter mAdapter;

    private DatabaseReference mDatabase, chatRoomRef;
    private FirebaseAuth mAuth;

    private String roomName;

    private User user;
    private String userNickName, userUuid, userPickuri;
    private User targetUser;
    private String targetNickName, targetUuid, targetPicuri;
    private  ImageAdapter.Item item;
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


        mListView = (ListView) findViewById(R.id.listView);
        mButtonSend = (ImageButton) findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mImageView = (ImageButton) findViewById(R.id.iv_image);
        FirebaseStorage storage = FirebaseStorage.getInstance();

        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);

        checkchatRoom();

        // 메세지 보내기
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                long currentTime = System.currentTimeMillis();
                writeMessage(roomName, null, userUuid, user.getId(), message,  currentTime, 1);
                mEditTextMessage.setText("");
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImageMessage();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_GALLERY) {

                Uri outputFileUri = data.getData();

                FirebaseStorage storage = FirebaseStorage.getInstance("gs://core-865fc.appspot.com");
                StorageReference imagesRef = storage.getReference().child("message/");
                long currentTime = System.currentTimeMillis();
                final String imageName = CryptoImeageName.md5(Long.toString(currentTime));
                final StorageReference imageMessageRef = imagesRef.child("image/"+roomName+"/"+userUuid+"/"+imageName);
                UploadTask uploadTask = imageMessageRef.putFile(outputFileUri);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("chatError",e.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        imageMessageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                long currentTime = System.currentTimeMillis();
                                writeMessage(roomName, uri.toString(), userUuid, user.getId(), null, currentTime, 1);
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
        }
    }

    private void writeMessage(String room, String image, String userId, String nickname, String content, long currentTime,int check) {
        MessageVO message = new MessageVO(image, userId, nickname, content,currentTime, check);
        String key = mDatabase.child("chat").child(room).push().getKey();

        Map<String, Object> childUpdates = new HashMap<>();
        if(image == null) {
            childUpdates.put("/chat/" + room + "/" + key, message);

            childUpdates.put("/chatRoomList/" + targetUuid + "/" + userUuid + "/" + "lastChatTime", currentTime);
            childUpdates.put("/chatRoomList/" + targetUuid + "/" + userUuid + "/" + "lastChat", content);

            childUpdates.put("/chatRoomList/" + userUuid + "/" + targetUuid + "/" + "lastViewTime", currentTime);
            childUpdates.put("/chatRoomList/" + userUuid + "/" + targetUuid + "/" + "lastChat", content);
        }else if(content == null){
            childUpdates.put("/chat/" + room + "/" + key, message);

            childUpdates.put("/chatRoomList/" + targetUuid + "/" + userUuid + "/" + "lastChatTime", currentTime);
            childUpdates.put("/chatRoomList/" + targetUuid + "/" + userUuid + "/" + "lastChat", "사진");
            childUpdates.put("/chatRoomList/" + userUuid + "/" + targetUuid + "/" + "lastViewTime", currentTime);
            childUpdates.put("/chatRoomList/" + userUuid + "/" + targetUuid + "/" + "lastChat", "사진");
        }
        mDatabase.updateChildren(childUpdates);
    }

    private void mimicOtherMessage(MessageVO message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
    }

    private void sendImageMessage() {
        LoadPicture loadPicture = new LoadPicture(this, this);
        loadPicture.onGallery();
    }

    private void mimicOtherMessage() {
        ChatMessage chatMessage = new ChatMessage(null, false, true);
        mAdapter.add(chatMessage);
    }

    public void checkchatRoom() {
        Intent p = getIntent();
        // 상대방 데이터 셋
        targetUser = (User) p.getSerializableExtra("user");
        targetUuid = (String) p.getSerializableExtra("userUuid");
        targetPicuri = (String) p.getSerializableExtra("userPicuri");
        //targetNickName = targetUser.getId();
        // 내정보 데이터 셋
        user = DataContainer.getInstance().getUser();
        userUuid = mAuth.getUid();
        userPickuri = user.getPicUrls().getPicUrl1();
        //userNickName = user.getId();

        chatRoomRef = FirebaseDatabase.getInstance().getReference("chatRoomList");

        chatRoomRef.child(userUuid).child(targetUuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                RoomVO checkRoomVO = dataSnapshot.getValue(RoomVO.class);
                long currentTime = System.currentTimeMillis();
                try {
                    roomName = checkRoomVO.getChatRoomid();
                    chatRoomRef.child(userUuid).child(targetUuid).child("lastViewTime").setValue(currentTime);
                } catch (Exception e) {
                    // 채팅방 이름 설정
                    roomName  = FirebaseDatabase.getInstance().getReference("chat").push().getKey();

                    Map<String, Object> childUpdates = new HashMap<>();
                    RoomVO roomVO = new RoomVO(roomName,null, userUuid,currentTime, userPickuri);
                    childUpdates.put("/chatRoomList/" + targetUuid + "/" + userUuid  , roomVO);
                    roomVO = new RoomVO(roomName,null, targetUuid,currentTime, targetPicuri);
                    childUpdates.put("/chatRoomList/" + userUuid + "/" + targetUuid , roomVO);
                    mDatabase.updateChildren(childUpdates);
                    chatRoomRef.child(userUuid).child(targetUuid).child("lastViewTime").setValue(currentTime);
                }
                item = new ImageAdapter.Item(0,targetUuid,targetUser,targetPicuri);
                // 상대방과의 거리 셋팅
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(FireBaseUtil.currentLocationPath);
                GeoFire geoFire = new GeoFire(ref);
                final Location location = GPSInfo.getmInstance(getApplication()).getGPSLocation();
                geoFire.getLocation(targetUuid, new LocationCallback() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onLocationResult(String s, GeoLocation geoLocation) {
                        Location targetLocation = new Location("");//provider name is unnecessary
                        targetLocation.setLatitude(geoLocation.latitude);//your coords of course
                        targetLocation.setLongitude(geoLocation.longitude);
                        final float distance = location.distanceTo(targetLocation);
                        item.setDistance(distance);
                        //Log.d("distancec",String.format("%.1f", distance / 1000));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


                FirebaseDatabase.getInstance().getReference().child("chat").child(roomName)
                        .addChildEventListener(childEventListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d("fireabaseUpdate", "onChildAdded:" + dataSnapshot.getKey());

            // A new comment has been added, add it to the displayed list
            MessageVO message = dataSnapshot.getValue(MessageVO.class);
            ChatMessage chatMessage;
            int check = message.getCheck();

            if(check!=0 && !message.getWriter().equals(userUuid)){
                message.setCheck(0);
                mDatabase.child("chat").child(roomName).child(dataSnapshot.getKey()).child("check").setValue(check-1);
            }

            if(message.getWriter().equals(userUuid) && message.getImage() == null) {
                chatMessage = new ChatMessage(message, true, false);
            }
            else if(!message.getWriter().equals(userUuid) && message.getImage() == null){
                chatMessage = new ChatMessage(message, false, false, item);
            }
            else if(message.getWriter().equals(userUuid) && message.getContent() == null){
                chatMessage = new ChatMessage(message, true, true);
            }
            else{
                chatMessage = new ChatMessage(message, false, true,item);
            }
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
            mAdapter.notifyDataSetChanged();
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
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.find_text_menu, menu);

        return super.onPrepareOptionsMenu(menu);
    }

}

