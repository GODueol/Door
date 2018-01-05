package com.example.kwoncheolhyeok.core.Entity;

/**
 * Created by gimbyeongjin on 2017. 12. 29..
 */
public class CoreListItem {
    User user;
    CorePost corePost;
    String postKey;

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

    public CoreListItem(User user, CorePost corePost, String postKey) {
        this.user = user;
        this.corePost = corePost;
        this.postKey = postKey;
    }
}