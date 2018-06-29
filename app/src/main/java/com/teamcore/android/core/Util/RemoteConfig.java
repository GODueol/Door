package com.teamcore.android.core.Util;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.teamcore.android.core.BuildConfig;
import com.teamcore.android.core.R;

public class RemoteConfig {

    public static int NORMAL_CORE_LIMIT = 300;
    public static int PLUS_CORE_LIMIT = 300;
    public static int CoreCloudMax = 7;
    public static int GridMax = 800;
    public static int MessageCount = 30;
    public static int LIMIT_MB = 5;
    public static int MAX_ALARM_COUNT = 5;

    public static int MainGridAdCount = 7;
    public static int ChatAdCount = 2;
    public static int FriendsAdCount = 3;
    public static int ProfileChatAdCount = 0;

    public static int CorePossibleOldFriendCount = 3;

    public static String WtBigTitle = "";
    public static String WtTitleKo = "";
    public static String WtTitleEn = "";
    public static String WtSubKo = "";
    public static String WtSubEn = "";

    public static int MinAppVersion = 68;
    public static int MaxAppVersion = 99999;

    // 결재 아이템 관련
    public static String CoreCloudItemId = "";
    public static String CorePlusItemId = "";

    static public Task<Void> getConfig(Activity activity){
        // Remote Config
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // 디버깅 테스트 ( 값이 3번 요청되면 거절되므로 디버깅시에는 방지 )
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        // default
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        return mFirebaseRemoteConfig.fetch(0)   // 요청 주기 , 0이면 실행할때마다
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        Log.d("KBJ","Fetch Succeeded");

                        // After config data is successfully fetched, it must be activated before newly fetched
                        // values are returned.
                        mFirebaseRemoteConfig.activateFetched();
                    } else {
                        Log.d("KBJ","Fetch Failed");
                    }
                    displayWelcomeMessage(mFirebaseRemoteConfig);

                });
    }


    private static void displayWelcomeMessage(FirebaseRemoteConfig mFirebaseRemoteConfig) {
        NORMAL_CORE_LIMIT = (int) mFirebaseRemoteConfig.getLong("NORMAL_CORE_LIMIT");
        PLUS_CORE_LIMIT = (int) mFirebaseRemoteConfig.getLong("PLUS_CORE_LIMIT");
        CoreCloudMax = (int) mFirebaseRemoteConfig.getLong("CoreCloudMax");
        GridMax = (int) mFirebaseRemoteConfig.getLong("GridMax");
        MessageCount = (int) mFirebaseRemoteConfig.getLong("MessageCount");
        LIMIT_MB = (int) mFirebaseRemoteConfig.getLong("LIMIT_MB");
        MAX_ALARM_COUNT = (int) mFirebaseRemoteConfig.getLong("MAX_ALARM_COUNT");

        MainGridAdCount = (int) mFirebaseRemoteConfig.getLong("MainGridAdCount");
        ChatAdCount = (int) mFirebaseRemoteConfig.getLong("ChatAdCount");
        FriendsAdCount = (int) mFirebaseRemoteConfig.getLong("FriendsAdCount");
        ProfileChatAdCount = (int) mFirebaseRemoteConfig.getLong("ProfileChatAdCount");
        CorePossibleOldFriendCount = (int) mFirebaseRemoteConfig.getLong("CorePossibleOldFriendCount");

        WtBigTitle = mFirebaseRemoteConfig.getString("WtBigTitle");
        WtTitleKo = mFirebaseRemoteConfig.getString("WtTitleKo");
        WtTitleEn = mFirebaseRemoteConfig.getString("WtTitleEn");
        WtSubKo = mFirebaseRemoteConfig.getString("WtSubKo");
        WtSubEn = mFirebaseRemoteConfig.getString("WtSubEn");

        MinAppVersion = (int) mFirebaseRemoteConfig.getLong("MinAppVersion");
        MaxAppVersion = (int) mFirebaseRemoteConfig.getLong("MaxAppVersion");

        CoreCloudItemId = mFirebaseRemoteConfig.getString("CoreCloudItemId");
        CorePlusItemId = mFirebaseRemoteConfig.getString("CorePlusItemId");

    }
}
