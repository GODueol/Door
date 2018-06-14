package com.teamcore.android.core.Util.BaseActivity;

import android.annotation.SuppressLint;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.UiUtil;
import com.teamcore.android.core.Util.bilingUtil.IabHelper;
import com.teamcore.android.core.Util.bilingUtil.Purchase;

import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    // 구독 결제 확인
    public Promise<Boolean, String, Integer> checkCorePlus(){
        DeferredObject deferred = new DeferredObject();
        Promise promise = deferred.promise();


        // 핼퍼 setup
        IabHelper iaphelper = new IabHelper(this, getString(R.string.GP_LICENSE_KEY));


        IabHelper.QueryInventoryFinishedListener mGotInventoryListener = (result, inv) -> {
            if (result.isFailure()) {
                //getPurchases() 실패했을때
                deferred.reject("getPurchases 실패");
                return;
            }

            //해당 아이템 구매 여부 체크
            Purchase purchase = inv.getPurchase(getString(R.string.subscribe));

            if (purchase != null &&  verifyDeveloperPayload(purchase)) {
                //해당 아이템을 가지고 있는 경우.
                //아이템에대한 처리를 한다.
                //alreadyBuyedItem();
                deferred.resolve(true);

            } else {
                deferred.resolve(false);
            }
        };

        iaphelper.startSetup(result -> {
            if (!result.isSuccess()) {
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

        return promise.done(isPlus -> DataContainer.getInstance().isPlus = (boolean) isPlus);
    }

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return payload.equals(DataContainer.getInstance().getUid());
    }

    public void startProgressDialog(){
        UiUtil.getInstance().startProgressDialog(BaseActivity.this);
    }

    public void stopProgressDialog(){
        UiUtil.getInstance().stopProgressDialog();
    }

    public void deleteMyIdentifier(){
        @SuppressLint("HardwareIds") String deviceIdentifier = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseDatabase.getInstance().getReference("identifier").child(deviceIdentifier).removeValue();
    }
}
