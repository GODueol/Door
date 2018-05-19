package com.teamcore.android.core.MessageActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.GlideApp;
import com.github.chrisbanes.photoview.PhotoView;


/**
 * Created by Administrator on 2018-01-28.
 */

public class ChattingFullImage extends AppCompatActivity {

    PhotoView imageView;
    Toolbar toolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_fullimage_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        Intent p = getIntent();
        String url = (String) p.getSerializableExtra("imageUrl");
        imageView = (PhotoView) findViewById(R.id.pictureImage);

        GlideApp.with(imageView.getContext())
                .load(url)
                .fitCenter()
                .into(imageView);

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

}
