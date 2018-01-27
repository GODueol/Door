package com.example.kwoncheolhyeok.core.SettingActivity;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.kwoncheolhyeok.core.FriendsActivity.UserListAdapter;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.BaseActivity.UserListBaseActivity;

import java.util.ArrayList;

/**
 * Created by Kwon on 2018-01-04.
 */

public class BlockActivity extends UserListBaseActivity {

    Toolbar toolbar = null;
    private UserListAdapter adapter;

    private ArrayList<UserListAdapter.Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_block_activity);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        // 리사이클뷰
        final RecyclerView recyclerView = findViewById(R.id.friendsRecyclerView);
        items = new ArrayList<>();
        items.add(new UserListAdapter.Item(true));

        adapter = new UserListAdapter(this, items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); //리사이클뷰 구분선
        // setRecyclerView (default)
        setRecyclerView(items, adapter, "blockUsers", R.menu.block_menu);
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