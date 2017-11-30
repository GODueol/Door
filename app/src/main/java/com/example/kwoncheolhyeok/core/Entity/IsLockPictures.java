package com.example.kwoncheolhyeok.core.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IsLockPictures implements Serializable {
    boolean isLockPic1;
    boolean isLockPic2;
    boolean isLockPic3;
    boolean isLockPic4;

    public boolean getIsLockPic1() {
        return isLockPic1;
    }

    public void setLockPic1(boolean lockPic1) {
        isLockPic1 = lockPic1;
    }

    public boolean getIsLockPic2() {
        return isLockPic2;
    }

    public void setIsLockPic2(boolean lockPic2) {
        isLockPic2 = lockPic2;
    }

    public boolean getIsLockPic3() {
        return isLockPic3;
    }

    public void setIsLockPic3(boolean lockPic3) {
        isLockPic3 = lockPic3;
    }

    public boolean getIsLockPic4() {
        return isLockPic4;
    }

    public void setIsLockPic4(boolean lockPic4) {
        isLockPic4 = lockPic4;
    }

    public Map<String, Boolean> toMap(){
        Map<String, Boolean> map = new HashMap();
        map.put("isLockPic1", getIsLockPic1());
        map.put("isLockPic2", getIsLockPic2());
        map.put("isLockPic3", getIsLockPic3());
        map.put("isLockPic4", getIsLockPic4());
        return map;
    }
    public ArrayList<Boolean> toArray(){
        ArrayList<Boolean> booleans = new ArrayList<>();
        booleans.add(getIsLockPic1());
        booleans.add(getIsLockPic2());
        booleans.add(getIsLockPic3());
        booleans.add(getIsLockPic4());
        return booleans;
    }
}
