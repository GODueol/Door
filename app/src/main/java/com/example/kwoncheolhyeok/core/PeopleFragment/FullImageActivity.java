package com.example.kwoncheolhyeok.core.PeopleFragment;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.kwoncheolhyeok.core.CorePage.CoreActivity;
import com.example.kwoncheolhyeok.core.PeopleFragment.FullImageViewPager.DetailImageActivity;
import com.example.kwoncheolhyeok.core.R;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;
import java.util.List;


public class FullImageActivity extends AppCompatActivity implements View.OnClickListener {

    int position;
    Toolbar toolbar = null;

    RelativeLayout core_enter = null;
    ImageView page1,page2,page3,page4;
    ImageView pic_open, message_white, add_friends, block_friends;

    // 기본 폰트 고정
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

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
        Intent p = getIntent();
        position = p.getExtras().getInt("id");

        ImageAdapter imageAdapter = new ImageAdapter(this);
        List<ImageView> images = new ArrayList<ImageView>();

        for (int i = 0; i < imageAdapter.getCount(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource((int) imageAdapter.getItemId(i));
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            images.add(imageView);
        }
        page1 = (ImageView) findViewById(R.id.image1);
        page1.setOnClickListener(this);
        page2 = (ImageView) findViewById(R.id.image2);
        page2.setOnClickListener(this);
        page3 = (ImageView) findViewById(R.id.image3);
        page3.setOnClickListener(this);
        page4 = (ImageView) findViewById(R.id.image4);
        page4.setOnClickListener(this);
        //FullImageAdapter 객체를 생성해 썸네일 풀 이미지를 한장씩 넘길 수 있게 함
        FullImagePagerAdapter pageradapter = new FullImagePagerAdapter(images);
        ViewPager viewpager = (ViewPager) findViewById(R.id.pager1);
        viewpager.setAdapter(pageradapter);

        //해당 썸네일을 클릭했을 때 그 이미지가 나올 수 있게 해줌
        viewpager.setCurrentItem(position);

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