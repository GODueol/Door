package com.example.kwoncheolhyeok.core.MessageActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;

import com.example.kwoncheolhyeok.core.FriendsActivity.ExpandableListAdapter;
import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.MessageVO;
import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.messageRecyclerAdapter;
import com.example.kwoncheolhyeok.core.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    Toolbar toolbar = null;

    messageRecyclerAdapter messageRecyclerAdapter;
    RecyclerView messageList;
    List<MessageVO> listrowItem;


    public void testMessageData(){
        listrowItem = new ArrayList<MessageVO>();

        for(int i=0; i<10; i++){
            MessageVO mv = new MessageVO();
            mv.setNickname("kaikai");
            mv.setContent("카이카이너모 멋졍"+i);
            mv.setDate("2016-10-17 18:30");
            mv.setEditimg(R.drawable.ic_dehaze_black_24dp);
            listrowItem.add(mv);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.message_activity);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        // preparing list data
        messageList = (RecyclerView) findViewById(R.id.messagelist);
        testMessageData();
        messageList.setAdapter(new messageRecyclerAdapter(listrowItem,R.layout.messagelist_row));
        messageList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        messageList.setItemAnimator(new DefaultItemAnimator());
        /*****************************************************************/




        // Listview on child click listener
     /*   expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
//				Toast.makeText(getApplicationContext(),listDataHeader.get(groupPosition)+ " : "+ listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition),Toast.LENGTH_SHORT)
//						.show();
                Intent i = new Intent(MessageActivity.this, ChattingActivity.class);
                startActivityForResult(i, 0);
                return false;
            }
        });*/
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