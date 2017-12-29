package com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util;

/**
 * Created by godueol on 2017. 12. 23..
 */

public class RoomVO {



    private String userUuid;
    private String chatRoomid;
    private long lastTime;
    private String targetUri;


    public RoomVO(){
    }

    public RoomVO(String userUuid, String chatRoomid, long lastTime, String targetUri){
        this.userUuid = userUuid;
        this.chatRoomid = chatRoomid;
        this.lastTime = lastTime;
        this.targetUri = targetUri;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }


    public String getChatRoomid() {
        return chatRoomid;
    }

    public void setChatRoomid(String chatRoomid) {
        this.chatRoomid = chatRoomid;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public String getTargetUri() {
        return targetUri;
    }

    public void setTargetUri(String targetUri) {
        this.targetUri = targetUri;
    }


}
