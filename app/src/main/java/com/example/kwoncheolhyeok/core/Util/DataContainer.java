package com.example.kwoncheolhyeok.core.Util;

import android.location.Location;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by gimbyeongjin on 2017. 8. 14..
 */

public class DataContainer {
    public static final String[] bodyTypes =  {"Underweight", "Skinny", "Standard", "Muscular", "Overweight"};

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
        return FirebaseDatabase.getInstance().getReference("users").child(uuid);
    }

    public static DataContainer getOurInstance() {
        return ourInstance;
    }
}
