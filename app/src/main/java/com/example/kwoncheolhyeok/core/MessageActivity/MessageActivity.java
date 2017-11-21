package com.example.kwoncheolhyeok.core.MessageActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;

import com.example.kwoncheolhyeok.core.FriendsActivity.ExpandableListAdapter;
import com.example.kwoncheolhyeok.core.R;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    Toolbar toolbar = null;


    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;


    // 기본 폰트 고정
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    /*
   * Preparing the list data
   */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Message");
        listDataHeader.add("Club Message");

        // Adding child data
        List<String> Friend_request_receive = new ArrayList<String>();
        Friend_request_receive.add("The Shawshank Redemption");
        Friend_request_receive.add("The Godfather");
        Friend_request_receive.add("The Godfather: Part II");
        Friend_request_receive.add("Pulp Fiction");
        Friend_request_receive.add("The Good, the Bad and the Ugly");
        Friend_request_receive.add("The Dark Knight");
        Friend_request_receive.add("12 Angry Men");

        List<String> Friend_request_send = new ArrayList<String>();
        Friend_request_send.add("The Conjuring");
        Friend_request_send.add("Despicable Me 2");
        Friend_request_send.add("Turbo");
        Friend_request_send.add("Grown Ups 2");
        Friend_request_send.add("Red 2");
        Friend_request_send.add("The Wolverine");


        listDataChild.put(listDataHeader.get(0), Friend_request_receive); // Header, Child data
        listDataChild.put(listDataHeader.get(1), Friend_request_send);

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


        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        //Expandable Listview 처음부터 확장된 상태로 보이게 함 (Message / Club Message 그룹을 확장)
        expListView.expandGroup(0);
        expListView.expandGroup(1);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
//				Toast.makeText(getApplicationContext(),
//						listDataHeader.get(groupPosition) + " Expanded",
//						Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
//				Toast.makeText(getApplicationContext(),
//						listDataHeader.get(groupPosition) + " Collapsed",
//						Toast.LENGTH_SHORT).show();
            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
//				Toast.makeText(getApplicationContext(),listDataHeader.get(groupPosition)+ " : "+ listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition),Toast.LENGTH_SHORT)
//						.show();
                Intent i = new Intent(MessageActivity.this, ChattingActivity.class);
                startActivityForResult(i, 0);
                return false;
            }
        });

        //listView 확장 후 축소 잠금
//        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
//        {
//            public boolean onGroupClick(ExpandableListView arg0, View itemView, int itemPosition, long itemId)
//            {
//                expListView.expandGroup(itemPosition);
//                return true;
//            }
//        });

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