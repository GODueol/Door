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


    public Task<Void> follow(final User oUser, String uuid , boolean isFollow, String myUuid) {
        final User mUser = DataContainer.getInstance().getUser();
        //final User oUser = item.getUser();
        DatabaseReference mDatabase = DataContainer.getInstance().getUsersRef();
        Map<String, Object> childUpdates = new HashMap<>();

        if(isFollow){
            // 친구 삭제
            if(!mUser.getFollowingUsers().containsKey(uuid)) return null;
            mUser.getFollowingUsers().remove(uuid);
            childUpdates.put("/" + myUuid + "/followingUsers", mUser.getFollowingUsers());

            oUser.getFollowerUsers().remove(myUuid);
            childUpdates.put("/" + uuid + "/followerUsers", oUser.getFollowerUsers());

            // 상대방이 팔로우 되어있으면 친구 삭제
            if(mUser.getFriendUsers().containsKey(uuid)) {
                mUser.getFriendUsers().remove(uuid);
                childUpdates.put("/" + myUuid + "/friendUsers", mUser.getFollowingUsers());

                oUser.getFriendUsers().remove(myUuid);
                childUpdates.put("/" + uuid + "/friendUsers", oUser.getFollowerUsers());
            }

        } else {
            // 친구 추가
            if(mUser.getFollowingUsers().containsKey(uuid)) return null;
            long now = System.currentTimeMillis();
            mUser.getFollowingUsers().put(uuid, now);
            childUpdates.put("/" + myUuid + "/followingUsers", mUser.getFollowingUsers());

            oUser.getFollowerUsers().put(myUuid, now);
            childUpdates.put("/" + uuid + "/followerUsers", oUser.getFollowerUsers());

            // 상대방이 팔로우 되어있으면 친구에 추가
            if(oUser.getFollowingUsers().containsKey(myUuid)){
                mUser.getFriendUsers().put(uuid, now);
                childUpdates.put("/" + myUuid + "/friendUsers", mUser.getFollowingUsers());

                oUser.getFriendUsers().put(myUuid, now);
                childUpdates.put("/" + uuid + "/friendUsers", oUser.getFollowerUsers());

            }
        }
        // 데이터 한꺼번에 업데이트
        return mDatabase.updateChildren(childUpdates);
    }

}
