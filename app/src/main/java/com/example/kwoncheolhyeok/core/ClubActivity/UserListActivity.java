package com.example.kwoncheolhyeok.core.ClubActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.kwoncheolhyeok.core.R;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KwonCheolHyeok on 2016-12-27.
 */

public class UserListActivity  extends AppCompatActivity {

    Toolbar toolbar = null;
    private ListView club_member_list;


    // 기본 폰트 고정
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.club_member_list_activity);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        club_member_list = (ListView) findViewById(R.id.club_member_list);

        List<String> list = new ArrayList<String>();
        for(int i=0; i<20; i++){
            list.add(i+"");
        }
        Club_Member_ListAdapter Club_Member_ListAdapter = new Club_Member_ListAdapter(list, this);
        club_member_list.setAdapter(Club_Member_ListAdapter);

        club_member_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

//                Intent myIntent = new Intent(getActivity(), ClubActivity.class);
//                getActivity().startActivity(myIntent);

            }
        });

        View header = getLayoutInflater().inflate(R.layout.club_member_list_header, null, false);
        club_member_list.addHeaderView(header);

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


}