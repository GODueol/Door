package com.teamdoor.android.door.Entity;

import java.io.Serializable;

/**
 * Created by godueol on 2018. 3. 24..
 */

public class AlarmSummary implements Serializable {
    private String nickname;
    private String text;
    private String type;
    private Long time;
    private Long viewTime;
    private String cUuid;
    private String pUuid;

    private String postId;

    private String key;

    public AlarmSummary() {

    }

    public AlarmSummary(String cUuid, String pUuid, String nickname, String text, String type, Long time) {
        this.cUuid = cUuid;
        this.pUuid = pUuid;
        this.nickname = nickname;
        this.text = text;
        this.type = type;
        this.time = time;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }


    public String getcUuid() {
        return cUuid;
    }

    public void setcUuid(String cUuid) {
        this.cUuid = cUuid;
    }


    public String getpUuid() {
        return pUuid;
    }

    public void setpUuid(String pUuid) {
        this.pUuid = pUuid;
    }


    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getViewTime() {
        return viewTime;
    }

    public void setViewTime(Long viewTime) {
        this.viewTime = viewTime;
    }
}
