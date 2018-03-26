package com.example.kwoncheolhyeok.core.MessageActivity.util;

import android.content.Context;

import com.example.kwoncheolhyeok.core.Exception.NotSetAutoTimeException;
import com.example.kwoncheolhyeok.core.Util.DataContainer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2018-01-07.
 */

public class DateUtil {

    Long date;

    public DateUtil(Long date) {
        this.date = date;
    }
    public void setDate(Long date){
        this.date = date;
    }

    public String getTime(){
        Date time = new Date(date);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.KOREA);
        return sdf.format(time);
    }
    public  String gethalfTime(){
        Date time = new Date(date);
        SimpleDateFormat sdf = new SimpleDateFormat("a hh:mm", Locale.KOREA);
        return sdf.format(time);
    }

    public String getDate(){
        Date time = new Date(date);
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy년 MM월 dd일 E요일", Locale.KOREAN );
        return sdf.format(time);
    }

    public String getDate2(){
        Date time = new Date(date);
        SimpleDateFormat  sdf = new SimpleDateFormat( "yyyy. MM. dd. E", Locale.KOREAN );
        return sdf.format(time);
    }


    public String getPreTime(Context context) throws NotSetAutoTimeException {
        return DataContainer.getInstance().convertBeforeFormat(date, context);
    }
}