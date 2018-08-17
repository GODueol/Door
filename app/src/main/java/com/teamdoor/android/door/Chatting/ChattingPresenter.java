package com.teamdoor.android.door.Chatting;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.teamdoor.android.door.Chatting.util.CryptoImeageName;
import com.teamdoor.android.door.ChattingRoomList.RxFirebaseModel;
import com.teamdoor.android.door.Entity.ChatMessage;
import com.teamdoor.android.door.Entity.MessageVO;
import com.teamdoor.android.door.Entity.RoomVO;
import com.teamdoor.android.door.Entity.User;
import com.teamdoor.android.door.PeopleFragment.GridItem;
import com.teamdoor.android.door.Util.FireBaseUtil;
import com.teamdoor.android.door.Util.FirebaseSendPushMsg;
import com.teamdoor.android.door.Util.GalleryPick;
import com.teamdoor.android.door.Util.RemoteConfig;
import com.teamdoor.android.door.Util.SharedPreferencesUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static com.teamdoor.android.door.ChattingRoomList.RxFirebaseModel.CHILD_ADD;
import static com.teamdoor.android.door.ChattingRoomList.RxFirebaseModel.CHILD_CHANGE;
import static com.teamdoor.android.door.ChattingRoomList.RxFirebaseModel.CHILD_SINGLE;

public class ChattingPresenter implements ChattingContract.Presenter {
    private final static String chatRoomList = "chatRoomList";
    private final static String chat = "chat";
    private final static String image = "image";
    private final static String strDelete = "DELETE";
    private int messageWeight = 1;

    private CompositeDisposable compositeDisposable;
    private SharedPreferencesUtil SPUtil;
    private ChattingContract.View mChattingView;

    private DatabaseReference databaseRef;
    private FirebaseStorage storage;
    private RxFirebaseModel rxFirebaseModel;

    ChattingPresenter(ChattingContract.View view, SharedPreferencesUtil SPUtil) {
        mChattingView = view;
        view.setPresenter(this);
        this.SPUtil = SPUtil;

        databaseRef = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();

        rxFirebaseModel = new RxFirebaseModel();
        compositeDisposable = new CompositeDisposable();
    }

    private Function<DataSnapshot, MessageVO> makeMessage = dataSnapshot -> {
        MessageVO msg = dataSnapshot.getValue(MessageVO.class);
        msg.setParent(dataSnapshot.getKey());
        return msg;
    };

    @Override
    public void start() {

    }

    private String setRoom(String targetUuid, String userUuid) {
        String Room = databaseRef.child(chat).push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + chatRoomList + "/" + targetUuid + "/" + userUuid + "/" + "chatRoomid", Room);
        childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid + "/" + "chatRoomid", Room);
        databaseRef.updateChildren(childUpdates).addOnFailureListener(Void -> mChattingView.finish());
        return Room;
    }


    // 처음접속시 채팅방 설정
    @SuppressLint("CheckResult")
    public Observable<String> setchatRoom(String userUuid, String targetUuid) {

        PublishSubject<String> subject = PublishSubject.create();
        long currentTime = mChattingView.getCleanTime();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/" + chatRoomList + "/" + targetUuid + "/" + userUuid + "/" + "targetUuid", userUuid);
        childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid + "/" + "targetUuid", targetUuid);
        childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid + "/" + "lastViewTime", currentTime);
        databaseRef.updateChildren(childUpdates);
        // 채팅 룸 셋팅
        Query query = databaseRef.child(chatRoomList).child(userUuid).child(targetUuid);
        rxFirebaseModel.getFirebaseForSingleValue(query, RoomVO.class, CHILD_SINGLE)
                .observeOn(Schedulers.newThread())
                .map(data -> (data.getChatRoomid() != null) ? data.getChatRoomid() : setRoom(userUuid, targetUuid))
                .subscribe(subject::onNext);

        return subject;
    }

    @SuppressLint("CheckResult")
    @Override
    public void checkReadChat(String Room, String userUuid) {
        // 처음 모든 메세지 읽음처리
        Query query = databaseRef.child(chat).child(Room).orderByChild("check").equalTo(1);
        rxFirebaseModel.getFirebaseForSingleValue(query)
                .concatMapIterable(DataSnapshot::getChildren)
                .map(makeMessage)
                .filter(data -> !data.getWriter().equals(userUuid))
                .subscribe(data -> databaseRef.child(chat).child(Room).child(data.getParent()).child("check").setValue(0));
    }

    @SuppressLint("CheckResult")
    public void getChattingLog(String Room, String userUuid) {
        // 그후 메세지 통신

        PublishProcessor<RxFirebaseModel.FirebaseData> publish = PublishProcessor.create();

        Disposable event1 = publish.filter(firebaseData -> firebaseData.getType() == CHILD_ADD)
                .subscribe(data -> {
                    MessageVO message = (MessageVO) data.getVaule();
                    message.setParent(data.getKey());
                    initMessage(Room, userUuid, message);
                });

        Disposable event2 = publish.filter(firebaseData -> firebaseData.getType() == CHILD_CHANGE)
                .subscribe(data -> {
                    MessageVO message = (MessageVO) data.getVaule();
                    if (message.getImage() != null && message.getImage().equals(strDelete) && !message.getWriter().equals(userUuid)) {
                        int position = mChattingView.getChatKeyList().indexOf(data.getKey());
                        mChattingView.getChatList().get(position).setImage(strDelete);
                        mChattingView.refreshChatLogView();
                    }else if(message.getCheck()==0){
                        checkRefreshChatLog();
                    }
                });

        Query query = databaseRef.child(chat).child(Room).limitToLast(RemoteConfig.MessageCount);
        Disposable event3 = rxFirebaseModel.getFirebaseChildeEvent(query, MessageVO.class).subscribe(publish::onNext);
        compositeDisposable.addAll(event1, event2, event3);
    }


    // 상대방 프로필 정보 셋팅
    public GridItem setUserInfo(User targetUser, String targetUuid) {
        try {
            GridItem item = new GridItem(0, targetUuid, targetUser.getSummaryUser(), targetUser.getPicUrls().getThumbNail_picUrl1());
            // 상대방과의 거리 셋팅
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(FireBaseUtil.currentLocationPath);
            GeoFire geoFire = new GeoFire(ref);
            final Location location = mChattingView.getLocation();
            geoFire.getLocation(targetUuid, new LocationCallback() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onLocationResult(String s, GeoLocation geoLocation) {
                    Location targetLocation = new Location("");//provider name is unnecessary
                    targetLocation.setLatitude(geoLocation.latitude);//your coords of course
                    targetLocation.setLongitude(geoLocation.longitude);
                    final float distance = location.distanceTo(targetLocation);
                    mChattingView.setGridItemDistance(distance);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            return item;
        } catch (Exception e) {
            GridItem item = new GridItem(0, null, null, null);
            item.setUuid(targetUuid);
            return item;
        }
    }


    // 메세지 보내기
    public void sendMessage(String Room, String id, String userUuid, String
            targetUuid, MessageVO message) {

        String key = databaseRef.child(chat).child(Room).push().getKey();

        Long currentTime = mChattingView.getCleanTime();
        Map<String, Object> childUpdates = new HashMap<>();

        if (message.getImage() == null) {        // 메세지일 경우
            childUpdates.put("/" + chat + "/" + Room + "/" + key, message);
            childUpdates.put("/" + chatRoomList + "/" + targetUuid + "/" + userUuid + "/" + "lastChatTime", currentTime);
            childUpdates.put("/" + chatRoomList + "/" + targetUuid + "/" + userUuid + "/" + "lastChat", message.getContent());
            childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid + "/" + "lastViewTime", currentTime);
            childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid + "/" + "lastChat", message.getContent());
        } else {          // 이미지일 경우
            childUpdates.put("/" + chat + "/" + Room + "/" + key, message);
            childUpdates.put("/" + chatRoomList + "/" + targetUuid + "/" + userUuid + "/" + "lastChatTime", currentTime);
            childUpdates.put("/" + chatRoomList + "/" + targetUuid + "/" + userUuid + "/" + "lastChat", "사진");
            childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid + "/" + "lastViewTime", currentTime);
            childUpdates.put("/" + chatRoomList + "/" + userUuid + "/" + targetUuid + "/" + "lastChat", "사진");
        }
        databaseRef.updateChildren(childUpdates).addOnSuccessListener(aVoid -> {
            // 상대방에게 내정보를 담아서 메세지를 보냄
            FirebaseSendPushMsg.sendPostToFCM(chat, targetUuid, id, mChattingView.getResourceAlert(), Room);
        });
    }

    // 메세지 보내기 (이미지)
    public void sendImageMessage(String Room, String id, String userUuid, String
            targetUuid, GalleryPick galleryPick) {

        StorageReference imagesRef = storage.getReference().child(chat);
        Long currentTime = mChattingView.getCleanTime();
        final String imageName = CryptoImeageName.md5(Long.toString(currentTime) + userUuid);
        final StorageReference imageMessageRef = imagesRef.child(image + "/" + Room + "/" + imageName);

        try {
            StorageTask<UploadTask.TaskSnapshot> uploadTask = galleryPick.upload(imageMessageRef);
            uploadTask.addOnFailureListener(e -> Log.d("chatError", e.getMessage())).addOnSuccessListener(taskSnapshot ->
                    imageMessageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Long currentTime1 = mChattingView.getCleanTime();
                        MessageVO message = new MessageVO(uri.toString(), userUuid, id, imageName, currentTime1, 1, 1);
                        sendMessage(Room, id, userUuid, targetUuid, message);
                    }).addOnFailureListener(exception -> {
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            mChattingView.ToastMessage(e.getMessage());
        }
    }

    // 이미지 제거 루틴
    @Override
    public void removeImeageMessage(String Room, final String parent, final int position) {
        String url = mChattingView.getChatList().get(position).getImage();
        FirebaseStorage.getInstance().getReferenceFromUrl(url).delete().addOnSuccessListener(aVoid -> {
            FirebaseDatabase.getInstance().getReference("chat").child(Room).child(parent).child("image").setValue(strDelete);
            FirebaseDatabase.getInstance().getReference("chat").child(Room).child(parent).child("isImage").setValue(0);
            mChattingView.getChatList().get(position).setImage(strDelete);
            mChattingView.refreshChatLogView();
        }).addOnFailureListener(e -> mChattingView.ToastMessage("이미지 삭제에 실패했습니다"));
    }

    // 채팅방 삭제
    @SuppressLint("CheckResult")
    public void clearChatLog(String Room, String userUuid, String targetUuid) {

        final String roomId = Room;
        SPUtil.removeChatRoomBadge(roomId);

        // 방제거
        FirebaseDatabase.getInstance().getReference().child(chatRoomList).child(targetUuid).child(userUuid).removeValue();
        FirebaseDatabase.getInstance().getReference().child(chatRoomList).child(userUuid).child(targetUuid).removeValue();

        // 채팅방 이미지 전체 삭제 및 채팅 로그 삭제
        Query query = databaseRef.child(chat).child(roomId).orderByChild("isImage").equalTo(1);
        rxFirebaseModel.getFirebaseForSingleValue(query)
                .concatMapIterable(DataSnapshot::getChildren)
                .map(dataSnapshot -> dataSnapshot.getValue(MessageVO.class))
                .doOnComplete(() -> databaseRef.child(chat).child(roomId).removeValue())
                .subscribe(messageVO -> FirebaseStorage.getInstance().getReferenceFromUrl(messageVO.getImage()).delete());
    }

    // 방을 본 시간을 업데이트
    @Override
    public void setLastChatView(String userUuid, String targetUuid) {
        Long currentTime = mChattingView.getCleanTime();
        databaseRef.child(chatRoomList).child(userUuid).child(targetUuid).child("lastViewTime").setValue(currentTime);
    }

    // 채팅 로딩 발생 메소드
    @SuppressLint("CheckResult")
    public void getPastChattingLog(String Room, String userUuid) {
        if (!mChattingView.getChatKeyList().isEmpty() && mChattingView.getChatKeyList().size() != 1) {
            if (mChattingView.getChatKeyList().size() < RemoteConfig.MessageCount) {
                return;
            }

            // 마지막으로 불러온 채팅 아이디
            String lastChildChatKey = mChattingView.getChatKeyList().get(0);
            mChattingView.getChatKeyList().clear();

            // 맨 위 아이템 제거(중복발생)
            mChattingView.getChatList().remove(0);
            mChattingView.refreshChatLogView();

            messageWeight *= 1;
            Query query = databaseRef.child(chat).child(Room).orderByKey().endAt(lastChildChatKey).limitToLast(RemoteConfig.MessageCount * messageWeight);
            rxFirebaseModel.getFirebaseForSingleValue(query)
                    .map(dataSnapshot -> dataSnapshot.getChildren().iterator())
                    .subscribe(child -> loadMessage(userUuid, child));
        }
    }

    @Override
    public void removeDisposable() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    // 읽음 처리 (클라이언트)
    private void checkRefreshChatLog() {
        for (ChatMessage chatMessage : mChattingView.getUnCheckList()) {
            chatMessage.setCheck(0);
        }
        mChattingView.getUnCheckList().clear();
        mChattingView.refreshChatLogView();
    }


    private ChatMessage makeChatMessage(String userUuid, MessageVO message) {

        ChatMessage chatMessage;
        if (message.getWriter().equals(userUuid) && message.getImage() == null) {
            chatMessage = new ChatMessage(message, true, false);
            if (chatMessage.getCheck() == 1) {
                mChattingView.getUnCheckList().add(chatMessage);
            }
        } else if (!message.getWriter().equals(userUuid) && message.getImage() == null) {
            chatMessage = new ChatMessage(message, false, false, mChattingView.getGridItem());
        } else if (message.getWriter().equals(userUuid)) {
            chatMessage = new ChatMessage(message, true, true);
            if (chatMessage.getCheck() == 1) {
                mChattingView.getUnCheckList().add(chatMessage);
            }
        } else {
            chatMessage = new ChatMessage(message, false, true, mChattingView.getGridItem());
        }
        return chatMessage;
    }

    // 채팅 초기화
    private void initMessage(String Room, String userUuid, MessageVO message) {

        ChatMessage chatMessage;
        int check = message.getCheck();
        String key = message.getParent();

        // 체크버튼
        if (check != 0 && !message.getWriter().equals(userUuid)) {
            message.setCheck(0);
            databaseRef.child(chat).child(Room).child(key).child("check").setValue(check - 1);
        }
        chatMessage = makeChatMessage(userUuid, message);
        //데이터 추가
        mChattingView.getChatList().add(chatMessage);
        mChattingView.getChatKeyList().add(key);
        mChattingView.refreshChatLogView();

        // 스크롤 다루기
        mChattingView.setScrollControl(chatMessage.getContent(), chatMessage.isMine(), chatMessage.isImage());
    }

    // 채팅 로딩
    private void loadMessage(String userUuid, Iterator<DataSnapshot> child) {

        int size = 0;
        int visilbeCompFirstPosition = mChattingView.findFirstCompletelyVisibleItemPosition();
        int visiblieCompLastPosition = mChattingView.findLastCompletelyVisibleItemPosition();
        while (child.hasNext()) {//마찬가지로 중복 유무 확인
            DataSnapshot ds = child.next();
            String childChatKey = ds.getKey();
            MessageVO message = ds.getValue(MessageVO.class);
            message.setParent(childChatKey);
            ChatMessage chatMessage = makeChatMessage(userUuid, message);
            size = mChattingView.getChatKeyList().size();
            mChattingView.getChatKeyList().add(size, childChatKey);
            mChattingView.getChatList().add(size, chatMessage);
        }

        int messageViewCount = size + (visiblieCompLastPosition - visilbeCompFirstPosition) - 2;
        mChattingView.refreshChatLogView();
        mChattingView.scrollToPosition(messageViewCount);
    }
}
