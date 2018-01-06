package com.example.kwoncheolhyeok.core.FriendsActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.CorePage.AudioActivity;
import com.example.kwoncheolhyeok.core.CorePage.CoreActivity;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.PeopleFragment.FullImageActivity;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.LinkedList;

public class FriendsActivity extends AppCompatActivity {

    Toolbar toolbar = null;
    private LinkedList<UserListAdapter.Item> items;
    private UserListAdapter adapter;
    private ValueEventListener listener;
    private Query ref;

    TextView ex_header = null;
    /*
    * Preparing the list data
    */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.friends_activity);

        // bottomTab
        BottomNavigationViewEx navigation = findViewById(R.id.navigation);
        navigation.enableAnimation(false);
        navigation.enableShiftingMode(false);
        navigation.enableItemShiftingMode(false);
        navigation.setIconVisibility(false);
        navigation.setTextSize(15);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_friends:
                        setRecyclerView(items, adapter, "friendUsers", R.menu.friends_set_unfollow);
                        return true;
                    case R.id.navigation_receive:
                        setRecyclerView(items, adapter, "followerUsers", R.menu.friends_set_follow);
                        return true;
                    case R.id.navigation_send:
                        setRecyclerView(items, adapter, "followingUsers", R.menu.friends_set_unfollow);
                        return true;
                    case R.id.navigation_recent:
                        setRecyclerView(items, adapter, "viewedMeUsers", R.menu.friends_set_follow);
                        return true;
                    /*case R.id.navigation_block:
                        setRecyclerView(items, adapter, "blockUsers", R.menu.friends_set_follow);
                        return true;*/
                }
                return false;
            }
        });


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        // 리사이클뷰
        final RecyclerView recyclerView = findViewById(R.id.friendsRecyclerView);
        items = new LinkedList<>();

        adapter = new UserListAdapter(this, items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // setRecyclerView (default)
        adapter.setItemMenu(R.menu.friends_set_follow);
        navigation.setSelectedItemId(R.id.navigation_receive);

        //임시 !!! 프렌즈 헤더 확인용
        ex_header = findViewById(R.id.ex_header);
        ex_header.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FriendsActivity.this, E_Header.class);
                startActivity(i);
            }
        });



    }

    private void setRecyclerView(final LinkedList<UserListAdapter.Item> items, final UserListAdapter adapter, final String field, int item_menu) {
        adapter.setItemMenu(item_menu);
        items.clear();
        if(ref != null && listener != null) ref.removeEventListener(listener);  // 이전 리스너 해제
        ref = DataContainer.getInstance().getMyUserRef().child(field).orderByValue();
        listener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(field, "DataChange : " + dataSnapshot.getKey() + ',' + dataSnapshot.getValue());
                items.clear();
                if(dataSnapshot.getValue() == null) adapter.notifyDataSetChanged();

                for(final DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d("snapshot", snapshot.getValue().toString());
                    final String oUuid = snapshot.getKey();
                    DataContainer.getInstance().getUsersRef().child(oUuid).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User oUser = dataSnapshot.getValue(User.class);
                            items.offerFirst(new UserListAdapter.Item(oUser, (long)snapshot.getValue(), oUuid));
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
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
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}