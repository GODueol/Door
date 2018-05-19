package com.teamcore.android.core;

 interface MyAidl{
 	void registerScreenShotObserver();
 	void unregisterScreenShotObserver();
 	void setScreenShotEnable(boolean enable);
 }