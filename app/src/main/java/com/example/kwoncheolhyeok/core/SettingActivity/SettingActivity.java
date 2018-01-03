package com.example.kwoncheolhyeok.core.SettingActivity;

import android.app.LauncherActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.example.kwoncheolhyeok.core.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Kwon on 2018-01-03.
 */

public class SettingActivity extends AppCompatActivity {

    Toolbar toolbar = null;

    ListView setting_list = null;
    private ArrayAdapter<String> listAdapter ;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.setting_activity);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        setting_list = (ListView) findViewById(R.id.setting_list);

        String[] setting_contents = new String[] { "CORE +", "공지 사항", "계정 설정", "알림 설정", "블럭 목록", "앱 정보", "로그아웃"};

        ArrayList<String> setting = new ArrayList<String>();
        setting.addAll( Arrays.asList(setting_contents) );

        listAdapter = new ArrayAdapter<String>(this, R.layout.setting_activity_list_item,setting);


        setting_list.setAdapter( listAdapter );



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