package com.teamcore.android.core.Entity;

/**
 * Created by gimbyeongjin on 2017. 12. 29..
 */
public class CoreListItem {
    private User user;
    private CorePost corePost;
    private String postKey;
    private String cUuid;

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CorePost getCorePost() {
        return corePost;
    }

    public void setCorePost(CorePost corePost) {
        this.corePost = corePost;
    }

    public String getcUuid() {
        return cUuid;
    }

    public void setcUuid(String cUuid) {
        this.cUuid = cUuid;
    }

    public CoreListItem(User user, CorePost corePost, String postKey, String cUuid) {
        this.user = user;
        this.corePost = corePost;
        this.postKey = postKey;
        this.cUuid = cUuid;
    }
}
