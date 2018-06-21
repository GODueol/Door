package com.teamcore.android.core.PeopleFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;
import com.teamcore.android.core.Entity.User;
import com.teamcore.android.core.Event.TargetUserBlocksMeEvent;
import com.teamcore.android.core.Exception.ChildSizeMaxException;
import com.teamcore.android.core.Exception.NotSetAutoTimeException;
import com.teamcore.android.core.MessageActivity.ChattingActivity;
import com.teamcore.android.core.PeopleFragment.FullImageViewPager.DetailImageActivity;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.BaseActivity.BlockBaseActivity;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.FireBaseUtil;
import com.teamcore.android.core.Util.FirebaseSendPushMsg;
import com.teamcore.android.core.Util.GPSInfo;
import com.teamcore.android.core.Util.GlideApp;
import com.teamcore.android.core.Util.UiUtil;
import com.teamcore.android.core.WaterMark.ScreenshotSetApplication;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.teamcore.android.core.MessageActivity.ChatFirebaseUtil.sendEventMessage;


public class FullImageActivity extends BlockBaseActivity implements View.OnClickListener {

    Toolbar toolbar = null;

    RelativeLayout core_enter = null;
    ImageView page1, page2, page3, page4;

    @Bind(R.id.text_physical)
    TextView textPhysical;

    @Bind(R.id.text_introduce)
    TextView textIntroduce;

    @Bind(R.id.text_id)
    TextView textId;

    @Bind(R.id.full_image_view)
    ImageView fullImageView;

    @Bind(R.id.distance)
    TextView distanceText;

    @Bind(R.id.login_time)
    TextView loginTime;
    private ArrayList<String> picUrlList;

    @Bind(R.id.pic_open)
    ImageView picOpen;

    @Bind(R.id.block_friends)
    ImageView blockFriends;

    @Bind(R.id.item_menu_btn)
    ImageView addFriends;

    @Bind(R.id.message_white)
    ImageView message;

    @Bind(R.id.core_counts)
    TextView corePostCount;

    @Bind(R.id.image2lock)
    ImageView image2lock;

    @Bind(R.id.image3lock)
    ImageView image3lock;

    @Bind(R.id.image4lock)
    ImageView image4lock;

    private User oUser;
    private ValueEventListener listener;
    private DatabaseReference oUserRef;
    private GridItem item;

    User mUser;
    private RewardedVideoAd mRewardedVideoAd;
    private InterstitialAd mInterstitialAd;

    @SuppressLint("DefaultLocale")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image_activity_main);
        ScreenshotSetApplication.getInstance().allowUserSaveScreenshot(true);

        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);
        setmInterstitialAd();

        //Tab Fragment 1에서 받아온 썸네일 이미지를 이 액티비티로 받아옴
        page1 = (ImageView) findViewById(R.id.image1);
        page1.setOnClickListener(this);
        page2 = (ImageView) findViewById(R.id.image2);
        page2.setOnClickListener(this);
        page3 = (ImageView) findViewById(R.id.image3);
        page3.setOnClickListener(this);
        page4 = (ImageView) findViewById(R.id.image4);
        page4.setOnClickListener(this);

        Intent p = getIntent();
        item = (GridItem) p.getSerializableExtra("item");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getApplicationContext());
        loadRewardedVideoAd();
        // 엑티비티 Uuid 저장
        SPUtil.setBlockMeUserCurrentActivity(getString(R.string.currentActivity), item.getUuid());
        DataContainer.getInstance().getMyUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);

                // 차단된 경우
                if (mUser != null && mUser.getBlockMeUsers().containsKey(item.getUuid())) {
                    Toast.makeText(FullImageActivity.this, "Block", Toast.LENGTH_SHORT).show();
                    finish();
                }

                setViewedMeUsers(item);
                // 리스너를 달아서 실시간 정보 변경
                listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // viewedMeUser
                        oUser = dataSnapshot.getValue(User.class);
                        if(oUser.getSummaryUser() == null) finish();
                        item.setSummaryUser(oUser.getSummaryUser());
                        setView(item);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        UiUtil.getInstance().stopProgressDialog();
                    }
                };
                oUserRef = DataContainer.getInstance().getUserRef(item.getUuid());
                oUserRef.addValueEventListener(listener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setView(final GridItem item) {
        if (item.getUuid().equals(DataContainer.getInstance().getUid())) {  // 본인
            message.setVisibility(View.INVISIBLE);  // 가림
        } else {
            message.setOnClickListener(v -> {
                if (DataContainer.getInstance().isBlockWithMe(item.getUuid())) return;
                Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
                intent.putExtra("user", oUser);
                intent.putExtra("userUuid", item.getUuid());
                intent.putExtra("userPicuri", item.getPicUrl());
                startActivity(intent);
                checkCorePlus().done(isPlus -> {
                    if (!isPlus) {
                        SPUtil.increaseAds(mInterstitialAd, "ProfileChat");
                    }
                });
            });
        }
        //개인 화면에서 코어 액티비티로 넘어감
        core_enter = (RelativeLayout) findViewById(R.id.core_enter_layout);
        core_enter.setOnClickListener(v -> {
            if (DataContainer.getInstance().isBlockWithMe(item.getUuid())) return;
            UiUtil.getInstance().goToCoreActivity(FullImageActivity.this, item.getUuid());
        });

        // 개인정보 Set
        textId.setText(oUser.getId());
        textPhysical.setText(UiUtil.getInstance().setSubProfile(oUser));
        textIntroduce.setText(oUser.getIntro());

        // 코어 카운트
        corePostCount.setText(Integer.toString(oUser.getSummaryUser().getCorePostCount()));

        // 거리 Set
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(FireBaseUtil.currentLocationPath);
        GeoFire geoFire = new GeoFire(ref);
        final Location location = GPSInfo.getmInstance(getApplication()).getGPSLocation();
        geoFire.getLocation(item.getUuid(), new LocationCallback() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onLocationResult(String s, GeoLocation geoLocation) {
                Location targetLocation = new Location("");//provider name is unnecessary
                targetLocation.setLatitude(geoLocation.latitude);//your coords of course
                targetLocation.setLongitude(geoLocation.longitude);
                final float distance = location.distanceTo(targetLocation);
                distanceText.setText(String.format("%.0f", distance / 1000));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // 로그인 시간
        if (oUser.getLoginDate() != 0) {
            try {
                loginTime.setText(DataContainer.getInstance().convertBeforeFormat(oUser.getLoginDate(), FullImageActivity.this));
            } catch (NotSetAutoTimeException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                ActivityCompat.finishAffinity(FullImageActivity.this);
            }
        }

        // 사진 출력
        ImageView profilePics[] = {page1, page2, page3, page4};
        ImageView imagelocks[] = {null, image2lock, image3lock, image4lock};
        picUrlList = oUser.getPicUrls().toNotNullArrayThumbNail(oUser.getIsLockPics(), oUser.getUnLockUsers(), item.getUuid());
        int i;
        for (i = 0; i < picUrlList.size(); i++) {
            String url = picUrlList.get(i);
            if (url == null) continue;

            GlideApp.with(getBaseContext())
                    .load(url)
                    .placeholder(R.drawable.a)
                    .into(profilePics[i]);

            // Lock인 사진 표현
            if (i == 0) continue;
            if (url.equals(oUser.getPicUrls().getThumbNail_picUrl2())) {
                if (oUser.getIsLockPics().getIsLockPic2())
                    imagelocks[i].setVisibility(View.VISIBLE);
                else imagelocks[i].setVisibility(View.INVISIBLE);
            } else if (url.equals(oUser.getPicUrls().getThumbNail_picUrl3())) {
                if (oUser.getIsLockPics().getIsLockPic3())
                    imagelocks[i].setVisibility(View.VISIBLE);
                else imagelocks[i].setVisibility(View.INVISIBLE);
            } else if (url.equals(oUser.getPicUrls().getThumbNail_picUrl4())) {
                if (oUser.getIsLockPics().getIsLockPic4())
                    imagelocks[i].setVisibility(View.VISIBLE);
                else imagelocks[i].setVisibility(View.INVISIBLE);
            }
        }

        for (; i < profilePics.length; i++) {
            profilePics[i].setImageResource(R.drawable.a);
            if (imagelocks[i] != null) imagelocks[i].setVisibility(View.INVISIBLE);
        }

        // 큰사진
        GlideApp.with(getBaseContext())
                .load(oUser.getPicUrls().getPicUrl1())
                .placeholder(null)
                .into(fullImageView);

        // 사진 잠금 해제
        setPicLock(item);

        // 블락
        setBlock(item);

        // 팔로우
        setFollowing(item);


    }

    public void setViewedMeUsers(final GridItem item) {

        final String mUuid = DataContainer.getInstance().getUid();
        if (item.getUuid().equals(DataContainer.getInstance().getUid(FullImageActivity.this)))
            return; // 자신일 경우

        // 트랜잭션을 이용해야함
        final DatabaseReference viewedMeUsersRef = DataContainer.getInstance().getUserRef(item.getUuid()).child("viewedMeUsers");
        viewedMeUsersRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Map map = (Map) mutableData.getValue();

                if (map == null) {
                    return Transaction.success(mutableData);
                }

                if (!map.containsKey(mUuid)) {
                    while (map.size() >= DataContainer.ViewedMeMax) {
                        // 데이터 삭제
                        MutableData min = null;

                        for (MutableData mutableChild : mutableData.getChildren()) {
                            if (min == null || min.getValue(Long.class) > mutableChild.getValue(Long.class)) {
                                min = mutableChild;
                            }
                        }

                        assert min != null;
                        map.remove(min.getKey());
                        mutableData.setValue(map);
                    }
                }

                mutableData.setValue(map);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(getClass().getName(), "viewedMeUsersTransaction:onComplete:" + databaseError);

                // Viewed me
                FirebaseSendPushMsg.sendPostToFCM("View", item.getUuid(), mUser.getId(), "");
                // 데이터 추가
                try {
                    viewedMeUsersRef.child(mUuid).setValue(UiUtil.getInstance().getCurrentTime(FullImageActivity.this));
                } catch (NotSetAutoTimeException e) {
                    e.printStackTrace();
                    Toast.makeText(FullImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    ActivityCompat.finishAffinity(FullImageActivity.this);
                }
            }
        });

    }

    private void setFollowing(final GridItem item) {
        final String myUuid = DataContainer.getInstance().getUid();
        if (item.getUuid().equals(myUuid)) {  // 본인
            addFriends.setVisibility(View.INVISIBLE);  // 가림
            return;
        }
        if (oUser.getFollowerUsers().containsKey(myUuid)) {   //  이미 팔로우함
            addFriends.setImageResource(R.drawable.unfollow); // 팔로우 취소 버튼
        } else {
            addFriends.setImageResource(R.drawable.follow); // 팔로우 버튼
        }

        addFriends.setOnClickListener(view -> {
            if (DataContainer.getInstance().isBlockWithMe(item.getUuid())) return;
            String title, message;
            final boolean isFollow = oUser.getFollowerUsers().containsKey(myUuid);  // 이미 팔로우한 유저
            if (isFollow) {
                title = "팔로잉 취소";
                message = "이 회원을 팔로잉 하지않습니다.";
            } else {
                title = "팔로잉";
                message = "이 회원을 팔로잉 합니다. 서로 팔로잉하면 친구가 됩니다.";
            }

            UiUtil.getInstance().showDialog(FullImageActivity.this, title, message, (dialog, whichButton) -> {
                UiUtil.getInstance().startProgressDialog(FullImageActivity.this);

                // 내 following 추가, 유저 follower c추가
                Task<Void> task = null;
                try {
                    try {
                        task = FireBaseUtil.getInstance().follow(FullImageActivity.this, oUser, item.getUuid(), isFollow);
                    } catch (NotSetAutoTimeException e) {
                        e.printStackTrace();
//                        Toast.makeText(FullImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        ActivityCompat.finishAffinity(FullImageActivity.this);
                    }
                } catch (ChildSizeMaxException e) {
//                    Toast.makeText(FullImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    UiUtil.getInstance().stopProgressDialog();
                    return;
                }
                if (task == null) {
//                    Toast.makeText(getBaseContext(), "오류 발생", Toast.LENGTH_SHORT).show();
                    UiUtil.getInstance().stopProgressDialog();
                    return;
                }
                task.addOnSuccessListener(aVoid -> {
                    if (oUser.getFollowerUsers().containsKey(myUuid)) {   //  이미 팔로우함
                        addFriends.setImageResource(R.drawable.unfollow); // 팔로잉 취소 버튼
                    } else {
                        addFriends.setImageResource(R.drawable.follow); // 팔로잉 버튼
                    }
                }).addOnCompleteListener(task1 -> UiUtil.getInstance().stopProgressDialog());

            }, (dialog, whichButton) -> {
            });
        });
    }

    private void setBlock(final GridItem item) {
        final String myUuid = DataContainer.getInstance().getUid();
        if (item.getUuid().equals(myUuid)) {  // 본인
            blockFriends.setVisibility(View.INVISIBLE);  // 가림
            return;
        }
        blockFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataContainer.getInstance().isBlockWithMe(item.getUuid())) return;

                new BlockReportSelectDialog(FullImageActivity.this, new BlockReportSelectDialog.BlockReportSelectDialogListener() {
                    @Override
                    public void block() {
                        // show block dialog
                        showBlockDialog();
                    }

                    @Override
                    public void report() {
                        new ReportDialog(FullImageActivity.this, item.getUuid()).show();
                    }
                }).show();

            }

            private void showBlockDialog() {
                UiUtil.getInstance().showDialog(FullImageActivity.this, "회원 차단", "이 회원을 차단합니다.", (dialog, whichButton) -> {
                    if (mUser.getBlockUsers().size() >= DataContainer.ChildrenMax) {
                        Toast.makeText(FullImageActivity.this,"차단 가능한 회원 수를 초과하였습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
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
                            FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid()).child(getString(R.string.blockCount)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        int value = Integer.valueOf(dataSnapshot.getValue().toString());
                                        Log.d("test", "몇개 : " + value);
                                        if (value > 0) {
                                            FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid()).child(getString(R.string.blockCount)).setValue(value - 1);
                                            UiUtil.getInstance().startProgressDialog(FullImageActivity.this);
                                            // blockUsers 추가
                                            try {
                                                FireBaseUtil.getInstance().block(item.getUuid()).addOnSuccessListener(aVoid -> finish()).addOnCompleteListener(task -> UiUtil.getInstance().stopProgressDialog());
                                            } catch (ChildSizeMaxException e) {
                                                Toast.makeText(FullImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                UiUtil.getInstance().stopProgressDialog();
                                            }
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
                            FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid()).child(getString(R.string.blockCount)).setValue(rewardItem.getAmount());
                        }

                        @Override
                        public void onRewardedVideoAdLeftApplication() {

                            Log.d("test", "onRewardedVideoAdLeftApplication");
                        }

                        @Override
                        public void onRewardedVideoAdFailedToLoad(int i) {
                            Log.d("test", "onRewardedVideoAdFailedToLoad" + i);
                        }
                    });

                    checkCorePlus().done(isPlus -> {
                        if (!isPlus) {
                            FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid()).child(getString(R.string.blockCount)).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                        FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid()).child(getString(R.string.blockCount)).setValue(value - 1);
                                        UiUtil.getInstance().startProgressDialog(FullImageActivity.this);
                                        // blockUsers 추가
                                        try {
                                            FireBaseUtil.getInstance().block(item.getUuid()).addOnSuccessListener(aVoid -> finish()).addOnCompleteListener(task -> UiUtil.getInstance().stopProgressDialog());
                                        } catch (ChildSizeMaxException e) {
                                            Toast.makeText(FullImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            UiUtil.getInstance().stopProgressDialog();
                                        }
                                    } else {
                                        mRewardedVideoAd.show();
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            UiUtil.getInstance().startProgressDialog(FullImageActivity.this);
                            // blockUsers 추가
                            try {
                                FireBaseUtil.getInstance().block(item.getUuid()).addOnSuccessListener(aVoid -> finish()).addOnCompleteListener(task -> UiUtil.getInstance().stopProgressDialog());
                            } catch (ChildSizeMaxException e) {
                                Toast.makeText(FullImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                UiUtil.getInstance().stopProgressDialog();
                            }
                        }
                    });
                });
            }
        });
    }

    private boolean isHaveLockPic() {
        for (boolean b : mUser.getIsLockPics().toArray()) {
            if (b) return true;
        }
        return false;
    }

    private void setPicLock(final GridItem item) {
        final String myUuid = DataContainer.getInstance().getUid();
        if (mUser == null) {
            UiUtil.getInstance().restartApp(this);
        }

        if (item.getUuid().equals(myUuid)) {  // 본인
            picOpen.setVisibility(View.INVISIBLE);  // 가림
            return;
        }

        // 잠근사진이 없으면 리턴
        if (!isHaveLockPic()) return;

        // 아이콘 크기 설정
        picOpen.getLayoutParams().width = (int) getResources().getDimension(R.dimen.image_lock_height);
        picOpen.getLayoutParams().height = (int) getResources().getDimension(R.dimen.image_lock_width);

        if (!mUser.getUnLockUsers().containsKey(item.getUuid())) {
            picOpen.setImageResource(R.drawable.picture_unlock); // "이 아이콘을 클릭하면 사진을 해제하겠다"
        } else {
            picOpen.setImageResource(R.drawable.picture_lock); // "이 아이콘을 클릭하면 사진을 잠그겠다"
        }
        picOpen.setOnClickListener(view -> {
            if (DataContainer.getInstance().isBlockWithMe(item.getUuid())) return;
            String title, message;
            final boolean isLock = !mUser.getUnLockUsers().containsKey(item.getUuid());  // 이미 해제한 유저
            if (isLock) {
                title = "사진 공개";
                message = "이 회원에게 당신이 잠근 사진을 공개하시겠습니까?";
            } else {
                title = "사진 잠금";
                message = "이 회원에게 당신의 사진을 잠그시겠습니까?";
            }

            UiUtil.getInstance().showDialog(FullImageActivity.this, title, message, (dialog, whichButton) -> {
                UiUtil.getInstance().startProgressDialog(FullImageActivity.this);
                if (isLock) {
                    try {
                        mUser.getUnLockUsers().put(item.getUuid(), UiUtil.getInstance().getCurrentTime(FullImageActivity.this)); // 해제
                    } catch (NotSetAutoTimeException e) {
                        e.printStackTrace();
                        Toast.makeText(FullImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        ActivityCompat.finishAffinity(FullImageActivity.this);
                    }

                } else {
                    mUser.getUnLockUsers().remove(item.getUuid());  // 잠금

                }
                DataContainer.getInstance().getUsersRef().child(myUuid).setValue(mUser)
                        .addOnSuccessListener(aVoid -> {
                            if (isLock) {
                                picOpen.setImageResource(R.drawable.picture_lock);    // 해제하기 (현재 사진이 잠겼다는 것을 암시함)
                                Toast.makeText(FullImageActivity.this, "잠긴 사진을 열었습니다.", Toast.LENGTH_SHORT).show();
                                sendEventMessage(myUuid, mUser.getId(), item.getUuid(), getString(R.string.alertUnlockPic), FullImageActivity.this);
                            } else {
                                picOpen.setImageResource(R.drawable.picture_unlock);  // 잠금 (현재 사진이 해제되어 있다는 암시함)
                                Toast.makeText(FullImageActivity.this, "사진을 비공개 합니다.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnCompleteListener(task -> UiUtil.getInstance().stopProgressDialog());
            }, (dialog, whichButton) -> {
            });
        });
    }

    // 뒤로가기 버튼 기능
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Action getAction() {
        return Actions.newView("FullImage Page", "http://[ENTER-YOUR-URL-HERE]");
    }

    @Override
    protected void onStart() {
        super.onStart();
   /* If you’re logging an action on an item that has already been added to the index,
   you don’t have to add the following update line. See
   https://firebase.google.com/docs/app-indexing/android/personal-content#update-the-index for
   adding content to the index */
        //FirebaseAppIndex.getInstance().update(getIndexable());
        FirebaseUserActions.getInstance().start(getAction());
    }

    @Override
    protected void onStop() {
        FirebaseUserActions.getInstance().end(getAction());
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        if (DataContainer.getInstance().isBlockWithMe(item.getUuid())) return;
        Intent myIntent = new Intent(getApplicationContext(), DetailImageActivity.class);

        // 큰 사진으로 넘김
        myIntent.putExtra("picUrlList", oUser.getPicUrls().toNotNullArray(oUser.getIsLockPics(), oUser.getUnLockUsers(), item.getUuid()));

        if (picUrlList.size() == 0) return;
        switch (view.getId()) {
            case R.id.image1:
                myIntent.putExtra("PagerPage", 0);
                startActivity(myIntent);
                break;
            case R.id.image2:
                myIntent.putExtra("PagerPage", 1);
                startActivity(myIntent);
                break;
            case R.id.image3:
                myIntent.putExtra("PagerPage", 2);
                startActivity(myIntent);
                break;
            case R.id.image4:
                myIntent.putExtra("PagerPage", 3);
                startActivity(myIntent);
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        ScreenshotSetApplication.getInstance().registerScreenshotObserver();
    }

    @Override
    public void onPause() {
        super.onPause();
        ScreenshotSetApplication.getInstance().unregisterScreenshotObserver();
    }

    @Override
    protected void onDestroy() {
        oUserRef.removeEventListener(listener);
        super.onDestroy();
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(getString(R.string.adsBlockUser),
                new AdRequest.Builder()
                        .build());
    }

    public void setmInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.adsFMainGrid));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }
//    @Subscribe
//    public void FinishActivity(TargetUserBlocksMeEvent someoneBlocksMeEvent){
//        Intent intent = new Intent(getApplication(), MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//    }

    @Subscribe
    public void FinishActivity(TargetUserBlocksMeEvent someoneBlocksMeEvent) {
        finish();
    }

}