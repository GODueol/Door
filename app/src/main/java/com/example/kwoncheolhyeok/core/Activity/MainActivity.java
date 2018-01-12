package com.example.kwoncheolhyeok.core.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.Event.RefreshLocationEvent;
import com.example.kwoncheolhyeok.core.Event.SetProfilePicEvent;
import com.example.kwoncheolhyeok.core.FriendsActivity.FriendsActivity;
import com.example.kwoncheolhyeok.core.LoginActivity.LoginActivity;
import com.example.kwoncheolhyeok.core.MessageActivity.MessageActivity;
import com.example.kwoncheolhyeok.core.ProfileModifyActivity.ProfileModifyActivity;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.ScreenshotSetApplication;
import com.example.kwoncheolhyeok.core.SettingActivity.SettingActivity;
import com.example.kwoncheolhyeok.core.Util.BusProvider;
import com.example.kwoncheolhyeok.core.Util.CloseActivityHandler;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.UiUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.otto.Subscribe;

/**
 *
 * drawer / viewpager drag duplication issue 
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer = null;
    ActionBarDrawerToggle toggle = null;
    Toolbar toolbar = null;
    //View people, board, club;

    ViewPager viewPager = null;
    Drawable icon_open,icon_close;
    ImageView profileImage;

    private CloseActivityHandler closeActivityHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ScreenshotSetApplication.getInstance().allowUserSaveScreenshot(true);

        // (Main View)네비게이션바 관련
        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

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
        toggle.setHomeAsUpIndicator(icon_open);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToggleIcon();
            }
        });
        //Navigation view
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawer.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                changeToggleIcon();
                return false;
            }
        });

        // people,board,club 스와이프 탭 view 관련
        // final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager = findViewById(R.id.pager);
        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(
                getSupportFragmentManager(),
                getResources().getStringArray(R.array.titles_tab));
        viewPager.setAdapter(tabPagerAdapter);
        viewPager.setCurrentItem(0);

//        //TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
//        tabLayout = findViewById(R.id.tab_layout);
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//        tabLayout.setupWithViewPager(viewPager);

        //네비게이션 뷰 내의 프로필 사진 클릭시 프로필 편집
        View headerView = navigationView.getHeaderView(0);
        profileImage = headerView.findViewById(R.id.profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i2 = new Intent(MainActivity.this, ProfileModifyActivity.class);
                startActivity(i2);
            }
        });

        // Set Profile Pic
        setProfilePic(profileImage);

        // 이메일 Set
        TextView emailText = headerView.findViewById(R.id.textView);
        try {
            emailText.setText(DataContainer.getInstance().getUser().getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }

        closeActivityHandler = new CloseActivityHandler(this);

        // Otto 등록
        BusProvider.getInstance().register(this);

        // 로그인 시간 Update
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) return;
        User mUser = DataContainer.getInstance().getUser();
        if(mUser == null || mUser.getEmail().isEmpty() || mUser.getEmail().equals("")) {
            Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
            logout();
            return;
        }
        mUser.setLoginDate(System.currentTimeMillis());
        DataContainer.getInstance().getUsersRef().child(user.getUid()).setValue(mUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(getLocalClassName(),"Success Save Login Time");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Save Fail", Toast.LENGTH_SHORT).show();
                        Log.d(getApplication().getClass().getName(), e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        UiUtil.getInstance().stopProgressDialog();
                    }
                });
    }

    private void setProfilePic(ImageView profileImage) {
        String picUrl1 = DataContainer.getInstance().getUser().getPicUrls().getPicUrl1();
        if(picUrl1 == null) return;
        Glide.with(getBaseContext()).load(picUrl1).into(profileImage);
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

        if (id == R.id.find_map) {
            Intent i = new Intent(MainActivity.this, MapsActivity.class);
            startActivityForResult(i, 0);
            return true;
        } else if (id == R.id.find_id) {
            return true;
        } else if (id == R.id.lock_all) {

            final User user = DataContainer.getInstance().getUser();
            if(user.getUnLockUsers().size()==0) {
                Toast.makeText(getBaseContext(),"이미 모든 유저에게 사진 잠금이 설정되어있습니다",Toast.LENGTH_SHORT).show();
                return true;
            }

            // 다이얼로그
            UiUtil.getInstance().showDialog(MainActivity.this, "모든 유저 사진 잠금"
                    , "모든 유저 대상으로 사진을 잠그시겠습니까?", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    UiUtil.getInstance().startProgressDialog(MainActivity.this);
                    user.getUnLockUsers().clear();
                    DataContainer.getInstance().getUsersRef().child(DataContainer.getInstance().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DataContainer.getInstance().setUser(user);
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
            return true;
        } else if(id == R.id.unblock_all) {
            final User user = DataContainer.getInstance().getUser();
            if(user.getBlockUsers().size()==0) {
                Toast.makeText(getBaseContext(),"이미 모든 유저 블락이 해제되어있습니다",Toast.LENGTH_SHORT).show();
                return true;
            }

            // 다이얼로그
            UiUtil.getInstance().showDialog(MainActivity.this, "모든 유저 블락 해제",
                    "모든 유저 대상으로 블럭을 해제하시겠습니까?", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    UiUtil.getInstance().startProgressDialog(MainActivity.this);
                    user.getBlockUsers().clear();
                    DataContainer.getInstance().getUsersRef().child(DataContainer.getInstance().getUid()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DataContainer.getInstance().setUser(user);
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
            return true;
        }

//        if (id == R.id.find_text) {
//            return true;
//        }
//
//        if (id == R.id.club_create) {
//            return true;
//        } else if (id == R.id.club_filter) {
////            Intent i = new Intent(MainActivity.this, Club_Filter_Activity.class);
////            startActivityForResult(i, 0);
//            return true;
//        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        switch (viewPager.getCurrentItem()) {
//            case 0:
//                getMenuInflater().inflate(R.menu.chat_menu, menu);
//                break;
            case 0:
                getMenuInflater().inflate(R.menu.main_acitivity_menu, menu);
                break;
//            case 2:
//                getMenuInflater().inflate(R.menu.club_menu, menu);
//                break;
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

            viewPager.setCurrentItem(0);
        } else if (id == R.id.nav_mycore) {
            UiUtil.getInstance().goToCoreActivity(MainActivity.this,DataContainer.getInstance().getUid());
        } else if (id == R.id.nav_message) {

            Intent i = new Intent(MainActivity.this, MessageActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_friends) {

            Intent i = new Intent(MainActivity.this, FriendsActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_setting) {
            Intent i = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_logout) {
            logout();
        }

        changeToggleIcon();

        return true;
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        DataContainer.getInstance().setUser(null);
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }


    public void ToggleIconSet(){
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.icon, getTheme());
        Drawable drawable2 = ResourcesCompat.getDrawable(getResources(), R.drawable.icon2, getTheme());
        Bitmap bitmap = null;
        if (drawable != null) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap2 = null;
        if (drawable2 != null) {
            bitmap2 = ((BitmapDrawable) drawable2).getBitmap();
        }
        if (bitmap != null) {
            icon_open = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 85, 85, true));
        }
        if (bitmap2 != null) {
            icon_close = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap2, 85, 85, true));
        }
    }
    /**
     * 토클 이미지 변경
     */
    public void changeToggleIcon(){

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
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void finishLoad(SetProfilePicEvent setProfilePicEvent) {
        // 이벤트가 발생한뒤 수행할 작업
        // 프로필 사진을 다시 받아옴
        setProfilePic(profileImage);
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

