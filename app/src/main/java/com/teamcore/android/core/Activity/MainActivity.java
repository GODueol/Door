package com.teamcore.android.core.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamcore.android.core.CorePage.CoreCloudActivity;
import com.teamcore.android.core.Entity.User;
import com.teamcore.android.core.Event.TargetUserBlocksMeEvent;
import com.teamcore.android.core.Exception.NotSetAutoTimeException;
import com.teamcore.android.core.FriendsActivity.FriendsActivity;
import com.teamcore.android.core.LoginActivity.LoginActivity;
import com.teamcore.android.core.MessageActivity.MessageActivity;
import com.teamcore.android.core.ProfileModifyActivity.ProfileModifyActivity;
import com.teamcore.android.core.R;
import com.teamcore.android.core.SettingActivity.CorePlusActivity;
import com.teamcore.android.core.SettingActivity.SettingActivity;
import com.teamcore.android.core.Util.BaseActivity.BaseActivity;
import com.teamcore.android.core.Util.BusProvider;
import com.teamcore.android.core.Util.CloseActivityHandler;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.FirebaseIDService;
import com.teamcore.android.core.Util.GlideApp;
import com.teamcore.android.core.Util.RemoteConfig;
import com.teamcore.android.core.Util.SharedPreferencesUtil;
import com.teamcore.android.core.Util.UiUtil;
import com.teamcore.android.core.Util.bilingUtil.IabHelper;
import com.teamcore.android.core.Util.bilingUtil.IabResult;
import com.teamcore.android.core.Util.bilingUtil.Inventory;
import com.teamcore.android.core.Util.bilingUtil.Purchase;

/**
 * drawer / viewpager drag duplication issue
 */
public class MainActivity extends BaseActivity
        implements OnNavigationItemSelectedListener, OnSharedPreferenceChangeListener {

    private static final int SETTING = 4;
    DrawerLayout drawer = null;
    ActionBarDrawerToggle toggle = null;
    Toolbar toolbar = null;
    //View people, board, club;

    ViewPager viewPager = null;
    Drawable icon_open, icon_close, icon_open_badge;
    ImageView profileImage;
    ImageButton nav_alarm;

    private CloseActivityHandler closeActivityHandler;

    TextView peopleBadge;
    TextView coreBadge;
    TextView messageBadge;
    TextView friendBadge;
    TextView settingBadge;

    private SharedPreferencesUtil SPUtil;

    IabHelper iaphelper;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SPUtil = new SharedPreferencesUtil(getApplicationContext());
//        ScreenshotSetApplication.getInstance().allowUserSaveScreenshot(true);
        // 블락 탐지 엑티비티 Uuid 초기화
        // (Main View)네비게이션바 관련
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent p = getIntent();
        String chatFlag = (String) p.getSerializableExtra("chat");
        if (chatFlag != null && chatFlag.equals("chat")) {
            Intent intent = new Intent(this, MessageActivity.class);
            startActivity(intent);
        }

        //툴바 이미지 붙이기 (코어 회원이면 drawable : tb_core / 코어플러스 회원이면 tb_coreplus)
        ImageView coreplus = (ImageView) findViewById(R.id.tb_coreplus);
        checkCorePlus().done(isPlus -> {
            DataContainer.getInstance().isPlus = isPlus;
            int res;
            if (isPlus) res = R.drawable.tb_coreplus;
            else res = R.drawable.tb_core;
            GlideApp.with(this)
                    .load(UiUtil.resourceToUri(this, res))
                    .fitCenter()
                    .into(coreplus);
        });

        setSupportActionBar(toolbar);
        checkCorePlus().done(isPlus -> {
            if (!isPlus) {
                AdView mAdView = (AdView) findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .addTestDevice("0D525D9C92269D80384121978C3C4267")
                        .build();
                mAdView.loadAd(adRequest);
            }
        });


        ToggleIconSet();
        toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);

        toggle.setToolbarNavigationClickListener(v -> {
            SPUtil.setMainIcon(getString(R.string.mainAlarm), false);
            changeToggleIcon();
        });

        //Navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null); //네비게이션 안의 아이콘 색을 오리지널로 표현

        //네비게이션 드로워 안의 COREPLUS 텍스트 색 변경
        Menu menu = navigationView.getMenu();
        MenuItem tools = menu.findItem(R.id.nav_coreplus);
        SpannableString s = new SpannableString(tools.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.CorePlusColor), 0, s.length(), 0);
        tools.setTitle(s);


        navigationView.setNavigationItemSelectedListener(this);
        drawer.setOnTouchListener((view, motionEvent) -> {
            changeToggleIcon();
            return false;
        });

        // people,board,club 스와이프 탭 view 관련
        // final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager = (ViewPager) findViewById(R.id.pager);
        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(
                getSupportFragmentManager(),
                getResources().getStringArray(R.array.titles_tab));
        try {
            LatLng latLng = p.getExtras().getParcelable("latLng");
            tabPagerAdapter.setLatLng(latLng);
        } catch (Exception e) {
            Log.d("main", "익셉션");
        }
        viewPager.setAdapter(tabPagerAdapter);
        viewPager.setCurrentItem(0);

        //네비게이션 뷰 내의 프로필 사진 클릭시 프로필 편집
        View headerView = navigationView.getHeaderView(0);
        profileImage = headerView.findViewById(R.id.profile_image);
        profileImage.setOnClickListener(v -> {
            Intent i2 = new Intent(MainActivity.this, ProfileModifyActivity.class);
            startActivity(i2);
        });

        // nav_alarm
        nav_alarm = headerView.findViewById(R.id.nav_alarm);
        boolean alarmIcon = SPUtil.getAlarmIcon(getString(R.string.navAlarm));
        if (!alarmIcon) {
            nav_alarm.setBackgroundResource(R.drawable.nav_alarm_off);
        } else {
            nav_alarm.setBackgroundResource(R.drawable.nav_alarm_on);
        }
        nav_alarm.setOnClickListener(v -> {
            SPUtil.setAlarmIcon(getString(R.string.navAlarm), false);
            checkCorePlus().done(isPlus ->{
                new NavAlarmDialog(MainActivity.this,isPlus).show();
            });
        });

        // Set Profile Pic
        setProfilePic(profileImage);

        closeActivityHandler = new CloseActivityHandler(this);

        // 로그인 시간 Update
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        User mUser = DataContainer.getInstance().getUser();
        if (mUser == null || mUser.getEmail().isEmpty() || mUser.getEmail().equals("")) {
            Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
            logout();
            return;
        }

        FirebaseIDService fids = new FirebaseIDService();
        fids.onTokenRefresh();
        try {
            mUser.setLoginDate(UiUtil.getInstance().getCurrentTime(MainActivity.this));
        } catch (NotSetAutoTimeException e) {
            e.printStackTrace();
            Toast.makeText(fids, e.getMessage(), Toast.LENGTH_SHORT).show();
            ActivityCompat.finishAffinity(MainActivity.this);
        }
        fids.setUserToken(mUser);
        DataContainer.getInstance().getUsersRef().child(user.getUid()).setValue(mUser)
                .addOnSuccessListener(aVoid -> Log.d(getLocalClassName(), "Success Save Login Time"))
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Save Fail", Toast.LENGTH_SHORT).show();
                    Log.d(getApplication().getClass().getName(), e.getMessage());
                })
                .addOnCompleteListener(task -> UiUtil.getInstance().stopProgressDialog());

        SPUtil.getBadgePreferences().registerOnSharedPreferenceChangeListener(this);

        boolean check = SPUtil.getMainIcon(getString(R.string.mainAlarm));
        if (!check) {
            toggle.setHomeAsUpIndicator(icon_open);
        } else {
            toggle.setHomeAsUpIndicator(icon_open_badge);
        }

        // 네비게이션 아이템 벳지
        navigationViewinitBadge(menu);

        Log.d("test", user.getUid());
        FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("blockMeUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SPUtil = new SharedPreferencesUtil(getApplicationContext());
                String currentActivity = SPUtil.getBlockMeUserCurrentActivity(getString(R.string.currentActivity));
                Log.d("test", "들어옴");
                for (DataSnapshot blockMeUserSnapshot : dataSnapshot.getChildren()) {
                    if (blockMeUserSnapshot.getKey() != null && blockMeUserSnapshot.getKey().equals(currentActivity)) {
                        /*Intent intent = new Intent(getApplication(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        Log.d("test","엑티비티 팅겨라");*/
                        BusProvider.getInstance().post(new TargetUserBlocksMeEvent());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        String PUBLIC_KEY = getString(R.string.GP_LICENSE_KEY);
        // 핼퍼 setup
        iaphelper = new IabHelper(this, PUBLIC_KEY);
        iaphelper.startSetup(result -> {
            if (!result.isSuccess()) {
                Toast.makeText(MainActivity.this, "문제발생", Toast.LENGTH_SHORT).show();
                return;
            }

            if (iaphelper == null) return;
            try {
                iaphelper.queryInventoryAsync(mGotInventoryListener);
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        });

        // remoteConfig
        RemoteConfig.getConfig(this);

    }

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
/*
            Bundle activeSubs;
            try {
                activeSubs = mService.getPurchases(3, getPackageName(), "subs", DataContainer.getInstance().getUid());
            } catch (RemoteException e) {
                e.printStackTrace();
            }*/

            //해당 아이템 구매 여부 체크
            Purchase purchase = inv.getPurchase(getString(R.string.subscribe));

            if (purchase != null && purchase.getPurchaseState() == 0 && verifyDeveloperPayload(purchase)) {
                //해당 아이템을 가지고 있는 경우.
                //아이템에대한 처리를 한다.
                Toast.makeText(getApplicationContext(), purchase.getPurchaseState() + "onQueryInventoryFinished 이미 보유중", Toast.LENGTH_SHORT).show();
            }
        }
    };

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        return payload.equals(DataContainer.getInstance().getUid());
    }


    private void navigationViewinitBadge(Menu menu) {
        //Gravity property aligns the text
        MenuItem people = menu.findItem(R.id.nav_People);
        peopleBadge = (TextView) people.getActionView();
        badgeRoundStyle(peopleBadge, false);

        MenuItem core = menu.findItem(R.id.nav_mycore);
        coreBadge = (TextView) core.getActionView();
        int badgePost = SPUtil.getBadgeCount(getString(R.string.badgePost));
        badgeStyle(coreBadge, badgePost);

        MenuItem message = menu.findItem(R.id.nav_message);
        messageBadge = (TextView) message.getActionView();
        int badgeChat = SPUtil.getBadgeCount(getString(R.string.badgeChat));
        badgeStyle(messageBadge, badgeChat);

        MenuItem friends = menu.findItem(R.id.nav_friends);
        friendBadge = (TextView) friends.getActionView();
        int badgeFriends = SPUtil.getBadgeCount(getString(R.string.badgeFriends));
        badgeStyle(friendBadge, badgeFriends);

        MenuItem setting = menu.findItem(R.id.nav_setting);
        settingBadge = (TextView) setting.getActionView();
        badgeRoundStyle(settingBadge, false);
    }

    private void badgeStyle(TextView badge, int i) {
        badge.setGravity(Gravity.CENTER_VERTICAL);
        badge.setTextSize(11);
        badge.setTextColor(getResources().getColor(R.color.black));
        String str;
        if (i != 0) {
            str = Integer.toString(i);
        } else {
            str = "";
        }
        badge.setText(str);
    }

    private void badgeRoundStyle(TextView badge, boolean c) {

        badge.setGravity(Gravity.CENTER_VERTICAL);
        badge.setTypeface(null, Typeface.BOLD);
        badge.setTextColor(getResources().getColor(R.color.colorAccent));
        badge.setTextSize(8);
        if (c) {
            badge.setText("●");
        } else {
            badge.setText("");
        }
    }

    private void setProfilePic(final ImageView profileImage) {
        try {
            DataContainer.getInstance().getMyUserRef().child("picUrls/thumbNail_picUrl1").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String picUrl1 = (String) dataSnapshot.getValue();
                    if (picUrl1 == null) return;
                    Glide.with(getBaseContext()).load(picUrl1).into(profileImage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            UiUtil.getInstance().restartApp(MainActivity.this);
        }
    }


    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            changeToggleIcon();

        } else {
//            super.onBackPressed();
            closeActivityHandler.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.find_map:
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivityForResult(i, 0);
                return true;
            case R.id.find_id:
                startActivity(new Intent(MainActivity.this, FindUserActivity.class));
                return true;
            case R.id.lock_all:

                final User user = DataContainer.getInstance().getUser();
                if (user.getUnLockUsers().size() == 0) {
                    Toast.makeText(getBaseContext(), "이미 모든 유저에게 사진을 비공개 하였습니다", Toast.LENGTH_SHORT).show();
                    return true;
                }

                // 다이얼로그
                UiUtil.getInstance().showDialog(MainActivity.this, "모든 유저 사진 잠금"
                        , "모든 유저 대상으로 사진을 잠그시겠습니까?", (dialog, whichButton) -> {
                            UiUtil.getInstance().startProgressDialog(MainActivity.this);
                            user.getUnLockUsers().clear();
                            DataContainer.getInstance().getUsersRef().child(DataContainer.getInstance().getUid()).setValue(user).addOnSuccessListener(aVoid -> DataContainer.getInstance().setUser(user)).addOnCompleteListener(task -> UiUtil.getInstance().stopProgressDialog());
                        }, (dialog, whichButton) -> {
                        });
                return true;


            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        switch (viewPager.getCurrentItem()) {
            case 0:
                getMenuInflater().inflate(R.menu.main_acitivity_menu, menu);
                break;
            default:
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_People) {
//            viewPager.setCurrentItem(0);
            Intent i = new Intent(MainActivity.this, CoreCloudActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_mycore) {
            UiUtil.getInstance().goToCoreActivity(MainActivity.this, DataContainer.getInstance().getUid());
        } else if (id == R.id.nav_message) {

            Intent i = new Intent(MainActivity.this, MessageActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_friends) {

            Intent i = new Intent(MainActivity.this, FriendsActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_setting) {
            Intent i = new Intent(MainActivity.this, SettingActivity.class);
            startActivityForResult(i, SETTING);

        } else if (id == R.id.nav_coreplus) {
            Intent i = new Intent(MainActivity.this, CorePlusActivity.class);
            startActivity(i);

        }

        changeToggleIcon();

        return true;
    }

    private void logout() {

        Task<Void> task = FirebaseDatabase.getInstance().getReference("users").child(DataContainer.getInstance().getUid()).child("token").removeValue();
        final Intent i = new Intent(this, LoginActivity.class);
        task.addOnSuccessListener(aVoid -> {
            FirebaseAuth.getInstance().signOut();
            DataContainer.getInstance().setUser(null);
            startActivity(i);
            finish();
        });

    }


    public void ToggleIconSet() {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.icon, getTheme());
        Drawable drawable2 = ResourcesCompat.getDrawable(getResources(), R.drawable.icon_badge, getTheme());
        Drawable drawable3 = ResourcesCompat.getDrawable(getResources(), R.drawable.icon2, getTheme());

        Bitmap bitmap = null;
        Bitmap bitmap2 = null;
        Bitmap bitmap3 = null;


        if (drawable != null) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        }
        if (drawable2 != null) {
            bitmap2 = ((BitmapDrawable) drawable2).getBitmap();
        }
        if (drawable3 != null) {
            bitmap3 = ((BitmapDrawable) drawable3).getBitmap();
        }
        if (bitmap != null) {
            icon_open = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 90, 90, true));
        }
        if (bitmap2 != null) {
            icon_open_badge = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap2, 90, 90, true));
        }
        if (bitmap3 != null) {
            icon_close = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap3, 90, 90, true));
        }
    }

    /**
     * 토클 이미지 변경
     */
    public void changeToggleIcon() {

        if (drawer.isDrawerVisible(GravityCompat.START)) {
            //드로워 열었을 때 아이콘
            toggle.setHomeAsUpIndicator(icon_open);
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //드로워 다시 닫혔을 때 아이콘
            toggle.setHomeAsUpIndicator(icon_close);
            drawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UiUtil.getInstance().stopProgressDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        SPUtil.setBlockMeUserCurrentActivity(getString(R.string.currentActivity), null);
        checkMainToggle();
//        ScreenshotSetApplication.getInstance().registerScreenshotObserver();
    }

    @Override
    public void onPause() {
        super.onPause();
//        ScreenshotSetApplication.getInstance().unregisterScreenshotObserver();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SETTING) {
            if (resultCode == SettingActivity.LOGOUT) {
                logout();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals("mainAlarm")) {
            boolean b = SPUtil.getMainIcon(key);
            if (b) {
                toggle.setHomeAsUpIndicator(icon_open_badge);
            } else {
                toggle.setHomeAsUpIndicator(icon_open);
            }
        }

        switch (key) {
            case "badgeChat":
                int badgeChat = sharedPreferences.getInt(key, 0);
                badgeStyle(messageBadge, badgeChat);
                break;
            case "badgePost":
                int badgePost = sharedPreferences.getInt(key, 0);
                badgeStyle(coreBadge, badgePost);
                break;
            case "badgeFriends":
                boolean badgeState = sharedPreferences.getBoolean("badgeView", false);
                int badgeFriends = sharedPreferences.getInt(key, 0);
                badgeStyle(friendBadge, badgeFriends);
                if (badgeFriends == 0) {
                    badgeRoundStyle(friendBadge, badgeState);
                }
                break;
            case "badgeView":
                boolean badgeStat = sharedPreferences.getBoolean(key, false);
                int count = SPUtil.getBadgeCount("badgeFriends");
                if (count == 0) {
                    badgeRoundStyle(friendBadge, badgeStat);
                }
                break;
            case "navAlarm":
                boolean navState = sharedPreferences.getBoolean(key, false);
            {
                if (!navState) {
                    nav_alarm.setBackgroundResource(R.drawable.nav_alarm_off);
                } else {
                    nav_alarm.setBackgroundResource(R.drawable.nav_alarm_on);
                }
            }
        }
    }

    // 메인토글버튼 동기화
    private void checkMainToggle() {
        Log.d("test", "dsaad");
        try {
            if (messageBadge.getText().equals("") && coreBadge.getText().equals("") && friendBadge.getText().equals("")) {
                SPUtil.setMainIcon(getString(R.string.mainAlarm), false);
            }
        } catch (Exception e) {
            SPUtil.setMainIcon(getString(R.string.mainAlarm), false);
        }
    }
}

