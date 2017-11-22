package com.example.kwoncheolhyeok.core.PeopleFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.kwoncheolhyeok.core.CorePage.CoreActivity;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.PeopleFragment.FullImageViewPager.DetailImageActivity;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;


public class FullImageActivity extends AppCompatActivity implements View.OnClickListener {

    int position;
    Toolbar toolbar = null;

    RelativeLayout core_enter = null;
    ImageView page1,page2,page3,page4;
    ImageView pic_open, message_white, add_friends, block_friends;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image_activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);


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
        ImageAdapter.Item item = (ImageAdapter.Item) p.getSerializableExtra("item");
        String uuid = item.getUuid();
        User user = item.getUser();

        // 프사 출력
        FireBaseUtil fbUtil = FireBaseUtil.getInstance();
        fbUtil.setImage(fbUtil.getParentPath(uuid) + "profilePic1.jpg", page1);
        fbUtil.setImage(fbUtil.getParentPath(uuid) + "profilePic2.jpg", page2);
        fbUtil.setImage(fbUtil.getParentPath(uuid) + "profilePic3.jpg", page3);
        fbUtil.setImage(fbUtil.getParentPath(uuid) + "profilePic4.jpg", page4);


        //개인 화면에서 코어 액티비티로 넘어감
        core_enter =(RelativeLayout)findViewById(R.id.core_enter_layout);
        core_enter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivityForResult(new Intent(FullImageActivity.this,CoreActivity.class), 0);
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
        switch (view.getId()){
            case R.id.image1:
                myIntent.putExtra("page",0);
                startActivity(myIntent);
                break;
            case R.id.image2:
                myIntent.putExtra("page",1);
                startActivity(myIntent);
                break;
            case R.id.image3:
                myIntent.putExtra("page",2);
                startActivity(myIntent);
                break;
            case R.id.image4:
                myIntent.putExtra("page",3);
                startActivity(myIntent);
                break;
        }

    }
}