package com.example.kwoncheolhyeok.core.Entity;

/**
 * Created by gimbyeongjin on 2017. 8. 13..
 */

public class User {

    public User() {
    }

    String email;
    String id;
    String age;
    String height;
    String weight;
    String bodyType;

    String intro;


    IntBoundary ageBoundary;
    IntBoundary heightBoundary;
    IntBoundary weightBoundary;
    StringBoundary bodyTypeBoundary;

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
