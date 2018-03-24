package com.example.kwoncheolhyeok.core.Entity;

/**
 * Created by Administrator on 2018-03-21.
 */

public class AlarmSummary {
    String NickName;
    String text;
    String type;
    Long time;

    public AlarmSummary(String NickName, String text, String type, Long time){
        this.NickName = NickName;
        this.text = text;
        this.type = type;
        this.time = time;
    }
    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
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
