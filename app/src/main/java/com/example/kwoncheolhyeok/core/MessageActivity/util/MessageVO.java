package com.example.kwoncheolhyeok.core.MessageActivity.util;

/**
 * Created by Administrator on 2017-12-04.
 */

public class MessageVO {

    private String content;
    private String image;
    private String nickname;
    private String writer;
    private int check;
    private long time;

    public MessageVO(){
    }


    public MessageVO(String image, String writer, String nickname, String content, Long time, int check){
        this.image = image;
        this.writer = writer;
        this.nickname = nickname;
        this.content = content;
        this.time = time;
        this.check = check;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

}