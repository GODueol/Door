package com.example.kwoncheolhyeok.core.MessageActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.ChatFirebaseUtil;
import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.MessageVO;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.Camera.LoadPicture;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ChattingActivity extends AppCompatActivity {

    Toolbar toolbar = null;
    private static final int REQUEST_GALLERY = 2;
    private ListView mListView;
    private ImageButton mButtonSend;
    private EditText mEditTextMessage;
    private ImageButton mImageView;

    private ChatMessageAdapter mAdapter;

    private FirebaseAuth mAuth;

    ChatFirebaseUtil chatFirebaseUtil;

    private User user;
    private String userUuid;
    private User targetUser;
    private String targetUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_chatting_activity);
        Intent p = getIntent();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        mAuth = FirebaseAuth.getInstance();

        mListView = (ListView) findViewById(R.id.listView);
        mButtonSend = (ImageButton) findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mImageView = (ImageButton) findViewById(R.id.iv_image);
        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>(), litener);
        mListView.setAdapter(mAdapter);

        // 상대방 데이터 셋
        targetUser = (User) p.getSerializableExtra("user");
        targetUuid = (String) p.getSerializableExtra("userUuid");
        // 내정보 데이터 셋
        user = DataContainer.getInstance().getUser();
        userUuid = mAuth.getUid();

        chatFirebaseUtil = new ChatFirebaseUtil(this, user, targetUser, userUuid, targetUuid);
        chatFirebaseUtil.setchatRoom(mAdapter, mListView);

        // 메세지 보내기
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                long currentTime = System.currentTimeMillis();
                writeMessage(null, userUuid, user.getId(), message, currentTime, 1);
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
                chatFirebaseUtil.sendImageMessage(outputFileUri);

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        chatFirebaseUtil.setLastChatView();
    }

    private void writeMessage(String image, String userId, String nickname, String content, long currentTime, int check) {
        MessageVO message = new MessageVO(image, userId, nickname, content, currentTime, check);
        chatFirebaseUtil.sendMessage(message);
    }

    private void sendImageMessage() {
        LoadPicture loadPicture = new LoadPicture(this, this);
        loadPicture.onGallery();
    }

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

    ChatMessageAdapter.OnCallbackList litener = new ChatMessageAdapter.OnCallbackList() {
        @Override
        public void onEvent() {
            mListView.smoothScrollToPosition(mListView.getAdapter().getCount()-1);
        }
    };

}

