package com.teamcore.android.core.SettingActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.teamcore.android.core.Entity.User;
import com.teamcore.android.core.Event.RefreshLocationEvent;
import com.teamcore.android.core.FriendsActivity.UserListAdapter;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.BaseActivity.UserListBaseActivity;
import com.teamcore.android.core.Util.BusProvider;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.FireBaseUtil;
import com.teamcore.android.core.Util.UiUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.friendsRecyclerView);

        DataContainer.getInstance().getMyUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataContainer.getInstance().setUser(dataSnapshot.getValue(User.class));

                // 리사이클뷰
                items = new ArrayList<>();
                //items.add(new UserListAdapter.Item(true));
                adapter = new UserListAdapter(BlockActivity.this, items);
                LinearLayoutManager layoutManager = new LinearLayoutManager(BlockActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                recyclerView.addItemDecoration(new DividerItemDecoration(BlockActivity.this, DividerItemDecoration.VERTICAL)); //리사이클뷰 구분선
                // setRecyclerView (default)
                setRecyclerView(items, adapter, "blockUsers", R.menu.block_menu);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.block_activity_menu, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            case R.id.unblock_all:
                final User user = DataContainer.getInstance().getUser();
                if (user.getBlockUsers().size() == 0) {
                    Toast.makeText(getBaseContext(), "이미 모든 유저 블락이 해제되어있습니다", Toast.LENGTH_SHORT).show();
                    return true;
                }

                // 다이얼로그
                UiUtil.getInstance().showDialog(BlockActivity.this, "모든 유저 블락 해제",
                        "모든 유저 대상으로 블럭을 해제하시겠습니까?", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                UiUtil.getInstance().startProgressDialog(BlockActivity.this);

                                FireBaseUtil.getInstance().allUnblock(user.getBlockUsers()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        user.getBlockUsers().clear();
                                        BusProvider.getInstance().post(new RefreshLocationEvent());
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        UiUtil.getInstance().stopProgressDialog();
                                    }
                                });

                            }
                        }, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}