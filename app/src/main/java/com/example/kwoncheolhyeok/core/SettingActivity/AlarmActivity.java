package com.example.kwoncheolhyeok.core.SettingActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.R;

import butterknife.Bind;

/**
 * Created by Kwon on 2018-01-04.
 */

public class AlarmActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {


    Toolbar toolbar = null;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_alarm_activity);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.alarm), MODE_PRIVATE);
        editor = sharedPref.edit();

        Switch switch_chat = (Switch) findViewById(R.id.switch1);
        Switch switch_follow = (Switch) findViewById(R.id.switch2);
        Switch switch_friend = (Switch) findViewById(R.id.switch3);
        Switch switch_privPic = (Switch) findViewById(R.id.switch4);
        Switch switch_post = (Switch) findViewById(R.id.switch5);
        Switch switch_like = (Switch) findViewById(R.id.switch6);

        boolean isCheck = sharedPref.getBoolean(getString(R.string.alertChat), true);
        switch_chat.setChecked(isCheck);
        isCheck = sharedPref.getBoolean(getString(R.string.alertFolow), true);
        switch_follow.setChecked(isCheck);
        isCheck = sharedPref.getBoolean(getString(R.string.alertFriend), true);
        switch_friend.setChecked(isCheck);
        isCheck = sharedPref.getBoolean(getString(R.string.alertUnlockPic), true);
        switch_privPic.setChecked(isCheck);
        isCheck = sharedPref.getBoolean(getString(R.string.alertPost), true);
        switch_post.setChecked(isCheck);
        isCheck = sharedPref.getBoolean(getString(R.string.alertLike), true);
        switch_like.setChecked(isCheck);

        switch_chat.setOnCheckedChangeListener(this);
        switch_follow.setOnCheckedChangeListener(this);
        switch_friend.setOnCheckedChangeListener(this);
        switch_privPic.setOnCheckedChangeListener(this);
        switch_post.setOnCheckedChangeListener(this);
        switch_like.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
        int button = compoundButton.getId();
        String switch_name = null;
        switch (button) {
            case R.id.switch1:
                switch_name = getString(R.string.alertChat);
                break;
            case R.id.switch2:
                switch_name = getString(R.string.alertFolow);
                break;
            case R.id.switch3:
                switch_name = getString(R.string.alertFriend);
                break;
            case R.id.switch4:
                switch_name = getString(R.string.alertUnlockPic);
                break;
            case R.id.switch5:
                switch_name = getString(R.string.alertPost);
                break;
            case R.id.switch6:
                switch_name = getString(R.string.alertLike);
                break;
        }
        editor.putBoolean(switch_name, isCheck).apply();
    }

    // 뒤로가기 버튼 기능
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}