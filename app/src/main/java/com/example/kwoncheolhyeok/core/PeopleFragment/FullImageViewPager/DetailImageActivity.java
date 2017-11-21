package com.example.kwoncheolhyeok.core.PeopleFragment.FullImageViewPager;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.kwoncheolhyeok.core.R;
import com.tsengvn.typekit.TypekitContextWrapper;


public class DetailImageActivity extends AppCompatActivity {

    // 기본 폰트 고정
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    DetailImageViewPagerAdapter mAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image_4_activity);
        Intent intent = getIntent();

        int pagenumber = intent.getIntExtra("page",1);
        mAdapter = new DetailImageViewPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager=(ViewPager)findViewById(R.id.viewpager);        //Viewpager 선언 및 초기화
        viewPager.setAdapter(mAdapter);     //선언한 viewpager에 adapter를 연결
        viewPager.setCurrentItem(pagenumber);
    }

}