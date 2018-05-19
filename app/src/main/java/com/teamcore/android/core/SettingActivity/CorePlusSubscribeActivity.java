package com.teamcore.android.core.SettingActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.android.vending.billing.IInAppBillingService;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.GlideApp;
import com.teamcore.android.core.Util.UiUtil;

/**
 * Created by Kwon on 2018-04-07.
 */

public class CorePlusSubscribeActivity extends AppCompatActivity {

    Toolbar toolbar = null;
    IInAppBillingService mService;

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


        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };

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