package com.example.kwoncheolhyeok.core.Entity;

/**
 * Created by gimbyeongjin on 2017. 8. 13..
 */

public class User {

    public User() { }

    String email;
    String id;
    String age;
    String height;
    String weight;

    public User(String email, String id, String age, String height, String weight) {
        this.email = email;
        this.id = id;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
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
