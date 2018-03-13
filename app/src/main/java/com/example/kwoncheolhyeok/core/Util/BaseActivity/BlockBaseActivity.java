package com.example.kwoncheolhyeok.core.Util.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.kwoncheolhyeok.core.Activity.MainActivity;
import com.example.kwoncheolhyeok.core.Event.SomeoneBlocksMeEvent;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.BusProvider;
import com.example.kwoncheolhyeok.core.Util.SharedPreferencesUtil;
import com.squareup.otto.Subscribe;

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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void FinishActivity(SomeoneBlocksMeEvent someoneBlocksMeEvent){
        finish();
    }
}
