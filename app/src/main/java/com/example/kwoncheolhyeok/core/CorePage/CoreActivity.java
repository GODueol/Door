package com.example.kwoncheolhyeok.core.CorePage;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.Entity.CoreListItem;
import com.example.kwoncheolhyeok.core.Entity.CorePost;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.Event.TargetUserBlocksMeEvent;
import com.example.kwoncheolhyeok.core.PeopleFragment.FullImageActivity;
import com.example.kwoncheolhyeok.core.PeopleFragment.GridItem;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.BaseActivity.BlockBaseActivity;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.SharedPreferencesUtil;
import com.example.kwoncheolhyeok.core.Util.WrapContentLinearLayoutManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;


public class CoreActivity extends BlockBaseActivity {

    private static final int WRITE_SUCC = 1;
    Toolbar toolbar = null;

    public CoreListAdapter coreListAdapter;
    private RecyclerView recyclerView;
    public Query postQuery;
    public ChildEventListener listener;
    private DataContainer dc;
    private String cUuid = null;
    public ArrayList<CoreListItem> list;
    private FloatingActionButton fab;
    public String postId;

    public static final String PREFS_NAME = "MyPrefsFile1";
    public CheckBox dontShowAgain;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView();

        dc = DataContainer.getInstance();

        //스크린샷 방지
//        ScreenshotSetApplication.getInstance().allowUserSaveScreenshot(false);

        toolbar = (Toolbar) findViewById(
                R.id.toolbar);
        setSupportActionBar(toolbar);

        setFab();

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //w액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        recyclerView = (RecyclerView) findViewById(R.id.core_listview);

        LinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        Intent intent = getIntent();
        cUuid = intent.getStringExtra("uuid");
        postId = intent.getStringExtra("postId");

        // 알람받은 포스트가 있는지 여부확인
        if(postId != null)FirebaseDatabase.getInstance().getReference().child("posts/" + cUuid + "/" + postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    // 없으면, 삭제되었다는 메세지
                    findViewById(R.id.removePostMsg).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (postId != null) {
            toolbar.invalidate();
            Log.d("test", postId);
        }
        // 엑티비티 Uuid 저장
        if (cUuid != null)
            SPUtil.setBlockMeUserCurrentActivity(getString(R.string.currentActivity), cUuid);

        list = new ArrayList<>();
        coreListAdapter = getCoreListAdapter(list);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(coreListAdapter);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        addPostsToList(list);

    }

    public void setContentView() {
        setContentView(R.layout.core_activity);
    }

    public void addPostsToList(final ArrayList<CoreListItem> list) {
        // 코어 주인의 User Get
        dc.getUserRef(cUuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User cUser = dataSnapshot.getValue(User.class);
                addCorePostsToList(cUuid, list, cUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        SharedPreferencesUtil SPUtil = new SharedPreferencesUtil(getApplicationContext());
        SPUtil.removeBadge(getString(R.string.badgePost));
    }

    @NonNull
    private CoreListAdapter getCoreListAdapter(ArrayList<CoreListItem> list) {
        return new CoreListAdapter(list, this);
    }

    public void setFab() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DataContainer.getInstance().getUserRef(cUuid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User cUser = dataSnapshot.getValue(User.class);

                        if (cUser != null) {
                            // CORE 주인 일반 회원
                            if(cUser.getAccountType() == null || cUser.getAccountType().equals(DataContainer.ACCOUNT_TYPE.NORMAL)){
                                // 100개 제한
                                if(cUser.getCorePostCount() >= DataContainer.NORMAL_CORE_LIMIT){
                                    Toast.makeText(CoreActivity.this, "Core 주인이 일반 계정이기 때문에 " + DataContainer.NORMAL_CORE_LIMIT + "초과하여 글을 추가할수 없습니다", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } else {
                                // 300개 제한
                                if(cUser.getCorePostCount() >= DataContainer.PLUS_CORE_LIMIT){
                                    Toast.makeText(CoreActivity.this, DataContainer.NORMAL_CORE_LIMIT + "초과하여 글을 추가할수 없습니다", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            // 블럭 관계 확인
                            if (cUser.getBlockUsers().containsKey(dc.getUid())) {
                                Toast.makeText(CoreActivity.this, "포스트를 작성할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            } else if (!cUuid.equals(dc.getUid()) && cUser.isAnonymityProhibition()) {
                                Toast.makeText(CoreActivity.this, "포스트를 작성할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        // 자신, 타인 액티비티 구별
                        Intent i;
                        i = new Intent(CoreActivity.this, CoreWriteActivity.class);
                        i.putExtra("cUuid", cUuid);

                        startActivityForResult(i, WRITE_SUCC);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public void addCorePostsToList(final String cUuid, final ArrayList<CoreListItem> list, final User cUser) {
        postQuery = getQuery(cUuid);
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final CorePost corePost = dataSnapshot.getValue(CorePost.class);
                final String postKey = dataSnapshot.getKey();
                if (corePost == null || corePost.getUuid() == null) return;
                addCoreListItem(corePost, postKey, cUser, list);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final CorePost corePost = dataSnapshot.getValue(CorePost.class);
                final String postKey = dataSnapshot.getKey();

                int i = 0;
                for (CoreListItem coreListItem : list) {
                    if (coreListItem.getPostKey().equals(postKey)) {
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
                for (CoreListItem coreListItem : list) {
                    if (coreListItem.getPostKey().equals(postKey)) {
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
        postQuery.addChildEventListener(listener);
    }

    private void addCoreListItem(CorePost corePost, String postKey, User cUser, ArrayList<CoreListItem> list) {
        if (corePost.getUuid().equals(cUuid)) { // 작성자가 코어의 주인인 경우
            list.add(0, new CoreListItem(cUser, corePost, postKey, cUuid));
        } else {  // 익명
            list.add(0, new CoreListItem(null, corePost, postKey, cUuid));
        }
        coreListAdapter.notifyItemInserted(0);
    }

    private Query getQuery(String cUuid) {
        // p.putExtra("uuid",item.getcUuid());
        // p.putExtra("postId",item.getPostId());

        String postId = getIntent().getStringExtra("postId");


        if (postId != null) { // 알람을 통해서 진행할 경우
            fab.setVisibility(View.INVISIBLE);
            return FirebaseDatabase.getInstance().getReference().child("posts").child(cUuid).orderByKey().equalTo(postId);

        } else {
            return FirebaseDatabase.getInstance().getReference().child("posts").child(cUuid).orderByChild("writeDate");
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.core_activity_menu, menu);
        MenuItem profile = menu.findItem(R.id.core_profile);
        MenuItem prohibition = menu.findItem(R.id.anonymity_prohibition);
        if (postId != null) {
            profile.setVisible(true);
            prohibition.setVisible(false);
        } else if (cUuid != null && cUuid.equals(dc.getUid())) {
            profile.setVisible(false);
            prohibition.setVisible(true);
            menu.getItem(0).setChecked(dc.getUser().isAnonymityProhibition());
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            case R.id.anonymity_prohibition:
                boolean isChecked = !item.isChecked();
                item.setChecked(isChecked);

                dc.getUser().setAnonymityProhibition(isChecked);
                dc.getMyUserRef().child("anonymityProhibition").setValue(isChecked);
                return true;
            case R.id.core_profile:
                if (postId != null) {


                    dc.getUserRef(cUuid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Intent p = new Intent(CoreActivity.this, FullImageActivity.class);
                            p.putExtra("item", new GridItem(0, cUuid, dataSnapshot.getValue(User.class).getSummaryUser(), ""));
                            CoreActivity.this.startActivity(p);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        // 코어 게시물 위반 및 제재 사항 고지 다이얼로그
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        LayoutInflater adbInflater = LayoutInflater.from(this);
        View eulaLayout = adbInflater.inflate(R.layout.core_notice_dialog, null);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String skipMessage = settings.getString("skipMessage", "NOT checked");

        dontShowAgain = (CheckBox) eulaLayout.findViewById(R.id.check_access);
        adb.setView(eulaLayout);

        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String checkBoxResult = "NOT checked";

                if (dontShowAgain.isChecked()) {
                    checkBoxResult = "checked";
                }

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString("skipMessage", checkBoxResult);
                editor.commit();

                // Do what you want to do on "OK" action

                return;
            }
        });


        if (!skipMessage.equals("checked")) {
            adb.show();
        }

        super.onResume();
//        ScreenshotSetApplication.getInstance().registerScreenshotObserver();
    }

    @Override
    public void onPause() {
        super.onPause();
//        ScreenshotSetApplication.getInstance().unregisterScreenshotObserver();
        coreListAdapter.clickPause();
    }

    public RecyclerView.ViewHolder getHolder(int position) {
        if (recyclerView == null) return null;
        return recyclerView.findViewHolderForAdapterPosition(position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITE_SUCC) {
            if (resultCode == Activity.RESULT_OK) recyclerView.scrollToPosition(0);
        }
    }

    @Override
    protected void onDestroy() {
        if (postQuery != null && listener != null) postQuery.removeEventListener(listener);
        super.onDestroy();
    }

    @Subscribe
    public void FinishActivity(TargetUserBlocksMeEvent someoneBlocksMeEvent) {
        finish();
    }
}

