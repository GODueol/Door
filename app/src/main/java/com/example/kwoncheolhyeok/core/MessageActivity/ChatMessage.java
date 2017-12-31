package com.example.kwoncheolhyeok.core.MessageActivity;

/**
 * Created by himanshusoni on 06/09/15.
 */
public class ChatMessage {
    private boolean isImage, isMine;
    private String content;
    private String profileImeage;

    public ChatMessage(String message, boolean mine, boolean image) {
        content = message;
        isMine = mine;
        isImage = image;
    }
    public ChatMessage(String message, boolean mine, boolean image, String profileImeage) {
        content = message;
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
}
