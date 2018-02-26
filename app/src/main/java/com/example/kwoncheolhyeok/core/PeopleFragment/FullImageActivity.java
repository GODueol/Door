package com.example.kwoncheolhyeok.core.PeopleFragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.Exception.ChildSizeMaxException;
import com.example.kwoncheolhyeok.core.MessageActivity.ChattingActivity;
import com.example.kwoncheolhyeok.core.PeopleFragment.FullImageViewPager.DetailImageActivity;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.example.kwoncheolhyeok.core.Util.FirebaseSendPushMsg;
import com.example.kwoncheolhyeok.core.Util.GPSInfo;
import com.example.kwoncheolhyeok.core.Util.GlideApp;
import com.example.kwoncheolhyeok.core.Util.UiUtil;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.example.kwoncheolhyeok.core.MessageActivity.ChatFirebaseUtil.sendEventMessage;


public class FullImageActivity extends AppCompatActivity implements View.OnClickListener {

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


    @SuppressLint("DefaultLocale")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image_activity_main);

//        ScreenshotSetApplication.getInstance().allowUserSaveScreenshot(true);

        ButterKnife.bind(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);


        //Tab Fragment 1에서 받아온 썸네일 이미지를 이 액티비티로 받아옴
        page1 = findViewById(R.id.image1);
        page1.setOnClickListener(this);
        page2 = findViewById(R.id.image2);
        page2.setOnClickListener(this);
        page3 = findViewById(R.id.image3);
        page3.setOnClickListener(this);
        page4 = findViewById(R.id.image4);
        page4.setOnClickListener(this);

        Intent p = getIntent();
        item = (GridItem) p.getSerializableExtra("item");

        DataContainer.getInstance().getMyUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);

                // 차단된 경우
                if (mUser.getBlockMeUsers().containsKey(item.getUuid())) {
                    Toast.makeText(FullImageActivity.this, "당신은 차단되었습니다", Toast.LENGTH_SHORT).show();
                    finish();
                }

                setViewedMeUsers(item);
                // 리스너를 달아서 실시간 정보 변경
                listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // viewedMeUser
                        oUser = dataSnapshot.getValue(User.class);
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
            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
                    intent.putExtra("user", oUser);
                    intent.putExtra("userUuid", item.getUuid());
                    intent.putExtra("userPicuri", item.getPicUrl());
                    startActivity(intent);
                }
            });
        }
        //개인 화면에서 코어 액티비티로 넘어감
        core_enter = findViewById(R.id.core_enter_layout);
        core_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtil.getInstance().goToCoreActivity(FullImageActivity.this, item.getUuid());
            }
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
            loginTime.setText(DataContainer.getInstance().convertBeforeFormat(oUser.getLoginDate()));
        }

        // 사진 출력
        ImageView profilePics[] = {page1, page2, page3, page4};
        ImageView lockPics[] = {null, image2lock, image3lock, image4lock};
        picUrlList = oUser.getPicUrls().toNotNullArrayThumbNail(oUser.getIsLockPics(), oUser.getUnLockUsers(), item.getUuid());
        for (int i = 0; i < picUrlList.size(); i++) {
            String url = picUrlList.get(i);
            if (url == null) continue;

            // Lock인 사진 표현
            if(url.equals(oUser.getPicUrls().getThumbNail_picUrl2())) {
                if(oUser.getIsLockPics().getIsLockPic2()) lockPics[i].setVisibility(View.VISIBLE);
                else lockPics[i].setVisibility(View.INVISIBLE);
            } else if(url.equals(oUser.getPicUrls().getThumbNail_picUrl3())) {
                if(oUser.getIsLockPics().getIsLockPic3()) lockPics[i].setVisibility(View.VISIBLE);
                else lockPics[i].setVisibility(View.INVISIBLE);
            } else if(url.equals(oUser.getPicUrls().getThumbNail_picUrl4())) {
                if(oUser.getIsLockPics().getIsLockPic4()) lockPics[i].setVisibility(View.VISIBLE);
                else lockPics[i].setVisibility(View.INVISIBLE);
            }

            GlideApp.with(getBaseContext())
                    .load(url)
                    .placeholder(R.drawable.a)
                    .into(profilePics[i]);
        }

        // 큰사진
        GlideApp.with(getBaseContext())
                .load(oUser.getPicUrls().getPicUrl1())
                .placeholder(R.drawable.a)
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
        DatabaseReference viewedMeUsersRef = DataContainer.getInstance().getUserRef(item.getUuid()).child("viewedMeUsers");
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

                        map.remove(min.getKey());
                        mutableData.setValue(map);
                    }
                }

                // 데이터 추가
                map.put(mUuid, System.currentTimeMillis());
                mutableData.setValue(map);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(getClass().getName(), "viewedMeUsersTransaction:onComplete:" + databaseError);
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

        addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title, message;
                final boolean isFollow = oUser.getFollowerUsers().containsKey(myUuid);  // 이미 팔로우한 유저
                if (isFollow) {
                    title = "팔로우 해제";
                    message = "해당유저를 팔로우해제하시겠습니까?";
                } else {
                    title = "팔로우 신청";
                    message = "해당유저를 팔로우하시겠습니까?";
                }

                UiUtil.getInstance().showDialog(FullImageActivity.this, title, message, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        UiUtil.getInstance().startProgressDialog(FullImageActivity.this);

                        // 내 following 추가, 유저 follower c추가
                        Task<Void> task;
                        try {
                            task = FireBaseUtil.getInstance().follow(oUser, item.getUuid(), isFollow);
                        } catch (ChildSizeMaxException e) {
                            Toast.makeText(FullImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            UiUtil.getInstance().stopProgressDialog();
                            return;
                        }
                        if (task == null) {
                            Toast.makeText(getBaseContext(), "오류 발생", Toast.LENGTH_SHORT).show();
                            UiUtil.getInstance().stopProgressDialog();
                            return;
                        }
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (oUser.getFollowerUsers().containsKey(myUuid)) {   //  이미 팔로우함
                                    addFriends.setImageResource(R.drawable.unfollow); // 팔로우 취소 버튼
                                } else {
                                    addFriends.setImageResource(R.drawable.follow); // 팔로우 버튼
                                }
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                UiUtil.getInstance().stopProgressDialog();
                            }
                        });

                    }
                }, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
            }
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
                UiUtil.getInstance().showDialog(FullImageActivity.this, "유저 차단", "해당 유저를 차단하시겠습니까?", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mUser.getBlockUsers().size() >= DataContainer.ChildrenMax) {
                            Toast.makeText(FullImageActivity.this, DataContainer.ChildrenMax + "명을 초과할 수 없습니다", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        UiUtil.getInstance().startProgressDialog(FullImageActivity.this);
                        // blockUsers 추가
                        try {
                            FireBaseUtil.getInstance().block(item.getUuid()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    finish();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    UiUtil.getInstance().stopProgressDialog();
                                }
                            });
                        } catch (ChildSizeMaxException e) {
                            Toast.makeText(FullImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            UiUtil.getInstance().stopProgressDialog();
                        }
                    }
                }, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
            }
        });
    }

    private boolean isHaveLockPic(){
        for(boolean b : mUser.getIsLockPics().toArray()){
            if(b) return true;
        }
        return false;
    }

    private void setPicLock(final GridItem item) {
        final String myUuid = DataContainer.getInstance().getUid();
        if (mUser == null) {
            UiUtil.getInstance().restartApp(this);
        }

        // 잠근사진이 없으면 리턴
        if(!isHaveLockPic()) return;

        // 아이콘 크기 설정
        picOpen.getLayoutParams().width = (int) getResources().getDimension(R.dimen.image_lock_height);
        picOpen.getLayoutParams().height = (int) getResources().getDimension(R.dimen.image_lock_width);

        if (item.getUuid().equals(myUuid)) {  // 본인
            picOpen.setVisibility(View.INVISIBLE);  // 가림
            return;
        }
        if (!mUser.getUnLockUsers().containsKey(item.getUuid())) {
            picOpen.setImageResource(R.drawable.picture_unlock); // "이 아이콘을 클릭하면 사진을 해제하겠다"
        } else {
            picOpen.setImageResource(R.drawable.picture_lock); // "이 아이콘을 클릭하면 사진을 잠그겠다"
        }
        picOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title, message;
                final boolean isLock = !mUser.getUnLockUsers().containsKey(item.getUuid());  // 이미 해제한 유저
                if (isLock) {
                    title = "사진 해제";
                    message = "이 회원에게 당신의 잠긴 사진을 공개하시겠습니까?";
                } else {
                    title = "사진 잠금";
                    message = "이 회원에게 당신의 사진을 잠그시겠습니까?";
                }

                UiUtil.getInstance().showDialog(FullImageActivity.this, title, message, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        UiUtil.getInstance().startProgressDialog(FullImageActivity.this);
                        if (isLock) {
                            mUser.getUnLockUsers().put(item.getUuid(), System.currentTimeMillis()); // 해제

                        } else {
                            mUser.getUnLockUsers().remove(item.getUuid());  // 잠금

                        }
                        DataContainer.getInstance().getUsersRef().child(myUuid).setValue(mUser)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (isLock) {
                                            picOpen.setImageResource(R.drawable.picture_lock);    // 해제하기 (현재 사진이 잠겼다는 것을 암시함)
                                            Toast.makeText(FullImageActivity.this, "잠긴 사진을 열었습니다.", Toast.LENGTH_SHORT).show();
                                            sendEventMessage(myUuid,mUser.getId(),item.getUuid(),getString(R.string.alertUnlockPic));
                                            FirebaseSendPushMsg.sendPostToFCM(item.getUuid(),mUser.getId(),getString(R.string.alertUnlockPic));
                                        } else {
                                            picOpen.setImageResource(R.drawable.picture_unlock);  // 잠금 (현재 사진이 해제되어 있다는 암시함)
                                            Toast.makeText(FullImageActivity.this, "사진을 비공개 합니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                UiUtil.getInstance().stopProgressDialog();
                            }
                        });
                    }
                }, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
            }
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

//        ScreenshotSetApplication.getInstance().registerScreenshotObserver();
    }

    @Override
    public void onPause() {
        super.onPause();
//        ScreenshotSetApplication.getInstance().unregisterScreenshotObserver();
    }

    @Override
    protected void onDestroy() {
        oUserRef.removeEventListener(listener);
        super.onDestroy();
    }
}