package com.example.kwoncheolhyeok.core.PeopleFragment.FullImageViewPager;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;

import java.util.ArrayList;


public class DetailImageActivity extends AppCompatActivity {

    DetailImageViewPagerAdapter mAdapter;
    ArrayList<String> picPaths;

    TabLayout tabLayout = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image_4_activity);

        Intent intent = getIntent();
        picPaths = DataContainer.getInstance().getUser().getPicUrls().toNotNullArray();

        int pagenumber = intent.getIntExtra("PagerPage",1);
        mAdapter = new DetailImageViewPagerAdapter(getSupportFragmentManager(), picPaths);
        ViewPager viewPager= findViewById(R.id.viewpager);        //Viewpager 선언 및 초기화
        viewPager.setAdapter(mAdapter);     //선언한 viewpager에 adapter를 연결
        viewPager.setCurrentItem(pagenumber);

        // Viewpager indicator
        tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);

    }

}