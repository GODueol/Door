package com.example.kwoncheolhyeok.core.Util;

import android.annotation.SuppressLint;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

/**
 * Created by gimbyeongjin on 2017. 8. 14..
 */

public class DataContainer {
    public static final String[] bodyTypes =  {"Underweight", "Skinny", "Standard", "Muscular", "Overweight"};
    @SuppressLint("SimpleDateFormat") public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yy.MM.dd HH:mm");

    private static final DataContainer ourInstance = new DataContainer();

    public static DataContainer getInstance() {
        return ourInstance;
    }

    private DataContainer() {
    }

    User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public DatabaseReference getUserRef(String uuid){
        return getUsersRef().child(uuid);
    }

    public DatabaseReference getMyUserRef(){
        return getUserRef(getUid());
    }

    public DatabaseReference getUsersRef(){
        return FirebaseDatabase.getInstance().getReference("users");
    }

}
