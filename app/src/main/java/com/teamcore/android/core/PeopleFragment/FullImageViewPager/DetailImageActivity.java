package com.teamcore.android.core.PeopleFragment.FullImageViewPager;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;

import com.teamcore.android.core.Event.TargetUserBlocksMeEvent;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.BaseActivity.BlockBaseActivity;
import com.teamcore.android.core.Util.ViewPagerFixed;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;


public class DetailImageActivity extends BlockBaseActivity {

    DetailImageViewPagerAdapter mAdapter;
    ArrayList<String> picUrlList;

    TabLayout tabLayout = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image_4_activity);

//        ScreenshotSetApplication.getInstance().allowUserSaveScreenshot(true);

        Intent intent = getIntent();
        picUrlList = intent.getStringArrayListExtra("picUrlList");

        int pagenumber = intent.getIntExtra("PagerPage", 1);
        mAdapter = new DetailImageViewPagerAdapter(getSupportFragmentManager(), picUrlList);
        ViewPagerFixed viewPager = (ViewPagerFixed) findViewById(R.id.viewpager);        //Viewpager 선언 및 초기화
        viewPager.setAdapter(mAdapter);     //선언한 viewpager에 adapter를 연결
        viewPager.setCurrentItem(pagenumber);

        // Viewpager indicator
        tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);

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

    @Subscribe
    public void FinishActivity(TargetUserBlocksMeEvent someoneBlocksMeEvent){
        finish();
    }

}