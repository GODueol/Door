package com.teamcore.android.core.Util.BaseActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.teamcore.android.core.Event.TargetUserBlocksMeEvent;
import com.teamcore.android.core.Util.BusProvider;
import com.teamcore.android.core.Util.SharedPreferencesUtil;
import com.squareup.otto.Bus;

/**
 * Created by Administrator on 2018-03-13.
 */

public abstract class BlockBaseActivity extends AppCompatActivity{


    protected SharedPreferencesUtil SPUtil;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SPUtil = new SharedPreferencesUtil(getApplicationContext());
        BusProvider.getInstance().register(this);

        Bus bus = BusProvider.getInstance();

        bus.toString();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    abstract public void FinishActivity(TargetUserBlocksMeEvent someoneBlocksMeEvent);

}
