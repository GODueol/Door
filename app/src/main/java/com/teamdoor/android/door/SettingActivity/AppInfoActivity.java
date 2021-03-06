package com.teamdoor.android.door.SettingActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamdoor.android.door.BuildConfig;
import com.teamdoor.android.door.R;

/**
 * Created by Kwon on 2018-01-04.
 */

public class AppInfoActivity extends AppCompatActivity {

    Toolbar toolbar = null;

    RelativeLayout access_terms1 = null;
    RelativeLayout access_terms2 = null;
    RelativeLayout access_terms3 = null;

    RelativeLayout SendBugReport = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_appinfo_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //status bar 없앰


        access_terms1 = (RelativeLayout) findViewById(R.id.layout2);
        access_terms1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AccessTerms1.class);
                startActivity(i);
            }
        });

        access_terms2 = (RelativeLayout) findViewById(R.id.layout3);
        access_terms2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AccessTerms2.class);
                startActivity(i);
            }
        });

        access_terms3 = (RelativeLayout) findViewById(R.id.layout4);
        access_terms3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AccessTerms3.class);
                startActivity(i);
            }
        });

        SendBugReport = (RelativeLayout) findViewById(R.id.layout5);
        SendBugReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendBugReportDialog(AppInfoActivity.this, new SendBugReportDialog.SendBugReportDialogListener() {
                    @Override
                    public void show_sendBugReport_dialog() {
                        show_sendBugReport_dialog();
                    }
                }).show();
            }
        });

        ((TextView)findViewById(R.id.appVersion)).setText(String.valueOf(BuildConfig.VERSION_CODE));
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

    //알람 다이얼로그
    public void show_sendBugReport_dialog() {

    }

}