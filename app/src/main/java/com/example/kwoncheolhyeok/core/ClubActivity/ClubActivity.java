package com.example.kwoncheolhyeok.core.ClubActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.kwoncheolhyeok.core.MessageActivity.MessageActivity;
import com.example.kwoncheolhyeok.core.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KwonCheolHyeok on 2016-12-26.
 */

public class ClubActivity  extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar = null;
    Button message = null;
    Button userlist = null;
    Button schedule = null;
    Button setting = null;

    ImageButton FAB;

    private ListView club_timeline_contents_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.club_activity);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        message = (Button) findViewById(R.id.btn_message);
        message.setOnClickListener(this);
        userlist = (Button) findViewById(R.id.btn_user_list);
        userlist.setOnClickListener(this);
        schedule = (Button) findViewById(R.id.btn_schedule);
        schedule.setOnClickListener(this);
        setting = (Button) findViewById(R.id.btn_setting);
        setting.setOnClickListener(this);

        // Fab button
        FAB = (ImageButton) findViewById(R.id.imageButton);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        club_timeline_contents_list = (ListView) findViewById(R.id.club_timeline_contents_listview);

        List<String> list = new ArrayList<String>();
        for(int i=0; i<10; i++){
            list.add(i+"");
        }
        ClubTimelineContentsListAdapter ClubTimelineContentsListAdapter = new ClubTimelineContentsListAdapter(list, this);
        club_timeline_contents_list.setAdapter(ClubTimelineContentsListAdapter);

        club_timeline_contents_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

//                Intent myIntent = new Intent(getActivity(), ClubActivity.class);
//                getActivity().startActivity(myIntent);

            }
        });

        View header = getLayoutInflater().inflate(R.layout.club_activity_timeline_list_header, null, false);
        club_timeline_contents_list.addHeaderView(header);


    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.club_activity_menu, menu);
        return true;
    }


    // 뒤로가기 버튼 기능
    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }

        int id = item.getItemId();

        if (id == R.id.Join_this_club) {
//            Intent i = new Intent(MainActivity.this, MapsActivity.class);
//            startActivityForResult(i, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_message:
                Intent i = new Intent(ClubActivity.this, MessageActivity.class);
                startActivity(i);
                break;

            case R.id.btn_user_list:
                Intent i2 = new Intent(ClubActivity.this, UserListActivity.class);
                startActivity(i2);
                break;

            case R.id.btn_schedule:
                Intent i3 = new Intent(ClubActivity.this, ScheduleActivity.class);
                startActivity(i3);
                break;

            case R.id.btn_setting:
                Intent i4 = new Intent(ClubActivity.this, ClubSettingActivity.class);
                startActivity(i4);
                break;

            default:
                break;
        }
    }



}