package com.teamdoor.android.door.Exception;

/**
 * Created by gimbyeongjin on 2018. 1. 27..
 */

public class NotSetAutoTimeException extends Exception {

    private int Err_Code;

    private NotSetAutoTimeException(String msg, int errCode) {
        super(msg);
        this.Err_Code = errCode;
    }

    public NotSetAutoTimeException(String msg) {
        this(msg, 100);
    }

    public int getErrCode() {
        return Err_Code;
    }
}
