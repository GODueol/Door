package com.example.kwoncheolhyeok.core.MessageActivity.util;

import com.example.kwoncheolhyeok.core.Util.DataContainer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018-01-07.
 */

public class DateUtil {

    Long date;

    public DateUtil(Long date) {
        this.date = date;
    }

    public  String getTime(){
        Date time = new Date(date);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String strTime = sdf.format(time);
        return strTime;
    }

    public String getPreTime() {
        return DataContainer.getInstance().convertBeforeFormat(date);
    }
}