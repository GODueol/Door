package com.example.kwoncheolhyeok.core.ClubActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.kwoncheolhyeok.core.R;

/**
 * Created by KwonCheolHyeok on 2017-01-14.
 */

public class Club_Filter_Activity extends AppCompatActivity {

    Toolbar toolbar = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.club_filter_activity);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

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
    };


    public void androidCheckBoxClicked(View view) {
        // action for checkbox click
        switch (view.getId()) {
            case R.id.checkBox1:
                //DO something when user check the box
                break;
            case R.id.checkBox2:
                //DO something when user check the box
                break;
            case R.id.checkBox3:
                //DO something when user check the box
                break;
            case R.id.checkBox4:
                //DO something when user check the box
                break;
            case R.id.checkBox5:
                //DO something when user check the box
                break;
            case R.id.checkBox6:
                //DO something when user check the box
                break;
            case R.id.checkBox7:
                //DO something when user check the box
                break;
            case R.id.checkBox8:
                //DO something when user check the box
                break;
            case R.id.age_checkBox1:
                //DO something when user check the box
                break;
            case R.id.age_checkBox2:
                //DO something when user check the box
                break;
            case R.id.age_checkBox3:
                //DO something when user check the box
                break;
            case R.id.age_checkBox4:
                //DO something when user check the box
                break;
            case R.id.age_checkBox5:
                //DO something when user check the box
                break;
            case R.id.theme_checkBox1:
                //DO something when user check the box
                break;
            case R.id.theme_checkBox2:
                //DO something when user check the box
                break;
            case R.id.theme_checkBox3:
                //DO something when user check the box
                break;
            case R.id.theme_checkBox4:
                //DO something when user check the box
                break;
            case R.id.theme_checkBox5:
                //DO something when user check the box
                break;
            case R.id.theme_checkBox6:
                //DO something when user check the box
                break;
            case R.id.theme_checkBox7:
                //DO something when user check the box
                break;
            case R.id.theme_checkBox8:
                //DO something when user check the box
                break;
            case R.id.theme_checkBox9:
                //DO something when user check the box
                break;
            case R.id.theme_checkBox10:
                //DO something when user check the box
                break;
            case R.id.theme_checkBox11:
                //DO something when user check the box
                break;
            case R.id.theme_checkBox12:
                //DO something when user check the box
                break;
            case R.id.theme_checkBox13:
                //DO something when user check the box
                break;
            case R.id.theme_checkBox14:
                //DO something when user check the box
                break;
            case R.id.theme_checkBox15:
                //DO something when user check the box
                break;

        }
    }


}