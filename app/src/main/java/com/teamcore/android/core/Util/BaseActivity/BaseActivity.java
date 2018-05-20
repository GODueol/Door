package com.teamcore.android.core.Util.BaseActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.teamcore.android.core.Entity.CoreListItem;
import com.teamcore.android.core.Entity.CorePost;
import com.teamcore.android.core.Exception.ChildSizeMaxException;
import com.teamcore.android.core.Exception.NotSetAutoTimeException;
import com.teamcore.android.core.LoginActivity.LoginActivity;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.FireBaseUtil;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {


    public void deleteStorageObject(String url){
        try {
            FirebaseStorage.getInstance().getReferenceFromUrl(url).delete();
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    // TODO : 계정 삭제
    protected void deleteMyAccount(String uuid){

        deleteAllPost(uuid);
        deleteAllFriend();
        deleteMyLocaiton(uuid);
        deleteAllBlock();

        // TODO : 프사, 썸네일 모두 삭제
        for(String picUrl : DataContainer.getInstance().getUser().getPicUrls().toArrayAll()){
            deleteStorageObject(picUrl);
        }

        // TODO : DB 데이터 삭제
        FirebaseDatabase.getInstance().getReference().child("users").child(uuid).removeValue();

        FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(task -> {
            // 데이터가 모두 삭제되었습니다.
            Toast.makeText(this, "데이터가 모두 삭제되었습니다.", Toast.LENGTH_SHORT).show();

            FirebaseAuth.getInstance().signOut();
            Intent homeIntent = new Intent(this, LoginActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        });
    }

    // TODO : 포스트 삭제
    void deleteAllPost(String uuid){

        DatabaseReference targetRef = FirebaseDatabase.getInstance().getReference("posts").child(uuid);

        // TODO : 포스트별 사진, 음성 삭제
        targetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    CorePost corePost = snapshot.getValue(CorePost.class);

                    DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("posts");
                    final CoreListItem coreListItem = new CoreListItem(DataContainer.getInstance().getUser(), corePost, snapshot.getKey(), uuid);

                    FireBaseUtil.getInstance().deletePostExcution(coreListItem, postsRef, uuid);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // TODO : 프렌즈 삭제
    void deleteAllFriend(){
        // TODO : DB 데이터 삭제
        try {
            for(String uuid : DataContainer.getInstance().getUser().getFollowerUsers().keySet()) {
                FireBaseUtil.getInstance().follow(this, null, uuid, true);
            }
        } catch (ChildSizeMaxException e) {
            e.printStackTrace();
        } catch (NotSetAutoTimeException e) {
            e.printStackTrace();
        }
    }

    // TODO : 블럭 삭제
    void deleteAllBlock(){
        FireBaseUtil.getInstance().allUnblock(DataContainer.getInstance().getUser().getBlockUsers());
    }

    // TODO : 로케이션 삭제
    void deleteMyLocaiton(String uuid){
        FirebaseDatabase.getInstance().getReference().child("location").child(uuid).removeValue();
    }

}
