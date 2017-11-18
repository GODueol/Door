package com.example.kwoncheolhyeok.core.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.ClubActivity.Club_Filter_Activity;
import com.example.kwoncheolhyeok.core.CorePage.CoreActivity;
import com.example.kwoncheolhyeok.core.FriendsActivity.FriednsActivity;
import com.example.kwoncheolhyeok.core.LoginActivity.LoginActivity;
import com.example.kwoncheolhyeok.core.MessageActivity.MessageActivity;
import com.example.kwoncheolhyeok.core.ProfileModifyActivity.ProfileModifyActivity;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.BusProvider;
import com.example.kwoncheolhyeok.core.Util.CloseActivityHandler;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.example.kwoncheolhyeok.core.Util.PushEvent;
import com.google.firebase.auth.FirebaseAuth;
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
    TabLayout tabLayout = null;
    Drawable icon_open,icon_close;
    ImageView profileImage;

    private CloseActivityHandler closeActivityHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // (Main View)네비게이션바 관련
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

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
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                changeToggleIcon();
                return false;
            }
        });

        // people,board,club 스와이프 탭 view 관련
        // final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new TabPagerAdapter(
                getSupportFragmentManager(),
                getResources().getStringArray(R.array.titles_tab)));

        //TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

//        getSupportFragmentManager().beginTransaction().add(R.id.container,new TabFragment2()).commit();

        //네비게이션 뷰 내의 프로필 사진 클릭시 프로필 편집
        View headerview = navigationView.getHeaderView(0);
        profileImage = (ImageView) headerview.findViewById(R.id.profile_image);
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
        TextView emailText = (TextView) headerview.findViewById(R.id.textView);
        emailText.setText(DataContainer.getInstance().getUser().getEmail());

        closeActivityHandler = new CloseActivityHandler(this);

        // Otto 등록
        BusProvider.getInstance().register(this);

    }

    private void setProfilePic(ImageView profileImage) {
        FireBaseUtil fbUtil = FireBaseUtil.getInstance();
        fbUtil.setImage(fbUtil.getParentPath() + "profilePic1.jpg", profileImage);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            closeActivityHandler.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
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
        }

        if (id == R.id.find_text) {
            return true;
        }

        if (id == R.id.club_create) {
            return true;
        } else if (id == R.id.club_filter) {
            Intent i = new Intent(MainActivity.this, Club_Filter_Activity.class);
            startActivityForResult(i, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_People) {

            viewPager.setCurrentItem(0);
        } else if (id == R.id.nav_mycore) {

            Intent i = new Intent(MainActivity.this, CoreActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_message) {

            Intent i = new Intent(MainActivity.this, MessageActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_friends) {

            Intent i = new Intent(MainActivity.this, FriednsActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_logout) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivityForResult(i, 0);

            FirebaseAuth.getInstance().signOut();
            finish();
        }

       // DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    public void ToggleIconSet(){
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.icon, getTheme());
        Drawable drawable2 = ResourcesCompat.getDrawable(getResources(), R.drawable.icon2, getTheme());
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap2 = ((BitmapDrawable) drawable2).getBitmap();
        icon_open = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 85, 85, true));
        icon_close = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap2, 85, 85, true));
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
    public void FinishLoad(PushEvent pushEvent) {
        // 이벤트가 발생한뒤 수행할 작업
        // 프로필 사진을 다시 받아옴
        setProfilePic(profileImage);
    }

}

