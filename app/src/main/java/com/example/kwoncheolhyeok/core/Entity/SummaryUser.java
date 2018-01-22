package com.example.kwoncheolhyeok.core.Entity;

import java.io.Serializable;

/**
 * Created by gimbyeongjin on 2018. 1. 22..
 */
public class SummaryUser implements Serializable {
    String pictureUrl;

    private int age;
    private int height;
    private int weight;
    private String bodyType;

    private int corePostCount;

    public SummaryUser(){};

    public SummaryUser(String pictureUrl, int age, int height, int weight, String bodyType, int corePostCount) {
        this.pictureUrl = pictureUrl;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.bodyType = bodyType;
        this.corePostCount = corePostCount;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public int getCorePostCount() {
        return corePostCount;
    }

    public void setCorePostCount(int corePostCount) {
        this.corePostCount = corePostCount;
    }
}
