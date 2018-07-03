package com.teamdoor.android.door.Entity;

/**
 * Created by godueol on 2018. 4. 14..
 */

public class Suggestion {

    String email;
    String recive_email;
    String content;
    String brand;
    String uuid;
    String version;

    public Suggestion(String email, String recive_email, String content, String brand, String uuid, String version) {
        this.email = email;
        this.recive_email = recive_email;
        this.content = content;
        this.brand = brand;
        this.uuid = uuid;
        this.version = version;

    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRecive_email() {
        return recive_email;
    }

    public void setRecive_email(String recive_email) {
        this.recive_email = recive_email;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
