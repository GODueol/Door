package com.teamdoor.android.door.Entity;

public class Notice {
    private String name;
    private String uid;
    private String title;
    private String text;
    private String pictureUrl;
    private long writeDate;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(long writeDate) {
        this.writeDate = writeDate;
    }

    public Notice() {
    }
}
