package com.teamdoor.android.door.CorePage.CoreCloudPayDialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.teamdoor.android.door.R;
import com.teamdoor.android.door.Util.GlideApp;
import com.teamdoor.android.door.Util.UiUtil;

@SuppressLint("ValidFragment")
public class CoreCloudNotice2 extends BaseFragment {

    @SuppressLint("ValidFragment")
    public CoreCloudNotice2(ViewPager viewPager) {
        super(viewPager);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.core_cloud_notice_2;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView img_cld_notice = view.findViewById(R.id.img_cld_notice);
        GlideApp.with(this)
                .load(UiUtil.resourceToUri(getContext(), R.drawable.cloud_notice_viewpager2))
                .into(img_cld_notice);


    }

}