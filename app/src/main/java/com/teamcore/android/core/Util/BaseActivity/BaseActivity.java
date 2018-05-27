package com.teamcore.android.core.Util.BaseActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
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

import org.jdeferred.Deferred;
import org.jdeferred.DeferredManager;
import org.jdeferred.Promise;
import org.jdeferred.impl.DefaultDeferredManager;
import org.jdeferred.impl.DeferredObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {


    public Task<Void> deleteStorageObject(String url){
        try {
            return FirebaseStorage.getInstance().getReferenceFromUrl(url).delete();
        } catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    // TODO : 계정 삭제
    protected void deleteMyAccount(String uuid){

        // task 등록
        DeferredManager dm = new DefaultDeferredManager();
        dm.when(deleteAllPost(uuid), deleteAllFriend(), deleteMyLocation(uuid), deleteAllBlock()
                ,getPromise(FirebaseDatabase.getInstance().getReference().child("users").child(uuid).removeValue())
                ,deleteAllProfilePic()
        ).done(result -> FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(task -> {
            // 데이터가 모두 삭제되었습니다.
            Toast.makeText(this, "데이터가 모두 삭제되었습니다.", Toast.LENGTH_SHORT).show();

            FirebaseAuth.getInstance().signOut();
            Intent homeIntent = new Intent(this, LoginActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
        }));
    }

    @SuppressLint("LogNotTimber")
    private Promise deleteAllProfilePic() {
        Deferred deferred = new DeferredObject();
        Promise promise = deferred.promise();
        AtomicInteger count = new AtomicInteger();
        ArrayList<String> array = DataContainer.getInstance().getUser().getPicUrls().toArrayAll();

        if(array.size() == 0) {
            deferred.resolve(null);
        }

        for(String picUrl : array){
            Task<Void> deleteTask = deleteStorageObject(picUrl);
            if(deleteTask != null){
                count.getAndIncrement();
                deleteTask.addOnCompleteListener(task -> {
                    Log.d("KBJ", "deleteAllProfilePic count : " + count.get());
                    if(count.decrementAndGet() == 0){
                        deferred.resolve(null);
                    }
                });
            }
        }

        promise.then(obj -> {
            Log.d("KBJ", "deleteAllProfilePic Done");
            Toast.makeText(this, "deleteAllProfilePic Done", Toast.LENGTH_SHORT).show();
        });

        return promise;
    }

    // TODO : 포스트 삭제
    @SuppressLint("LogNotTimber")
    Promise deleteAllPost(String uuid){

        Deferred deferred = new DeferredObject();
        Promise promise = deferred.promise();


        DatabaseReference targetRef = FirebaseDatabase.getInstance().getReference("posts").child(uuid);

        // TODO : 포스트별 사진, 음성 삭제
        targetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                AtomicInteger childCount = new AtomicInteger((int) dataSnapshot.getChildrenCount());
                if(childCount.get() == 0){
                    deferred.resolve("deleteAllPost Done");
                }
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    CorePost corePost = snapshot.getValue(CorePost.class);

                    DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("posts");
                    final CoreListItem coreListItem = new CoreListItem(DataContainer.getInstance().getUser(), corePost, snapshot.getKey(), uuid);

                    FireBaseUtil.getInstance().deletePostExecution(coreListItem, postsRef, uuid, () -> {
                        // 모두 삭제되는지 체크
                        if(childCount.decrementAndGet() == 0) {
                            deferred.resolve("deleteAllPost Done");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        promise.then(obj -> {
            Log.d("KBJ", "deleteAllPost Done");
            Toast.makeText(BaseActivity.this, "deleteAllPost Done", Toast.LENGTH_SHORT).show();
        });

        return promise;

    }

    Promise getPromise(Task task){
        Deferred deferred = new DeferredObject();
        Promise promise = deferred.promise();
        task.addOnCompleteListener(deferred::resolve);
        return promise;
    }

    // TODO : 프렌즈 삭제
    Promise deleteAllFriend(){
        Deferred deferred = new DeferredObject();
        Promise promise = deferred.promise();

        // TODO : DB 데이터 삭제
        try {
            AtomicInteger childCount = new AtomicInteger(DataContainer.getInstance().getUser().getFollowerUsers().size());
            if(childCount.get() == 0) {
                deferred.resolve(null);
            }

            for(String uuid : DataContainer.getInstance().getUser().getFollowerUsers().keySet()) {
                FireBaseUtil.getInstance().follow(this, null, uuid, true).addOnCompleteListener(task -> {
                    if(childCount.decrementAndGet() == 0) {
                        deferred.resolve(null);
                    }
                });
            }
        } catch (ChildSizeMaxException e) {
            e.printStackTrace();
        } catch (NotSetAutoTimeException e) {
            e.printStackTrace();
        }

        promise.then(obj -> {
            Log.d("KBJ", "deleteAllFriend Done");
            Toast.makeText(this, "deleteAllFriend Done", Toast.LENGTH_SHORT).show();
        });

        return promise;
    }

    // TODO : 블럭 삭제
    Promise deleteAllBlock(){
        Promise promise = getPromise(FireBaseUtil.getInstance().allUnblock(DataContainer.getInstance().getUser().getBlockUsers()));
        promise.then(object -> {
            Log.d("KBJ", "deleteAllBlock Done");
            Toast.makeText(this, "deleteAllBlock Done", Toast.LENGTH_SHORT).show();
        });
        return promise;
    }

    // TODO : 로케이션 삭제
    Promise deleteMyLocation(String uuid){

        Promise promise = getPromise(FirebaseDatabase.getInstance().getReference().child("location").child(uuid).removeValue());
        promise.then(object -> {
            Log.d("KBJ", "deleteMyLocation Done");
            Toast.makeText(this, "deleteMyLocation Done", Toast.LENGTH_SHORT).show();
        });
        return promise;
    }

}
