package com.example.kwoncheolhyeok.core.PeopleFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.example.kwoncheolhyeok.core.PeopleFragment.FullImage_4_ViewPager.page_1;
import com.example.kwoncheolhyeok.core.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;


public class FullImageActivity extends AppCompatActivity {

    int position;
    Toolbar toolbar = null;

    private GoogleApiClient client;

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





        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("FullImage Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    ;

}