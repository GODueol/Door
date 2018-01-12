package com.example.kwoncheolhyeok.core.MessageActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.ChatFirebaseUtil;
import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.MessageVO;
import com.example.kwoncheolhyeok.core.PeopleFragment.FullImageActivity;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.Camera.LoadPicture;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.example.kwoncheolhyeok.core.Util.UiUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ChattingActivity extends AppCompatActivity {

    Toolbar toolbar = null;
    private static final int REQUEST_GALLERY = 2;
    private ListView mListView;
    private ImageButton mButtonSend;
    private EditText mEditTextMessage;
    private ImageButton mImageView;

    private ChatMessageAdapter mAdapter;
    private List<ChatMessage> chatListItem;
    private FirebaseAuth mAuth;

    ChatFirebaseUtil chatFirebaseUtil;

    private User user;
    private String userUuid;
    private User targetUser;
    private String targetUuid;

    @SuppressLint("ClickableViewAccessibility")
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
        chatListItem = new ArrayList<ChatMessage>();
        mAdapter = new ChatMessageAdapter(this,chatListItem, litener);
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(detectTopPosition);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mAdapter.deletRequestListener();
                return false;
            }
        });

        // 상대방 데이터 셋
        targetUser = (User) p.getSerializableExtra("user");
        targetUuid = (String) p.getSerializableExtra("userUuid");
        // 내정보 데이터 셋
        user = DataContainer.getInstance().getUser();
        userUuid = mAuth.getUid();

        chatFirebaseUtil = new ChatFirebaseUtil(this, user, targetUser, userUuid, targetUuid);
        chatFirebaseUtil.setchatRoom(mAdapter);

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
    protected void onDestroy() {
        super.onDestroy();
        chatFirebaseUtil.deleteFirebaseRef();
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
        switch (id){
            case R.id.profile:
                Intent p = new Intent(ChattingActivity.this, FullImageActivity.class);
                p.putExtra("item", chatFirebaseUtil.getItem());
                p.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ChattingActivity.this.startActivity(p);
                break;
            case R.id.block:
                // 다이얼로그
                UiUtil.getInstance().showDialog(ChattingActivity.this, "유저 차단", "해당 유저를 차단하시겠습니까?", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        UiUtil.getInstance().startProgressDialog(ChattingActivity.this);
                        // blockUsers 추가
                        FireBaseUtil.getInstance().block(chatFirebaseUtil.getItem().getUuid()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finish();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                UiUtil.getInstance().stopProgressDialog();
                                Intent p = new Intent(ChattingActivity.this, MessageActivity.class);
                                p.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                ChattingActivity.this.startActivity(p);
                            }
                        });
                    }
                }, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                break;
            case R.id.report:
                Toast.makeText(this,"report",-1).show();
                break;
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
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        ArrayList<String> menulist = new ArrayList<String>();
        menulist.add("profile");menulist.add("block");menulist.add("report");

        int positionOfMenuItem = 0;
        for (String name: menulist) {
            MenuItem item = menu.getItem(positionOfMenuItem++);
            SpannableString s = new SpannableString(name);
            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
            item.setTitle(s);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    ChatMessageAdapter.OnCallbackList litener = new ChatMessageAdapter.OnCallbackList() {
        @Override
        public void onEvent() {
            mListView.setSelection(mListView.getAdapter().getCount());
        }
    };

    AbsListView.OnScrollListener  detectTopPosition= new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(firstVisibleItem == 0 && view.getChildAt(0) != null && view.getChildAt(0).getTop() == 0){
                chatFirebaseUtil.addChatLog();

            }
        }
    };

}

