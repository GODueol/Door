package com.example.kwoncheolhyeok.core.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.example.kwoncheolhyeok.core.CorePage.CoreActivity;
import com.example.kwoncheolhyeok.core.R;

/**
 * Created by gimbyeongjin on 2017. 10. 5..
 */

public class UiUtil {
    private static final UiUtil ourInstance = new UiUtil();

    public static UiUtil getInstance() {
        return ourInstance;
    }

    private ProgressDialog progressDialog;

    private UiUtil() {
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

    public void showDialog(Context context, String title, String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener){
        showDialog(context, title, message, okListener, cancelListener, "OK", "Cancel");
    }

    public void showDialog(Context context, String title, String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener, String okStr, String cancelStr){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        builder.setIcon(R.drawable.icon);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(okStr, okListener);
        builder.setNegativeButton(cancelStr, cancelListener);
        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기
    }

    public void goToCoreActivity(Context context, String uuid){
        Intent intent = new Intent(context, CoreActivity.class);
        intent.putExtra("uuid", uuid);
        context.startActivity(intent);
    }

}
