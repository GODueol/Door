package com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017-12-04.
 */

public class MessageVO {

    private String content;
    private int img;
    private String nickname;
    private String writer;
    private int check;
    private long time;

    public MessageVO(){
    }


    public MessageVO(int img,String writer,  String nickname, String content, Long time, int check){
        this.img = img;
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

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
