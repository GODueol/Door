package com.example.kwoncheolhyeok.core.PeopleFragment.FullImageViewPager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by godueol on 2017. 11. 11..
 */

public class DetailImageViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<String> picPaths;

    DetailImageViewPagerAdapter(FragmentManager fm, ArrayList<String> picPaths) {
        super(fm);
        this.picPaths = picPaths;
    }

    @Override
    public Fragment getItem(int position) {
        if (position < 0 || picPaths.size() <= position)        //가리키는 페이지가 0 이하거나 MAX_PAGE보다 많을 시 null로 리턴
            return null;

        Fragment cur_fragment = new PagerPage();
        Bundle args = new Bundle();
        args.putString("picPath", picPaths.get(position));
        args.putInt("position", position);
        cur_fragment.setArguments(args);

        return cur_fragment;
    }

    @Override
    public int getCount() {
        return picPaths.size();
    }

}
