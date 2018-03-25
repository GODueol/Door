package com.example.kwoncheolhyeok.core.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.kwoncheolhyeok.core.CorePage.CustomDialog;
import com.example.kwoncheolhyeok.core.Entity.AlarmSummary;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kwon on 2018-03-16.
 */

public class NavAlarmDialog extends CustomDialog {


    @Bind(R.id.navAlarmList)
    RecyclerView recyclerView;

    private NavAlarmAdapter navAlarmAdapter;
    private List<AlarmSummary> items;

    public NavAlarmDialog(@NonNull Context context) {
        super(context);
    }

    // 포스트 아이디로 받아온다면? (
    // 1. 내 코어에 누군가가 익명포스트를 남겼을떄
    // 2. 다른사람 코어의 익명포스트에 답변이 달렸을때
    // 3. 내 코어의 내글 나의 좋아요가 눌렸을떄

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_alarm_activity_main);
        ButterKnife.bind(this);
        setRecyclerView();
        setItems();

    }

    private void setRecyclerView(){
        items = new ArrayList<AlarmSummary>();



        navAlarmAdapter = new NavAlarmAdapter(getContext(),items);
        recyclerView.setAdapter(navAlarmAdapter);

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL)); //리사이클뷰 구분선

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
    }


    private void setItems(){
        String Uuid = DataContainer.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference("Alarm").child(Uuid).orderByChild("time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()){
                    DataSnapshot data = iterator.next();
                    AlarmSummary alarmSummary = data.child("alarmSummary").getValue(AlarmSummary.class);

                    // 포스트키를 잘라줌 (뒤에 Post,Like,Answer)
                    String key = data.getKey();
                    key = key.substring(0,key.lastIndexOf("_"));
                    alarmSummary.setPostId(key);
                    items.add(0,alarmSummary);
                }
                recyclerView.getRecycledViewPool().clear();
                navAlarmAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}