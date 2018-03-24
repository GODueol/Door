package com.example.kwoncheolhyeok.core.Entity;

/**
 * Created by godueol on 2018. 3. 24..
 */

public class AlarmSummary {
    private String nickname;
    private String text;
    private String type;
    private Long time;

    public AlarmSummary(String nickname, String text, String type, Long time){
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

}
