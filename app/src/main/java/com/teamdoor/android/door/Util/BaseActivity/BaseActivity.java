package com.teamdoor.android.door.Util.BaseActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.teamdoor.android.door.Entity.User;
import com.teamdoor.android.door.LoginActivity.LoginActivity;
import com.teamdoor.android.door.R;
import com.teamdoor.android.door.Util.DataContainer;
import com.teamdoor.android.door.Util.FireBaseUtil;
import com.teamdoor.android.door.Util.NetworkUtil;
import com.teamdoor.android.door.Util.RemoteConfig;
import com.teamdoor.android.door.Util.UiUtil;
import com.teamdoor.android.door.Util.bilingUtil.IabHelper;
import com.teamdoor.android.door.Util.bilingUtil.Purchase;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    private static Thread.UncaughtExceptionHandler mDefaultUEH;
    private ProgressDialog progressDialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Second, set custom UncaughtExceptionHandler
        //mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        //Thread.setDefaultUncaughtExceptionHandler(mCaughtExceptionHandler);

        registerWifiReceiver();
        setProgressDialog2();
        super.onCreate(savedInstanceState);
    }

    public void deleteNetWorkReceiver(){
        try {
            this.unregisterReceiver(NetworkDetectedReceiver);
        }catch (Exception ignored){}
    }


    @Override
    protected void onDestroy() {
        try {
            this.unregisterReceiver(NetworkDetectedReceiver);
        }catch (Exception ignored){}
        super.onDestroy();
    }

    // 구독 결제 확인
    public Task<Boolean> checkCorePlus() {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();


        // 핼퍼 setup
        IabHelper iaphelper = new IabHelper(this, getString(R.string.GP_LICENSE_KEY));
        iaphelper.startSetup(result -> {
            if (!result.isSuccess()) {
                taskCompletionSource.setException(new Exception(result.getMessage()));
                return;
            }

            try {
                iaphelper.queryInventoryAsync((result2, inv) -> {
                    if (result2.isFailure()) {
                        //getPurchases() 실패했을때
                        taskCompletionSource.setException(new Exception("getPurchases 실패"));
                        return;
                    }

                    //해당 아이템 구매 여부 체크
                    Purchase purchase = inv.getPurchase(RemoteConfig.CorePlusItemId);

                    if (purchase != null && verifyDeveloperPayload(purchase)) {
                        //해당 아이템을 가지고 있는 경우.
                        //아이템에대한 처리를 한다.
                        //alreadyBuyedItem();
                        taskCompletionSource.setResult(true);
                    } else {
//                deferred.resolve(false);
                        taskCompletionSource.setResult(false);
                    }
                });
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
                taskCompletionSource.setException(e);
            }
        });

        return taskCompletionSource.getTask().addOnCompleteListener(task -> DataContainer.getInstance().isPlus = task.getResult());
    }


    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return payload.equals(DataContainer.getInstance().getUid(getApplication()));
    }

    public void startProgressDialog() {
        UiUtil.getInstance().startProgressDialog(BaseActivity.this);
    }

    public void stopProgressDialog() {
        UiUtil.getInstance().stopProgressDialog();
    }

    public void deleteMyIdentifier() {
        @SuppressLint("HardwareIds") String deviceIdentifier = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseDatabase.getInstance().getReference("identifier").child(deviceIdentifier).removeValue();
    }

    public User getUser() {
        return DataContainer.getInstance().getUser(this::appRestert);
    }

    public void appRestert() {
        UiUtil.getInstance().restartApp(getApplicationContext());
    }

    public void logout() {
        try {
            DataContainer.getInstance().getMyUserRef().child("token").removeValue().addOnSuccessListener(aVoid -> {
                FirebaseAuth.getInstance().signOut();
                DataContainer.getInstance().setUser(null);
                startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                finish();
            });
        } catch (Exception e) {
            e.printStackTrace();
            appRestert();
        }
    }

    interface OnChangeNetworkStatusListener {
        void OnChanged(int status);
    }

    OnChangeNetworkStatusListener listener = status -> {
        switch (status) {
            case 0:
                startProgressDialog2();
                break;
            default:
                stopProgressDialog2();
                break;
        }
    };

    // 네트워크 상태 감지를 위한 리스너
    BroadcastReceiver NetworkDetectedReceiver = new BroadcastReceiver() {
        private OnChangeNetworkStatusListener onChangeNetworkStatusListener = null;

        @Override
        public void onReceive(final Context context, final Intent intent) {
            int status = NetworkUtil.getConnectivityStatus(context);
            onChangeNetworkStatusListener = listener;
            onChangeNetworkStatusListener.OnChanged(status);
        }
    };

    private void registerWifiReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(NetworkDetectedReceiver, filter);
    }


    public void setProgressDialog2() {
        progressDialog2 = new ProgressDialog(this, R.style.MyTheme);
        progressDialog2.setIndeterminate(true);
        progressDialog2.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog2.setIndeterminateDrawable(this.getResources().getDrawable(R.drawable.network_error_drawable_animation));
        progressDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog2.setCancelable(false);
    }

    public void startProgressDialog2() {
        if (!progressDialog2.isShowing()) {
            progressDialog2.show();
            Toast.makeText(this, "네트워크를 확인해주세요", Toast.LENGTH_LONG).show();
        }
    }

    public void stopProgressDialog2() {
        if (progressDialog2 != null && progressDialog2.isShowing() && progressDialog2.getContext() != null)
            progressDialog2.dismiss();
    }

    // 예외치 못한 상황 처리리
    private Thread.UncaughtExceptionHandler mCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {

            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "비정상 종료로 앱이 재시작됩니다.", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }.start();

            try {
                Thread.sleep(2000);
            } catch (Exception ignored) {
            }

            appRestert();

            // This will make Crashlytics do its job
            mDefaultUEH.uncaughtException(thread, ex);
        }
    };

    public boolean isOldFriends(String cUuid) {
        return DataContainer.getInstance().isPlus || FireBaseUtil.getInstance().isOldFriends(cUuid, getUser()) || DataContainer.getInstance().getUid(getApplication()).equals(cUuid);
    }

}
