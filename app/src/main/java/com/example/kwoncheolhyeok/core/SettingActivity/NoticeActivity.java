package com.example.kwoncheolhyeok.core.SettingActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;

import com.example.kwoncheolhyeok.core.CorePage.NoticeAdapter;
import com.example.kwoncheolhyeok.core.Entity.Notice;
import com.example.kwoncheolhyeok.core.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Kwon on 2018-01-04.
 */

public class NoticeActivity extends AppCompatActivity {

    Toolbar toolbar = null;
    public RecyclerView recyclerView;
    private Query noticeQuery;
    private ValueEventListener noticeListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_notice_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);


        // Notice Set
        recyclerView = (RecyclerView) findViewById(R.id.core_listview);

        final ArrayList<Notice> list = new ArrayList<>();
        final NoticeAdapter noticeAdapter = new NoticeAdapter(list, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(noticeAdapter);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        // 코어 주인의 User Get
        noticeQuery = FirebaseDatabase.getInstance().getReference().child("notice").orderByChild("writeDate");
        noticeListner = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Notice notice = snapshot.getValue(Notice.class);
                    assert notice != null;
                    notice.setKey(snapshot.getKey());
                    list.add(0,notice);
                }
                noticeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        noticeQuery.addValueEventListener(noticeListner);

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
    protected void onDestroy() {
        if(noticeQuery != null && noticeListner != null) noticeQuery.removeEventListener(noticeListner);
        super.onDestroy();
    }
}