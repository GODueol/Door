package com.example.kwoncheolhyeok.core.Entity;

import java.util.ArrayList;

/**
 * Created by gimbyeongjin on 2017. 12. 14..
 */

public class CorePost {
    String uuid;
    long writeDate;
    String text;
    String pictureUrl;
    String soundUrl;
    String videoUrl;
    ArrayList<String> likeUsers = new ArrayList<>();


    public CorePost() {
    }

    public CorePost(String uuid) {
        this.uuid = uuid;
        this.writeDate = System.currentTimeMillis();
    }

    public CorePost(String uuid, long writeDate, String text, String pictureUrl, String soundUrl, String videoUrl, ArrayList<String> likeUsers) {
        this.uuid = uuid;
        this.writeDate = writeDate;
        this.text = text;
        this.pictureUrl = pictureUrl;
        this.soundUrl = soundUrl;
        this.videoUrl = videoUrl;
        this.likeUsers = likeUsers;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(long writeDate) {
        this.writeDate = writeDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getSoundUrl() {
        return soundUrl;
    }

    public void setSoundUrl(String soundUrl) {
        this.soundUrl = soundUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public ArrayList<String> getLikeUsers() {
        return likeUsers;
    }

    public void setLikeUsers(ArrayList<String> likeUsers) {
        this.likeUsers = likeUsers;
    }
}
