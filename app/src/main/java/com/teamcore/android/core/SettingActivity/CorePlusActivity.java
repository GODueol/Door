package com.teamcore.android.core.SettingActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.firebase.database.FirebaseDatabase;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.GlideApp;
import com.teamcore.android.core.Util.UiUtil;
import com.teamcore.android.core.Util.bilingUtil.IabHelper;
import com.teamcore.android.core.Util.bilingUtil.IabResult;
import com.teamcore.android.core.Util.bilingUtil.Inventory;
import com.teamcore.android.core.Util.bilingUtil.Purchase;

/**
 * Created by Kwon on 2018-01-04.
 */

public class CorePlusActivity extends AppCompatActivity {

    Toolbar toolbar = null;

    IInAppBillingService mService;
    ServiceConnection mServiceConn;
    IabHelper iaphelper;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int contentView = (DataContainer.getInstance().isPlus ? R.layout.setting_coreplus_activity : R.layout.setting_normal_activity);
        setContentView(contentView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);


        ImageView cor2 = (ImageView) findViewById(R.id.cor2);
        GlideApp.with(this)
                .load(UiUtil.resourceToUri(this, R.drawable.cp_main))
                .fitCenter()
                .into(cor2);


        ImageView img_cp_4 = (ImageView) findViewById(R.id.img_cp_4);
        GlideApp.with(this)
                .load(UiUtil.resourceToUri(this, R.drawable.cp_4))
                .fitCenter()
                .into(img_cp_4);
        setBilingService();

        Button btn_cp_subs = (Button) findViewById(R.id.btn_cp_subs);
        btn_cp_subs.setOnClickListener(view -> buyItem(getString(R.string.subscribe)));

        TextView sub_txt2 = (TextView) findViewById(R.id.sub_txt2);
        sub_txt2.setText(DataContainer.getInstance().getUser().getId() + sub_txt2.getText().toString());

    }

    private void setBilingService() {
        String PUBLIC_KEY = getString(R.string.GP_LICENSE_KEY);

        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
            }
        };

        // 결제 서비스를 위한 인텐트 초기화
        Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage("com.android.vending");
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);

        // 핼퍼 setup
        iaphelper = new IabHelper(this, PUBLIC_KEY);
        iaphelper.startSetup(result -> {
            if (!result.isSuccess()) {
                Toast.makeText(CorePlusActivity.this, "문제발생", Toast.LENGTH_SHORT).show();
                return;
            }

            if (iaphelper == null) return;
            try {
                iaphelper.queryInventoryAsync(mGotInventoryListener);
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        });
    }

    private void buyItem(String item) {
        try {
            String payLoad = DataContainer.getInstance().getUid();

            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), item, "subs", payLoad);
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            if (pendingIntent != null) {
                iaphelper.launchSubscriptionPurchaseFlow(this, getString(R.string.subscribe), 1001, mPurchaseFinishedListener, payLoad);
            } else {
                // 결제가 막혔다면 왜 결제가 막혀있찌 대체????
                Toast.makeText(CorePlusActivity.this, "구매실패", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }



    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        return payload.equals(DataContainer.getInstance().getUid());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (iaphelper == null) return;
        if (!iaphelper.handleActivityResult(requestCode, resultCode, data)) {
            //처리할 결과물이 아닐 경우 이곳으로 빠져 기본처리를 하도록한다
            Toast.makeText(this, "지금", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    // 뒤로가기 버튼 기능
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 보유중인 아이템 체크
     */
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
            Log.d(getClass().getSimpleName(), "onQueryInventoryFinished");
            if (iaphelper == null) return;
            if (result.isFailure()) {
                Toast.makeText(getApplicationContext(), "onQueryInventoryFinished 실패", Toast.LENGTH_SHORT).show();
                //getPurchases() 실패했을때
                return;
            }
/*
            Bundle activeSubs;
            try {
                activeSubs = mService.getPurchases(3, getPackageName(), "subs", DataContainer.getInstance().getUid());
            } catch (RemoteException e) {
                e.printStackTrace();
            }*/

            //해당 아이템 구매 여부 체크
            Purchase purchase = inv.getPurchase(getString(R.string.subscribe));

            if (purchase != null && purchase.getPurchaseState() ==0 && verifyDeveloperPayload(purchase)) {
                //해당 아이템을 가지고 있는 경우.
                //아이템에대한 처리를 한다.
                Toast.makeText(getApplicationContext(), purchase.getPurchaseState() + "onQueryInventoryFinished 이미 보유중", Toast.LENGTH_SHORT).show();
            }
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase info) {
            if (iaphelper == null) return;
            if (result.isFailure()) {
                Toast.makeText(CorePlusActivity.this, result.getResponse() + "구매 실패, 정상 경로를 이용해주세요.111", Toast.LENGTH_SHORT).show();
            } else {
                if (verifyDeveloperPayload(info)) {
                    //보낸 신호와 맞는경우
                    if (info.getSku().equals(getString(R.string.subscribe))) {
                        FirebaseDatabase.getInstance().getReference("subscribe").child(DataContainer.getInstance().getUid()).child(String.valueOf(info.getPurchaseTime())).setValue(info);
                    } else {
                        Toast.makeText(CorePlusActivity.this, "구매 실패, 정상 경로를 이용해주세요.222", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CorePlusActivity.this, "구매 실패, 정상 경로를 이용해주세요.333", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


}