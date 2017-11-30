package com.example.kwoncheolhyeok.core.Entity;

import java.io.Serializable;
import java.util.ArrayList;

public class PictureUrls implements Serializable {
    private String picUrl1;
    private String picUrl2;
    private String picUrl3;
    private String picUrl4;

    public String getPicUrl1() {
        return picUrl1;
    }

    public void setPicUrl1(String picUrl1) {
        this.picUrl1 = picUrl1;
    }

    private String getPicUrl2() {
        return picUrl2;
    }

    public void setPicUrl2(String picUrl2) {
        this.picUrl2 = picUrl2;
    }

    private String getPicUrl3() {
        return picUrl3;
    }

    public void setPicUrl3(String picUrl3) {
        this.picUrl3 = picUrl3;
    }

    private String getPicUrl4() {
        return picUrl4;
    }

    public void setPicUrl4(String picUrl4) {
        this.picUrl4 = picUrl4;
    }

    public ArrayList<String> toArray(){
        ArrayList<String> booleans = new ArrayList<>();
        booleans.add(getPicUrl1());
        booleans.add(getPicUrl2());
        booleans.add(getPicUrl3());
        booleans.add(getPicUrl4());
        return booleans;
    }

    public ArrayList<String> toNotNullArray(){
        ArrayList<String> booleans = new ArrayList<>();
        if(getPicUrl1() != null) booleans.add(getPicUrl1());
        if(getPicUrl2() != null) booleans.add(getPicUrl2());
        if(getPicUrl3() != null) booleans.add(getPicUrl3());
        if(getPicUrl4() != null) booleans.add(getPicUrl4());
        return booleans;
    }
}
