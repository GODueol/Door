package com.teamcore.android.core.SettingActivity;

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

    private String PUBLIC_KEY;
    IInAppBillingService mService;
    ServiceConnection mServiceConn;
    IabHelper iaphelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_coreplus_activity);
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
        btn_cp_subs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void setBilingService(){
        PUBLIC_KEY = getString(R.string.GP_LICENSE_KEY);

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

        Intent intent=new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage("com.android.vending");
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);

        iaphelper = new IabHelper(this, PUBLIC_KEY);
        /**
         * 보유중인 아이템 체크
         */
        IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {

                if (iaphelper == null) return;
                if (result.isFailure()) {
                    //getPurchases() 실패했을때

                    return;
                }
                //해당 아이템 구매 여부 체크
                Purchase purchase = inv.getPurchase(getString(R.string.purchase));

                if (purchase != null && verifyDeveloperPayload(purchase)) {
                    //해당 아이템을 가지고 있는 경우.
                    //아이템에대한 처리를 한다.
                    //alreadyBuyedItem();

                    /**
                     * 재구매가 가능한 경우는 아래와 같이 구매 목록을 소비와 동시에 그에 맞는 이벤트를 실행해
                     * 사용자가 같은 아이템을 재 구매 가능하도록 해야합니다.
                     * 저는 1회성 아이템이므로 소비과정은 생략하겠습니다.
                     */
                    String payload= DataContainer.getInstance().getUid();
                    try {
                        iaphelper.consumeAsync(inv.getPurchase(getString(R.string.purchase)),mConsumeFinishedListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        iaphelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
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
            }
        });
    }

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {

            // mHelper가 소거되었다면 종료
            if (iaphelper == null) return;

            // 이 샘플에서는 "관리되지 않는 제품"은 "가스" 한가지뿐이므로 상품에 대한 체크를 하지 않습니다.
            // 하지만 다수의 제품이 있을 경우 상품 아이디를 비교하여 처리할 필요가 있습니다.
            if (result.isSuccess()) {
                // 성공적으로 소진되었다면 상품의 효과를 게임상에 적용합니다. 여기서는 가스를 충전합니다.
            }
            else {
            }
        }
    };

    private void buyItem(String item) {

        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                if (iaphelper == null) return;

                if (result.isFailure()) {
                    Toast.makeText(CorePlusActivity.this, "구매 실패, 정상 경로를 이용해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    if(verifyDeveloperPayload(info)){
                        //보낸 신호와 맞는경우
                        if(info.getSku().equals(getString(R.string.purchase))){
                            Toast.makeText(CorePlusActivity.this, "구매 성공", Toast.LENGTH_SHORT).show();
                            //alreadyBuyedItem();
                        }else{
                            Toast.makeText(CorePlusActivity.this, "구매 실패, 정상 경로를 이용해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(CorePlusActivity.this, "구매 실패, 정상 경로를 이용해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        };

        try {
            String payLoad = DataContainer.getInstance().getUid();

            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), item, "inapp", payLoad);
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            if (pendingIntent != null) {
                iaphelper.launchPurchaseFlow(this, item, 1001, mPurchaseFinishedListener, payLoad);
            } else {
                // 결제가 막혔다면
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

        if(payload.equals(DataContainer.getInstance().getUid())){
            return true;
        }else{
            return false;
        }
        /*
         * TODO: 위의 그림에서 설명하였듯이 로컬 저장소 또는 원격지로부터 미리 저장해둔 developerPayload값을 꺼내 변조되지 않았는지 여부를 확인합니다.
         *
         * 이 payload의 값은 구매가 시작될 때 랜덤한 문자열을 생성하는것은 충분히 좋은 접근입니다.
         * 하지만 두개의 디바이스를 가진 유저가 하나의 디바이스에서 결제를 하고 다른 디바이스에서 검증을 하는 경우가 발생할 수 있습니다.
         * 이 경우 검증을 실패하게 될것입니다. 그러므로 개발시에 다음의 상황을 고려하여야 합니다.
         *
         * 1. 두명의 유저가 같은 아이템을 구매할 때, payload는 같은 아이템일지라도 달라야 합니다.
         *    두명의 유저간 구매가 이어져서는 안됩니다.
         *
         * 2. payload는 앱을 두대를 사용하는 유저의 경우에도 정상적으로 동작할 수 있어야 합니다.
         *    이 payload값을 저장하고 검증할 수 있는 자체적인 서버를 구축하는것을 권장합니다.
         */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (iaphelper == null) return;
        if (!iaphelper.handleActivityResult(requestCode, resultCode, data)) {
            //처리할 결과물이 아닐 경우 이곳으로 빠져 기본처리를 하도록한다
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

}