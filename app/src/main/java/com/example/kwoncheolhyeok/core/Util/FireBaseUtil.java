package com.example.kwoncheolhyeok.core.Util;

import android.support.annotation.NonNull;

import com.example.kwoncheolhyeok.core.Entity.CoreListItem;
import com.example.kwoncheolhyeok.core.Entity.CorePost;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.Exception.ChildSizeMaxException;
import com.example.kwoncheolhyeok.core.MessageActivity.util.RoomVO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FireBaseUtil {
    public static final String currentLocationPath = "location/users";
    private static final FireBaseUtil ourInstance = new FireBaseUtil();

    public static FireBaseUtil getInstance() {
        return ourInstance;
    }

    private FireBaseUtil() {
    }

    @NonNull
    private String getStorageProfilePicPath(String uuid) {
        return "profile/pic/" + uuid + "/";
    }

    @NonNull
    public String getStorageProfilePicPath() {
        return getStorageProfilePicPath(DataContainer.getInstance().getUid());
    }


    public Task<Void> follow(final User oUser, String oUuid, boolean isFollowed) throws ChildSizeMaxException {
        String myUuid = DataContainer.getInstance().getUid();
        final User mUser = DataContainer.getInstance().getUser();
        if (mUser.getFollowingUsers().size() >= DataContainer.ChildrenMax) {
            throw new ChildSizeMaxException("Follow가 " + DataContainer.ChildrenMax + "명 이상이므로 Follow 불가능합니다");
        }
        //final User oUser = item.getSummaryUser();
        DatabaseReference mDatabase = DataContainer.getInstance().getUsersRef();
        Map<String, Object> childUpdates = new HashMap<>();

        if (isFollowed) {
            // 친구 삭제
            if (!mUser.getFollowingUsers().containsKey(oUuid)) return null;

            mUser.getFollowingUsers().remove(oUuid);
            childUpdates.put("/" + myUuid + "/followingUsers/" + oUuid, null);

            oUser.getFollowerUsers().remove(myUuid);
            childUpdates.put("/" + oUuid + "/followerUsers/" + myUuid, null);

            // 상대방이 팔로우 되어있으면 친구 삭제
            if (mUser.getFriendUsers().containsKey(oUuid)) {
                mUser.getFriendUsers().remove(oUuid);
                childUpdates.put("/" + myUuid + "/friendUsers/" + oUuid, null);

                oUser.getFriendUsers().remove(myUuid);
                childUpdates.put("/" + oUuid + "/friendUsers/" + myUuid, null);
            }

        } else {
            // 친구 추가
            if (mUser.getFollowingUsers().containsKey(oUuid)) return null;
            long now = System.currentTimeMillis();

            mUser.getFollowingUsers().put(oUuid, now);
            childUpdates.put("/" + myUuid + "/followingUsers/" + oUuid, now);

            oUser.getFollowerUsers().put(myUuid, now);
            childUpdates.put("/" + oUuid + "/followerUsers/" + myUuid, now);

            // 상대방이 팔로우 되어있으면 친구에 추가
            if (oUser.getFollowingUsers().containsKey(myUuid)) {
                mUser.getFriendUsers().put(oUuid, now);
                childUpdates.put("/" + myUuid + "/friendUsers/" + oUuid, now);

                oUser.getFriendUsers().put(myUuid, now);
                childUpdates.put("/" + oUuid + "/friendUsers/" + myUuid, now);

                FirebaseSendPushMsg.sendPostToFCM("friend", oUuid, mUser.getId(), "새로운 친구가 생겼어요!");
            } else {
                FirebaseSendPushMsg.sendPostToFCM("follow", oUuid, mUser.getId(), "누군가가 당신을 팔로우 했어요!");
            }
        }
        // 데이터 한꺼번에 업데이트
        return mDatabase.updateChildren(childUpdates);
    }

    public Task<Void> block(final String oUuid) throws ChildSizeMaxException {

        final String mUuid = DataContainer.getInstance().getUid();
        final User mUser = DataContainer.getInstance().getUser();

        if (mUser.getBlockUsers().size() >= DataContainer.ChildrenMax) {
            throw new ChildSizeMaxException("Block된 유저들이 " + DataContainer.ChildrenMax + "명 이상이므로 Block 불가능합니다");
        }

        long now = System.currentTimeMillis();
        DatabaseReference mDatabase = DataContainer.getInstance().getUsersRef();
        final Map<String, Object> childUpdate = new HashMap<>();

        // block 리스트 삭제
        // 로컬 상에서 Block 리스트 추가
        mUser.getBlockUsers().put(oUuid, now);
        childUpdate.put("/" + mUuid + "/blockUsers/" + oUuid, now);  // DB 상에서 본인 Block 리스트 추가
        childUpdate.put("/" + oUuid + "/blockMeUsers/" + mUuid, now);  // DB 상에서 상대 BlockMe 리스트 추가


        // 내 팔로우 관계 모두 삭제(로컬)
        mUser.getFollowerUsers().remove(oUuid);
        mUser.getFollowingUsers().remove(oUuid);
        mUser.getFriendUsers().remove(oUuid);
        mUser.getViewedMeUsers().remove(oUuid);

        // 내 팔로우 관계 모두 삭제(DB)
        childUpdate.put("/" + mUuid + "/followerUsers/" + oUuid, null);
        childUpdate.put("/" + mUuid + "/followingUsers/" + oUuid, null);
        childUpdate.put("/" + mUuid + "/friendUsers/" + oUuid, null);
        childUpdate.put("/" + mUuid + "/viewedMeUsers/" + oUuid, null);

        // 친구의 팔로우 관계 모두 삭제(DB)
        childUpdate.put("/" + oUuid + "/blockUsers/" + mUuid, null);
        childUpdate.put("/" + oUuid + "/followerUsers/" + mUuid, null);
        childUpdate.put("/" + oUuid + "/friendUsers/" + mUuid, null);
        childUpdate.put("/" + oUuid + "/viewedMeUsers/" + mUuid, null);

        // 채팅 관계 모두 삭제(DB)
        //TODO:미완성
        FirebaseDatabase.getInstance().getReference("chatRoomList").child(mUuid).child(oUuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String roomId = dataSnapshot.getValue(RoomVO.class).getChatRoomid();
                    FirebaseDatabase.getInstance().getReference("chat").child(roomId).removeValue();
                    FirebaseDatabase.getInstance().getReference("chatRoomList").child(mUuid).child(oUuid).removeValue();
                    FirebaseDatabase.getInstance().getReference("chatRoomList").child(oUuid).child(mUuid).removeValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return mDatabase.updateChildren(childUpdate);
    }

    public void syncCorePostCount(final String cUuid) {

        final DatabaseReference corePostCountRef = DataContainer.getInstance().getUserRef(cUuid);
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference()
                .child("posts").child(cUuid);
        postRef.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                int count = 0;
                if (mutableData.getValue() != null) {

                    // 갯수 제한
                    Map map = (Map) mutableData.getValue();
                    while (map.size() > DataContainer.ChildrenMax) {
                        MutableData min = null;
                        for (final MutableData mutableChild : mutableData.getChildren()) {
                            if (min == null || min.getValue(CorePost.class).getWriteDate() > mutableChild.getValue(CorePost.class).getWriteDate()) {
                                min = mutableChild;
                            }
                        }

                        StorageReference postStorageRef = FirebaseStorage.getInstance().getReference().child("posts").child(cUuid).child(min.getKey());
                        postStorageRef.child("sound").delete();
                        postStorageRef.child("picture").delete();

                        map.remove(min.getKey());
                        mutableData.setValue(map);
                    }

                    count = map.size();
                }

                final Map<String, Object> childUpdate = new HashMap<>();

                childUpdate.put("/corePostCount", count);
                childUpdate.put("/summaryUser/corePostCount", count);

                corePostCountRef.updateChildren(childUpdate);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            }
        });
    }

    public Task<Void> unblock(String oUuid) {

        DatabaseReference mDatabase = DataContainer.getInstance().getUsersRef();
        String mUuid = DataContainer.getInstance().getUid();
        final Map<String, Object> childUpdate = new HashMap<>();

        childUpdate.put("/" + oUuid + "/blockMeUsers/" + mUuid, null);
        childUpdate.put("/" + mUuid + "/blockUsers/" + oUuid, null);

        return mDatabase.updateChildren(childUpdate);
    }

    public Task allUnblock(Map<String, Long> blockUsers) {
        DatabaseReference mDatabase = DataContainer.getInstance().getUsersRef();
        String mUuid = DataContainer.getInstance().getUid();
        final Map<String, Object> childUpdate = new HashMap<>();

        for (String oUuid : blockUsers.keySet()) {
            childUpdate.put("/" + oUuid + "/blockMeUsers/" + mUuid, null);
        }

        childUpdate.put("/" + mUuid + "/blockUsers", null);

        return mDatabase.updateChildren(childUpdate);
    }

    public void deletePostExcution(final CoreListItem coreListItem, DatabaseReference postsRef, final String cUuid) {
        postsRef.child(cUuid).child(coreListItem.getPostKey())
                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                final ArrayList<Task> deleteTasks = new ArrayList<>();
                // 갯수 갱신
                FireBaseUtil.getInstance().syncCorePostCount(cUuid);

                // Storage Delete
                StorageReference postStorageRef = FirebaseStorage.getInstance().getReference().child("posts").child(cUuid).child(coreListItem.getPostKey());
                if (coreListItem.getCorePost().getSoundUrl() != null)
                    deleteTasks.add(postStorageRef.child("sound").delete());
                if (coreListItem.getCorePost().getPictureUrl() != null)
                    deleteTasks.add(postStorageRef.child("picture").delete());

//                                if(deleteTasks.isEmpty()) UiUtil.getInstance().stopProgressDialog();    // 사진이나 음성이 없으면 프로그레스바 종료

                for (Task task : deleteTasks) {
                    task.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task mTask) {
                            for (Task t : deleteTasks) {
                                if (!t.isComplete()) return;
//                                                UiUtil.getInstance().stopProgressDialog();
                            }
                        }
                    });
                }

            }
        });
    }
}