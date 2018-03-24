package com.example.kwoncheolhyeok.core.Util;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.Entity.Alarm;
import com.example.kwoncheolhyeok.core.Entity.CorePost;
import com.example.kwoncheolhyeok.core.Exception.NotSetAutoTimeException;
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

    public static AlarmUtil getInstance() {
        return ourInstance;
    }

    private AlarmUtil() {
    }

    public void sendAlarm(Context context, final String type, final String NickName, final CorePost corePost, final String PostKey, final String oUuid) {
        Long time = null;
        try {
            time = UiUtil.getInstance().getCurrentTime(context);
        } catch (NotSetAutoTimeException e) {
            e.printStackTrace();
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            ActivityCompat.finishAffinity((Activity) context);
        }

        final String Uuid = DataContainer.getInstance().getUid();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Alarm").child(oUuid);
        final Alarm alarm = new Alarm(NickName, corePost.getText(), type, time);
        final Long finalTime = time;
        mDatabase.child(corePost.getUuid() + "_" + PostKey + "_" + type).child("userLog").child(Uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // 존재하지않으면
                if (!dataSnapshot.exists()) {
                    final Map<String, Object> childUpdate = new HashMap<>();

                    childUpdate.put("/" + corePost.getUuid() + "_" + PostKey + "_" + type + "/userLog/" + Uuid, finalTime);
                    childUpdate.put("/" + corePost.getUuid() + "_" + PostKey + "_" + type + "/SummaryInfo", alarm);
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


    public void sendLikeAlarm(Context context, final String type, final String NickName, final CorePost corePost, final String PostKey, final String oUuid) {
        Long time = null;
        try {
            time = UiUtil.getInstance().getCurrentTime(context);
        } catch (NotSetAutoTimeException e) {
            e.printStackTrace();
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            ActivityCompat.finishAffinity((Activity) context);
        }

        final String Uuid = DataContainer.getInstance().getUid();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Alarm").child(oUuid);
        final Alarm alarm = new Alarm(Integer.toString(corePost.getLikeUsers().size())+"명 ", corePost.getText(), type, time);
        final Long finalTime = time;
        mDatabase.child(corePost.getUuid() + "_" + PostKey + "_" + type).child("userLog").child(Uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // 존재하지않으면
                if (!dataSnapshot.exists()) {
                    final Map<String, Object> childUpdate = new HashMap<>();

                    childUpdate.put("/" + corePost.getUuid() + "_" + PostKey + "_" + type + "/userLog/" + Uuid, finalTime);
                    childUpdate.put("/" + corePost.getUuid() + "_" + PostKey + "_" + type + "/SummaryInfo", alarm);
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