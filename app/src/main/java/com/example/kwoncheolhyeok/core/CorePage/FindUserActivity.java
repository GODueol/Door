package com.example.kwoncheolhyeok.core.CorePage;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.SearchView;

import com.example.kwoncheolhyeok.core.FriendsActivity.UserListAdapter;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.BaseActivity.UserListBaseActivity;
import com.example.kwoncheolhyeok.core.Util.DataContainer;

import java.util.ArrayList;

/**
 * Created by Kwon on 2018-01-04.
 */

public class FindUserActivity extends UserListBaseActivity {

    Toolbar toolbar = null;
    private UserListAdapter adapter;

    private ArrayList<UserListAdapter.Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_user_activity);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        // 리사이클뷰
        final RecyclerView recyclerView = findViewById(R.id.friendsRecyclerView);
        items = new ArrayList<>();

        adapter = new UserListAdapter(this, items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); //리사이클뷰 구분선
        // setRecyclerView (default)


        SearchView search_view = findViewById(R.id.search_view);
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String id) {
                setRecyclerView(items, adapter, "Find User", R.menu.follower_menu, DataContainer.getInstance().getUsersRef().orderByChild("id").equalTo(id));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
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


}