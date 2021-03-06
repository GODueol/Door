package com.teamdoor.android.door.Util;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teamdoor.android.door.Entity.User;
import com.teamdoor.android.door.Exception.NotSetAutoTimeException;

public class DataContainer {
    public final String PREFERENCE = "com.teamdoor.android.door.samplesharepreference";

    public static final int ChildrenMax = 1000;
    public static final int ViewedMeMax = 45;
    public static final long SecToDay = TIME_MAXIMUM.SEC*TIME_MAXIMUM.MIN*TIME_MAXIMUM.HOUR*1000;
    public static final int RadiusMax = 300;
    public boolean isPlus = false;

    private static class TIME_MAXIMUM {
        static final int SEC = 60;
        static final int MIN = 60;
        static final int HOUR = 24;
        static final int DAY = 30;
        static final int MONTH = 12;
    }

    public static final String[] bodyTypes = {"Slim", "Light", "Normal", "Muscular", "Heavy", "Fat"};

    private static final DataContainer ourInstance = new DataContainer();

    public static DataContainer getInstance() {
        return ourInstance;
    }

    private DataContainer() {
    }

    static private User user = null;

    public User getUser() {
        return user;
    }

    public User getUser(Runnable userNullAction) {
        if(user == null){
            userNullAction.run();
        }
        return user;
    }

    public void setUser(User user) {
        DataContainer.user = user;
    }

    public String getUid() {
        if(FirebaseAuth.getInstance().getCurrentUser() == null) return null;
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public String getUid(Context context) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            UiUtil.getInstance().restartApp(context);
        return getUid();
    }

    public DatabaseReference getUserRef(String uuid) {
        return getUsersRef().child(uuid);
    }

    public DatabaseReference getMyUserRef() {
        return getUserRef(getUid());
    }

    public DatabaseReference getUsersRef() {
        return FirebaseDatabase.getInstance().getReference("users");
    }

    public DatabaseReference getCoreCloudRef(){
        return FirebaseDatabase.getInstance().getReference("coreCloud");
    }

    public String convertBeforeFormat(long longDate, Context context) throws NotSetAutoTimeException {
        long curTime = UiUtil.getInstance().getCurrentTime(context);
        long diffTime = (curTime - longDate) / 1000;

        String msg;

        if (diffTime < TIME_MAXIMUM.SEC) {
            // sec
            if(diffTime<=10) {
                msg = "방금 전";
            }else {
                msg = diffTime + "초 전";
            }
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
            // min
            System.out.println(diffTime);

            msg = diffTime + "분 전";
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
            // hour
            msg = (diffTime) + "시간 전";
        } else {
            // day
            msg = (diffTime / TIME_MAXIMUM.HOUR) + "일 전";
        }

        return msg;
    }

    public boolean isBlockWithMe(String oUuid){
        return user.getBlockUsers().containsKey(oUuid) || user.getBlockMeUsers().containsKey(oUuid);
    }

}
