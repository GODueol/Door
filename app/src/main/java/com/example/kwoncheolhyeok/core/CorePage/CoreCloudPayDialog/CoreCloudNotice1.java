package com.example.kwoncheolhyeok.core.CorePage.CoreCloudPayDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.Exception.NotSetAutoTimeException;
import com.example.kwoncheolhyeok.core.MessageActivity.util.DateUtil;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.GlideApp;
import com.example.kwoncheolhyeok.core.Util.UiUtil;

import static com.example.kwoncheolhyeok.core.Util.DataContainer.SecToDay;

@SuppressLint("ValidFragment")
public class CoreCloudNotice1 extends BaseFragment {

    long oldestPostDate;

    @SuppressLint("ValidFragment")
    public CoreCloudNotice1(ViewPager viewPager, long oldestPostDate) {
        super(viewPager);
        this.oldestPostDate = oldestPostDate;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.core_cloud_notice_1;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView img_cld_notice = view.findViewById(R.id.img_cld_notice);
        GlideApp.with(this)
                .load(UiUtil.resourceToUri(getContext(), R.drawable.cloud_notice_viewpager1))
//                .override(80, 23)
//                .centerCrop()
                .into(img_cld_notice);

        TextView payable_time = view.findViewById(R.id.payable_time);

        if(isPostPossible(oldestPostDate)){
            // 업로드 가능
            payable_time.setText(R.string.possibleUploadCloud);
        }else {
            // 업로드 불가능
            payable_time.setText(new DateUtil(oldestPostDate).getDateAndTime());

            Log.d("kbj", new DateUtil(System.currentTimeMillis()).getDateAndTime());
        }
    }
}
