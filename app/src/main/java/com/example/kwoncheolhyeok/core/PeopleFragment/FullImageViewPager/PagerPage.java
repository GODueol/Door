package com.example.kwoncheolhyeok.core.PeopleFragment.FullImageViewPager;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;

/**
 * Created by songmho on 2015-01-02.
 */
public class PagerPage extends android.support.v4.app.Fragment {

    ScrollView scrollView;
    ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.full_image_4_page, container, false);

        if(getArguments() != null) {
            String picPath = getArguments().getString("picPath");
            scrollView = view.findViewById(R.id.scrollView);
            imageView = view.findViewById(R.id.pictureImage);
            final LinearLayout background = view.findViewById(R.id.background);

            FireBaseUtil fbUtil = FireBaseUtil.getInstance();
            fbUtil.setImage(picPath, imageView, new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                    return false;
                }
                @Override
                public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                    // 이미지 파일의 가로 세로 비율에 맞게 이미지 크기를 늘림
                    int width = drawable.getIntrinsicWidth();
                    int height = drawable.getIntrinsicHeight();
                    int pWidth = background.getMeasuredWidth();
                    imageView.getLayoutParams().width = pWidth;
                    imageView.getLayoutParams().height = height*pWidth/width;
                    return false;
                }
            });

        }
        return view;
    }
}
