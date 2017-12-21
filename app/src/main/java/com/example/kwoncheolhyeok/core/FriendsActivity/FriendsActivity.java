package com.example.kwoncheolhyeok.core.FriendsActivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

public class FriendsActivity extends AppCompatActivity {

    Toolbar toolbar = null;
    private LinkedList<userListAdapter.Item> items;
    private userListAdapter adapter;
    private ValueEventListener listener;
    private Query ref;

    /*
    * Preparing the list data
    */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.friends_activity);

        // bottomTab
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_receive:
                        setRecyclerView(items, adapter, "followerUsers", R.menu.receive_item_menu);
                        return true;
                    case R.id.navigation_send:
                        setRecyclerView(items, adapter, "followingUsers", R.menu.friends_item_menu);
                        return true;
                    case R.id.navigation_friends:
                        setRecyclerView(items, adapter, "friendUsers", R.menu.friends_item_menu);
                        return true;
                    case R.id.navigation_recent:
                        setRecyclerView(items, adapter, "recentUsers", R.menu.receive_item_menu);
                        return true;
                    case R.id.navigation_block:
                        setRecyclerView(items, adapter, "blockUsers", R.menu.receive_item_menu);
                        return true;
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

        adapter = new userListAdapter(this, items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // setRecyclerView (default)
        adapter.setItemMenu(R.menu.receive_item_menu);
        navigation.setSelectedItemId(R.id.navigation_receive);

    }

    private void setRecyclerView(final LinkedList<userListAdapter.Item> items, final userListAdapter adapter, final String field, int item_menu) {
        adapter.setItemMenu(item_menu);
        items.clear();
        if(ref != null && listener != null) ref.removeEventListener(listener);  // 이전 리스너 해제
        ref = DataContainer.getInstance().getMyUserRef().child(field).orderByValue();
        listener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(field, "DataChange : " + dataSnapshot.getValue());
                items.clear();
                for(final DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d("snapshot", snapshot.getValue().toString());
                    final String oUuid = snapshot.getKey();
                    DataContainer.getInstance().getUsersRef().child(oUuid).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User oUser = dataSnapshot.getValue(User.class);
                            items.offerFirst(new userListAdapter.Item(oUser, (long)snapshot.getValue(), oUuid));
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