package com.example.kwoncheolhyeok.core.SettingActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.kwoncheolhyeok.core.R;

/**
 * Created by Kwon on 2018-01-04.
 */

public class AccountActivity extends AppCompatActivity {

    Toolbar toolbar = null;

    RelativeLayout set_password = null;
    RelativeLayout new_password_layout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_account_activity);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        set_password = findViewById(R.id.layout1);
        new_password_layout = findViewById(R.id.layout2);


        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        new_password_layout.setVisibility(View.GONE);
        set_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new_password_layout.getVisibility() == View.VISIBLE) {
                    new_password_layout.setVisibility(View.GONE);
                } else {
                    new_password_layout.setVisibility(View.VISIBLE);
                }
            }
        });

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