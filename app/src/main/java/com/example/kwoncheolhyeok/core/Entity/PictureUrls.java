package com.example.kwoncheolhyeok.core.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PictureUrls implements Serializable {
    String picUrl1;
    String picUrl2;
    String picUrl3;
    String picUrl4;

    public String getPicUrl1() {
        return picUrl1;
    }

    public void setPicUrl1(String picUrl1) {
        this.picUrl1 = picUrl1;
    }

    public String getPicUrl2() {
        return picUrl2;
    }

    public void setPicUrl2(String picUrl2) {
        this.picUrl2 = picUrl2;
    }

    public String getPicUrl3() {
        return picUrl3;
    }

    public void setPicUrl3(String picUrl3) {
        this.picUrl3 = picUrl3;
    }

    public String getPicUrl4() {
        return picUrl4;
    }

    public void setPicUrl4(String picUrl4) {
        this.picUrl4 = picUrl4;
    }

    public Map<String, String> toMap(){
        Map<String, String> map = new HashMap();
        map.put("picUrl1", getPicUrl1());
        map.put("picUrl2", getPicUrl2());
        map.put("picUrl3", getPicUrl3());
        map.put("picUrl4", getPicUrl4());
        return map;
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
