package com.example.kwoncheolhyeok.core.MessageActivity;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.Exception.ChildSizeMaxException;
import com.example.kwoncheolhyeok.core.MessageActivity.ChattingMessageAdapter.OnImesageLoadingCallback;
import com.example.kwoncheolhyeok.core.MessageActivity.util.MessageVO;
import com.example.kwoncheolhyeok.core.PeopleFragment.FullImageActivity;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.example.kwoncheolhyeok.core.Util.GalleryPick;
import com.example.kwoncheolhyeok.core.Util.SharedPreferencesUtil;
import com.example.kwoncheolhyeok.core.Util.UiUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ChattingActivity extends AppCompatActivity {

    private Toolbar toolbar = null;
    private RecyclerView chattingRecyclerview;
    private ImageButton mButtonSend, mImageView;
    private EditText mEditTextMessage;
    private LinearLayout send_message_layout, overlay, custom_top_container;
    private TextView hideText, topDateText;
    private ChattingMessageAdapter chattingMessageAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<ChatMessage> chatListItem;
    private FirebaseAuth mAuth;
    private InputMethodManager imm;
    private Animation hide, fadeout;

    private ChatFirebaseUtil chatFirebaseUtil;

    private User user;
    private String userUuid;
    private User targetUser;
    private String targetUuid;
    private GalleryPick galleryPick;

    private SharedPreferencesUtil SPUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();

        window.setContentView(R.layout.chatting_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        TypedValue tv = new TypedValue();
        int actionBarHeight = 150;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        Intent p = getIntent();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        mAuth = FirebaseAuth.getInstance();
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        chattingRecyclerview = (RecyclerView) findViewById(R.id.listView);
        mButtonSend = (ImageButton) findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mImageView = (ImageButton) findViewById(R.id.iv_image);

        LayoutInflater inflater = (LayoutInflater) getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        LinearLayout linear = (LinearLayout) inflater.inflate(R.layout.chatting_list_overlay, null);
        LinearLayout.LayoutParams paramlinear = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        send_message_layout = (LinearLayout) findViewById(R.id.send_message_layout);
        send_message_layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int layout_height = send_message_layout.getMeasuredHeight();

        paramlinear.setMargins(0, actionBarHeight + 5, 0, layout_height);
        window.addContentView(linear, paramlinear);
        custom_top_container = (LinearLayout) findViewById(R.id.custom_top_container);
        overlay = (LinearLayout) findViewById(R.id.scrollDown);
        topDateText = (TextView) findViewById(R.id.topDate);
        hideText = (TextView) findViewById(R.id.hideText);

        fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        hide = AnimationUtils.loadAnimation(this, R.anim.hide);


        chatListItem = new ArrayList<ChatMessage>();
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chattingMessageAdapter = new ChattingMessageAdapter(chatListItem, litener);
        chattingRecyclerview.setAdapter(chattingMessageAdapter);
        chattingRecyclerview.setLayoutManager(linearLayoutManager);
        chattingRecyclerview.setOnTouchListener(onTouchListener);
        // 상대방 데이터 셋
        targetUser = (User) p.getSerializableExtra("user");
        targetUuid = (String) p.getSerializableExtra("userUuid");
        // 내정보 데이터 셋
        user = DataContainer.getInstance().getUser();
        userUuid = mAuth.getUid();

        SPUtil = new SharedPreferencesUtil(getApplicationContext());
        chatFirebaseUtil = new ChatFirebaseUtil(this, user, targetUser, userUuid, targetUuid, overlay, hideText);
        chatFirebaseUtil.setchatRoom(chattingRecyclerview, chatListItem);
        chattingRecyclerview.addOnScrollListener(dateToastListener);
        // 메세지 보내기
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();

                if (TextUtils.isEmpty(message.replace(System.getProperty("line.separator"),"").replace(" ",""))) {
                    return;
                }
                long currentTime = System.currentTimeMillis();
                writeMessage(null, userUuid, user.getId(), message, currentTime, 1);
                mEditTextMessage.setText("");
            }
        });

        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chattingRecyclerview.scrollToPosition(chattingMessageAdapter.getItemCount() - 1);
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
        if (requestCode == GalleryPick.REQUEST_GALLERY && data != null && data.getData() != null) {

            try {
                galleryPick.invoke(data);
                Uri outputFileUri = galleryPick.getUri();
                chatFirebaseUtil.sendImageMessage(outputFileUri, galleryPick);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(ChattingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ChattingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageView.setClickable(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SPUtil.removeCurrentChat(getString(R.string.currentRoom));
        chatFirebaseUtil.deleteFirebaseRef();
        chatFirebaseUtil.setLastChatView();
    }

    private void writeMessage(String image, String userId, String nickname, String content, long currentTime, int check) {
        MessageVO message = new MessageVO(image, userId, nickname, content, currentTime, check);
        chatFirebaseUtil.sendMessage(message);
    }

    private void sendImageMessage() {
        galleryPick = new GalleryPick(ChattingActivity.this).goToGallery();
        mImageView.setClickable(false);
    }

    // 뒤로가기 버튼 기능
    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        int id = item.getItemId();
        switch (id) {
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
        menulist.add("프로필");
        menulist.add("블럭");

        int positionOfMenuItem = 0;
        for (String name : menulist) {
            MenuItem item = menu.getItem(positionOfMenuItem++);
            SpannableString s = new SpannableString(name);
            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
            item.setTitle(s);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    OnImesageLoadingCallback litener = new OnImesageLoadingCallback() {
        @Override
        public void onReady() {
            // 처음으로 가려는 리스너
            //chattingRecyclerview.scrollToPosition(chattingMessageAdapter.getItemCount()-1);
            chattingRecyclerview.smoothScrollToPosition(View.FOCUS_DOWN);
            //chattingRecyclerview.scrollToPosition(View.FOCUS_DOWN);
        }

        @Override
        public void onRemove(String parent, int i) {
            chatFirebaseUtil.removeImeageMessage(parent, i);
        }
    };

    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            chattingMessageAdapter.deletRequestListener();
            if (imm != null) {
                imm.hideSoftInputFromWindow(mEditTextMessage.getWindowToken(), 0);
            }
            return false;
        }
    };

    RecyclerView.OnScrollListener dateToastListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView absListView, int i) {
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            int chattingSize = chattingMessageAdapter.getItemCount() - 1;

            try {
                String toastString = chattingMessageAdapter.getDate(lastVisibleItemPosition);
                topDateText.setText(toastString);
            } catch (Exception e) {
            }

            if (lastVisibleItemPosition != chattingSize) {
                custom_top_container.setVisibility(View.INVISIBLE);
                custom_top_container.startAnimation(fadeout);
            } else {
                overlay.setVisibility(View.GONE);
                custom_top_container.startAnimation(hide);
            }
        }
    };

}