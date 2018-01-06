package com.example.kwoncheolhyeok.core.SettingActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Kwon on 2018-01-03.
 */

public class SettingActivity extends AppCompatActivity {

    Toolbar toolbar = null;

    ListView setting_list = null;
    private ArrayAdapter<String> listAdapter;

    TextView t = null;
    TextView dasdf = null;

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

        String[] setting_contents = new String[]{"CORE +", "공지사항", "계정", "알림", "블럭", "앱 정보", "로그아웃"};

        ArrayList<String> setting = new ArrayList<String>();
        setting.addAll(Arrays.asList(setting_contents));

        listAdapter = new ArrayAdapter<String>(this, R.layout.setting_activity_list_item, setting);


        setting_list.setAdapter(listAdapter);

        setting_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(SettingActivity.this, setting_contents[position], Toast.LENGTH_SHORT).show();
                switch (position) {
                    case 0:
//                        Toast.makeText(SettingActivity.this, setting_contents[position], Toast.LENGTH_SHORT).show();
                        Intent i = new Intent (SettingActivity.this, CorePlusActivity.class);
                        startActivity(i);
                        break;
                    case 1:
                        Intent i2 = new Intent (SettingActivity.this, NoticeActivity.class);
                        startActivity(i2);
                        break;
                    case 2:
                        Intent i3 = new Intent (SettingActivity.this, AccountActivity.class);
                        startActivity(i3);
                        break;
                    case 3:
                        Intent i4 = new Intent (SettingActivity.this, AlarmActivity.class);
                        startActivity(i4);
                        break;
                    case 4:
                        Intent i5 = new Intent (SettingActivity.this, BlockActivity.class);
                        startActivity(i5);
                        break;
                    case 5:
                        Intent i6 = new Intent (SettingActivity.this, AppInfoActivity.class);
                        startActivity(i6);
                        break;
                    case 6:
//                        logout();
                        break;
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