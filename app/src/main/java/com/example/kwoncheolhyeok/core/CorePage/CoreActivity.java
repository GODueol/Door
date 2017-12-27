package com.example.kwoncheolhyeok.core.CorePage;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.MyApplcation;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;

import java.util.ArrayList;
import java.util.List;


public class CoreActivity extends AppCompatActivity {

    Toolbar toolbar = null;

    TextView media_player = null;
    TextView other_user = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.core_activity);

        Intent intent = getIntent();
        final String uuid = intent.getStringExtra("uuid");

        //스크린샷 방지
        MyApplcation.getInstance().allowUserSaveScreenshot(false);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                // 자신, 타인 액티비티 구별
                Intent i;
                if(uuid.equals(DataContainer.getInstance().getUid())){
                    i = new Intent(CoreActivity.this, CoreWriteActivity.class);
                } else {
                    i = new Intent(CoreActivity.this, otherUser_write_core.class);
                }

                startActivity(i);
            }
        });

        media_player = findViewById(R.id.media_player);
        media_player.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CoreActivity.this, audioactivity.class);
                startActivity(i);
            }
        });

        other_user = findViewById(R.id.other_user);
        other_user.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CoreActivity.this, otherUser_write_core.class);
                startActivity(i);
            }
        });

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);


        ListView core_list_view = findViewById(R.id.core_listview);

        List<String> list = new ArrayList<>();
        for(int i=0; i<3; i++){
            list.add(i+"");
        }
        Core_List_Adapter Core_List_Adapter = new Core_List_Adapter(list, this);
        core_list_view.setAdapter(Core_List_Adapter);

        core_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

//                Intent myIntent = new Intent(getActivity(), ClubActivity.class);
//                getActivity().startActivity(myIntent);

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

    @Override
    public void onResume() {
        super.onResume();
        MyApplcation.getInstance().registerScreenshotObserver();
    }

    @Override
    public void onPause() {
        super.onPause();
        MyApplcation.getInstance().unregisterScreenshotObserver();
    }


}