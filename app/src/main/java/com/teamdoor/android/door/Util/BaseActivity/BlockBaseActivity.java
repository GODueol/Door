package com.teamdoor.android.door.Util.BaseActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.squareup.otto.Bus;
import com.teamdoor.android.door.Event.TargetUserBlocksMeEvent;
import com.teamdoor.android.door.Util.BusProvider;
import com.teamdoor.android.door.Util.SharedPreferencesUtil;

/**
 * Created by Administrator on 2018-03-13.
 */

public abstract class BlockBaseActivity extends BaseActivity{


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
