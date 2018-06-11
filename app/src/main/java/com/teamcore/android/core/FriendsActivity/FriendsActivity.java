package com.teamcore.android.core.FriendsActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.teamcore.android.core.Entity.User;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.BaseActivity.UserListBaseActivity;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.SharedPreferencesUtil;
import com.teamcore.android.core.Util.UiUtil;

import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class FriendsActivity extends UserListBaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    Toolbar toolbar = null;
    private ArrayList<UserListAdapter.Item> items;
    private UserListAdapter adapter;

    private List<Badge> badges;
    boolean firstView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.friends_activity);
        firstView = true;
        // bottomTab
        final BottomNavigationViewEx navigation = (BottomNavigationViewEx) findViewById(R.id.navigation);
        navigation.enableAnimation(false);
        navigation.enableShiftingMode(false);
        navigation.enableItemShiftingMode(false);
        navigation.setTextSize(15);
        navigation.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        navigation.getMeasuredWidth();
        navigation.getMeasuredHeight();
        navigation.setIconVisibility(false);
        navigation.setItemHeight(navigation.getMeasuredHeight());

        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navigation.getChildAt(0);

        BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (items.size() != 0 && navigation.getMenu().findItem(navigation.getSelectedItemId()).equals(item)) {
                    switch (item.getItemId()) {
                        case R.id.navigation_friends:
                            SPUtil.removeFriendsBadge(getString(R.string.badgeFriend));
                            return true;
                        case R.id.navigation_receive:
                            SPUtil.removeFriendsBadge(getString(R.string.badgeFollow));
                            return true;
                        case R.id.navigation_send:
                            SPUtil.removeFriendsBadge(getString(R.string.badgeFollowing));
                            return true;
                        case R.id.navigation_recent:
                            SPUtil.switchBadgeState(getString(R.string.badgeView), false);
                            return true;
                    }

                    return true;
                }
                Log.d("test", "처음");
                switch (item.getItemId()) {
                    case R.id.navigation_friends:
                        if (!firstView) {
                            SPUtil.removeFriendsBadge(getString(R.string.badgeFriend));
                        } else {
                            firstView = false;
                        }
                        setRecyclerView(items, adapter, "friendUsers", R.menu.friend_menu);
                        return true;
                    case R.id.navigation_receive:
                        SPUtil.removeFriendsBadge(getString(R.string.badgeFollow));
                        setRecyclerView(items, adapter, "followerUsers", R.menu.follower_menu);
                        return true;
                    case R.id.navigation_send:
                        SPUtil.removeFriendsBadge(getString(R.string.badgeFollowing));
                        setRecyclerView(items, adapter, "followingUsers", R.menu.following_menu);
                        return true;
                    case R.id.navigation_recent:
                        SPUtil.switchBadgeState(getString(R.string.badgeView), false);
                        setRecyclerView(items, adapter, "viewedMeUsers", R.menu.viewed_me_menu);
                        return true;
                }
                return false;
            }
        };
        navigation.setOnNavigationItemSelectedListener(selectedListener);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.friendsRecyclerView);

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
                /*
                이부분을 넣으면 비동기문제때문인지 널문제가 나버리네요.. 코어플러스때매 어뎁터에 넘겨주는방법 뿐인데 어떻게
                바로 감이 안잡히네요 ㅜㅜ.. 핼프미..

                checkCorePlus().done(isPlus -> {
                                adapter = new UserListAdapter(FriendsActivity.this, items,isPlus);
                });
                */

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

        SPUtil = new SharedPreferencesUtil(getApplicationContext());
        SPUtil.getBadgePreferences().registerOnSharedPreferenceChangeListener(this);
        badges = new ArrayList<>();
        navigationViewinitBadge(bottomNavigationMenuView);
    }

    private void navigationViewinitBadge(BottomNavigationMenuView bottomNavigationMenuView) {
        //뱃지 위치 조정 코드 : 수평축은 숫자 작아질수록 우로 / 수직축은 숫자 작아질수록 위로
        int badge;
        View friend = bottomNavigationMenuView.getChildAt(0);
        badge = SPUtil.getBadgeCount(getString(R.string.badgeFriend));
        setQbadge(friend, badge, (float) 15.5, (float) -7.5);

        View follower = bottomNavigationMenuView.getChildAt(1);
        badge = SPUtil.getBadgeCount(getString(R.string.badgeFollow));
        setQbadge(follower, badge, (float) 9.5, (float) -7.5);

        View following = bottomNavigationMenuView.getChildAt(2);
        badge = SPUtil.getBadgeCount(getString(R.string.badgeFollowing));
        setQbadge(following, badge, (float) 9.5, (float) -7.5);

        View viewed = bottomNavigationMenuView.getChildAt(3);
        boolean state = SPUtil.getBadgeState(getString(R.string.badgeView));
        setQbadge(viewed, state, (float) 1.5, (float) -4.5);
    }

    private void setQbadge(View view, int num, float x, float y) {

        badges.add(new QBadgeView(this).bindTarget(view)
                .setBadgeTextColor(getResources().getColor(R.color.black))
                .setBadgeGravity(Gravity.END | Gravity.TOP)
                .setGravityOffset(x, y, true)
                .setExactMode(true)
                .setBadgeNumber(num)
                .setShowShadow(false)
                .setBadgeBackgroundColor(getResources().getColor(R.color.transparent))
        );
    }

    private void setQbadge(View view, boolean b, float x, float y) {
        String str;
        if (b) {
            str = "●";
        } else {
            str = "";
        }
        badges.add(new QBadgeView(this).bindTarget(view)
                .setBadgeTextColor(getResources().getColor(R.color.skyblue))
                .setBadgeGravity(Gravity.END | Gravity.TOP)
                .setGravityOffset(x, y, true)
                .setExactMode(true)
                .setBadgeText(str)
                .setBadgeTextSize((float) 6, true)
                .setShowShadow(false)
                .setBadgeBackgroundColor(getResources().getColor(R.color.transparent))
        );
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "badgeFriend":
                int badgeFriend = SPUtil.getBadgeCount(key);
                badges.get(0).setBadgeNumber(badgeFriend);
                break;
            case "badgeFollow":
                int badgeFollow = SPUtil.getBadgeCount(key);
                badges.get(1).setBadgeNumber(badgeFollow);
                break;
            case "badgeFollowing":
                int badgeFollowing = SPUtil.getBadgeCount(key);
                badges.get(2).setBadgeNumber(badgeFollowing);
                break;
            case "badgeView":
                boolean badgeState = SPUtil.getBadgeState(key);
                String str;
                if (badgeState) {
                    str = "●";
                } else {
                    str = "";
                }
                badges.get(3).setBadgeText(str);
                break;
        }
    }
}