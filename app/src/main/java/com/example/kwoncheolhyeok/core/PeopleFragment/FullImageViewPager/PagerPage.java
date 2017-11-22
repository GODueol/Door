package com.example.kwoncheolhyeok.core.PeopleFragment.FullImageViewPager;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;

/**
 * Created by songmho on 2015-01-02.
 */
public class PagerPage extends android.support.v4.app.Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.full_image_4_page, container, false);
        if(getArguments() != null) {
            String picPath = getArguments().getString("picPath");

            RelativeLayout background = view.findViewById(R.id.background);

            // 사진 출력
            FireBaseUtil fbUtil = FireBaseUtil.getInstance();
            fbUtil.setBackgroundImage(picPath, background);
            int position = getArguments().getInt("position");
            TextView page_num= view.findViewById(R.id.page_num);
            page_num.setText(String.valueOf(position));
        }

        return view;
    }




}
