package com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018-01-07.
 */

public class DateUtil {

    public static final int SEC = 60;
    public static final int MIN = 60;
    public static final int HOUR = 24;
    public static final int DAY = 30;
    public static final int MONTH = 12;

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
        long curTime = System.currentTimeMillis();
        long regTime = date;
        long diffTime = (curTime - regTime) / 1000;

        String msg = null;

        if (diffTime < SEC) {
            // sec
            Math.abs(diffTime);
            msg = diffTime + "sec ago";
        } else if ((diffTime /= SEC) < MIN) {
            // min
            System.out.println(diffTime);

            msg = diffTime + "min ago";
        } else if ((diffTime /= MIN) < HOUR) {
            // hour
            msg = (diffTime) + "hour ago";
        } else if ((diffTime /= HOUR) < DAY) {
            // day
            msg = (diffTime) + "day ago";
        } else if ((diffTime /= DAY) < MONTH) {
            // day
            msg = (diffTime) + "month ago";
        } else {
            msg = (diffTime) + "year ago";
        }

        return msg;
    }
}