package com.example.kwoncheolhyeok.core.Util;

import com.example.kwoncheolhyeok.core.Entity.AlarmSummary;
import com.example.kwoncheolhyeok.core.Entity.CorePost;
import com.example.kwoncheolhyeok.core.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

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

    public void sendAlarm(final String type, final String NickName, final CorePost corePost, final String PostKey, final String oUuid){
        //TODO : 바꿔야함
        Long time = System.currentTimeMillis();

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Alarm");
        final AlarmSummary alarmSummary = new AlarmSummary(NickName,corePost.getText(),type,time);
        FirebaseDatabase.getInstance().getReference("Alarm").child(corePost.getUuid()+"_"+PostKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // 존재하지않으면
                if(!dataSnapshot.exists()){
                    String Uuid = DataContainer.getInstance().getUid();
                    final Map<String, Object> childUpdate = new HashMap<>();

                    childUpdate.put("/" + corePost.getUuid()+"_"+PostKey , Uuid);
                    childUpdate.put("/" + corePost.getUuid()+"_"+PostKey + "SummaryInfo", alarmSummary);
                    mDatabase.updateChildren(childUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            switch (type){
                                case "Post":
                                    FirebaseSendPushMsg.sendPostToFCM("Post",oUuid,"UnKnown","누군가 내 코어에 질문을 남겼어요!");
                                    break;
                                case "Like":
                                    FirebaseSendPushMsg.sendPostToFCM("Like",oUuid,NickName,"누군가 내가 올린 포스트를 좋아하네요!");
                                    break;
                                case "Answer":
                                    FirebaseSendPushMsg.sendPostToFCM("Answer",oUuid,NickName,"당신이 작성한 질문글에 답이 왔네요!");
                                    break;
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}