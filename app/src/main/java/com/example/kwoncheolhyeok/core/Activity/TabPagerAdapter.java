package com.example.kwoncheolhyeok.core.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.kwoncheolhyeok.core.PeopleFragment.PeopleFragment;
import com.google.android.gms.maps.model.LatLng;

public class TabPagerAdapter extends FragmentPagerAdapter {

    private int position;

    private LatLng latLng;
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


    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public Fragment getItem(int position) {
        this.position = position;
        switch (position) {
            case 0:
                PeopleFragment peopleFragment = new PeopleFragment();
                Bundle args = new Bundle();
                args.putParcelable("latlng",latLng);
                peopleFragment.setArguments(args);
                return peopleFragment;
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
