package com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util;

import android.annotation.SuppressLint;
import android.location.Location;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ListView;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.MessageActivity.ChatMessage;
import com.example.kwoncheolhyeok.core.MessageActivity.ChatMessageAdapter;
import com.example.kwoncheolhyeok.core.PeopleFragment.ImageAdapter;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.example.kwoncheolhyeok.core.Util.GPSInfo;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018-01-08.
 */

public class ChatFirebaseUtil {


    final static String chatRoomList = "chatRoomList";
    final static String chat = "chat";
    final static String image = "image";

    Context context;
    String userUuid, targetUuid;
    User currentUser,targetUser;
    String userPickuri, targetPicuri;
    String roomName;
    DatabaseReference databaseRef;
    FirebaseStorage storage;


    public ChatFirebaseUtil(Context context,User currentUser,User targetUser, String userUuid, String targetUuid) {
        databaseRef = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        this.context = context;
        this.currentUser = currentUser;
        this.targetUser = targetUser;
        this.userUuid = userUuid;
        this.targetUuid = targetUuid;
        userPickuri = currentUser.getPicUrls().getPicUrl1();
        targetPicuri = targetUser.getPicUrls().getPicUrl1();

    }

    public void sendMessage(MessageVO message) {

        String room = roomName;
        String key = databaseRef.child(chat).child(room).push().getKey();

        Long currentTime = getTime();
        Map<String, Object> childUpdates = new HashMap<>();

        if (message.getImage() == null) {
            childUpdates.put("/"+chat+"/"+ room + "/" + key, message);

            childUpdates.put("/"+chatRoomList+"/"+ targetUuid + "/" + userUuid + "/" + "lastChatTime", currentTime);
            childUpdates.put("/"+chatRoomList+"/"+ targetUuid + "/" + userUuid + "/" + "lastChat", message.getContent());

            childUpdates.put("/"+chatRoomList+"/"+ userUuid + "/" + targetUuid + "/" + "lastViewTime", currentTime);
            childUpdates.put("/"+chatRoomList+"/"+ userUuid + "/" + targetUuid + "/" + "lastChat", message.getContent());
        } else if (message.getContent() == null) {
            childUpdates.put("/"+chat+"/" + room + "/" + key, message);

            childUpdates.put("/"+chatRoomList+"/"+ targetUuid + "/" + userUuid + "/" + "lastChatTime", currentTime);
            childUpdates.put("/"+chatRoomList+"/" + targetUuid + "/" + userUuid + "/" + "lastChat", "사진");
            childUpdates.put("/"+chatRoomList+"/" + userUuid + "/" + targetUuid + "/" + "lastViewTime", currentTime);
            childUpdates.put("/"+chatRoomList+"/" + userUuid + "/" + targetUuid + "/" + "lastChat", "사진");
        }
        databaseRef.updateChildren(childUpdates);
    }

    public void sendImageMessage(Uri outputFileUri){

        StorageReference imagesRef = storage.getReference().child(chat);
        long currentTime = System.currentTimeMillis();
        final String imageName = CryptoImeageName.md5(Long.toString(currentTime));
        final StorageReference imageMessageRef = imagesRef.child(image+"/"+roomName+"/"+userUuid+"/"+imageName);
        UploadTask uploadTask = imageMessageRef.putFile(outputFileUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("chatError",e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUri = taskSnapshot.getDownloadUrl();
                imageMessageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        long currentTime = getTime();
                        MessageVO message = new MessageVO(uri.toString(), userUuid, currentUser.getId(), null,currentTime, 1);
                        sendMessage(message);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

            }
        });
    }

    public void checkchatRoom(final ChatMessageAdapter mAdapter, final ListView listView) {
        final  DatabaseReference chatRoomRef = FirebaseDatabase.getInstance().getReference(chatRoomList);

        chatRoomRef.child(userUuid).child(targetUuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final RoomVO checkRoomVO = dataSnapshot.getValue(RoomVO.class);
                long currentTime = System.currentTimeMillis();
                try {
                    roomName = checkRoomVO.getChatRoomid();
                    chatRoomRef.child(userUuid).child(targetUuid).child("lastViewTime").setValue(currentTime);
                } catch (Exception e) {
                    // 채팅방 이름 설정
                    roomName  = FirebaseDatabase.getInstance().getReference(chat).push().getKey();

                    Map<String, Object> childUpdates = new HashMap<>();
                    RoomVO roomVO = new RoomVO(roomName,null, userUuid,currentTime, userPickuri);
                    childUpdates.put("/"+chatRoomList+"/" + targetUuid + "/" + userUuid  , roomVO);
                    roomVO = new RoomVO(roomName,null, targetUuid,currentTime, targetPicuri);
                    childUpdates.put("/"+chatRoomList+"/" + userUuid + "/" + targetUuid , roomVO);
                    databaseRef.updateChildren(childUpdates);
                    chatRoomRef.child(userUuid).child(targetUuid).child("lastViewTime").setValue(currentTime);
                }
                final ImageAdapter.Item item = new ImageAdapter.Item(0,targetUuid,targetUser,targetPicuri);
                // 상대방과의 거리 셋팅
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(FireBaseUtil.currentLocationPath);
                GeoFire geoFire = new GeoFire(ref);
                final Location location = GPSInfo.getmInstance(context).getGPSLocation();
                geoFire.getLocation(targetUuid, new LocationCallback() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onLocationResult(String s, GeoLocation geoLocation) {
                        Location targetLocation = new Location("");//provider name is unnecessary
                        targetLocation.setLatitude(geoLocation.latitude);//your coords of course
                        targetLocation.setLongitude(geoLocation.longitude);
                        final float distance = location.distanceTo(targetLocation);
                        item.setDistance(distance);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                FirebaseDatabase.getInstance().getReference().child(chat).child(roomName)
                        .addChildEventListener(new ChildEventListener() {
                            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                                Log.d("fireabaseUpdate", "onChildAdded:" + dataSnapshot.getKey());

                                // A new comment has been added, add it to the displayed list
                                MessageVO message = dataSnapshot.getValue(MessageVO.class);
                                ChatMessage chatMessage;
                                int check = message.getCheck();

                                if(message.getWriter().equals(userUuid) && message.getImage() == null) {
                                    chatMessage = new ChatMessage(message, true, false);
                                }
                                else if(!message.getWriter().equals(userUuid) && message.getImage() == null){
                                    chatMessage = new ChatMessage(message, false, false, item);
                                }
                                else if(message.getWriter().equals(userUuid) && message.getContent() == null){
                                    chatMessage = new ChatMessage(message, true, true);
                                }
                                else{
                                    chatMessage = new ChatMessage(message, false, true,item);
                                }

                                mAdapter.add(chatMessage);
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                                Log.d("fireabaseUpdate", "onChildChanged:" + dataSnapshot.getKey());

                                // A comment has changed, use the key to determine if we are displaying this
                                // comment and if so displayed the changed comment.
                                MessageVO user = dataSnapshot.getValue(MessageVO.class);
                                String commentKey = dataSnapshot.getKey();
                                mAdapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public Long getTime() {
        return System.currentTimeMillis();
    }

    public String getRoomName(){
        return roomName;
    }
}

