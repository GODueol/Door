package com.example.kwoncheolhyeok.core.PeopleFragment;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * FullImageAcitivity의 PageAdapter
 */
public class FullImagePagerAdapter extends PagerAdapter {

    private List<ImageView> images;

    public FullImagePagerAdapter(List<ImageView> images) {
        this.images = images;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = images.get(position);
        container.addView(imageView);
        return imageView;
    }

//    @Override
//    public Fragment getItem(int position) {
//
//        switch (position) {
//            case 0:
//                page1 tab1 = new page1();
//                return tab1;
//            case 1:
//                page2 tab2 = new page2();
//                return tab2;
//            case 2:
//                page3 tab3 = new page3();
//                return tab3;
//            case 3:
//                page4 tab4 = new page4();
//                return tab4;
//            default:
//                return null;
//        }
//    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(images.get(position));
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }



}
