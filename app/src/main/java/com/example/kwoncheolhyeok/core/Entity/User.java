package com.example.kwoncheolhyeok.core.Entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {

    public User() {
    }

    private String email;
    private String id;
    private int age;
    private int height;
    private int weight;
    private String bodyType;

    private String intro;

    private String token;

    private IntBoundary ageBoundary;
    private IntBoundary heightBoundary;
    private IntBoundary weightBoundary;
    private StringBoundary bodyTypeBoundary;

    private PictureUrls picUrls = new PictureUrls();
    private IsLockPictures isLockPics = new IsLockPictures();

    private SummaryUser summaryUser;
    private String totalProfile;

    private int corePostCount;
    private long loginDate;
    private boolean isUseFilter;
    private boolean isAnonymityProhibition;

    private Map<String, Long> unLockUsers = new HashMap<>();    // uuid, 추가 시간

    private Map<String, Long> blockUsers = new HashMap<>();    // uuid, 추가 시간

    private Map<String, Long> blockMeUsers = new HashMap<>();    // uuid, 추가 시간

    private Map<String, Long> followingUsers = new HashMap<>();    // uuid, 추가 시간

    private Map<String, Long> followerUsers = new HashMap<>();    // uuid, 추가 시간

    private Map<String, Long> friendUsers = new HashMap<>();    // uuid, 추가 시간

    private Map<String, Long> recentUsers = new HashMap<>();    // uuid, 추가 시간

    private Map<String, Long> viewedMeUsers = new HashMap<>();    // uuid, 추가 시간

    private String accountType;

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public boolean isAnonymityProhibition() {
        return isAnonymityProhibition;
    }

    public void setAnonymityProhibition(boolean anonymityProhibition) {
        isAnonymityProhibition = anonymityProhibition;
    }

    public SummaryUser getSummaryUser() {
        return new SummaryUser(getPicUrls().getThumbNail_picUrl1(), age, height, weight, bodyType, corePostCount);
    }

    public Map<String, Long> getBlockMeUsers() {
        return blockMeUsers;
    }

    public void setBlockMeUsers(Map<String, Long> blockMeUsers) {
        this.blockMeUsers = blockMeUsers;
    }

    public int getCorePostCount() {
        return corePostCount;
    }

    public void setCorePostCount(int corePostCount) {
        this.corePostCount = corePostCount;
    }

    public Map<String, Long> getViewedMeUsers() {
        return viewedMeUsers;
    }

    public void setViewedMeUsers(Map<String, Long> viewedMeUsers) {
        this.viewedMeUsers = viewedMeUsers;
    }

    public Map<String, Long> getRecentUsers() {
        return recentUsers;
    }

    public void setRecentUsers(Map<String, Long> recentUsers) {
        this.recentUsers = recentUsers;
    }

    public Map<String, Long> getFriendUsers() {
        return friendUsers;
    }

    public void setFriendUsers(Map<String, Long> friendUsers) {
        this.friendUsers = friendUsers;
    }

    public Map<String, Long> getFollowingUsers() {
        return followingUsers;
    }

    public void setFollowingUsers(Map<String, Long> followingUsers) {
        this.followingUsers = followingUsers;
    }

    public Map<String, Long> getFollowerUsers() {
        return followerUsers;
    }

    public void setFollowerUsers(Map<String, Long> followerUsers) {
        this.followerUsers = followerUsers;
    }

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

    public long getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(long loginDate) {
        this.loginDate = loginDate;
    }


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

    public User(String email, String id, int age, int height, int weight, String bodyType) {
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


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTotalProfile() {
        return getAge() + " / " + getHeight() + " / " + getWeight() + " / " + getBodyType();
    }

}
