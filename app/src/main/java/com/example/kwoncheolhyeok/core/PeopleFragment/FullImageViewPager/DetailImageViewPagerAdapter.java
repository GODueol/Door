package com.example.kwoncheolhyeok.core.PeopleFragment.FullImageViewPager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by godueol on 2017. 11. 11..
 */

public class DetailImageViewPagerAdapter extends FragmentPagerAdapter {




    private static final int PAGE_NUMBER = 4;

    public DetailImageViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    Fragment cur_fragment=new Fragment();   //현재 Viewpager가 가리키는 Fragment를 받을 변수 선언

    @Override
    public Fragment getItem(int position) {
        if(position<0 || PAGE_NUMBER<=position)        //가리키는 페이지가 0 이하거나 MAX_PAGE보다 많을 시 null로 리턴
            return null;
        switch (position){              //포지션에 맞는 Fragment찾아서 cur_fragment변수에 대입
            case 0:
                cur_fragment=new page1();
                break;

            case 1:
                cur_fragment=new page2();
                break;

            case 2:
                cur_fragment=new page3();
                break;

            case 3:
                cur_fragment=new page4();
                break;
        }

        return cur_fragment;
    }

    @Override
    public int getCount() {
        return PAGE_NUMBER;
    }

}
