package com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017-12-04.
 */

public class MessageVO {

    private int img;
    private String writer;
    private String nickname;
    private String content;
    private int editimg;

    public MessageVO(){
    }


    public MessageVO(int img,String writer,  String nickname, String content, int editimg){
        this.img = img;
        this.writer = writer;
        this.nickname = nickname;
        this.content = content;
        this.editimg = editimg;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("img", img);
        result.put("nickname", nickname);
        result.put("content", content);
        result.put("editimg", editimg);

        return result;
    }
    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public int getEditimg() {
        return editimg;
    }

    public void setEditimg(int editimg) {
        this.editimg = editimg;
    }



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



}
