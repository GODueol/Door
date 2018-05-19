package com.example.kwoncheolhyeok.core.Util.BaseActivity;

import android.support.v7.app.AppCompatActivity;

import com.example.kwoncheolhyeok.core.Entity.CoreListItem;
import com.example.kwoncheolhyeok.core.Entity.CorePost;
import com.example.kwoncheolhyeok.core.Exception.ChildSizeMaxException;
import com.example.kwoncheolhyeok.core.Exception.NotSetAutoTimeException;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.example.kwoncheolhyeok.core.Util.UiUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class BaseActivity extends AppCompatActivity {


    public void deleteStorageObject(String url){
        FirebaseStorage.getInstance().getReferenceFromUrl(url).delete();
    }

    public String getUid() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            UiUtil.getInstance().restartApp(this);
        return getUid();
    }

    // TODO : 계정 삭제
    void deleteMyAccount(){

        deleteAllPost();
        deleteAllFriend();
        deleteMyLocaiton();
        deleteAllBlock();

        // TODO : 프사, 썸네일 모두 삭제
        for(String picUrl : DataContainer.getInstance().getUser().getPicUrls().toArrayAll()){
            deleteStorageObject(picUrl);
        }

        // TODO : DB 데이터 삭제
        FirebaseDatabase.getInstance().getReference().child("users").child(getUid()).removeValue();
    }

    // TODO : 포스트 삭제
    void deleteAllPost(){

        DatabaseReference targetRef = FirebaseDatabase.getInstance().getReference("posts").child(getUid());

        // TODO : 포스트별 사진, 음성 삭제
        targetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    CorePost corePost = snapshot.getValue(CorePost.class);

                    DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("posts");
                    final String cUuid = getUid();
                    final CoreListItem coreListItem = new CoreListItem(DataContainer.getInstance().getUser(), corePost, snapshot.getKey(), cUuid);

                    FireBaseUtil.getInstance().deletePostExcution(coreListItem, postsRef, cUuid);
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
    void deleteMyLocaiton(){
        FirebaseDatabase.getInstance().getReference().child("location").child(getUid()).removeValue();
    }

}
