package com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util;

/**
 * Created by godueol on 2017. 12. 23..
 */

public class RoomVO {



    private String nickName;
    private String chatId;
    private long lastTime;
    private String targetUri;


    public RoomVO(){
    }

    public RoomVO(String nickName, String chatId, long lastTime, String targetUri){
        this.nickName = nickName;
        this.chatId = chatId;
        this.lastTime = lastTime;
        this.targetUri = targetUri;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
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
