package com.teamcore.android.core.Util.BaseActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.teamcore.android.core.Entity.User;
import com.teamcore.android.core.LoginActivity.LoginActivity;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.UiUtil;
import com.teamcore.android.core.Util.bilingUtil.IabHelper;
import com.teamcore.android.core.Util.bilingUtil.Purchase;

import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    private Timer timer;
    private boolean timerToast;
    private static Thread.UncaughtExceptionHandler mDefaultUEH;

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

            try{
                Thread.sleep(2000);
            }catch (Exception ignored){
            }

            appRestert();

            // This will make Crashlytics do its job
            mDefaultUEH.uncaughtException(thread, ex);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Second, set custom UncaughtExceptionHandler
        mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(mCaughtExceptionHandler);

        TimerTask detectedNetwrok = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (isNetworkAvailable()) {
                        // 네트워크 사용가능
                        timerToast = false;
                        stopProgressDialog2();
                    } else {
                        // 네트워크 사용 불가능
                        if (!timerToast) {
                            Toast.makeText(getApplicationContext(), "네트워크를 확인해주세요", Toast.LENGTH_LONG).show();
                        }
                        timerToast = true;
                        startProgressDialog2();
                    }
                });

            }
        };

        timer = new Timer();
        timer.schedule(detectedNetwrok, 0, 1000);
        super.onCreate(savedInstanceState);

    }


    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    // 구독 결제 확인
    public Promise<Boolean, String, Integer> checkCorePlus() {
        DeferredObject deferred = new DeferredObject();
        Promise promise = deferred.promise();


        // 핼퍼 setup
        IabHelper iaphelper = new IabHelper(this, getString(R.string.GP_LICENSE_KEY));


        IabHelper.QueryInventoryFinishedListener mGotInventoryListener = (result, inv) -> {
            if (result.isFailure()) {
                //getPurchases() 실패했을때
                deferred.reject("getPurchases 실패");
                return;
            }

            //해당 아이템 구매 여부 체크
            Purchase purchase = inv.getPurchase(getString(R.string.subscribe));

            if (purchase != null && verifyDeveloperPayload(purchase)) {
                //해당 아이템을 가지고 있는 경우.
                //아이템에대한 처리를 한다.
                //alreadyBuyedItem();
                deferred.resolve(true);

            } else {
//                deferred.resolve(false);
                deferred.resolve(false);
            }
        };

        iaphelper.startSetup(result -> {
            if (!result.isSuccess()) {
                deferred.reject(result.getMessage());
                return;
            }

            try {
                iaphelper.queryInventoryAsync(mGotInventoryListener);
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
                deferred.reject(e.getMessage());
            }
        });

        return promise.done(isPlus -> DataContainer.getInstance().isPlus = (boolean) isPlus);
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

    public void startProgressDialog2() {
        UiUtil.getInstance().startProgressDialog2(BaseActivity.this);
    }

    public void stopProgressDialog2() {
        UiUtil.getInstance().stopProgressDialog2();
    }


    public void deleteMyIdentifier() {
        @SuppressLint("HardwareIds") String deviceIdentifier = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseDatabase.getInstance().getReference("identifier").child(deviceIdentifier).removeValue();
    }

    public User getUser() {
        return DataContainer.getInstance().getUser(this::logout);
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

}
