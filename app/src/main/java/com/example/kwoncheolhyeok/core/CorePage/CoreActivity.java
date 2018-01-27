package com.example.kwoncheolhyeok.core.CorePage;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.kwoncheolhyeok.core.Entity.CoreListItem;
import com.example.kwoncheolhyeok.core.Entity.CorePost;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CoreActivity extends AppCompatActivity {

    private static final int WRITE_SUCC = 1;
    Toolbar toolbar = null;

    private CoreListAdapter coreListAdapter;
    private RecyclerView recyclerView;
    private Query postQuery;
    private ChildEventListener listner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.core_activity);

        Intent intent = getIntent();
        final String cUuid = intent.getStringExtra("uuid");

        //스크린샷 방지
//        ScreenshotSetApplication.getInstance().allowUserSaveScreenshot(false);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 자신, 타인 액티비티 구별
                Intent i;
                i = new Intent(CoreActivity.this, CoreWriteActivity.class);
                i.putExtra("cUuid",cUuid);

                startActivityForResult(i, WRITE_SUCC);
            }
        });

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);


        recyclerView = findViewById(R.id.core_listview);

        final ArrayList<CoreListItem> list = new ArrayList<>();
        coreListAdapter = new CoreListAdapter(list, this, cUuid);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(coreListAdapter);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        // 코어 주인의 User Get
        DataContainer dc = DataContainer.getInstance();
        dc.getUserRef(cUuid).addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 User cUser = dataSnapshot.getValue(User.class);
                 addPostToList(cUuid, list, cUser);
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {
             }
        });
    }

    private void addPostToList(final String cUuid, final ArrayList<CoreListItem> list, final User cUser) {
        postQuery = FirebaseDatabase.getInstance().getReference().child("posts").child(cUuid).orderByChild("writeDate");
        listner = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final CorePost corePost = dataSnapshot.getValue(CorePost.class);
                final String postKey = dataSnapshot.getKey();
                if(corePost.getUuid().equals(cUuid)) { // 작성자가 코어의 주인인 경우
                    addCoreListItem(cUser, corePost, postKey);
                }
                else {  // 익명
                    addCoreListItem(null, corePost, postKey);
                }
            }

            private void addCoreListItem(User user, CorePost corePost, String postKey) {
                list.add(0,new CoreListItem(user, corePost, postKey));
                coreListAdapter.notifyItemInserted(0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final CorePost corePost = dataSnapshot.getValue(CorePost.class);
                final String postKey = dataSnapshot.getKey();

                int i = 0;
                for(CoreListItem coreListItem : list){
                    if(coreListItem.getPostKey().equals(postKey)){
                        coreListItem.setCorePost(corePost);
                        coreListAdapter.notifyItemChanged(i);
                        break;
                    }
                    i++;
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final String postKey = dataSnapshot.getKey();
                int i = 0;
                for(CoreListItem coreListItem : list){
                    if(coreListItem.getPostKey().equals(postKey)){
                        list.remove(coreListItem);
                        coreListAdapter.notifyItemRemoved(i);
                        break;
                    }
                    i++;
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        postQuery.addChildEventListener(listner);
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
        if(postQuery != null && listner != null) postQuery.addChildEventListener(listner);
//        ScreenshotSetApplication.getInstance().registerScreenshotObserver();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(postQuery != null && listner != null) postQuery.removeEventListener(listner);
//        ScreenshotSetApplication.getInstance().unregisterScreenshotObserver();
        coreListAdapter.clickPause();
    }

    public RecyclerView.ViewHolder getHolder(int position){
        if(recyclerView == null) return null;
        return recyclerView.findViewHolderForAdapterPosition(position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == WRITE_SUCC){
            if(resultCode == Activity.RESULT_OK) recyclerView.scrollToPosition(0);
        }
    }
}