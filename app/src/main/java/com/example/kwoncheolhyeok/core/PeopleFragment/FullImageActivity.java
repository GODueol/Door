package com.example.kwoncheolhyeok.core.PeopleFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.CorePage.CoreActivity;
import com.example.kwoncheolhyeok.core.PeopleFragment.FullImage_4_ViewPager.FullImage_4_Activity;
import com.example.kwoncheolhyeok.core.R;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;

import java.util.ArrayList;
import java.util.List;


public class FullImageActivity extends AppCompatActivity {

    int position;
    Toolbar toolbar = null;


    TextView coretext,coretext2 = null;
    ImageView coreenter = null;

    ImageView pic1, pic2, pic3, pic4 = null;

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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);


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

        //FullImageAdapter 객체를 생성해 썸네일 풀 이미지를 한장씩 넘길 수 있게 함
        FullImagePagerAdapter pageradapter = new FullImagePagerAdapter(images);
        ViewPager viewpager = (ViewPager) findViewById(R.id.pager1);
        viewpager.setAdapter(pageradapter);

        //해당 썸네일을 클릭했을 때 그 이미지가 나올 수 있게 해줌
        viewpager.setCurrentItem(position);

        // 코어 버튼 눌렀을 때 CoreActivity로 들어감
        coretext = (TextView) findViewById(R.id.btn_core);
        coretext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(FullImageActivity.this,
                        CoreActivity.class);
                startActivity(myIntent);
            }
        });

        // 코어 버튼 눌렀을 때 CoreActivity로 들어감
        coretext2 = (TextView) findViewById(R.id.btn_core2);
        coretext2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(FullImageActivity.this,
                        CoreActivity.class);
                startActivity(myIntent);
            }
        });

        // 코어 버튼 눌렀을 때 CoreActivity로 들어감
        coreenter = (ImageView) findViewById(R.id.core_enter);
        coreenter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(FullImageActivity.this,
                        CoreActivity.class);
                startActivity(myIntent);
            }
        });

        // 코어 버튼 눌렀을 때 CoreActivity로 들어감
        pic1 = (ImageView) findViewById(R.id.image1);
        pic1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(getApplicationContext(),FullImage_4_Activity.class);
                startActivity(myIntent);
               }
        });

        // 코어 버튼 눌렀을 때 CoreActivity로 들어감
        pic2 = (ImageView) findViewById(R.id.image2);
        pic2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(FullImageActivity.this,FullImage_4_Activity.class);
                startActivity(myIntent);
            }
        });

        // 코어 버튼 눌렀을 때 CoreActivity로 들어감
        pic3 = (ImageView) findViewById(R.id.image3);
        pic3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(FullImageActivity.this,FullImage_4_Activity.class);
                startActivity(myIntent);
            }
        });

        // 코어 버튼 눌렀을 때 CoreActivity로 들어감
        pic4 = (ImageView) findViewById(R.id.image4);
        pic4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(FullImageActivity.this,FullImage_4_Activity.class);
                startActivity(myIntent);
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

}