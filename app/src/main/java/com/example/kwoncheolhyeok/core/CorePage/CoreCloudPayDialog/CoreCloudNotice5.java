package com.example.kwoncheolhyeok.core.CorePage.CoreCloudPayDialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.GlideApp;
import com.example.kwoncheolhyeok.core.Util.UiUtil;

@SuppressLint("ValidFragment")
public class CoreCloudNotice5 extends BaseFragment {

    DealDialogFragment.CallbackListener callbackListener;
    DealDialogFragment dealDialogFragment;
    long oldestPostDate;

    @SuppressLint("ValidFragment")
    public CoreCloudNotice5(ViewPager viewPager, DealDialogFragment.CallbackListener callbackListener, DealDialogFragment dealDialogFragment, long oldestPostDate) {
        super(viewPager);
        this.callbackListener = callbackListener;
        this.dealDialogFragment = dealDialogFragment;
        this.oldestPostDate = oldestPostDate;
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

        final CheckBox check_notice = view.findViewById(R.id.check_notice);

        RelativeLayout checkLayout = view.findViewById(R.id.checkLayout);
        if(isPostPossible(oldestPostDate)){
            approval.setVisibility(View.VISIBLE);
            checkLayout.setVisibility(View.VISIBLE);

            approval.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!check_notice.isChecked()){
                        Toast.makeText(getActivity(), "동의 체크를 누르셔야 결제 가능합니다", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    callbackListener.callback();
                    dealDialogFragment.dismiss();
                }
            });
        } else {
            approval.setVisibility(View.INVISIBLE);
            checkLayout.setVisibility(View.INVISIBLE);

            approval.setOnClickListener(null);
        }


    }


}
