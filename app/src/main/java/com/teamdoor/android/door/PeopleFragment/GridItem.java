package com.teamdoor.android.door.PeopleFragment;

import com.teamdoor.android.door.Entity.SummaryUser;

import java.io.Serializable;

/**
 * Created by gimbyeongjin on 2018. 1. 23..
 */
public class GridItem implements Serializable {

    float distance;
    String uuid;
    SummaryUser summaryUser;
    String picUrl;

    @Override
    public int hashCode() {
        return getUuid().hashCode();
    }

    @Override
    public String toString() {
        return "[ distance : " + distance + ", uuid : " + uuid + "]";
    }

    public GridItem(float distance, String uuid, SummaryUser summaryUser, String picUrl) {
        this.distance = distance;
        this.uuid = uuid;
        this.summaryUser = summaryUser;
        this.picUrl = picUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public SummaryUser getSummaryUser() {
        return summaryUser;
    }

    public void setSummaryUser(SummaryUser user) {
        this.summaryUser = user;
    }
}
