package com.teamdoor.android.door.Chatting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;
import com.teamdoor.android.door.Chatting.ChattingMessageAdapter.OnImesageLoadingCallback;
import com.teamdoor.android.door.Entity.ChatMessage;
import com.teamdoor.android.door.Entity.MessageVO;
import com.teamdoor.android.door.Entity.User;
import com.teamdoor.android.door.Event.TargetUserBlocksMeEvent;
import com.teamdoor.android.door.Exception.ChildSizeMaxException;
import com.teamdoor.android.door.Exception.NotSetAutoTimeException;
import com.teamdoor.android.door.PeopleFragment.FullImageActivity;
import com.teamdoor.android.door.PeopleFragment.GridItem;
import com.teamdoor.android.door.R;
import com.teamdoor.android.door.Util.BaseActivity.BlockBaseActivity;
import com.teamdoor.android.door.Util.DataContainer;
import com.teamdoor.android.door.Util.FireBaseUtil;
import com.teamdoor.android.door.Util.GPSInfo;
import com.teamdoor.android.door.Util.GalleryPick;
import com.teamdoor.android.door.Util.SharedPreferencesUtil;
import com.teamdoor.android.door.Util.UiUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ChattingActivity extends BlockBaseActivity implements ChattingContract.View {

    private Toolbar toolbar = null;
    private RecyclerView chattingRecyclerview;
    private ImageButton mButtonSend, mImageView;
    private EditText mEditTextMessage;
    private LinearLayout send_message_layout, overlay, custom_top_container;
    private TextView hideText, topDateText;
    private ChattingMessageAdapter chattingMessageAdapter;
    private LinearLayoutManager linearLayoutManager;

    private List<String> chatKeyList;
    private List<ChatMessage> chatList;
    private List<ChatMessage> uncheckList;

    private FirebaseAuth mAuth;
    private InputMethodManager imm;
    private Animation hide, fadeout;

    private User user;
    private String userUuid;
    private User targetUser;
    private String targetUuid;
    private GalleryPick galleryPick;
    private RewardedVideoAd mRewardedVideoAd;
    private InterstitialAd noFillInterstitialAd;
    private GridItem Item;
    boolean isFillReward = false;
    private String room;
    /***************************************************************/

    private ChattingContract.Presenter mPresenter;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chatKeyList = new ArrayList<>();
        chatList = new ArrayList<>();
        uncheckList = new ArrayList<>();

        // 상대방 데이터 셋
        Intent p = getIntent();
        targetUser = (User) p.getSerializableExtra("user");
        targetUuid = (String) p.getSerializableExtra("userUuid");

        setLayout();
        // 광고 셋
        loadRewardedVideoAd();
        setnoFillInterstitialAd();

        // 엑티비티 Uuid 저장
        SPUtil.setBlockMeUserCurrentActivity(getString(R.string.currentActivity), targetUuid);

        // 내정보 데이터 셋
        mAuth = FirebaseAuth.getInstance();
        user = getUser();
        userUuid = mAuth.getUid();

        chattingRecyclerview.addOnScrollListener(dateToastListener);

        // 메세지 보내기
        mButtonSend.setOnClickListener(v -> {
            String message = mEditTextMessage.getText().toString();

            if (TextUtils.isEmpty(message.replace(System.getProperty("line.separator"), "").replace(" ", ""))) {
                return;
            }

            long currentTime = getCleanTime();
            writeMessage(room, null, userUuid, user.getId(), message, currentTime, 1);

            mEditTextMessage.setText("");
        });

        overlay.setOnClickListener(view -> chattingRecyclerview.scrollToPosition(chattingMessageAdapter.getItemCount() - 1));

        mImageView.setOnClickListener(v -> sendImageMessage());
        /******************************************/
        SPUtil = new SharedPreferencesUtil(this);
        new ChattingPresenter(this, SPUtil);


        Item = mPresenter.setUserInfo(targetUser, targetUuid);
        mPresenter.setchatRoom(userUuid, targetUuid).subscribe(
                chatRoom -> {
                    room = chatRoom;
                    SPUtil.setCurrentChat(getString(R.string.currentRoom),room);
                    mPresenter.checkReadChat(chatRoom, userUuid);
                    mPresenter.getChattingLog(chatRoom, userUuid);
                }
        );

        chattingRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView absListView, int i) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.computeVerticalScrollOffset() == 0) {
                    mPresenter.getPastChattingLog(room, userUuid);
                }
            }
        });


    }


    public void setLayout() {
        Window window = getWindow();
        window.setContentView(R.layout.chatting_activity);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        TypedValue tv = new TypedValue();
        int actionBarHeight = 150;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        chattingRecyclerview = findViewById(R.id.listView);
        mButtonSend = findViewById(R.id.btn_send);
        mEditTextMessage = findViewById(R.id.et_message);
        mImageView = findViewById(R.id.iv_image);

        LayoutInflater inflater = (LayoutInflater) getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        LinearLayout linear = (LinearLayout) inflater.inflate(R.layout.chatting_list_overlay, null);
        LinearLayout.LayoutParams paramlinear = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        send_message_layout = findViewById(R.id.send_message_layout);
        send_message_layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int layout_height = send_message_layout.getMeasuredHeight();
        paramlinear.setMargins(0, actionBarHeight + 5, 0, layout_height);
        window.addContentView(linear, paramlinear);

        custom_top_container = findViewById(R.id.custom_top_container);
        overlay = findViewById(R.id.scrollDown);
        topDateText = findViewById(R.id.topDate);
        hideText = findViewById(R.id.hideText);

        fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        hide = AnimationUtils.loadAnimation(this, R.anim.hide);

        // TeamCore 메세지의 경우
        if (targetUuid.equals(getApplicationContext().getString(R.string.TeamCore))) {
            send_message_layout.setVisibility(View.GONE);
        }

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chattingMessageAdapter = new ChattingMessageAdapter(chatList, litener);
        chattingRecyclerview.setAdapter(chattingMessageAdapter);
        chattingRecyclerview.setLayoutManager(linearLayoutManager);
        chattingRecyclerview.setOnTouchListener(onTouchListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick.REQUEST_GALLERY && data != null && data.getData() != null) {

            try {
                galleryPick.invoke(data);
                mPresenter.sendImageMessage(room, user.getId(), userUuid, targetUuid, galleryPick);
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
//        chatFirebaseUtil.Resume();
        super.onResume();
        mImageView.setClickable(true);
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
//        chatFirebaseUtil.Pause();
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        mRewardedVideoAd.destroy(this);
        mRewardedVideoAd.setRewardedVideoAdListener(null);
        mPresenter.removeDisposable();
        super.onDestroy();
        SPUtil.removeCurrentChat(getString(R.string.currentRoom));
        FireBaseUtil.getInstance().queryBlockWithMe(targetUuid, isBlockWithMe -> {
            if (isBlockWithMe) {
                Log.d("test", "ture");
                mPresenter.clearChatLog(room, userUuid, targetUuid);
            } else {
                Log.d("test", "false");
                mPresenter.setLastChatView(userUuid, targetUuid);
            }
        });
    }

    private void writeMessage(String Room, String image, String userUuid, String nickname, String content, long currentTime, int check) {
        MessageVO message = new MessageVO(image, userUuid, nickname, content, currentTime, check);
        mPresenter.sendMessage(Room, nickname, userUuid, targetUuid, message);
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
                p.putExtra("item", Item);
                ChattingActivity.this.startActivity(p);
                break;
            case R.id.block:
                // 다이얼로그
                UiUtil.getInstance().showDialog(ChattingActivity.this, "유저 차단", "해당 유저를 차단하시겠습니까?", (dialog, whichButton) ->
                        checkCorePlus().addOnSuccessListener(isPlus -> {
                            if (!isPlus) {
                                Query query =  FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid(getApplication())).child(getString(R.string.blockCount));
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
                                                FireBaseUtil.getInstance().block(Item.getUuid())
                                                        .addOnSuccessListener(aVoid -> finish())
                                                        .addOnCompleteListener(task -> {
                                                            UiUtil.getInstance().stopProgressDialog();
                                                            finish();
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
                                                    FireBaseUtil.getInstance().block(Item.getUuid())
                                                            .addOnSuccessListener(aVoid -> finish())
                                                            .addOnCompleteListener(task -> {
                                                        UiUtil.getInstance().stopProgressDialog();
                                                        finish();
                                                    });
                                                } catch (ChildSizeMaxException e) {
                                                    Toast.makeText(ChattingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    UiUtil.getInstance().stopProgressDialog();
                                                }
                                                noFillInterstitialAd.show();
                                            } else {
                                                if (mRewardedVideoAd.isLoaded()) {
                                                    mRewardedVideoAd.show();
                                                }
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
                                    FireBaseUtil.getInstance().block(Item.getUuid())
                                            .addOnSuccessListener(aVoid -> finish())
                                            .addOnCompleteListener(task -> {
                                                UiUtil.getInstance().stopProgressDialog();
                                                finish();
                                            });
                                } catch (ChildSizeMaxException e) {
                                    Toast.makeText(ChattingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    UiUtil.getInstance().stopProgressDialog();
                                }
                            }
                        }), (dialog, whichButton) -> {
                });
                break;
            case android.R.id.home:
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
            chattingRecyclerview.smoothScrollToPosition(View.FOCUS_DOWN);
        }

        @Override
        public void onRemove(String parent, int i) {
            mPresenter.removeImeageMessage(room, parent, i);
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
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getApplicationContext());
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
                            FireBaseUtil.getInstance().block(Item.getUuid()).addOnSuccessListener(new OnSuccessListener<Void>() {
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
            loadRewardedVideoAd();
            FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid(getApplication())).child(getString(R.string.blockCount)).setValue(rewardItem.getAmount());
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {
            Log.d("test", "onRewardedVideoAdLeftApplication");
        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int i) {
            //Toast.makeText(getApplicationContext(), "에러코드chattingList" + String.valueOf(i), Toast.LENGTH_LONG).show();
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

        @Override
        public void onRewardedVideoCompleted() {

        }
    };

    @Override
    public Long getCleanTime() {
        try {
            return UiUtil.getInstance().getCurrentTime(this);
        } catch (NotSetAutoTimeException e) {
            finish();
            return null;
        }
    }

    @Override
    public void ToastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /*******************************View Impelements*******************************/
    @Override
    public void refreshChatLogView() {
        chattingRecyclerview.getRecycledViewPool().clear();
        chattingMessageAdapter.notifyDataSetChanged();
    }

    @Override
    public String getResourceAlert() {
        return getString(R.string.alertChat);
    }

    @Override
    public List<ChatMessage> getChatList() {
        return chatList;
    }

    @Override
    public List<String> getChatKeyList() {
        return chatKeyList;
    }

    @Override
    public List<ChatMessage> getUnCheckList() {
        return uncheckList;
    }

    @Override
    public void setScrollControl(String message, boolean isMine, boolean isImage) {
        chattingRecyclerview.post(() -> {
            int pos = chattingMessageAdapter.getItemCount() - 1;
            LinearLayoutManager lm = (LinearLayoutManager) chattingRecyclerview.getLayoutManager();
            int visiblieCompLastPosition = lm.findLastVisibleItemPosition();

            if (pos - 2 <= visiblieCompLastPosition) {
                //맨마지막에서 2이내에 있을경우
                chattingRecyclerview.scrollToPosition(pos);
            } else if (!isMine && !isImage) {
                // 내것이 아니고 텍스트
                showBottomOverlay(message);
            } else if (!isMine) {
                // 내것이 아니고 이미지
                showBottomOverlay("사진");
            } else {
                // 내 메세지일경우
                chattingRecyclerview.scrollToPosition(chattingMessageAdapter.getItemCount() - 1);
            }
        });
    }

    public void showBottomOverlay(String str) {
        hideText.setText(str);
        overlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void scrollToPosition(int position) {
        chattingRecyclerview.scrollToPosition(position);
    }


    @Override
    public int findFirstCompletelyVisibleItemPosition() {
        return linearLayoutManager.findFirstCompletelyVisibleItemPosition();
    }

    @Override
    public int findLastCompletelyVisibleItemPosition() {
        return linearLayoutManager.findLastCompletelyVisibleItemPosition();
    }

    @Override
    public Location getLocation() {
        return GPSInfo.getmInstance(this).getGPSLocation();
    }

    @Override
    public GridItem getGridItem() {
        return Item;
    }

    @Override
    public void setGridItemDistance(float distance) {
        Item.setDistance(distance);
    }

    @Override
    public void setPresenter(ChattingContract.Presenter presenter) {
        mPresenter = presenter;
    }
}