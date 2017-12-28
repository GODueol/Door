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


    public Task<Void> follow(final User oUser, String oUuid , boolean isFollow, String myUuid) {
        final User mUser = DataContainer.getInstance().getUser();
        //final User oUser = item.getUser();
        DatabaseReference mDatabase = DataContainer.getInstance().getUsersRef();
        Map<String, Object> childUpdates = new HashMap<>();

        if(isFollow){
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

}
