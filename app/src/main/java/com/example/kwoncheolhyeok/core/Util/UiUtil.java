package com.example.kwoncheolhyeok.core.Util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;

import com.example.kwoncheolhyeok.core.CorePage.CoreActivity;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.LoginActivity.IntroActivity;
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

    public String setSubProfile(User user) {
        return TextUtils.join("/", new String[]{Integer.toString(user.getAge()), Integer.toString(user.getHeight()),
                Integer.toString(user.getWeight()), user.getBodyType()});
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

    public void stopProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing() && progressDialog.getContext() != null)
                progressDialog.dismiss();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showDialog(Context context, String title, String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        showDialog(context, title, message, okListener, cancelListener, "OK", "Cancel");
    }

    private void showDialog(Context context, String title, String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener, String okStr, String cancelStr) {
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

    public void goToCoreActivity(Context context, String uuid) {
        Intent intent = new Intent(context, CoreActivity.class);
        intent.putExtra("uuid", uuid);
        context.startActivity(intent);
    }

    public void restartApp(Context context) {
        Intent mStartActivity = new Intent(context, IntroActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    public static Uri resourceToUri(Context context, int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID));
    }

}
