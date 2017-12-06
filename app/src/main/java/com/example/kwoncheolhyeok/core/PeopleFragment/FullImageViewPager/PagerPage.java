package com.example.kwoncheolhyeok.core.PeopleFragment.FullImageViewPager;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.example.kwoncheolhyeok.core.R;
import com.github.chrisbanes.photoview.PhotoView;

public class PagerPage extends android.support.v4.app.Fragment {

    ScrollView scrollView;
    PhotoView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.full_image_4_page, container, false);

        if (getArguments() != null) {
            String picPath = getArguments().getString("picPath");
            scrollView = view.findViewById(R.id.scrollView);
            imageView = view.findViewById(R.id.pictureImage);

            Glide.with(imageView.getContext() /* context */)
                    .load(picPath).into(imageView);

        }
        return view;
    }

}
