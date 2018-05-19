package com.example.kwoncheolhyeok.core.Entity;

import com.example.kwoncheolhyeok.core.Util.DataContainer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class PictureUrls implements Serializable {
    String picUrl1;
    String picUrl2;
    String picUrl3;
    String picUrl4;

    String thumbNail_picUrl1;
    String thumbNail_picUrl2;
    String thumbNail_picUrl3;
    String thumbNail_picUrl4;

    public String getThumbNail_picUrl1() {
        if (thumbNail_picUrl1 == null) return getPicUrl1();
        return thumbNail_picUrl1;
    }

    public void setThumbNail_picUrl1(String thumbNail_picUrl1) {
        this.thumbNail_picUrl1 = thumbNail_picUrl1;
    }

    public String getThumbNail_picUrl2() {
        if (thumbNail_picUrl2 == null) return getPicUrl2();
        return thumbNail_picUrl2;
    }

    public void setThumbNail_picUrl2(String thumbNail_picUrl2) {
        this.thumbNail_picUrl2 = thumbNail_picUrl2;
    }

    public String getThumbNail_picUrl3() {
        if (thumbNail_picUrl3 == null) return getPicUrl3();
        return thumbNail_picUrl3;
    }

    public void setThumbNail_picUrl3(String thumbNail_picUrl3) {
        this.thumbNail_picUrl3 = thumbNail_picUrl3;
    }

    public String getThumbNail_picUrl4() {
        if (thumbNail_picUrl4 == null) return getPicUrl4();
        return thumbNail_picUrl4;
    }

    public void setThumbNail_picUrl4(String thumbNail_picUrl4) {
        this.thumbNail_picUrl4 = thumbNail_picUrl4;
    }

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

    public ArrayList<String> toArrayAll() {
        ArrayList<String> array = toArray();

        array.add(getThumbNail_picUrl1());
        array.add(getThumbNail_picUrl2());
        array.add(getThumbNail_picUrl3());
        array.add(getThumbNail_picUrl4());

        return array;
    }

    public ArrayList<String> toArray() {
        ArrayList<String> booleans = new ArrayList<>();
        booleans.add(getPicUrl1());
        booleans.add(getPicUrl2());
        booleans.add(getPicUrl3());
        booleans.add(getPicUrl4());
        return booleans;
    }

    public ArrayList<String> toNotNullArrayThumbNail(IsLockPictures isLockPictures, Map<String, Long> unLockUsers, String uuid) {
        // Lock한 사진은 안나오고, 해제한 유저는 사진 나오도록 필터링
        String myUuid = DataContainer.getInstance().getUid();
        ArrayList<String> notNullArray = new ArrayList<>();
        if (getThumbNail_picUrl1() != null && (uuid.equals(myUuid) || (!isLockPictures.getIsLockPic1() || unLockUsers.containsKey(myUuid))))
            notNullArray.add(getThumbNail_picUrl1());
        if (getThumbNail_picUrl2() != null && (uuid.equals(myUuid) || (!isLockPictures.getIsLockPic2() || unLockUsers.containsKey(myUuid))))
            notNullArray.add(getThumbNail_picUrl2());
        if (getThumbNail_picUrl3() != null && (uuid.equals(myUuid) || (!isLockPictures.getIsLockPic3() || unLockUsers.containsKey(myUuid))))
            notNullArray.add(getThumbNail_picUrl3());
        if (getThumbNail_picUrl4() != null && (uuid.equals(myUuid) || (!isLockPictures.getIsLockPic4() || unLockUsers.containsKey(myUuid))))
            notNullArray.add(getThumbNail_picUrl4());
        return notNullArray;
    }

    public ArrayList<String> toNotNullArray(IsLockPictures isLockPictures, Map<String, Long> unLockUsers, String uuid) {
        // Lock한 사진은 안나오고, 해제한 유저는 사진 나오도록 필터링
        String myUuid = DataContainer.getInstance().getUid();
        ArrayList<String> booleans = new ArrayList<>();
        if (getPicUrl1() != null && (uuid.equals(myUuid) || (!isLockPictures.getIsLockPic1() || unLockUsers.containsKey(myUuid))))
            booleans.add(getPicUrl1());
        if (getPicUrl2() != null && (uuid.equals(myUuid) || (!isLockPictures.getIsLockPic2() || unLockUsers.containsKey(myUuid))))
            booleans.add(getPicUrl2());
        if (getPicUrl3() != null && (uuid.equals(myUuid) || (!isLockPictures.getIsLockPic3() || unLockUsers.containsKey(myUuid))))
            booleans.add(getPicUrl3());
        if (getPicUrl4() != null && (uuid.equals(myUuid) || (!isLockPictures.getIsLockPic4() || unLockUsers.containsKey(myUuid))))
            booleans.add(getPicUrl4());
        return booleans;
    }

}
