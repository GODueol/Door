package com.teamcore.android.core.MessageActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;
import com.teamcore.android.core.Entity.User;
import com.teamcore.android.core.Event.TargetUserBlocksMeEvent;
import com.teamcore.android.core.Exception.ChildSizeMaxException;
import com.teamcore.android.core.Exception.NotSetAutoTimeException;
import com.teamcore.android.core.MessageActivity.ChattingMessageAdapter.OnImesageLoadingCallback;
import com.teamcore.android.core.MessageActivity.util.MessageVO;
import com.teamcore.android.core.PeopleFragment.FullImageActivity;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.BaseActivity.BlockBaseActivity;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.FireBaseUtil;
import com.teamcore.android.core.Util.GalleryPick;
import com.teamcore.android.core.Util.UiUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ChattingActivity extends BlockBaseActivity {

    private static final int REQUEST_FIRST_ENTER = 1;

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
    private RewardedVideoAd mRewardedVideoAd;
    private InterstitialAd noFillInterstitialAd;
    boolean isFillReward = false;

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
        // 상대방 데이터 셋
        targetUser = (User) p.getSerializableExtra("user");
        targetUuid = (String) p.getSerializableExtra("userUuid");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getApplicationContext());
        loadRewardedVideoAd();
        setnoFillInterstitialAd();
        // 엑티비티 Uuid 저장
        SPUtil.setBlockMeUserCurrentActivity(getString(R.string.currentActivity), targetUuid);
        // 내정보 데이터 셋
        mAuth = FirebaseAuth.getInstance();
        user = getUser();
        userUuid = mAuth.getUid();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

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

        // TeamCore 메세지의 경우
        if (targetUuid.equals(getApplicationContext().getString(R.string.TeamCore))) {
            send_message_layout.setVisibility(View.GONE);
        }
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

        // 블락이면 안들어가지게
        chatFirebaseUtil = new ChatFirebaseUtil(this, user, targetUser, userUuid, targetUuid, overlay, hideText);
        chatFirebaseUtil.setchatRoom(chattingRecyclerview, chatListItem);
        chattingRecyclerview.addOnScrollListener(dateToastListener);

        // 메세지 보내기
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mEditTextMessage.getText().toString();

                if (TextUtils.isEmpty(message.replace(System.getProperty("line.separator"), "").replace(" ", ""))) {
                    return;
                }
                long currentTime = 0;
                try {
                    currentTime = UiUtil.getInstance().getCurrentTime(ChattingActivity.this);
                    writeMessage(null, userUuid, user.getId(), message, currentTime, 1);
                } catch (NotSetAutoTimeException e) {
                    e.printStackTrace();
                    Toast.makeText(ChattingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    ActivityCompat.finishAffinity(ChattingActivity.this);
                }

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

        mRewardedVideoAd.resume(this);
        mRewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener);
        super.onResume();
        mImageView.setClickable(true);
    }

    @Override
    protected void onDestroy() {
        mRewardedVideoAd.destroy(this);
        mRewardedVideoAd.setRewardedVideoAdListener(null);
        super.onDestroy();
        SPUtil.removeCurrentChat(getString(R.string.currentRoom));
        chatFirebaseUtil.deleteFirebaseRef();
        FireBaseUtil.getInstance().queryBlockWithMe(targetUuid, new FireBaseUtil.BlockListener() {
            @Override
            public void isBlockCallback(boolean isBlockWithMe) {
                if (isBlockWithMe) {
                    Log.d("test", "ture");
                    chatFirebaseUtil.clearChatLog();
                } else {
                    Log.d("test", "false");
                    chatFirebaseUtil.setLastChatView();
                }
            }
        });
    }

    private void writeMessage(String image, String userId, String nickname, String content, long currentTime, int check) throws NotSetAutoTimeException {
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
                ChattingActivity.this.startActivity(p);
                break;
            case R.id.block:
                // 다이얼로그
                UiUtil.getInstance().showDialog(ChattingActivity.this, "유저 차단", "해당 유저를 차단하시겠습니까?", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        checkCorePlus().done(isPlus -> {
                            if (!isPlus) {
                                FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid(getApplication())).child(getString(R.string.blockCount)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        int value;
                                        try {
                                            value = Integer.valueOf(dataSnapshot.getValue().toString());
                                        } catch (Exception e) {
                                            value = 0;
                                        }
                                        if (value > 0) {
                                            FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid(getApplication())).child(getString(R.string.blockCount)).setValue(value - 1);

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
                                                        finish();
                                                    }
                                                });
                                            } catch (ChildSizeMaxException e) {
                                                Toast.makeText(ChattingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                UiUtil.getInstance().stopProgressDialog();
                                            }
                                        } else {
                                            if (isFillReward) {
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
                                                            finish();
                                                        }
                                                    });
                                                } catch (ChildSizeMaxException e) {
                                                    Toast.makeText(ChattingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    UiUtil.getInstance().stopProgressDialog();
                                                }
                                                noFillInterstitialAd.show();
                                            } else {
                                                mRewardedVideoAd.show();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            } else {
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
                                            finish();
                                        }
                                    });
                                } catch (ChildSizeMaxException e) {
                                    Toast.makeText(ChattingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    UiUtil.getInstance().stopProgressDialog();
                                }
                            }
                        });

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
        if (!targetUuid.equals(getApplicationContext().getString(R.string.TeamCore))) {
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

    @Subscribe
    public void FinishActivity(TargetUserBlocksMeEvent someoneBlocksMeEvent) {
        finish();
    }

    public void setnoFillInterstitialAd() {
        noFillInterstitialAd = new InterstitialAd(this);
        noFillInterstitialAd.setAdUnitId(getString(R.string.noFillReward));
        noFillInterstitialAd.loadAd(new AdRequest.Builder().build());
        noFillInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                noFillInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(getString(R.string.adsBlockUser),
                new AdRequest.Builder()
                        .build());
        mRewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener);
    }

    RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {
        @Override
        public void onRewardedVideoAdLoaded() {
            Log.d("test", "onRewardedVideoAdLoaded");
        }

        @Override
        public void onRewardedVideoAdOpened() {
            Log.d("test", "onRewardedVideoAdOpened" +
                    "");
        }

        @Override
        public void onRewardedVideoStarted() {
            Log.d("test", "onRewardedVideoStarted");
        }

        @Override
        public void onRewardedVideoAdClosed() {
            loadRewardedVideoAd();
            FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid(getApplication())).child(getString(R.string.blockCount)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int value;
                    try {
                        value = Integer.valueOf(dataSnapshot.getValue().toString());
                    } catch (Exception e) {
                        value = 0;
                    }
                    Log.d("test", "몇개 : " + value);
                    if (value > 0) {
                        FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid(getApplication())).child(getString(R.string.blockCount)).setValue(value - 1);

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
                                    finish();
                                }
                            });
                        } catch (ChildSizeMaxException e) {
                            Toast.makeText(ChattingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            UiUtil.getInstance().stopProgressDialog();
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Log.d("test", "onRewardedVideoAdClosed");
        }

        @Override
        public void onRewarded(RewardItem rewardItem) {
            FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid(getApplication())).child(getString(R.string.blockCount)).setValue(rewardItem.getAmount());
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {

            Log.d("test", "onRewardedVideoAdLeftApplication");
        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int i) {
            Toast.makeText(getApplicationContext(), "에러코드chattingList" + String.valueOf(i), Toast.LENGTH_LONG).show();
            switch (i) {
                case 0:
                    // 에드몹 내부서버에러
                    Toast.makeText(getApplicationContext(), "내부서버에 문제가 있습니다.", Toast.LENGTH_LONG).show();
                    loadRewardedVideoAd();
                    break;
                case 2:
                    // 네트워크 연결상태 불량
                    Toast.makeText(getApplicationContext(), "네트워크 연결상태가 좋지 않습니다.", Toast.LENGTH_LONG).show();
                    loadRewardedVideoAd();
                    break;
                case 3:
                    // 에드몹 광고 인벤토리 부족
                    isFillReward = true;
                    break;
            }
        }
    };

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        mRewardedVideoAd.setRewardedVideoAdListener(null);
        super.onPause();
    }

}