package com.teamcore.android.core.SettingActivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.GlideApp;
import com.teamcore.android.core.Util.UiUtil;

/**
 * Created by Kwon on 2018-01-04.
 */

public class CorePlusActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    Toolbar toolbar = null;
    private BillingProcessor bp;
    private String uuid;
    public static SkuDetails products;
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

        uuid = DataContainer.getInstance().getUid();
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
        bp = new BillingProcessor(this, getString(R.string.GP_LICENSE_KEY), this);

        Button btn_cp_subs = (Button) findViewById(R.id.btn_cp_subs);
        btn_cp_subs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subscribe();
            }
        });

    }

    private void subscribe() {
        if (bp.loadOwnedPurchasesFromGoogle() && !bp.isSubscribed(getString(R.string.subscribe))) {
            bp.subscribe(this, getString(R.string.subscribe));
        } else {
            Toast.makeText(this, "이미 구독중", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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


    @Override
    public void onProductPurchased(@NonNull String s, @Nullable TransactionDetails transactionDetails) {
        Toast.makeText(this, "구매완료", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable throwable) {
        if (errorCode != Constants.BILLING_RESPONSE_RESULT_USER_CANCELED) {
            String errorMessage = "에러발생(" + errorCode + ")";
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBillingInitialized() {
        products = bp.getSubscriptionListingDetails(getString(R.string.subscribe));
    }
}