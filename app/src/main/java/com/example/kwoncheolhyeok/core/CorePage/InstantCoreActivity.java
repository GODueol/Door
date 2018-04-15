package com.example.kwoncheolhyeok.core.CorePage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.Entity.CorePost;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Administrator on 2018-03-25.
 */

public class InstantCoreActivity extends AppCompatActivity {

    private String cUuid,postId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent p = getIntent();
        cUuid = (String)p.getSerializableExtra("cUuid");
        postId = (String)p.getSerializableExtra("postId");
        // 포스트 데이터를 가져옴
        getPostData();
    }

    public void getPostData(){
        FirebaseDatabase.getInstance().getReference("posts").child(cUuid).child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    CorePost corePost = dataSnapshot.getValue(CorePost.class);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"삭제된 포스트 입니다.",Toast.LENGTH_SHORT);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
