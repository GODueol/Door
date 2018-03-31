package com.example.kwoncheolhyeok.core.CorePage.CoreCloudPayDialog;

import android.annotation.SuppressLint;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.kwoncheolhyeok.core.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class DealDialogFragment extends BaseDialogFragment {

    private List<Fragment> fragmentList = new ArrayList<>();
    CallbackListener callbackListener;

    TabLayout tabLayout = null;
    long possibleDate;

    @SuppressLint("ValidFragment")
    public DealDialogFragment(long possibleDate, CallbackListener callbackListener){
        this.callbackListener = callbackListener;
        this.possibleDate = possibleDate;
    }

    @Override
    protected void attachView() {

    }

    public int getLayoutResId() {
        return R.layout.core_cloud_notice_viewpager;
    }


    protected void initView(View dialog) {

        ViewPager viewPager = dialog.findViewById(R.id.view_pager);

        fragmentList.add(new CoreCloudNotice1(viewPager, possibleDate));
        fragmentList.add(new CoreCloudNotice2(viewPager));
        fragmentList.add(new CoreCloudNotice3(viewPager));
        fragmentList.add(new CoreCloudNotice4(viewPager));
        fragmentList.add(new CoreCloudNotice5(viewPager, callbackListener, DealDialogFragment.this, possibleDate));

        CustomViewpagerAdapter customViewpagerAdapter = new CustomViewpagerAdapter(getChildFragmentManager(), fragmentList);
        viewPager.setAdapter(customViewpagerAdapter);
        viewPager.setCurrentItem(0);

        // Viewpager indicator
        tabLayout = dialog.findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);

    }

    public class CustomViewpagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        CustomViewpagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }

    public interface CallbackListener {
        void callback();
    }
}
