package com.example.kwoncheolhyeok.core.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable{

    public User() {
    }

    private String email;// = "";
    private String id;// = "";
    private String age;
    private String height;
    private String weight;
    private String bodyType;

    private String intro;


    private IntBoundary ageBoundary;
    private IntBoundary heightBoundary;
    private IntBoundary weightBoundary;
    private StringBoundary bodyTypeBoundary;

    private PictureUrls picUrls = new PictureUrls();
    private IsLockPictures isLockPics = new IsLockPictures();

    private Map<String, Long> unLockUsers = new HashMap<>();    // uuid, 추가 시간

    private Map<String, Long> blockUsers = new HashMap<>();    // uuid, 추가 시간

    public Map<String, Long> getBlockUsers() {
        return blockUsers;
    }

    public void setBlockUsers(Map<String, Long> blockUsers) {
        this.blockUsers = blockUsers;
    }

    public Map<String, Long> getUnLockUsers() {
        return unLockUsers;
    }

    public void setUnLockUsers(Map<String, Long> unLockUsers) {
        this.unLockUsers = unLockUsers;
    }

    public PictureUrls getPicUrls() {
        return picUrls;
    }

    public void setPicUrls(PictureUrls picUrls) {
        this.picUrls = picUrls;
    }

    public IsLockPictures getIsLockPics() {
        return isLockPics;
    }

    public void setIsLockPics(IsLockPictures isLockPics) {
        this.isLockPics = isLockPics;
    }

    private long loginDate;

    public long getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(long loginDate) {
        this.loginDate = loginDate;
    }

    private boolean isLockPic2;
    private boolean isLockPic3;
    private boolean isLockPic4;

    public boolean isLockPic2() {
        return isLockPic2;
    }

    public void setLockPic2(boolean lockPic2) {
        isLockPic2 = lockPic2;
    }

    public boolean isLockPic3() {
        return isLockPic3;
    }

    public void setLockPic3(boolean lockPic3) {
        isLockPic3 = lockPic3;
    }

    public boolean isLockPic4() {
        return isLockPic4;
    }

    public void setLockPic4(boolean lockPic4) {
        isLockPic4 = lockPic4;
    }

    private boolean isUseFilter;

    public boolean isUseFilter() {
        return isUseFilter;
    }

    public void setUseFilter(boolean useFilter) {
        isUseFilter = useFilter;
    }

    public IntBoundary getAgeBoundary() {
        return ageBoundary;
    }

    public void setAgeBoundary(IntBoundary ageBoundary) {
        this.ageBoundary = ageBoundary;
    }

    public IntBoundary getHeightBoundary() {
        return heightBoundary;
    }

    public void setHeightBoundary(IntBoundary heightBoundary) {
        this.heightBoundary = heightBoundary;
    }

    public IntBoundary getWeightBoundary() {
        return weightBoundary;
    }

    public void setWeightBoundary(IntBoundary weightBoundary) {
        this.weightBoundary = weightBoundary;
    }

    public StringBoundary getBodyTypeBoundary() {
        return bodyTypeBoundary;
    }

    public void setBodyTypeBoundary(StringBoundary bodyTypeBoundary) {
        this.bodyTypeBoundary = bodyTypeBoundary;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public User(String email, String id, String age, String height, String weight, String bodyType) {
        this.email = email;
        this.id = id;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.bodyType = bodyType;
    }

    public String getEmail() {
        return email;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

}
