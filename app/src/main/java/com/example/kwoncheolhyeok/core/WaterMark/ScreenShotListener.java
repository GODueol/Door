package com.example.kwoncheolhyeok.core.WaterMark;

import android.graphics.Bitmap;

/**
 * Created by Joel on 26/10/2015.
 */
public interface ScreenShotListener {
    void onScreenshotTaken(Bitmap bitmap, String fileName);
}