package com.example.kwoncheolhyeok.core.Util;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

public class CustomFontApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "DroidSans.ttf"))
                .addBold(Typekit.createFromAsset(this, "DroidSans-Bold.ttf"))
                .addCustom1(Typekit.createFromAsset(this, "FuturaStd-Medium.otf"));

    }
}