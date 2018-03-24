package com.example.kwoncheolhyeok.core.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018-03-21.
 */

public class Alarm {

    private Map<String, Long> userLog = new HashMap<>();    // uuid, 추가 시간
    private String nickname;
    private String text;
    private String type;
    private Long time;


    private AlarmSummary alarmSummary;

    public Alarm(String nickname, String text, String type, Long time){
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


    public Map<String, Long> getUserLog() {
        return userLog;
    }

    public void setUserLog(Map<String, Long> userLog) {
        this.userLog = userLog;
    }

    public AlarmSummary getAlarmSummary() {
        return new AlarmSummary(nickname, text, type, time);
    }


}
