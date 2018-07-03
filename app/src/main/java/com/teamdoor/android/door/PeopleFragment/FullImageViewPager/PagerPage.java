package com.teamdoor.android.door.PeopleFragment.FullImageViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.teamdoor.android.door.R;
import com.github.chrisbanes.photoview.PhotoView;

public class PagerPage extends android.support.v4.app.Fragment {

    PhotoView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.full_image_4_page, container, false);


        if (getArguments() != null) {
            String picPath = getArguments().getString("picPath");
            imageView = view.findViewById(R.id.pictureImage);

            Glide.with(imageView.getContext() /* context */)
                    .load(picPath).into(imageView);

        }
        return view;
    }

}
