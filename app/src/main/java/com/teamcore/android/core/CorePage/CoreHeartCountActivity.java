package com.teamcore.android.core.CorePage;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.teamcore.android.core.FriendsActivity.UserListAdapter;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.BaseActivity.UserListBaseActivity;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Kwon on 2018-02-26.
 */

public class CoreHeartCountActivity extends UserListBaseActivity {

    Toolbar toolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.core_heart_count_activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String postKey = getIntent().getStringExtra("postKey");
        final String cUuid = getIntent().getStringExtra("cUuid");

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // 리사이클뷰
        ArrayList<UserListAdapter.Item> items = new ArrayList<>();
        //items.add(new UserListAdapter.Item(true));
        UserListAdapter adapter = new UserListAdapter(CoreHeartCountActivity.this, items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CoreHeartCountActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(CoreHeartCountActivity.this, DividerItemDecoration.VERTICAL)); //리사이클뷰 구분선
        // setRecyclerView (default)
        setRecyclerView(items, adapter, "Core Heart Count", R.menu.follower_menu, FirebaseDatabase.getInstance().getReference("posts").child(cUuid).child(postKey).child("likeUsers").orderByValue());


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