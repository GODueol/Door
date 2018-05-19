package com.teamcore.android.core.Util;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.teamcore.android.core.Entity.AlarmSummary;
import com.teamcore.android.core.Entity.CorePost;
import com.teamcore.android.core.Exception.NotSetAutoTimeException;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Alarm");
    private final String Uuid = DataContainer.getInstance().getUid();

    public static AlarmUtil getInstance() {
        return ourInstance;
    }

    private AlarmUtil() {
    }

    public void sendAlarm(Context context, final String type, final String NickName, final CorePost corePost, final String PostKey, final String oUuid, String cUuid) {
        Long time = getRealTime(context);
        final Long finalTime = time;
        final AlarmSummary alarmSummary;

        if(type.equals("Like")){
            String LikeNumber = Integer.toString(corePost.getLikeUsers().size() + 1) + "명";
            alarmSummary = new AlarmSummary(cUuid, corePost.getUuid(), LikeNumber, corePost.getText(), type, time);
        }else{
            alarmSummary = new AlarmSummary(cUuid, corePost.getUuid(), NickName, corePost.getText(), type, time);
        }

        mDatabase.child(oUuid).child(PostKey + "_" + type).child("userLog").child(Uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // 존재하지않으면
                if (!dataSnapshot.exists()) {
                    final Map<String, Object> childUpdate = new HashMap<>();

                    childUpdate.put(oUuid + "/" + PostKey + "_" + type + "/userLog/" + Uuid, finalTime);
                    childUpdate.put(oUuid + "/" + PostKey + "_" + type + "/alarmSummary", alarmSummary);
                    mDatabase.updateChildren(childUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            switch (type) {
                                case "Post":
                                    FirebaseSendPushMsg.sendPostToFCM("Post", oUuid, "UnKnown", "누군가 내 코어에 질문을 남겼어요!");
                                    break;
                                case "Answer":
                                    FirebaseSendPushMsg.sendPostToFCM("Answer", oUuid, NickName, "당신이 작성한 질문글에 답이 왔네요!");
                                    break;
                                case "Like":
                                    FirebaseSendPushMsg.sendPostToFCM("Like", oUuid, NickName, "누군가 내가 올린 포스트를 좋아하네요!");
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


    public Long getRealTime(Context context) {
        Long time = null;
        try {
            time = UiUtil.getInstance().getCurrentTime(context);
        } catch (NotSetAutoTimeException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            ActivityCompat.finishAffinity((Activity) context);
        }

        return time;
    }
}


