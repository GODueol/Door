package com.example.kwoncheolhyeok.core.CorePage.CoreCloudPayDialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.GlideApp;
import com.example.kwoncheolhyeok.core.Util.UiUtil;

@SuppressLint("ValidFragment")
public class CoreCloudNotice5 extends BaseFragment {

    DealDialogFragment.CallbackListener callbackListener;
    DealDialogFragment dealDialogFragment;

    @SuppressLint("ValidFragment")
    public CoreCloudNotice5(ViewPager viewPager, DealDialogFragment.CallbackListener callbackListener, DealDialogFragment dealDialogFragment) {
        super(viewPager);
        this.callbackListener = callbackListener;
        this.dealDialogFragment = dealDialogFragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.core_cloud_notice_5;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView img_cld_notice = view.findViewById(R.id.img_cld_notice);
        GlideApp.with(this)
                .load(UiUtil.resourceToUri(getContext(), R.drawable.cloud_notice_viewpager5))
                .into(img_cld_notice);

        TextView approval = view.findViewById(R.id.approval);
        approval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbackListener.callback();
                dealDialogFragment.dismiss();
            }
        });

    }


}
