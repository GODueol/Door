package com.teamdoor.android.door.MessageList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.teamdoor.android.door.Entity.RoomVO;
import com.teamdoor.android.door.MessageActivity.ChattingActivity;
import com.teamdoor.android.door.MessageList.messageRecyclerAdapter.OnRemoveChattingListCallback;
import com.teamdoor.android.door.R;
import com.teamdoor.android.door.Util.BaseActivity.BaseActivity;
import com.teamdoor.android.door.Util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageActivity extends BaseActivity implements MessageContract.View {

    private messageRecyclerAdapter messageRecyclerAdapter;
    private List<RoomVO> listrowItem;
    private MessageContract.Presenter mPresenter;

    private SharedPreferencesUtil SPUtil;
    private InterstitialAd mInterstitialAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.chatting_list_activity);
        setToolbar();
        SPUtil = new SharedPreferencesUtil(getApplicationContext());
        setmInterstitialAd();

        // setPresenter <-> MessageView
        new MessagePresenter(this, SPUtil);

        listrowItem = new ArrayList<>();
        mPresenter.setListItem(listrowItem);

        setListView();
        mPresenter.setMessageList();
    }

    @Override
    public void setPresenter(MessageContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);
    }

    public void setListView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        messageRecyclerAdapter = new messageRecyclerAdapter(MessageActivity.this, listrowItem, R.layout.chatting_list_row, listener, chatlistener);
        messageRecyclerAdapter.setHasStableIds(true);
        RecyclerView messageList = findViewById(R.id.messagelist);
        messageList.setAdapter(messageRecyclerAdapter);
        messageList.setLayoutManager(linearLayoutManager);
        messageList.addItemDecoration(new DividerItemDecoration(MessageActivity.this, DividerItemDecoration.VERTICAL)); //리사이클뷰 구분선
        messageList.setItemAnimator(new DefaultItemAnimator());
    }

    com.teamdoor.android.door.MessageList.messageRecyclerAdapter.RecyclerViewClickListener listener = this::onClick;


    OnRemoveChattingListCallback chatlistener = new OnRemoveChattingListCallback() {
        @Override
        public void onRemove(String target) {
            mPresenter.removeMessageList(target);
        }
    };

    // 뒤로가기 버튼 기능
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setmInterstitialAd() {
        mInterstitialAd = new InterstitialAd(getApplicationContext());
        mInterstitialAd.setAdUnitId(getString(R.string.adsChatingList));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    @Override
    public void refreshMessageListView() {
        messageRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void startChattingActivity(Bundle bundle) {
        Intent intent = new Intent(getApplicationContext(), ChattingActivity.class);
        intent.putExtra("user", bundle.getSerializable("user"));
        intent.putExtra("userUuid", bundle.getString("userUuid"));
        startActivity(intent);

        checkCorePlus().addOnSuccessListener(isPlus -> {
            if (!isPlus) {
                SPUtil.increaseAds(mInterstitialAd, "ChatList");
            }
        });
    }

    @Override
    public String getResourceBadge() {
        return getString(R.string.badgeChat);
    }

    @Override
    public String getResourceTeamCore() {
        return getString(R.string.TeamCore);
    }

    private void onClick(int position) {
        mPresenter.enterChatRoom(messageRecyclerAdapter.getItemRoomVO(position));
    }
}