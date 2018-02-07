package com.example.kwoncheolhyeok.core.FriendsActivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.BaseActivity.UserListBaseActivity;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

public class FriendsActivity extends UserListBaseActivity {

    Toolbar toolbar = null;
    private ArrayList<UserListAdapter.Item> items;
    private UserListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.friends_activity);

        // bottomTab
        final BottomNavigationViewEx navigation = findViewById(R.id.navigation);
        navigation.enableAnimation(false);
        navigation.enableShiftingMode(false);
        navigation.enableItemShiftingMode(false);
        navigation.setIconVisibility(false);
        navigation.setTextSize(15);

        BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_friends:
                        setRecyclerView(items, adapter, "friendUsers", R.menu.friend_menu);
                        return true;
                    case R.id.navigation_receive:
                        setRecyclerView(items, adapter, "followerUsers", R.menu.follower_menu);
                        return true;
                    case R.id.navigation_send:
                        setRecyclerView(items, adapter, "followingUsers", R.menu.following_menu);
                        return true;
                    case R.id.navigation_recent:
                        setRecyclerView(items, adapter, "viewedMeUsers", R.menu.follower_menu);
                        return true;
                    /*case R.id.navigation_block:
                        setRecyclerView(items, adapter, "blockUsers", R.menu.follower_menu);
                        return true;*/
                }
                return false;
            }
        };
        navigation.setOnNavigationItemSelectedListener(selectedListener);


        toolbar = findViewById(R.id.toolbar);
        final RecyclerView recyclerView = findViewById(R.id.friendsRecyclerView);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        DataContainer.getInstance().getMyUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DataContainer.getInstance().setUser(dataSnapshot.getValue(User.class));

                // 리사이클뷰
                items = new ArrayList<>();
                adapter = new UserListAdapter(FriendsActivity.this, items);
                LinearLayoutManager layoutManager = new LinearLayoutManager(FriendsActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                recyclerView.addItemDecoration(new DividerItemDecoration(FriendsActivity.this, DividerItemDecoration.VERTICAL)); //리사이클뷰 구분선

                // setRecyclerView (default)
                navigation.setSelectedItemId(R.id.navigation_friends);
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
}