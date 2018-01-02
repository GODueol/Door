package com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util;

/**
 * Created by godueol on 2017. 12. 23..
 */

public class RoomVO {



    private String userUuid;
    private String chatRoomid;
    private String targetNickName;
    private String targetProfile;
    private String lastChat;
    private long lastChatTime;


    private long lastViewTime;
    private String targetUri;


    public RoomVO(){
    }

    public RoomVO(String chatRoomid, String lastChat, String userUuid, long lastChatTime, String targetUri){

        this.chatRoomid = chatRoomid;
        this.lastChat = lastChat;
        this.userUuid = userUuid;
        this.lastChatTime = lastChatTime;
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

    public long getLastChatTime() {
        return lastChatTime;
    }

    public void setLastChatTime(long lastChatTime) {
        this.lastChatTime = lastChatTime;
    }

    public String getTargetUri() {
        return targetUri;
    }

    public void setTargetUri(String targetUri) {
        this.targetUri = targetUri;
    }


    public String getLastChat() {
        return lastChat;
    }

    public void setLastChat(String lastChat) {
        this.lastChat = lastChat;
    }

    public String getTargetNickName() {
        return targetNickName;
    }

    public void setTargetNickName(String targetNickName) {
        this.targetNickName = targetNickName;
    }

    public String getTargetProfile() {
        return targetProfile;
    }

    public void setTargetProfile(String targetProfile) {
        this.targetProfile = targetProfile;
    }



    public long getLastViewTime() {
        return lastViewTime;
    }

    public void setLastViewTime(long lastViewTime) {
        this.lastViewTime = lastViewTime;
    }
}
