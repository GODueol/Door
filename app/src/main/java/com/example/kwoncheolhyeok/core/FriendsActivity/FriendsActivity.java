package com.example.kwoncheolhyeok.core.FriendsActivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.BaseActivity.UserListBaseActivity;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import q.rorbin.badgeview.QBadgeView;

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
        navigation.setTextSize(15);
        navigation.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        navigation.getMeasuredWidth();
        navigation.getMeasuredHeight();
        navigation.setIconVisibility(false);
        navigation.setItemHeight(navigation.getMeasuredHeight());

        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        navigationViewinitBadge(bottomNavigationMenuView);

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

    private void navigationViewinitBadge(BottomNavigationMenuView bottomNavigationMenuView) {
        //Gravity property aligns the text
        View friend = bottomNavigationMenuView.getChildAt(0);
        setQbadge(friend,999,(float)9.5,(float)-7.5);

        View follower = bottomNavigationMenuView.getChildAt(1);
        setQbadge(follower,999,(float)9.5,(float)-7.5);

        View following = bottomNavigationMenuView.getChildAt(2);
        setQbadge(following,999,(float)9.5,(float)-7.5);

        View viewed = bottomNavigationMenuView.getChildAt(3);
        setQbadge(viewed,999,(float)9.5,(float)-7.5);
    }

    private void setQbadge(View view,int num,float x, float y) {
        new QBadgeView(this).bindTarget(view)
                .setBadgeTextColor(getResources().getColor(R.color.black))
                .setBadgeGravity(Gravity.END | Gravity.TOP)
                .setGravityOffset(x, y, true)
                .setExactMode(true)
                .setBadgeNumber(num)
                .setShowShadow(false)
                .setBadgeBackgroundColor(getResources().getColor(R.color.transparent));
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