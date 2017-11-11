package com.example.kwoncheolhyeok.core.PeopleFragment.FullImageViewPager;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.R;

/**
 * Created by songmho on 2015-01-02.
 */
public class page2 extends android.support.v4.app.Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.full_image_4_page, container, false);

        RelativeLayout background=(RelativeLayout)view.findViewById(R.id.background);
        TextView page_num=(TextView)view.findViewById(R.id.page_num);

//        LinearLayout linearLayout=(LinearLayout)inflater.inflate(R.layout.full_image_4_page,container,false);

//        LinearLayout background=(LinearLayout)linearLayout.findViewById(R.id.background);
//        TextView page_num=(TextView)linearLayout.findViewById(R.id.page_num);

        ImageView imageView=(ImageView)view.findViewById(R.id.image1);

        page_num.setText(String.valueOf(2));
//        background.setBackground(new ColorDrawable(0xff26abb5));
        return view;
    }
}
