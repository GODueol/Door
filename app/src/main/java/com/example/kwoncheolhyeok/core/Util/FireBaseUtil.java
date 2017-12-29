package com.example.kwoncheolhyeok.core.Util;

import android.support.annotation.NonNull;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

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


    public Task<Void> follow(final User oUser, String oUuid , boolean isFollowed) {
        String myUuid = DataContainer.getInstance().getUid();
        final User mUser = DataContainer.getInstance().getUser();
        //final User oUser = item.getUser();
        DatabaseReference mDatabase = DataContainer.getInstance().getUsersRef();
        Map<String, Object> childUpdates = new HashMap<>();

        if(isFollowed){
            // 친구 삭제
            if(!mUser.getFollowingUsers().containsKey(oUuid)) return null;

            mUser.getFollowingUsers().remove(oUuid);
            childUpdates.put("/" + myUuid + "/followingUsers/" + oUuid, null);

            oUser.getFollowerUsers().remove(myUuid);
            childUpdates.put("/" + oUuid + "/followerUsers/" + myUuid, null);

            // 상대방이 팔로우 되어있으면 친구 삭제
            if(mUser.getFriendUsers().containsKey(oUuid)) {
                mUser.getFriendUsers().remove(oUuid);
                childUpdates.put("/" + myUuid + "/friendUsers/" + oUuid, null);

                oUser.getFriendUsers().remove(myUuid);
                childUpdates.put("/" + oUuid + "/friendUsers/" + myUuid, null);
            }

        } else {
            // 친구 추가
            if(mUser.getFollowingUsers().containsKey(oUuid)) return null;
            long now = System.currentTimeMillis();

            mUser.getFollowingUsers().put(oUuid, now);
            childUpdates.put("/" + myUuid + "/followingUsers/" + oUuid, now);

            oUser.getFollowerUsers().put(myUuid, now);
            childUpdates.put("/" + oUuid + "/followerUsers/" + myUuid, now);

            // 상대방이 팔로우 되어있으면 친구에 추가
            if(oUser.getFollowingUsers().containsKey(myUuid)){
                mUser.getFriendUsers().put(oUuid, now);
                childUpdates.put("/" + myUuid + "/friendUsers/" + oUuid, now);

                oUser.getFriendUsers().put(myUuid, now);
                childUpdates.put("/" + oUuid + "/friendUsers/" + myUuid, now);

            }
        }
        // 데이터 한꺼번에 업데이트
        return mDatabase.updateChildren(childUpdates);
    }

    public Task<Void> block(String oUuid){

        String mUuid = DataContainer.getInstance().getUid();
        User mUser = DataContainer.getInstance().getUser();
        long now = System.currentTimeMillis();

        DatabaseReference mDatabase = DataContainer.getInstance().getUsersRef();
        Map<String, Object> childUpdate = new HashMap<>();

        // block 리스트 삭제
        // 로컬 상에서 Block 리스트 추가
        mUser.getBlockUsers().put(oUuid, now);
        childUpdate.put("/" + mUuid + "/blockUsers/" + oUuid, now);  // DB 상에서 Block 리스트 추가

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

        return mDatabase.updateChildren(childUpdate);

    }
}
