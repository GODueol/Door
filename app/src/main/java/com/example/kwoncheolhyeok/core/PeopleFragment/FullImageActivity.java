package com.example.kwoncheolhyeok.core.PeopleFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kwoncheolhyeok.core.CorePage.CoreActivity;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.MyApplcation;
import com.example.kwoncheolhyeok.core.PeopleFragment.FullImageViewPager.DetailImageActivity;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;


public class FullImageActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar = null;

    RelativeLayout core_enter = null;
    ImageView page1,page2,page3,page4;
    ImageView message_white, add_friends, block_friends;

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

    @SuppressLint("DefaultLocale")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image_activity_main);

        MyApplcation.getInstance().allowUserSaveScreenshot(true);

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
        final ImageAdapter.Item item = (ImageAdapter.Item) p.getSerializableExtra("item");


        //개인 화면에서 코어 액티비티로 넘어감
        core_enter = findViewById(R.id.core_enter_layout);
        core_enter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivityForResult(new Intent(FullImageActivity.this,CoreActivity.class), 0);
            }
        });

        // 개인정보 Set
        final User user = item.getUser();
        textId.setText(user.getId());
        textPhysical.setText(TextUtils.join("/", new String[]{user.getAge(), user.getHeight(), user.getWeight(), user.getBodyType()}));
        textIntroduce.setText(user.getIntro());
        distanceText.setText(String.format("%.1f", item.getDistance()/1000));

        // 로그인 시간
        if(user.getLoginDate() != 0) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yy.MM.dd HH:mm");
            loginTime.setText( dateFormat.format(new Date(user.getLoginDate())));
        }

        // 사진 출력
        ImageView profilePics[] = {page1, page2, page3, page4};
        picUrlList = user.getPicUrls().toNotNullArray(user.getIsLockPics(), user.getUnLockUsers(), item.getUuid());
        for (int i=0; i<picUrlList.size(); i++){
            String url = picUrlList.get(i);
            if(url == null) continue;
            Glide.with(getBaseContext()).load(url).into(profilePics[i]);
            if(i==0){ // 프사
                Glide.with(getBaseContext()).load(url).into(fullImageView);
            }
        }

        // 사진 잠금 해제
        setPicLock(item);

    }

    private void setPicLock(final ImageAdapter.Item item) {
        final String myUuid = DataContainer.getInstance().getUid();
        final User mUser = DataContainer.getInstance().getUser();

        // 아이콘 크기 설정ㅇㅁㄴㄹ
        picOpen.getLayoutParams().width = (int) getResources().getDimension(R.dimen.image_lock_height);
        picOpen.getLayoutParams().height = (int) getResources().getDimension(R.dimen.image_lock_width);

        if(item.getUuid().equals(myUuid)){  // 본인
            picOpen.setVisibility(View.INVISIBLE);  // 가림

            return;
        }
        if(!mUser.getUnLockUsers().containsKey(item.getUuid())) {
            picOpen.setImageResource(R.drawable.picture_unlock); // "이 아이콘을 클릭하면 사진을 해제하겠다"
        }else {
            picOpen.setImageResource(R.drawable.picture_lock); // "이 아이콘을 클릭하면 사진을 잠그겠다"
        }
        picOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title, message;
                final boolean isLock = !mUser.getUnLockUsers().containsKey(item.getUuid());  // 이미 해제한 유저
                if(isLock){
                    title = "사진 해제";
                    message = "이 회원에게 당신의 잠긴 사진을 공개하시겠습니까?";
                } else {
                    title = "사진 잠금";
                    message = "이 회원에게 당신의 사진을 잠그시겠습니까?";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(FullImageActivity.this, R.style.MyAlertDialogStyle);
                builder.setIcon(R.drawable.icon);
                builder.setTitle(title);
                builder.setMessage(message);
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(isLock){
                            mUser.getUnLockUsers().put(item.getUuid(), System.currentTimeMillis()); // 해제
                            Toast.makeText(FullImageActivity.this, "잠긴 사진을 열었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            mUser.getUnLockUsers().remove(item.getUuid());  // 잠금
                            Toast.makeText(FullImageActivity.this, "사진을 비공개 합니다.", Toast.LENGTH_SHORT).show();

                        }
                        FirebaseDatabase.getInstance().getReference("users").child(myUuid).setValue(mUser)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(isLock) {
                                    picOpen.setImageResource(R.drawable.picture_lock);    // 해제하기 (현재 사진이 잠겼다는 것을 암시함)
                                }else {
                                    picOpen.setImageResource(R.drawable.picture_unlock);  // 잠금 (현재 사진이 해제되어 있다는 암시함)
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기

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
        Intent myIntent = new Intent(getApplicationContext(),DetailImageActivity.class);
        myIntent.putExtra("picUrlList", picUrlList);
        if(picUrlList.size() == 0) return;
        switch (view.getId()){
            case R.id.image1:
                myIntent.putExtra("PagerPage",0);
                startActivity(myIntent);
                break;
            case R.id.image2:
                myIntent.putExtra("PagerPage",1);
                startActivity(myIntent);
                break;
            case R.id.image3:
                myIntent.putExtra("PagerPage",2);
                startActivity(myIntent);
                break;
            case R.id.image4:
                myIntent.putExtra("PagerPage",3);
                startActivity(myIntent);
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        MyApplcation.getInstance().registerScreenshotObserver();
    }

    @Override
    public void onPause() {
        super.onPause();
        MyApplcation.getInstance().unregisterScreenshotObserver();
    }
}