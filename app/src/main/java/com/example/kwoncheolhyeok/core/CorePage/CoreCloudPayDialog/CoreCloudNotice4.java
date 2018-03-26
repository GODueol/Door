package com.example.kwoncheolhyeok.core.CorePage.CoreCloudPayDialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.example.kwoncheolhyeok.core.R;

@SuppressLint("ValidFragment")
public class CoreCloudNotice4 extends BaseFragment {

    DealDialogFragment.CallbackListener callbackListener;
    DealDialogFragment dealDialogFragment;

    @SuppressLint("ValidFragment")
    public CoreCloudNotice4(ViewPager viewPager, DealDialogFragment.CallbackListener callbackListener, DealDialogFragment dealDialogFragment) {
        super(viewPager);
        this.callbackListener = callbackListener;
        this.dealDialogFragment = dealDialogFragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.core_cloud_notice_4;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button approval = view.findViewById(R.id.approval);
        approval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbackListener.callback();
                dealDialogFragment.dismiss();
            }
        });

    }


}
