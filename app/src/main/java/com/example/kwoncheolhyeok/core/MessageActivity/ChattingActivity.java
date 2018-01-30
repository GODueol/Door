package com.example.kwoncheolhyeok.core.MessageActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.Exception.ChildSizeMaxException;
import com.example.kwoncheolhyeok.core.MessageActivity.util.MessageVO;
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
    private RecyclerView chattingRecyclerview;
    private ImageButton mButtonSend;
    private EditText mEditTextMessage;
    private ImageButton mImageView;
    public Toast mToast;
    private ChattingMessageAdapter chattingMessageAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<ChatMessage> chatListItem;
    private FirebaseAuth mAuth;
    InputMethodManager imm;

    ChatFirebaseUtil chatFirebaseUtil;

    private User user;
    private String userUuid;
    private User targetUser;
    private String targetUuid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToastMessage();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        Intent p = getIntent();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mAuth = FirebaseAuth.getInstance();
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        chattingRecyclerview = (RecyclerView) findViewById(R.id.listView);
        mButtonSend = (ImageButton) findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mImageView = (ImageButton) findViewById(R.id.iv_image);
        chatListItem = new ArrayList<ChatMessage>();

        linearLayoutManager = new LinearLayoutManager(this);
        chattingMessageAdapter = new ChattingMessageAdapter(chatListItem,litener);
        chattingRecyclerview.setAdapter(chattingMessageAdapter);
        chattingRecyclerview.setLayoutManager(linearLayoutManager);
        chattingRecyclerview.setOnTouchListener(onTouchListener);
        // 상대방 데이터 셋
        targetUser = (User) p.getSerializableExtra("user");
        targetUuid = (String) p.getSerializableExtra("userUuid");
        // 내정보 데이터 셋
        user = DataContainer.getInstance().getUser();
        userUuid = mAuth.getUid();

        chatFirebaseUtil = new ChatFirebaseUtil(this, user, targetUser, userUuid, targetUuid);
        chatFirebaseUtil.setchatRoom(chattingRecyclerview,chatListItem);
        chattingRecyclerview.addOnScrollListener(dateToastListener);
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

    public void setToastMessage(){
        TypedValue tv = new TypedValue();
        int actionBarHeight = 150;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        mToast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
        ViewGroup group = (ViewGroup) mToast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        mToast.setGravity(Gravity.TOP,0,actionBarHeight+5);
        messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        messageTextView.setTextColor(Color.WHITE);
        group.setBackgroundColor(Color.rgb(60,60,60));
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
                        try {
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
                        } catch (ChildSizeMaxException e) {
                            Toast.makeText(ChattingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            UiUtil.getInstance().stopProgressDialog();
                        }
                    }
                }, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                break;
            case R.id.report:
                Toast.makeText(this,"report",Toast.LENGTH_SHORT).show();
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

    ChattingMessageAdapter.OnCallbackList litener = new ChattingMessageAdapter.OnCallbackList() {
        @Override
        public void onEvent() {
            // 처음으로 가려는 리스너
            //chattingRecyclerview.scrollToPosition(chattingMessageAdapter.getItemCount()-1);
            chattingRecyclerview.smoothScrollToPosition(View.FOCUS_DOWN);
            chattingRecyclerview.scrollToPosition(chattingMessageAdapter.getItemCount()-1);
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (imm != null) {
                imm.hideSoftInputFromWindow(mEditTextMessage.getWindowToken(), 0);
            }
            chattingMessageAdapter.deletRequestListener();
            return false;
        }
    };

    RecyclerView.OnScrollListener dateToastListener= new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView absListView, int i) {
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int lastVisibleItemPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if(lastVisibleItemPosition!=0){
                lastVisibleItemPosition-=1;
            }
            String toastString = chattingMessageAdapter.getDate(lastVisibleItemPosition);
            mToast.setText(toastString);
            mToast.show();
        }
    };

}