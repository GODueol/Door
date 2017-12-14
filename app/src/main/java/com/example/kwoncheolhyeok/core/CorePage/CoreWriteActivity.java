package com.example.kwoncheolhyeok.core.CorePage;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.MyApplcation;
import com.example.kwoncheolhyeok.core.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kwon on 2017-12-14.
 */

public class CoreWriteActivity  extends AppCompatActivity {

    Toolbar toolbar = null;

    FloatingActionButton fab, picture_fab, audio_fab;
    LinearLayout pictureFab_layout, audioFab_layout;
    View fabBGLayout;
    boolean isFABOpen=false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.core_write_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        pictureFab_layout= (LinearLayout) findViewById(R.id.fabLayout2);
        audioFab_layout= (LinearLayout) findViewById(R.id.fabLayout3);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        picture_fab= (FloatingActionButton) findViewById(R.id.fab2);
        audio_fab = (FloatingActionButton) findViewById(R.id.fab3);
        fabBGLayout=findViewById(R.id.fabBGLayout);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });


    }

    private void showFABMenu(){
        isFABOpen=true;
        pictureFab_layout.setVisibility(View.VISIBLE);
        audioFab_layout.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);

        fab.animate().rotationBy(135);
        pictureFab_layout.animate().translationY(-getResources().getDimension(R.dimen.standard_75));
        audioFab_layout.animate().translationY(-getResources().getDimension(R.dimen.standard_150));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotationBy(-135);
        pictureFab_layout.animate().translationY(0);
        audioFab_layout.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(!isFABOpen){
                    pictureFab_layout.setVisibility(View.GONE);
                    audioFab_layout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

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
    };

    @Override
    public void onBackPressed() {
        if(isFABOpen){
            closeFABMenu();
        }else{
            super.onBackPressed();
        }
    }

}