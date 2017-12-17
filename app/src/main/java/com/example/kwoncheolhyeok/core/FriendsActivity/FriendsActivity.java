package com.example.kwoncheolhyeok.core.FriendsActivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class FriendsActivity extends AppCompatActivity {

    Toolbar toolbar = null;
    private ArrayList<userListAdapter.Item> items;
    private userListAdapter adapter;

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
                String msg;
                switch (item.getItemId()) {
                    case R.id.navigation_receive:
                        setRecyclerView(items, adapter, "followingUsers");
                        return true;
                    case R.id.navigation_send:
                        setRecyclerView(items, adapter, "followerUsers");
                        return true;
                    case R.id.navigation_friends:
                        msg = "friends";
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.navigation_recent:
                        msg = "recent";
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.navigation_block:
                        msg = "block";
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
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
        items = new ArrayList<>();

        adapter = new userListAdapter(items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // setRecyclerView (default)
        setRecyclerView(items, adapter, "followingUsers");

    }

    private void setRecyclerView(final ArrayList<userListAdapter.Item> items, final userListAdapter adapter, String field) {
        DataContainer.getInstance().getMyUserRef().child(field).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Map<String, Long> friendsUuidMap = (Map<String, Long>) dataSnapshot.getValue();
                items.clear();
                if(friendsUuidMap == null) {
                    adapter.notifyDataSetChanged();
                    return;
                }
                for(final String oUuid : friendsUuidMap.keySet()){
                    DataContainer.getInstance().getUsersRef().child(oUuid).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User oUser = dataSnapshot.getValue(User.class);
                            items.add(new userListAdapter.Item(oUser,friendsUuidMap.get(oUuid)));
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


}