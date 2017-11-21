package com.example.kwoncheolhyeok.core.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.kwoncheolhyeok.core.PeopleFragment.TabFragment1;
import com.example.kwoncheolhyeok.core.BoardActivity.TabFragment2;
import com.example.kwoncheolhyeok.core.ClubActivity.TabFragment3;

public class TabPagerAdapter extends FragmentPagerAdapter {

    private String[] mNumOfTabs;

    public TabPagerAdapter(FragmentManager fm, String[] NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }



    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TabFragment1 tab1 = new TabFragment1();
                return tab1;
            case 1:
                TabFragment2 tab2 = new TabFragment2();
                return tab2;
            case 2:
                TabFragment3 tab3 = new TabFragment3();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.mNumOfTabs[position];
    }
}
