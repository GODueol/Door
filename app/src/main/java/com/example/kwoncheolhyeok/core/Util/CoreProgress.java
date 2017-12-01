package com.example.kwoncheolhyeok.core.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.example.kwoncheolhyeok.core.LoginActivity.LoginActivity;
import com.example.kwoncheolhyeok.core.R;

/**
 * Created by gimbyeongjin on 2017. 10. 5..
 */

public class CoreProgress {
    private static final CoreProgress ourInstance = new CoreProgress();

    public static CoreProgress getInstance() {
        return ourInstance;
    }

    private Activity activity;
    private ProgressDialog progressDialog;

    private CoreProgress() {
    }

    public void startProgressDialog(Activity activity) {
        //프로그레스 다이얼로그 이미지만 센터에서 돌아가게
        progressDialog = new ProgressDialog(activity, R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setIndeterminateDrawable(activity.getResources().getDrawable(R.drawable.progress_dialog_icon_drawable_animation));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void stopProgressDialog(){
        if(progressDialog != null) progressDialog.dismiss();
    }
}
