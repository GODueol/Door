package com.example.kwoncheolhyeok.core.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018-03-21.
 */

public class Alarm {

    private Map<String, Long> userLog = new HashMap<>();    // uuid, 추가 시간

    private AlarmSummary alarmSummary;

    public Alarm(){
    }

    public Map<String, Long> getUserLog() {
        return userLog;
    }
    
    public AlarmSummary getAlarmSummary() {
        return alarmSummary;
    }

    public void setAlarmSummary(AlarmSummary alarmSummary) {
        this.alarmSummary = alarmSummary;
    }



}
