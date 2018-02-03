package com.example.kwoncheolhyeok.core.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.kwoncheolhyeok.core.PeopleFragment.PeopleFragment;

public class TabPagerAdapter extends FragmentPagerAdapter {

    private int position;
    private String[] mNumOfTabs;

    TabPagerAdapter(FragmentManager fm, String[] NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public Fragment getItem(int position) {
        this.position = position;
        switch (position) {
            case 0:
                return new PeopleFragment();
//            case 1:
//                PeopleFragment tab1 = PeopleFragment.getInstance();
//                return tab1;
//            case 2:
//                TabFragment3 tab3 = TabFragment3.getInstance();
//                return tab3;
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
