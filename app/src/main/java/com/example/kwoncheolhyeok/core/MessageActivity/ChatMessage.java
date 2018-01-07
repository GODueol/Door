package com.example.kwoncheolhyeok.core.MessageActivity;

import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.MessageVO;
import com.example.kwoncheolhyeok.core.PeopleFragment.ImageAdapter;

/**
 * Created by himanshusoni on 06/09/15.
 */
public class ChatMessage {
    private boolean isImage, isMine;
    private String content;
    private String profileImage;
    private String image;
    private Long time;
    private int check;
    private ImageAdapter.Item item;


    public ChatMessage(MessageVO message, boolean mine, boolean isImage) {
        content = message.getContent();
        time = message.getTime();
        check = message.getCheck();
        this.image = message.getImage();
        isMine = mine;
        this.isImage = isImage;
    }
    public ChatMessage(MessageVO message, boolean mine, boolean image, ImageAdapter.Item item) {
        content = message.getContent();
        time = message.getTime();
        check = message.getCheck();
        this.image = message.getImage();
        isMine = mine;
        isImage = image;
        this.item = item;
        this.profileImage = item.getPicUrl();
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
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


    public ImageAdapter.Item getItem() {
        return item;
    }

    public void setItem(ImageAdapter.Item item) {
        this.item = item;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
