package com.example.kwoncheolhyeok.core.CorePage;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.kwoncheolhyeok.core.R;

import java.util.ArrayList;
import java.util.List;


public class CoreActivity extends AppCompatActivity {

    Toolbar toolbar = null;

    private ListView core_list_view;

//    // 기본 폰트 고정
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.core_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);


        core_list_view = (ListView) findViewById(R.id.core_listview);

        List<String> list = new ArrayList<String>();
        for(int i=0; i<10; i++){
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
    };




}