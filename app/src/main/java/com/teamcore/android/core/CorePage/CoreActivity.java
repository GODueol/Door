package com.teamcore.android.core.CorePage;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;
import com.teamcore.android.core.Entity.CloudEntity;
import com.teamcore.android.core.Entity.CoreListItem;
import com.teamcore.android.core.Entity.CorePost;
import com.teamcore.android.core.Entity.User;
import com.teamcore.android.core.Event.TargetUserBlocksMeEvent;
import com.teamcore.android.core.Exception.NotSetAutoTimeException;
import com.teamcore.android.core.PeopleFragment.FullImageActivity;
import com.teamcore.android.core.PeopleFragment.GridItem;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.BaseActivity.BlockBaseActivity;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.FireBaseUtil;
import com.teamcore.android.core.Util.RemoteConfig;
import com.teamcore.android.core.Util.SharedPreferencesUtil;
import com.teamcore.android.core.Util.UiUtil;
import com.teamcore.android.core.Util.WrapContentLinearLayoutManager;
import com.teamcore.android.core.Util.bilingUtil.IabHelper;
import com.teamcore.android.core.Util.bilingUtil.IabResult;
import com.teamcore.android.core.Util.bilingUtil.Inventory;
import com.teamcore.android.core.Util.bilingUtil.Purchase;

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

    public CheckBox dontShowAgain;

    IInAppBillingService mService;
    ServiceConnection mServiceConn;
    IabHelper iaphelper;
    private CloudEntity cloudEntity;

    @SuppressLint("LogNotTimber")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 일반유저, 가장 오래된 친구 3명 이외에 다른 회원 코어 확인 불가능
        if(!isOldFriends()) {
            Toast.makeText(this, "가장 오래된 친구 3명 이외에 다른 회원 코어 확인 불가능합니다", Toast.LENGTH_SHORT).show();
            finish();
        }

        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView();

        Intent intent = getIntent();
        cUuid = intent.getStringExtra("uuid");
        postId = intent.getStringExtra("postId");


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


        // 알람받은 포스트가 있는지 여부확인
        if (postId != null)
            FirebaseDatabase.getInstance().getReference().child("posts/" + cUuid + "/" + postId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
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
        checkCorePlus().done(isPlus -> {
           coreListAdapter = new CoreListAdapter(list, this, cloudLitener,isPlus);
        });
        //coreListAdapter = getCoreListAdapter(list);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(coreListAdapter);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        addPostsToList(list);

    }

    public boolean isOldFriends(){
        return DataContainer.getInstance().isPlus || FireBaseUtil.getInstance().isOldFriends(cUuid) || DataContainer.getInstance().getUid().equals(cUuid);
    }

    public void setContentView() {
        setContentView(R.layout.core_activity);
        checkCorePlus().done(isPlus -> {
           if(!isPlus){
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("0D525D9C92269D80384121978C3C4267")
                .build();
        mAdView.loadAd(adRequest);
        }
        });
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
/*
    @NonNull
    private CoreListAdapter getCoreListAdapter(ArrayList<CoreListItem> list) {

    }
*/
    public void setFab() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            // 포스트 제재 확인
            UiUtil.getInstance().checkPostPrevent(CoreActivity.this, (isRelease, releaseDate) -> {
                        if (!isRelease) {
                            Toast.makeText(CoreActivity.this,
                                    "포스트 사진 제재 당하셨기 때문에 " +
                                            releaseDate + " 까지 프로필을 업로드 할 수 없습니다"
                                    , Toast.LENGTH_SHORT).show();
                            return;
                        }

                        DataContainer.getInstance().getUserRef(cUuid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User cUser = dataSnapshot.getValue(User.class);
                                checkCorePlus().done(isPlus -> {
                                    if (cUser != null) {
                                        // CORE 주인 일반 회원
                                        if (cUser.getAccountType() == null || !isPlus) {
                                            // 100개 제한
                                            if (cUser.getCorePostCount() >= RemoteConfig.NORMAL_CORE_LIMIT) {
                                                Toast.makeText(CoreActivity.this, "Core 주인이 일반 계정이기 때문에 " + RemoteConfig.NORMAL_CORE_LIMIT + "초과하여 글을 추가할수 없습니다", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        } else {
                                            // 300개 제한
                                            if (cUser.getCorePostCount() >= RemoteConfig.PLUS_CORE_LIMIT) {
                                                Toast.makeText(CoreActivity.this, RemoteConfig.NORMAL_CORE_LIMIT + "초과하여 글을 추가할수 없습니다", Toast.LENGTH_SHORT).show();
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
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
            );
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
            menu.getItem(0).setChecked(dc.getUser(this::finish).isAnonymityProhibition());
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
        try {
            if (SPUtil.isCoreNoticePossible(CoreActivity.this)) {

                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                LayoutInflater adbInflater = LayoutInflater.from(this);
                @SuppressLint("InflateParams")

                View v = adbInflater.inflate(R.layout.core_notice_dialog, null);
                dontShowAgain = v.findViewById(R.id.check_access);
                adb.setView(v);
                adb.setPositiveButton("Ok", (dialog, which) -> {
                    if (dontShowAgain.isChecked()) {
                        try {
                            SPUtil.putCoreNoticeCheck(CoreActivity.this);
                        } catch (NotSetAutoTimeException e) {
                            e.printStackTrace();
                        }
                    }
                });
                adb.show();
            }
        } catch (NotSetAutoTimeException e) {
            e.printStackTrace();
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

        if (iaphelper == null) return;
        if (!iaphelper.handleActivityResult(requestCode, resultCode, data)) {
            //처리할 결과물이 아닐 경우 이곳으로 빠져 기본처리를 하도록한다
            Toast.makeText(this,"지금",Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        if (postQuery != null && listener != null) postQuery.removeEventListener(listener);
        if (mService != null) {
            unbindService(mServiceConn);
        }

        super.onDestroy();
    }


    private void setBilingService() {
        String PUBLIC_KEY = getString(R.string.GP_LICENSE_KEY);

        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
            }
        };

        // 결제 서비스를 위한 인텐트 초기화
        Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage("com.android.vending");
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);

        // 핼퍼 setup
        iaphelper = new IabHelper(this, PUBLIC_KEY);
        iaphelper.startSetup(result -> {
            if (!result.isSuccess()) {
                Toast.makeText(getApplicationContext(), "문제발생", Toast.LENGTH_SHORT).show();
                return;
            }

            if (iaphelper == null) return;
            try {
                iaphelper.queryInventoryAsync(mGotInventoryListener);
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        });
    }


    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {

            // mHelper가 소거되었다면 종료
            if (iaphelper == null) return;

            // 이 샘플에서는 "관리되지 않는 제품"은 "가스" 한가지뿐이므로 상품에 대한 체크를 하지 않습니다.
            // 하지만 다수의 제품이 있을 경우 상품 아이디를 비교하여 처리할 필요가 있습니다.
            if (result.isSuccess()) {
                Toast.makeText(getApplicationContext(), "소비성공??", Toast.LENGTH_SHORT).show();
                UiUtil.getInstance().startProgressDialog(CoreActivity.this);
                try {

                    FireBaseUtil.getInstance().putCoreCloud(cloudEntity.getCUuid(), cloudEntity.getCoreListItem(), getApplicationContext(), cloudEntity.getDeletePostKey(), cloudEntity.getDeletePostKey()).addOnSuccessListener(o -> {
                        Toast.makeText(getApplicationContext(), "코어가 클라우드에 추가되었습니다", Toast.LENGTH_SHORT).show();
                        UiUtil.getInstance().stopProgressDialog();
                    });
                } catch (NotSetAutoTimeException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    ActivityCompat.finishAffinity((Activity) getApplicationContext());
                }
                // 성공적으로 소진되었다면 상품의 효과를 게임상에 적용합니다. 여기서는 가스를 충전합니다.
            }
        }
    };

    private void buyItem(String item) {
        try {
            String payLoad = DataContainer.getInstance().getUid();

            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), item, "inapp", payLoad);
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            if (pendingIntent != null) {
                iaphelper.launchPurchaseFlow(this, getString(R.string.purchase), 1001, mPurchaseFinishedListener, payLoad);
            } else {
                // 결제가 막혔다면 왜 결제가 막혀있찌 대체????
                Toast.makeText(getApplicationContext(), "구매실패", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        return payload.equals(DataContainer.getInstance().getUid());
    }

    /**
     * 보유중인 아이템 체크
     */
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
            Log.d(getClass().getSimpleName(), "onQueryInventoryFinished");
            if (iaphelper == null) return;
            if (result.isFailure()) {
                Toast.makeText(getApplicationContext(), "onQueryInventoryFinished 실패", Toast.LENGTH_SHORT).show();
                //getPurchases() 실패했을때

                return;
            }
            //해당 아이템 구매 여부 체크
            Purchase purchase = inv.getPurchase(getString(R.string.purchase));

            if (purchase != null && verifyDeveloperPayload(purchase)) {
                //해당 아이템을 가지고 있는 경우.
                //아이템에대한 처리를 한다.

                Toast.makeText(getApplicationContext(), "onQueryInventoryFinished 이미 보유중", Toast.LENGTH_SHORT).show();

                try {
                    iaphelper.consumeAsync(inv.getPurchase(getString(R.string.purchase)), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }

            }else{
                // 가지고있지 않다면 구입
                buyItem(getString(R.string.purchase));
            }
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase info) {
            Toast.makeText(getApplicationContext(), "onIabPurchaseFinished 진입", Toast.LENGTH_SHORT).show();
            if (iaphelper == null) {
                Toast.makeText(getApplicationContext(), "iaphelper null", Toast.LENGTH_SHORT).show();
                return;
            }

            if (result.isFailure()) {
                Toast.makeText(getApplicationContext(), "구매 실패, 정상 경로를 이용해주세요.111", Toast.LENGTH_SHORT).show();
            } else {

                if (verifyDeveloperPayload(info)) {
                    //보낸 신호와 맞는경우
                    if (info.getSku().equals(getString(R.string.purchase))) {
                        Toast.makeText(getApplicationContext(), "구매 성공", Toast.LENGTH_SHORT).show();

                        try {
                            iaphelper.consumeAsync(info, mConsumeFinishedListener);
                        } catch (IabHelper.IabAsyncInProgressException e) {
                            e.printStackTrace();
                        }
                        //alreadyBuyedItem();
                    } else {
                        Toast.makeText(getApplicationContext(), "구매 실패, 정상 경로를 이용해주세요.222", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "구매 실패, 정상 경로를 이용해주세요.333", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    CoreListAdapter.OnUploadCloudCallback cloudLitener = new CoreListAdapter.OnUploadCloudCallback() {
        @Override
        public void upload(CloudEntity c) {

            setBilingService();
            cloudEntity = c;


        }
    };


    @Subscribe
    public void FinishActivity(TargetUserBlocksMeEvent someoneBlocksMeEvent) {
        finish();
    }
}

