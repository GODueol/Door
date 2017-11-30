package com.example.kwoncheolhyeok.core.Util;

import android.support.annotation.NonNull;

public class FireBaseUtil {
    public static final String currentLocationPath = "location/users";
    private static final FireBaseUtil ourInstance = new FireBaseUtil();

    public static FireBaseUtil getInstance() {
        return ourInstance;
    }

    private FireBaseUtil() {
    }

    @NonNull
    public String getParentPath(String uuid) {
        return "profile/pic/" + uuid + "/";
    }

    @NonNull
    public String getParentPath() {
        return getParentPath(DataContainer.getInstance().getUid());
    }

}
