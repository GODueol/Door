package com.example.kwoncheolhyeok.core.MessageActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.GlideApp;
import com.github.chrisbanes.photoview.PhotoView;


/**
 * Created by Administrator on 2018-01-28.
 */

public class ChattingFullImage extends AppCompatActivity {

    PhotoView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_fullimage_activity);
        Intent p = getIntent();
        String uri = (String) p.getSerializableExtra("imageUri");
        imageView = (PhotoView) findViewById(R.id.pictureImage);

        GlideApp.with(imageView.getContext())
                .load(uri)
                .fitCenter()
                .into(imageView);

    }
}
