package com.teamdoor.android.door.Activity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.teamdoor.android.door.FriendsActivity.UserListAdapter;
import com.teamdoor.android.door.R;
import com.teamdoor.android.door.Util.BaseActivity.UserListBaseActivity;
import com.teamdoor.android.door.Util.DataContainer;

import java.util.ArrayList;

/**
 * Created by Kwon on 2018-01-04.
 */

public class FindUserActivity extends UserListBaseActivity {

    Toolbar toolbar = null;
    private UserListAdapter adapter;

    private ArrayList<UserListAdapter.Item> items;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_user_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);
        mAdView = (AdView) findViewById(R.id.adView);
        checkCorePlus().addOnSuccessListener(isPlus -> {
            if (!isPlus) {
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                mAdView.loadAd(adRequest);
            } else {
                mAdView.destroy();
                mAdView.setVisibility(View.GONE);
            }
        });
        // 리사이클뷰
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.friendsRecyclerView);
        items = new ArrayList<>();

        adapter = new UserListAdapter(this, items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); //리사이클뷰 구분선
        // setRecyclerView (default)


        final SearchView search_view = (SearchView) findViewById(R.id.search_view);
        search_view.onActionViewExpanded();
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String id) {
                findUserSetQuery(id);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        ImageButton save = (ImageButton) findViewById(R.id.save);
        save.setOnClickListener(view -> findUserSetQuery(search_view.getQuery().toString()));
    }

    private void findUserSetQuery(String id) {
        if(id.equals(DataContainer.getInstance().getUser().getId())) return;
        setRecyclerView(items, adapter, "Find User", R.menu.follower_menu, DataContainer.getInstance().getUsersRef().orderByChild("id").equalTo(id));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        if (adapter != null) {
            adapter.Resume();
        }
        super.onResume();

        if(mAdView.isLoading()){
            mAdView.resume();
        }
    }

    @Override
    public void onPause() {
        if (adapter != null) {
            adapter.Pause();
        }

        if(mAdView.isLoading()){
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (adapter != null) {
            adapter.Destroy();
        }
        if(mAdView.isLoading()){
            mAdView.destroy();
        }
        super.onDestroy();
    }

}