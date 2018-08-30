package com.teamdoor.android.door.ChattingRoomList;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.teamdoor.android.door.Entity.MessageVO;
import com.teamdoor.android.door.Entity.RoomVO;
import com.teamdoor.android.door.Entity.User;
import com.teamdoor.android.door.Util.FireBaseUtil;
import com.teamdoor.android.door.Util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;

import static com.teamdoor.android.door.ChattingRoomList.RxFirebaseModel.CHILD_ADD;
import static com.teamdoor.android.door.ChattingRoomList.RxFirebaseModel.CHILD_CHANGE;
import static com.teamdoor.android.door.ChattingRoomList.RxFirebaseModel.CHILD_REMOVE;

public class ChattingRoomListPresenter implements ChattingRoomListContract.Presenter, SharedPreferences.OnSharedPreferenceChangeListener {


    private ChattingRoomListContract.View mMeesageView;
    private List<RoomVO> RoomItemList;
    private List<String> UserUuidList;
    private DatabaseReference databaseReference;
    private DatabaseReference chatRoomListRef;
    private String userId;
    private SharedPreferencesUtil SPUtil;
    private CompositeDisposable compositeDisposable;
    private int BadgeCount = 0;
    private RxFirebaseModel rxFirebaseModel;

    ChattingRoomListPresenter(ChattingRoomListContract.View MessageView, SharedPreferencesUtil SPUtil) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        chatRoomListRef = databaseReference.child("chatRoomList");

        this.SPUtil = SPUtil;
        SPUtil.getChatListPreferences().registerOnSharedPreferenceChangeListener(this);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();

        UserUuidList = new ArrayList<>();
        mMeesageView = MessageView;
        mMeesageView.setPresenter(this);

        rxFirebaseModel = new RxFirebaseModel();
        compositeDisposable = new CompositeDisposable();
    }

    private Query addListItem_ReturnQuery(RoomVO roomInfo) {
        UserUuidList.add(roomInfo.getTargetUuid());
        RoomItemList.add(roomInfo);
        return databaseReference.child("users").child(roomInfo.getTargetUuid());
    }

    @Override
    public void start() {
    }

    @Override
    public void setListItem(List<RoomVO> listrowItem) {
        this.RoomItemList = listrowItem;
    }

    @SuppressLint("CheckResult")
    @Override
    public void enterChatRoom(RoomVO item) {

        FireBaseUtil.getInstance().queryBlockWithMe(item.getTargetUuid(), isBlockWithMe -> {
            //블럭이 아니면
            if (!isBlockWithMe) {
                Query query = databaseReference.child("users").child(item.getTargetUuid());
                rxFirebaseModel.getFirebaseForSingleValue(query, User.class, RxFirebaseModel.CHILD_SINGLE)
                        .subscribe(user -> {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("user", user);
                            bundle.putString("userUuid", item.getTargetUuid());
                            SPUtil.removeChatRoomBadge(item.getChatRoomid());
                            mMeesageView.startChattingActivity(bundle);
                        });
            } else {// 블럭이면
                // 채팅 리스트 아이템 삭
                chatRoomListRef.child(userId).child(item.getTargetUuid()).removeValue();
                // 채팅방 이미지 전체 삭제 및 채팅 로그 삭제
                final String roomId = item.getChatRoomid();
                SPUtil.removeChatRoomBadge(roomId);

                Query query = databaseReference.child("chat").child(roomId).orderByChild("isImage").equalTo(1);
                rxFirebaseModel.getFirebaseForSingleValue(query, MessageVO.class, RxFirebaseModel.CHILD_MULTI)
                        .subscribe(message -> {
                            FirebaseStorage.getInstance().getReferenceFromUrl(message.getImage()).delete();
                            FirebaseDatabase.getInstance().getReference("chat").child(roomId).removeValue();
                        });

                mMeesageView.refreshChattingRoomListView();
            }
        });
    }

    public void syncronizeBadgeCount(RoomVO roomInfo) {
        try {
            // 뱃지 수 동기화
            int c = SPUtil.getChatRoomBadge(roomInfo.getChatRoomid());
            roomInfo.setBadgeCount(c);
            BadgeCount += c;
            SPUtil.setBadgeCount(mMeesageView.getResourceBadge(), BadgeCount);
        } catch (Exception e) {
            roomInfo.setBadgeCount(0);
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void setRoomItemList() {
        PublishProcessor<RxFirebaseModel.FirebaseData> publish = PublishProcessor.create();
        Query query = chatRoomListRef.child(userId).orderByChild("lastChatTime");

        Disposable event1 = publish.filter(firebaseData -> firebaseData.getType() == CHILD_ADD)
                .map(firebaseData -> (RoomVO) firebaseData.getVaule())
                .subscribe(data -> {
                    syncronizeBadgeCount(data);
                    Observable<User> observable = Observable.just(data)
                            .filter(RoomVO::hasTargetUuid_LastChat)
                            .map(this::addListItem_ReturnQuery)
                            .concatMap(inquery -> rxFirebaseModel.getFirebaseForSingleValue(inquery))
                            .filter(DataSnapshot::exists)
                            .filter(userDataSnapshot -> !userDataSnapshot.getKey().equals(mMeesageView.getResourceTeamCore()))
                            .map(userDataSnapshot -> userDataSnapshot.getValue(User.class));
                    observable.subscribe(userinfo -> realTimeChattingRoomChange(userinfo, data));
                });

        Disposable event2 = publish.filter(firebaseData -> firebaseData.getType() == CHILD_CHANGE)
                .map(firebaseData -> (RoomVO) firebaseData.getVaule())
                .subscribe(roomInfo -> {
                    roomInfo.setBadgeCount(SPUtil.getChatRoomBadge(roomInfo.getChatRoomid()));

                    Observable.just(roomInfo)
                            .filter(data1 -> data1.getLastChat() != null)
                            .map(data2 -> databaseReference.child("users").child(data2.getTargetUuid()))
                            .concatMap(inquery -> rxFirebaseModel.getFirebaseForSingleValue(inquery))
                            .filter(DataSnapshot::exists)
                            .map(dataSnapshot -> dataSnapshot.getValue(User.class))
                            .subscribe(userDataSnapshot -> {
                                try {
                                    roomInfo.setBadgeCount(SPUtil.getChatRoomBadge(roomInfo.getChatRoomid()));
                                    realTimeChattingRoomChange(userDataSnapshot, roomInfo, true);
                                } catch (Exception e) {
                                    UserUuidList.add(roomInfo.getTargetUuid());
                                    RoomItemList.add(roomInfo);
                                    realTimeChattingRoomChange(userDataSnapshot, roomInfo);
                                }
                            });
                    Observable.just(roomInfo)
                            .filter(data1 -> data1.getLastChat() == null)
                            .map(roomInfo1 -> UserUuidList.indexOf(roomInfo1.getTargetUuid()))
                            .filter(key -> key >= 0)
                            .subscribe(key -> {
                                RoomItemList.remove((int) key);
                                UserUuidList.remove((int) key);
                                mMeesageView.refreshChattingRoomListView();
                            });

                });

        Disposable event3 = publish.filter(firebaseData -> firebaseData.getType() == CHILD_REMOVE)
                .map(firebaseData -> (RoomVO) firebaseData.getVaule())
                .subscribe(roomInfo -> {
                    Observable.just(UserUuidList.indexOf(roomInfo.getTargetUuid()))
                            .filter(index -> index > 0)
                            .subscribe(key -> {
                                RoomItemList.remove((int) key);
                                UserUuidList.remove((int) key);
                                mMeesageView.refreshChattingRoomListView();
                            });
                });

        Disposable event = rxFirebaseModel.getFirebaseChildeEvent(query, RoomVO.class).subscribe(publish::onNext);
        compositeDisposable.addAll(event, event1, event2, event3);
    }

    @Override
    public void realTimeChattingRoomChange(User target, RoomVO roomList, boolean changeFlag) {
        int key = UserUuidList.indexOf(roomList.getTargetUuid());
        Long startTime = RoomItemList.get(key).getLastChatTime();
        Long endTime = roomList.getLastChatTime();
        RoomItemList.remove(key);

        if (startTime.equals(endTime)) {
            // 새로고침
            RoomItemList.add(key, roomList);
            realTimeChattingRoomChange(target, roomList);
        } else {      // 맨위로 올림
            UserUuidList.remove(key);
            UserUuidList.add(roomList.getTargetUuid());
            RoomItemList.add(roomList);
            realTimeChattingRoomChange(target, roomList);
        }
    }

    @Override
    public void removeChattingRoomList(String target) {
        chatRoomListRef.child(userId).child(target).child("lastChat").removeValue();
    }

    @Override
    public void realTimeChattingRoomChange(User target, RoomVO roomList) {
        try {
            if (target.getId() != null && !target.getId().equals(roomList.getTargetNickName())) {
                roomList.setTargetNickName(target.getId());
                chatRoomListRef.child(userId).child(roomList.getTargetUuid()).child("targetNickName").setValue(target.getId());
            }

            if (target.getTotalProfile() != null && !target.getTotalProfile().equals(roomList.getTargetProfile())) {
                roomList.setTargetProfile(target.getTotalProfile());
                chatRoomListRef.child(userId).child(roomList.getTargetUuid()).child("targetProfile").setValue(target.getTotalProfile());
            }

            if (target.getPicUrls() != null && target.getPicUrls().getThumbNail_picUrl1() != null && !target.getPicUrls().getThumbNail_picUrl1().equals(roomList.getTargetUrl())) {
                roomList.setTargetUrl(target.getPicUrls().getThumbNail_picUrl1());
                chatRoomListRef.child(userId).child(roomList.getTargetUuid()).child("targetUrl").setValue(target.getPicUrls().getThumbNail_picUrl1());
            }
        } catch (Exception ignore) {
        }

        mMeesageView.refreshChattingRoomListView();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        int num = sharedPreferences.getInt(key, 0);
        //리스트 아이템 뱃지 동기화
        for (RoomVO r : RoomItemList) {
            if (r.getChatRoomid().equals(key)) {
                int i = RoomItemList.indexOf(r);
                RoomItemList.get(i).setBadgeCount(num);
                mMeesageView.refreshChattingRoomListView();
                break;
            }
        }
    }

    @Override
    public void removeDisposable() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}
