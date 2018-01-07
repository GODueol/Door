package com.example.kwoncheolhyeok.core.MessageActivity;

import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.MessageVO;

/**
 * Created by himanshusoni on 06/09/15.
 */
public class ChatMessage {
    private boolean isImage, isMine;
    private String content;
    private String profileImeage;
    private Long time;
    private int check;


    public ChatMessage(MessageVO message, boolean mine, boolean image) {
        content = message.getContent();
        time = message.getTime();
        check = message.getCheck();
        isMine = mine;
        isImage = image;
    }
    public ChatMessage(MessageVO message, boolean mine, boolean image, String profileImeage) {
        content = message.getContent();
        time = message.getTime();
        check = message.getCheck();
        isMine = mine;
        isImage = image;
        this.profileImeage = profileImeage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setIsMine(boolean isMine) {
        this.isMine = isMine;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setIsImage(boolean isImage) {
        this.isImage = isImage;
    }

    public String getProfileImeage() {
        return profileImeage;
    }

    public void setProfileImeage(String profileImeage) {
        this.profileImeage = profileImeage;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

}
