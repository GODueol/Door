package com.teamdoor.android.door.Entity;
import com.teamdoor.android.door.PeopleFragment.GridItem;

public class ChatMessage {
    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1, MY_IMAGE = 2, OTHER_IMAGE = 3;

    private String parent;
    private boolean isImage, isMine;
    private String content;
    private String profileImage;
    private String image;
    private Long time;
    private int check;
    private GridItem item;


    public ChatMessage(MessageVO message, boolean mine, boolean isImage) {
        parent = message.getParent();
        content = message.getContent();
        time = message.getTime();
        check = message.getCheck();
        this.image = message.getImage();
        isMine = mine;
        this.isImage = isImage;
    }

    public ChatMessage(MessageVO message, boolean mine, boolean image, GridItem item) {
        parent = message.getParent();
        content = message.getContent();
        time = message.getTime();
        check = message.getCheck();
        this.image = message.getImage();
        isMine = mine;
        isImage = image;
        this.item = item;
        try {
            this.profileImage = item.getPicUrl();
        } catch (Exception e) {

        }
    }


    public int getType() {
        if (isMine() && !isImage()) return MY_MESSAGE;
        else if (!isMine() && !isImage()) return OTHER_MESSAGE;
        else if (isMine() && isImage()) return MY_IMAGE;
        else return OTHER_IMAGE;

    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
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


    public GridItem getItem() {
        return item;
    }

    public void setItem(GridItem item) {
        this.item = item;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
