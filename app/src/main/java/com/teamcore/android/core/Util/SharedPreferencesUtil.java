package com.teamcore.android.core.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.ads.InterstitialAd;
import com.teamcore.android.core.Exception.NotSetAutoTimeException;
import com.teamcore.android.core.R;

/**
 * Created by Administrator on 2018-03-08.
 */

public class SharedPreferencesUtil {
    private Context context;

    private SharedPreferences sharedPref;
    private SharedPreferences sharedPref_chatListBadge;
    private SharedPreferences sharedPref_badge;
    private SharedPreferences sharedPref_friends;
    private SharedPreferences sharedPref_notice;

    private SharedPreferences sharedPref_ad;

    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor editor_badge;
    private SharedPreferences.Editor editor_friends;
    private SharedPreferences.Editor editor_chatListBadge;
    private SharedPreferences.Editor editor_notice;
    private SharedPreferences.Editor editor_ad;

    @SuppressLint("CommitPrefEdits")
    public SharedPreferencesUtil(Context context) {
        this.context = context;
        sharedPref = context.getSharedPreferences(context.getString(R.string.alarm), Context.MODE_PRIVATE);
        sharedPref_badge = context.getSharedPreferences(context.getString(R.string.badge), Context.MODE_PRIVATE);
        sharedPref_chatListBadge = context.getSharedPreferences(context.getString(R.string.chatListBadge), Context.MODE_PRIVATE);
        sharedPref_friends = context.getSharedPreferences(context.getString(R.string.friends), Context.MODE_PRIVATE);
        sharedPref_notice = context.getSharedPreferences(context.getString(R.string.notice), Context.MODE_PRIVATE);
        sharedPref_ad = context.getSharedPreferences("ads", Context.MODE_PRIVATE);

        editor = sharedPref.edit();
        editor_badge = sharedPref_badge.edit();
        editor_friends = sharedPref_friends.edit();
        editor_chatListBadge = sharedPref_chatListBadge.edit();
        editor_notice = sharedPref_notice.edit();
        editor_ad = sharedPref_ad.edit();
    }


    public SharedPreferences getBadgePreferences() {
        return sharedPref_badge;
    }

    public SharedPreferences getChatListPreferences(){
        return sharedPref_chatListBadge;
    }

    public void setBlockMeUserCurrentActivity(String str,String uuid){
     editor.putString(str,uuid).apply();
    }

    public String getBlockMeUserCurrentActivity(String str){
        return sharedPref.getString(str,"");
    }

    // 네비 알람 버튼
    public void setAlarmIcon(String str, boolean b) {
        editor_badge.putBoolean(str, b).apply();
    }

    // 네비 알람 버튼
    public boolean getAlarmIcon(String str) {
        return sharedPref_badge.getBoolean(str, false);
    }


    // 메인 토글버튼
    public void setMainIcon(String str, boolean b) {
        editor_badge.putBoolean(str, b).apply();
    }

    // 메인 토글 버튼
    public boolean getMainIcon(String str) {
        return sharedPref_badge.getBoolean(str, false);
    }

    // 알림 스위치 버튼
    public void setSwitchState(String str, boolean isCheck) {
        editor.putBoolean(str, isCheck).apply();
    }

    // 알림 스위치 버튼
    public boolean getSwitchState(String str) {
        return sharedPref.getBoolean(str, true);
    }

    // 뱃지
    public int getBadgeCount(String str) {
        return sharedPref_badge.getInt(str, 0);
    }

    // 뱃지
    public void setBadgeCount(String str,int i) {
        editor_badge.putInt(str, i).apply();
    }

    public boolean getBadgeState(String str){
        return sharedPref_badge.getBoolean(str,false);
    }
    // 뱃지 ++
    public void increaseBadgeCount(String str) {
        // 트렌젝션 문제는 없는지?
        int badgeCount = sharedPref_badge.getInt(str, 0);
        editor_badge.putInt(str, ++badgeCount).apply();
        editor_badge.putBoolean("mainAlarm",true).apply();
        // 프렌즈 관련 뱃지인경우 프랜즈뱃지도 업데이트
        if (str.equals("badgeFriend") || str.equals("badgeFollow") || str.equals("badgeFollowing")) {
            int badgeFriends = getBadgeCount("badgeFriend") + getBadgeCount("badgeFollow") + getBadgeCount("badgeFollowing");
            editor_badge.putInt("badgeFriends",badgeFriends).apply();
        }
    }
    // 스위치 뱃지
    public void switchBadgeState(String str,boolean b){
        editor_badge.putBoolean(str,b).apply();
        // 뱃지가 ture가 되면 메인뱃지 변경
        if(b) {
            editor_badge.putBoolean("mainAlarm", true).apply();
        }
    }

    // 뱃지 삭제
    public void removeBadge(String str) {
        editor_badge.remove(str).apply();
    }

    // 프렌즈 통합뱃지
    public void removeFriendsBadge(String str) {
        String badge = context.getString(R.string.badgeFriends);
        int badgeCount = getBadgeCount(badge);
        badgeCount -= sharedPref_badge.getInt(str,0);
        editor_badge.remove(str).apply();
        editor_badge.putInt(badge,badgeCount).apply();
    }

    // 채팅방 별 뱃지 갯수
    public int getChatRoomBadge(String str) {
        return sharedPref_chatListBadge.getInt(str, 0);
    }

    // 각 채팅방 별개 뱃지
    public void increaseChatRoomBadge(String str) {
        int badgeCount = sharedPref_chatListBadge.getInt(str, 0);
        editor_chatListBadge.putInt(str, ++badgeCount).apply();
        increaseBadgeCount(context.getString(R.string.badgeChat));
    }

    // messagebadge 통합 뱃지
    public void removeChatRoomBadge(String str) {
        String badge = context.getString(R.string.badgeChat);
        int badgeCount = getBadgeCount(badge);
        badgeCount -= sharedPref_chatListBadge.getInt(str, 0);
        editor_chatListBadge.remove(str).apply();
        editor_badge.putInt(badge, badgeCount).apply();
    }

    // 현재 접속한 채팅방
    public String getCurrentChat() {
        return sharedPref.getString(context.getString(R.string.currentRoom), "");
    }

    // 현재 접속한 채팅방 설정
    public void setCurrentChat(String str, String room) {
        editor.putString(str, room).apply();
    }

    // 현재 접속한 채팅방 제거
    public void removeCurrentChat(String str) {
        editor.remove(str).apply();
    }

    // 공지 읽었는지 여부
    public boolean isNoticeRead(String key) {
        return sharedPref_notice.getBoolean(key, false);
    }

    // 공지 읽었음 저장
    public void putNoticeRead(String key) {
        editor_notice.putBoolean(key, true).apply();
    }

    // 코어 공지 가능 여부
    public boolean isCoreNoticePossible(Context context) throws NotSetAutoTimeException {
        return isPossibleView(sharedPref_notice.getLong("coreNoticeCheckDate", -1));
    }

    // 코어 공지 가능 여부
    public boolean isWeeklyTopicPossible(Context context) throws NotSetAutoTimeException {
        return isPossibleView(sharedPref_notice.getLong("weeklyTopicDate", -1));
    }

    private boolean isPossibleView(long checkDate) throws NotSetAutoTimeException {
        long currentDate = UiUtil.getInstance().getCurrentTime(context);
        return checkDate == -1 || currentDate - checkDate > DataContainer.SecToDay;
    }

    // 코어 공지 읽은 날짜 저장
    public void putCoreNoticeCheck(Context context) throws NotSetAutoTimeException {
        editor_notice.putLong("coreNoticeCheckDate", UiUtil.getInstance().getCurrentTime(context)).apply();
    }

    // 코어 공지 읽은 날짜 저장
    public void putWeeklyTopicCheck(Context context) throws NotSetAutoTimeException {
        editor_notice.putLong("weeklyTopicDate", UiUtil.getInstance().getCurrentTime(context)).apply();
    }

    // 처음 접속시 광고 횟수 초기화
    public void initAds() {
        editor_ad.putInt("mainGrid", 0).apply();
        editor_ad.putInt("chat", 0).apply();
        editor_ad.putInt("friends", 0).apply();
        editor_ad.putInt("profileChat", 0).apply();
    }
    // 각 채팅방 별개 뱃지
    public void increaseAds(InterstitialAd mInterstitialAd, String str) {
        int now = sharedPref_ad.getInt(str, 0);
        int count=0;

        switch (str){
            case "FMainGrid" :
                count=RemoteConfig.MainGridAdCount;
                break;
            case "ChatList" :
                count=RemoteConfig.ChatAdCount;
                break;
            case "Friends" :
                count=RemoteConfig.FriendsAdCount;
                break;
            case "ProfileChat" :
                count=RemoteConfig.ProfileChatAdCount;
                break;
        }

        if(now>=count) {
            if(mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                editor_ad.putInt(str, 0).apply();
            }else{
                editor_ad.putInt(str, ++now).apply();
            }
        }else{
            editor_ad.putInt(str, ++now).apply();
        }
    }

}
