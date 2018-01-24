package com.example.kwoncheolhyeok.core.Util;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by gimbyeongjin on 2017. 8. 14..
 */

public class DataContainer {
    private static class TIME_MAXIMUM
    {
        static final int SEC = 60;
        static final int MIN = 60;
        static final int HOUR = 24;
        static final int DAY = 30;
        static final int MONTH = 12;
    }

    public static final String[] bodyTypes =  {"Underweight", "Skinny", "Standard", "Muscular", "Overweight"};

    private static final DataContainer ourInstance = new DataContainer();

    public static DataContainer getInstance() {
        return ourInstance;
    }

    private DataContainer() {
    }

    private User user = null;

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

    public String convertBeforeFormat(long longDate){
        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - longDate) / 1000;

        String msg;

        if (diffTime < TIME_MAXIMUM.SEC)
        {
            // sec
            msg = diffTime + "초전";
        }
        else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN)
        {
            // min
            System.out.println(diffTime);

            msg = diffTime + "분전";
        }
        else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR)
        {
            // hour
            msg = (diffTime ) + "시간전";
        }
        else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY)
        {
            // day
            msg = (diffTime ) + "일전";
        }
        else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH)
        {
            // day
            msg = (diffTime ) + "달전";
        }
        else
        {
            msg = (diffTime) + "년전";
        }

        return msg;
    }

}
