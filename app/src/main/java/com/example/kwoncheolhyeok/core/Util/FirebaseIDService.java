package com.example.kwoncheolhyeok.core.Util;

import android.util.Log;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Administrator on 2018-02-21.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {

    private String refreshedToken;

    @Override
    public void onTokenRefresh() {

        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer();
        Log.d("token",refreshedToken);
    }
    // [END refresh_token]

    public void setUserToken(User user) {
        user.setToken(refreshedToken);
    }

    private void sendRegistrationToServer() {
     /*   try {
            DataContainer.getInstance().getUsersRef().child(DataContainer.getInstance().getUid()).child("token").setValue(refreshedToken);
            Log.d("das","성공");
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }
}
