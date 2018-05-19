package com.teamcore.android.core.Util;

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
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.teamcore.android.core.CorePage.CoreActivity;
import com.teamcore.android.core.Entity.CorePost;
import com.teamcore.android.core.Entity.User;
import com.teamcore.android.core.Exception.NotSetAutoTimeException;
import com.teamcore.android.core.LoginActivity.IntroActivity;
import com.teamcore.android.core.MessageActivity.util.DateUtil;
import com.teamcore.android.core.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import io.reactivex.annotations.NonNull;

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
        if (user.getAge() == 0 && user.getBodyType() == null) return "";
        return TextUtils.join("/", new String[]{Integer.toString(user.getAge()), Integer.toString(user.getHeight()),
                Integer.toString(user.getWeight()), user.getBodyType()});
    }

    public void startProgressDialog(Activity activity) {
        if(progressDialog != null && progressDialog.isShowing()) return;
        //프로그레스 다이얼로그 이미지만 센터에서 돌아가게
        progressDialog = new ProgressDialog(activity, R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setIndeterminateDrawable(activity.getResources().getDrawable(R.drawable.progress_dialog_icon_drawable_animation));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        if(!progressDialog.isShowing())progressDialog.show();
    }

    public void stopProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing() && progressDialog.getContext() != null)
                progressDialog.dismiss();
        } catch (Exception e) {
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

    public boolean isAutoTimeSet(@NonNull Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AUTO_TIME) == 1;
        } catch (Settings.SettingNotFoundException e) {
            return false;
        }

    }

    public long getCurrentTime(@NonNull Context context) throws NotSetAutoTimeException {
        if(context == null) return 0;
        if(isAutoTimeSet(context)){
            return System.currentTimeMillis();
        } else {
            throw new NotSetAutoTimeException("시간 수정설정 되어있으면 앱을 사용할 수 없습니다");
        }

    }

    public void noticeModifyToCloud(CorePost corePost, String postKey, Activity activity) {
        if(corePost.isCloud()){
            try {
                DataContainer.getInstance().getCoreCloudRef().child(postKey).child("modifyDate").setValue(UiUtil.getInstance().getCurrentTime(activity));
            } catch (NotSetAutoTimeException e) {
                e.printStackTrace();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                ActivityCompat.finishAffinity(activity);
            }
        }
    }

    public interface CheckPreventCallback {
        void run(Boolean isRelease, String releaseDate);
    }

    public void checkPostPrevent(final Context context, final CheckPreventCallback callback) {
        checkPrevent(context, FireBaseUtil.getInstance().getPreventsPost(DataContainer.getInstance().getUid()), callback);
    }

    public void checkUserPrevent(final Context context, final CheckPreventCallback callback) {
        checkPrevent(context, FireBaseUtil.getInstance().getPreventsUser(DataContainer.getInstance().getUid()), callback);
    }

    private void checkPrevent(final Context context, DatabaseReference reference, final CheckPreventCallback callback) {
        reference.child("releaseDate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    boolean isRelease = true;
                    long releaseDate = 0;

                    if(dataSnapshot.exists()) {
                        releaseDate = dataSnapshot.getValue(Long.class);
                        long currentTime = UiUtil.getInstance().getCurrentTime(context);
                        if(releaseDate > currentTime){
                            isRelease = false;
                        }
                    }

                    // callback
                    callback.run(isRelease, new DateUtil(releaseDate).getDate2());

                } catch (NotSetAutoTimeException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
