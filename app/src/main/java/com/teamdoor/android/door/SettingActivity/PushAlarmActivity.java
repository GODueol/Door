package com.teamdoor.android.door.SettingActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.teamdoor.android.door.R;
import com.teamdoor.android.door.Util.SharedPreferencesUtil;

/**
 * Created by Kwon on 2018-01-04.
 */

public class PushAlarmActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {


    Toolbar toolbar = null;
    private SharedPreferencesUtil SPUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_alarm_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        SPUtil = new SharedPreferencesUtil(getApplicationContext());

        Switch switch_vibrate = (Switch) findViewById(R.id.switch0);
        Switch switch_chat = (Switch) findViewById(R.id.switch1);
        Switch switch_follow = (Switch) findViewById(R.id.switch2);
        Switch switch_friend = (Switch) findViewById(R.id.switch3);
        Switch switch_post = (Switch) findViewById(R.id.switch5);
        Switch switch_answer = (Switch) findViewById(R.id.switch6);
        Switch switch_like = (Switch) findViewById(R.id.switch7);


        boolean isCheck = SPUtil.getSwitchState(getString(R.string.alertChat));
        switch_chat.setChecked(isCheck);
        isCheck = SPUtil.getSwitchState(getString(R.string.alertFolow));
        switch_follow.setChecked(isCheck);
        isCheck = SPUtil.getSwitchState(getString(R.string.alertFriend));
        switch_friend.setChecked(isCheck);
        isCheck = SPUtil.getSwitchState(getString(R.string.alertPost));
        switch_post.setChecked(isCheck);
        isCheck = SPUtil.getSwitchState(getString(R.string.alertAnswer));
        switch_answer.setChecked(isCheck);
        isCheck = SPUtil.getSwitchState(getString(R.string.alertLike));
        switch_like.setChecked(isCheck);
        isCheck = SPUtil.getSwitchState(getString(R.string.set_vibrate));
        switch_vibrate.setChecked(isCheck);

        switch_vibrate.setOnCheckedChangeListener(this);
        switch_chat.setOnCheckedChangeListener(this);
        switch_follow.setOnCheckedChangeListener(this);
        switch_friend.setOnCheckedChangeListener(this);
        switch_post.setOnCheckedChangeListener(this);
        switch_answer.setOnCheckedChangeListener(this);
        switch_like.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
        int button = compoundButton.getId();
        String switch_name = null;
        switch (button) {
            case R.id.switch0:
                switch_name = getString(R.string.set_vibrate);
                break;
            case R.id.switch1:
                switch_name = getString(R.string.alertChat);
                break;
            case R.id.switch2:
                switch_name = getString(R.string.alertFolow);
                break;
            case R.id.switch3:
                switch_name = getString(R.string.alertFriend);
                break;
            case R.id.switch5:
                switch_name = getString(R.string.alertPost);
                break;
            case R.id.switch6:
                switch_name = getString(R.string.alertAnswer);
                break;
            case R.id.switch7:
                switch_name = getString(R.string.alertLike);
                break;
        }
        SPUtil.setSwitchState(switch_name, isCheck);
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