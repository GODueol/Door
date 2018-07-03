package  com.teamdoor.android.door;

 interface MyAidl{
 	void registerScreenShotObserver();
 	void unregisterScreenShotObserver();
 	void setScreenShotEnable(boolean enable);
 }