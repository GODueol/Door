package com.example.kwoncheolhyeok.core.MessageActivity.util;

import android.support.annotation.NonNull;

/**
 * Created by godueol on 2017. 12. 23..
 */

public class RoomVO {



    private String targetUuid;
    private String chatRoomid;
    private String targetNickName;
    private String targetProfile;
    private String targetUrl;
    private String lastChat;
    private int badgeCount=0;
    private long lastChatTime;


    private long lastViewTime;


    public RoomVO(){
    }

    public RoomVO(String chatRoomid, String lastChat, String targetUuid, long lastChatTime){
        this.chatRoomid = chatRoomid;
        this.lastChat = lastChat;
        this.targetUuid = targetUuid;
        this.lastChatTime = lastChatTime;
    }

    public String getTargetUuid() {
        return targetUuid;
    }

    public void setTargetUuid(String targetUuid) {
        this.targetUuid = targetUuid;
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

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
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


    public int getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(int badgeCount) {
        this.badgeCount = badgeCount;
    }

    public long getLastViewTime() {
        return lastViewTime;
    }

    public void setLastViewTime(long lastViewTime) {
        this.lastViewTime = lastViewTime;
    }

}
