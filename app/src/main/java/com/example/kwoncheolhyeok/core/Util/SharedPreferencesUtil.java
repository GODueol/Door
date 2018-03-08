package com.example.kwoncheolhyeok.core.Util;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.Context;
import com.example.kwoncheolhyeok.core.R;

/**
 * Created by Administrator on 2018-03-08.
 */

public class SharedPreferencesUtil {
    private Context context;

    private SharedPreferences sharedPref;
    private SharedPreferences sharedPref_chatListBadge;
    private SharedPreferences sharedPref_badge;
    private SharedPreferences sharedPref_friends;

    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor editor_badge;
    private SharedPreferences.Editor editor_friends;
    private SharedPreferences.Editor editor_chatListBadge;

    @SuppressLint("CommitPrefEdits")
    public SharedPreferencesUtil(Context context){
        this.context = context;
        sharedPref = context.getSharedPreferences(context.getString(R.string.alarm), Context.MODE_PRIVATE);
        sharedPref_badge = context.getSharedPreferences(context.getString(R.string.badge), Context.MODE_PRIVATE);
        sharedPref_chatListBadge = context.getSharedPreferences(context.getString(R.string.chatListBadge), Context.MODE_PRIVATE);
        sharedPref_friends = context.getSharedPreferences(context.getString(R.string.friends), Context.MODE_PRIVATE);

        editor = sharedPref.edit();
        editor_badge = sharedPref_badge.edit();
        editor_friends = sharedPref_friends.edit();
        editor_chatListBadge = sharedPref_chatListBadge.edit();
    }

    public SharedPreferences getPreferences(){
        return sharedPref;
    }

    public SharedPreferences getBadgePreferences(){
        return sharedPref_badge;
    }

    public SharedPreferences getfriendsPreferences(){
        return sharedPref_friends;
    }

    // 메인 토글버튼
    public void setMainIcon(String str,boolean b){
        editor.putBoolean(str,b).apply();
    }
    // 메인 토글 버튼
    public boolean getMainIcon(String str){
        return sharedPref.getBoolean(str,false);
    }

    // 알림 스위치 버튼
    public void setSwitchState(String str,boolean isCheck){
        editor.putBoolean(str, isCheck).apply();
    }
    // 알림 스위치 버튼
    public boolean getSwitchState(String str){
        return sharedPref.getBoolean(str, true);
    }

    // 뱃지
    public int getBadgeCount(String str){
        return sharedPref_badge.getInt(str,0);
    }
    // 뱃지 ++
    public void increaseCount(String str){
        // 트렌젝션 문제는 없는지?
        int badgeCount = sharedPref_badge.getInt(str,0);
        editor_badge.putInt(str, ++badgeCount).apply();
    }
    // 뱃지 삭제
    public void removeBadge(String str){
        editor_badge.remove(str).apply();
    }

    public int getChatRoomBadge(String str){
        return sharedPref_chatListBadge.getInt(str,0);
    }

    public void increaseChatRoomBadge(String str){
        int badgeCount = sharedPref_chatListBadge.getInt(str,0);
        editor_chatListBadge.putInt(str,++badgeCount).apply();
        increaseCount(context.getString(R.string.badgeChat));
    }
    public void removeChatRoomBadge(String str){
        String badge = context.getString(R.string.badgeChat);
        int badgeCount = getBadgeCount(badge);
        badgeCount -= sharedPref_chatListBadge.getInt(str,0);
        editor_chatListBadge.remove(str).apply();
        editor_badge.putInt(badge,badgeCount).apply();
    }

    // 현재 접속한 채팅방
    public String getCurrentChat(){
        return sharedPref.getString(context.getString(R.string.currentRoom),"");
    }
    // 현재 접속한 채팅방 설정
    public void setCurrentChat(String str,String room){
        editor.putString(str,room).apply();
    }
    // 현재 접속한 채팅방 제거
    public void removeCurrentChat(String str){
        editor.remove(str).apply();
    }
}
