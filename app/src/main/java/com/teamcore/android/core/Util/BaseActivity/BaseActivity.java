package com.teamcore.android.core.Util.BaseActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
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
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.FireBaseUtil;
import com.teamcore.android.core.Util.bilingUtil.IabHelper;
import com.teamcore.android.core.Util.bilingUtil.Purchase;

import org.jdeferred.Deferred;
import org.jdeferred.DeferredManager;
import org.jdeferred.DeferredRunnable;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredObject;
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


    // TODO : 구독 결제 확인
    IInAppBillingService mService;
    public Promise<Boolean, String, Integer> checkCorePlus(){


        DeferredObject deferred = new DeferredObject();
        Promise promise = deferred.promise();


        // 핼퍼 setup
        IabHelper iaphelper = new IabHelper(this, getString(R.string.GP_LICENSE_KEY));
        ServiceConnection mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
            }
        };

        // 결제 서비스를 위한 인텐트 초기화
        Intent intent=new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage("com.android.vending");
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);


        IabHelper.QueryInventoryFinishedListener mGotInventoryListener = (result, inv) -> {
            Toast.makeText(getApplicationContext(),"onQueryInventoryFinished",Toast.LENGTH_SHORT).show();
            if (result.isFailure()) {
                Toast.makeText(getApplicationContext(),"onQueryInventoryFinished 실패",Toast.LENGTH_SHORT).show();
                //getPurchases() 실패했을때

                deferred.reject("getPurchases 실패");
                return;
            }
            Bundle activeSubs;
            try {
                activeSubs = mService.getPurchases(3, getPackageName(), "subs", DataContainer.getInstance().getUid());
            } catch (RemoteException e) {
                e.printStackTrace();
                deferred.reject(e.getMessage());
                return;
            }

            //해당 아이템 구매 여부 체크
            Purchase purchase = inv.getPurchase(getString(R.string.subscribe));

            if (purchase != null &&  verifyDeveloperPayload(purchase)) {
                //해당 아이템을 가지고 있는 경우.
                //아이템에대한 처리를 한다.
                //alreadyBuyedItem();

                Toast.makeText(getApplicationContext(),purchase.getPurchaseState()+"onQueryInventoryFinished 이미 보유중",Toast.LENGTH_SHORT).show();

                deferred.resolve(true);

            }
            deferred.resolve(false);
        };

        iaphelper.startSetup(result -> {
            if (!result.isSuccess()) {
                Toast.makeText(BaseActivity.this, "문제발생", Toast.LENGTH_SHORT).show();
                deferred.reject(result.getMessage());
                return;
            }

            try {
                iaphelper.queryInventoryAsync(mGotInventoryListener);
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
                deferred.reject(e.getMessage());
            }
        });

        return promise;
    }

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return payload.equals(DataContainer.getInstance().getUid());
    }

}
