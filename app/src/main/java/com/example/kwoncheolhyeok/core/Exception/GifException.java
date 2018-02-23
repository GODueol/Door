package com.example.kwoncheolhyeok.core.Exception;

/**
 * Created by gimbyeongjin on 2018. 1. 27..
 */

public class GifException extends Exception {

    private int Err_Code;

    private GifException(String msg, int errCode) {
        super(msg);
        this.Err_Code = errCode;
    }

    public GifException(String msg) {
        this(msg, 100);
    }

    public int getErrCode() {
        return Err_Code;
    }
}
