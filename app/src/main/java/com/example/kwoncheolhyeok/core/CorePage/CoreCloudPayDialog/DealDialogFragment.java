package com.example.kwoncheolhyeok.core.CorePage.CoreCloudPayDialog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.kwoncheolhyeok.core.R;

import java.util.ArrayList;
import java.util.List;

public class DealDialogFragment extends BaseDialogFragment {

    private List<Fragment> fragmentList = new ArrayList<>();
    private CustomViewpagerAdapter customViewpagerAdapter;

    @Override
    protected void attachView() {

    }

    public int getLayoutResId() {
        return R.layout.core_cloud_notice_viewpager;
    }


    protected void initView(View dialog) {

        ViewPager viewPager = dialog.findViewById(R.id.view_pager);

        fragmentList.add(new CoreCloudNotice1());
        fragmentList.add(new CoreCloudNotice2());

        customViewpagerAdapter = new CustomViewpagerAdapter(getChildFragmentManager(),fragmentList);
        viewPager.setAdapter(customViewpagerAdapter);
        viewPager.setCurrentItem(0);

    }

    public class CustomViewpagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public CustomViewpagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
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

}
