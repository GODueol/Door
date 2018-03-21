package com.example.kwoncheolhyeok.core.Util;

import com.example.kwoncheolhyeok.core.Entity.AlarmSummary;
import com.example.kwoncheolhyeok.core.Entity.CorePost;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Administrator on 2018-03-21.
 */

public class AlarmUtil {

    private static final AlarmUtil ourInstance = new AlarmUtil();
    public static AlarmUtil getInstance() {
        return ourInstance;
    }
    private AlarmUtil(){
    }

    public Task<Void> sendAlarm(String type,String NickName, CorePost corePost){
        Long time = System.currentTimeMillis();

        AlarmSummary alarmSummary = new AlarmSummary(NickName,corePost.getText(),type,time);
        return FirebaseDatabase.getInstance().getReference("Alarm").child(corePost.getUuid()+"_"+corePost.getUuid()).child("SummaryInfo").setValue(alarmSummary);
    }
}
