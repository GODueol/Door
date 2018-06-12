package com.teamcore.android.core.Util;

import android.content.Context;
import android.support.annotation.NonNull;

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
import com.teamcore.android.core.Entity.CoreCloud;
import com.teamcore.android.core.Entity.CoreListItem;
import com.teamcore.android.core.Entity.CorePost;
import com.teamcore.android.core.Entity.User;
import com.teamcore.android.core.Exception.ChildSizeMaxException;
import com.teamcore.android.core.Exception.NotSetAutoTimeException;
import com.teamcore.android.core.MessageActivity.util.MessageVO;
import com.teamcore.android.core.MessageActivity.util.RoomVO;
import com.teamcore.android.core.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

import static com.teamcore.android.core.Util.RemoteConfig.CorePossibleOldFriendCount;

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


    public Task<Void> follow(Context context, final User oUser, String oUuid, boolean isFollowed) throws ChildSizeMaxException, NotSetAutoTimeException {
        String myUuid = DataContainer.getInstance().getUid();
        SharedPreferencesUtil SPUtil = new SharedPreferencesUtil(context.getApplicationContext());
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

            if(oUser != null) oUser.getFollowerUsers().remove(myUuid);
            childUpdates.put("/" + oUuid + "/followerUsers/" + myUuid, null);

            // 상대방이 팔로우 되어있으면 친구 삭제
            if (mUser.getFriendUsers().containsKey(oUuid)) {
                mUser.getFriendUsers().remove(oUuid);
                childUpdates.put("/" + myUuid + "/friendUsers/" + oUuid, null);

                if(oUser != null) oUser.getFriendUsers().remove(myUuid);
                childUpdates.put("/" + oUuid + "/friendUsers/" + myUuid, null);
            }

        } else {
            // 친구 추가
            if (mUser.getFollowingUsers().containsKey(oUuid)) return null;
            long now = UiUtil.getInstance().getCurrentTime(context);

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
                SPUtil.increaseBadgeCount(context.getString(R.string.badgeFollowing));
                // 상대방에게
                FirebaseSendPushMsg.sendPostToFCM("follow", oUuid, mUser.getId(), context.getString(R.string.alertFolow));
                FirebaseSendPushMsg.sendPostToFCM("friend", oUuid, mUser.getId(), context.getString(R.string.alertFriend));
                // 나한테
                FirebaseSendPushMsg.sendPostToFCM("friend", myUuid, oUser.getId(), context.getString(R.string.alertFriend));
            } else {
                // 상대방에게
                SPUtil.increaseBadgeCount(context.getString(R.string.badgeFollowing));
                FirebaseSendPushMsg.sendPostToFCM("follow", oUuid, mUser.getId(), context.getString(R.string.alertFolow));
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
        childUpdate.put("/" + oUuid + "/followerUsers/" + mUuid, null);
        childUpdate.put("/" + oUuid + "/followingUsers/" + mUuid, null);
        childUpdate.put("/" + oUuid + "/friendUsers/" + mUuid, null);
        childUpdate.put("/" + oUuid + "/viewedMeUsers/" + mUuid, null);

        // 채팅 관계 모두 삭제(DB)
        FirebaseDatabase.getInstance().getReference("chatRoomList").child(mUuid).child(oUuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    final String roomId = dataSnapshot.getValue(RoomVO.class).getChatRoomid();
                    // 채팅방 이미지 젼체 삭제
                    FirebaseDatabase.getInstance().getReference("chat").child(roomId).orderByChild("isImage").equalTo(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {//마찬가지로 중복 유무 확인
                                MessageVO message = ds.getValue(MessageVO.class);
                                FirebaseStorage.getInstance().getReferenceFromUrl(message.getImage()).delete();
                            }
                            FirebaseDatabase.getInstance().getReference("chat").child(roomId).removeValue();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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
        // Code to be executed when when the interstitial ad is closed.


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

    public void deletePostExecution(final CoreListItem coreListItem, DatabaseReference postsRef, final String cUuid, Runnable runnable) {
        postsRef.child(cUuid).child(coreListItem.getPostKey())
                .removeValue().addOnSuccessListener(aVoid -> {

            final ArrayList<Task> deleteTasks = new ArrayList<>();

            // coreCloud
            if (coreListItem.getCorePost().isCloud()) {
                DataContainer.getInstance().getCoreCloudRef().child(coreListItem.getPostKey()).removeValue();
            }

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
                task.addOnCompleteListener(mTask -> {
                    for (Task t : deleteTasks) {
                        if (!t.isComplete()) {
                            runnable.run();
                        }
//                                                UiUtil.getInstance().stopProgressDialog();
                    }
                });
            }

        });
    }

    public void syncUser(final SyncUserListener syncUserListener) {
        final DataContainer dc = DataContainer.getInstance();
        dc.getMyUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dc.setUser(dataSnapshot.getValue(User.class));
                syncUserListener.onSuccessSync(dc.getUser());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public Task putCoreCloud(String cUuid, CoreListItem coreListItem, final Context context, String deletePostKey, String deleteCUuid) throws NotSetAutoTimeException {

        Map<String, Object> childUpdates = new HashMap<>();

        // delete coreCloud
        if (deletePostKey != null) {  // 지워야할 포스트 지우기
            childUpdates.put("coreCloud/" + deletePostKey, null);
            childUpdates.put("posts/" + deleteCUuid + "/" + deletePostKey + "/isCloud", false);
        }

        // add coreCloud
        childUpdates.put("coreCloud/" + coreListItem.getPostKey(), new CoreCloud(cUuid, UiUtil.getInstance().getCurrentTime(context), UiUtil.getInstance().getCurrentTime(context)));
        childUpdates.put("posts/" + cUuid + "/" + coreListItem.getPostKey() + "/isCloud", true);

        return FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }

    public interface SyncUserListener {
        void onSuccessSync(User user);
    }

    public void queryBlockWithMe(final String uuid, final BlockListener blockListener) {
        syncUser(mUser -> blockListener.isBlockCallback(mUser.getBlockMeUsers().containsKey(uuid) || mUser.getBlockUsers().containsKey(uuid)));
    }

    public interface BlockListener {
        void isBlockCallback(boolean isBlockWithMe);

    }

    public DatabaseReference getPreventsUser(String uuid) {
        return FirebaseDatabase.getInstance().getReference().child("prevents/user").child(uuid);
    }

    public DatabaseReference getPreventsPost(String uuid) {
        return FirebaseDatabase.getInstance().getReference().child("prevents/post").child(uuid);
    }

    /* 가장 오래된 친구 CorePossibleOldFriendCount 명인지 확인 */
    public boolean isOldFriends(String uuid) {
        return Observable.fromIterable(DataContainer.getInstance().getUser().getFriendUsers().entrySet())
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .take(CorePossibleOldFriendCount)
                .toMap(Map.Entry::getKey, Map.Entry::getValue).blockingGet()
                .keySet().contains(uuid);
    }


}