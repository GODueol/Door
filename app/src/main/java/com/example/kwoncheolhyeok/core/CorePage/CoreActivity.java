package com.example.kwoncheolhyeok.core.CorePage;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.Entity.CoreListItem;
import com.example.kwoncheolhyeok.core.Entity.CorePost;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.ScreenshotSetApplication;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;


public class CoreActivity extends AppCompatActivity {

    Toolbar toolbar = null;

    TextView media_player = null;
    TextView other_user = null;
    private CoreListAdapter coreListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.core_activity);

        Intent intent = getIntent();
        final String uuid = intent.getStringExtra("uuid");

        //스크린샷 방지
        ScreenshotSetApplication.getInstance().allowUserSaveScreenshot(false);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                // 자신, 타인 액티비티 구별
                Intent i;
                if(uuid.equals(DataContainer.getInstance().getUid())){
                    i = new Intent(CoreActivity.this, CoreWriteActivity.class);
                } else {
                    i = new Intent(CoreActivity.this, otherUser_write_core.class);
                }

                startActivity(i);
            }
        });

        media_player = findViewById(R.id.media_player);
        media_player.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CoreActivity.this, AudioActivity.class);
                startActivity(i);
            }
        });

        other_user = findViewById(R.id.other_user);
        other_user.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CoreActivity.this, otherUser_write_core.class);
                startActivity(i);
            }
        });

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);


        final ListView core_list_view = findViewById(R.id.core_listview);

        final LinkedList<CoreListItem> list = new LinkedList<>();
        coreListAdapter = new CoreListAdapter(list, this, uuid);
        core_list_view.setAdapter(coreListAdapter);

        core_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

//                Intent myIntent = new Intent(getActivity(), ClubActivity.class);
//                getActivity().startActivity(myIntent);

            }
        });

        // Post, User Get
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("posts").child(uuid).orderByChild("writeDate").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DataContainer dc = DataContainer.getInstance();
                final CorePost corePost = dataSnapshot.getValue(CorePost.class);
                final String postKey = dataSnapshot.getKey();
                final User[] user = new User[1];
                if(corePost.getUuid().equals(dc.getUid())) {
                    user[0] = dc.getUser();
                    addCoreListItem(user[0], corePost, postKey);
                }
                else {
                    dc.getUserRef(corePost.getUuid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user[0] = dataSnapshot.getValue(User.class);
                            addCoreListItem(user[0], corePost, postKey);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            private void addCoreListItem(User user, CorePost corePost, String postKey) {
                list.offerFirst(new CoreListItem(user, corePost, postKey));
                coreListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final CorePost corePost = dataSnapshot.getValue(CorePost.class);
                final String postKey = dataSnapshot.getKey();
                for(CoreListItem coreListItem : list){
                    if(coreListItem.getPostKey().equals(postKey)){
                        coreListItem.setCorePost(corePost);
                        coreListAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final CorePost corePost = dataSnapshot.getValue(CorePost.class);
                final String postKey = dataSnapshot.getKey();
                for(CoreListItem coreListItem : list){
                    if(coreListItem.getPostKey().equals(postKey)){
                        list.remove(coreListItem);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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
    public void onResume() {
        super.onResume();
        ScreenshotSetApplication.getInstance().registerScreenshotObserver();
    }

    @Override
    public void onPause() {
        super.onPause();
        ScreenshotSetApplication.getInstance().unregisterScreenshotObserver();
    }


}