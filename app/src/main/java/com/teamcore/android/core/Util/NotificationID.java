package com.teamcore.android.core.Util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2018-02-28.
 */

public class NotificationID {
    private final static AtomicInteger c = new AtomicInteger(0);
    public static int getID() {
        return c.incrementAndGet();
    }
}