package com.example.kwoncheolhyeok.core.Util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gimbyeongjin on 2017. 8. 14..
 */

public class DataContainer {
    public static final String[] bodyTypes =  {"Underweight", "Skinny", "Standard", "Muscular", "Overweight"};
    @SuppressLint("SimpleDateFormat") public static final SimpleDateFormat commonDateFormat = new SimpleDateFormat("yy.MM.dd HH:mm");

    private static final DataContainer ourInstance = new DataContainer();

    public static DataContainer getInstance() {
        return ourInstance;
    }

    private DataContainer() {
    }

    User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public DatabaseReference getUserRef(String uuid){
        return getUsersRef().child(uuid);
    }

    public DatabaseReference getMyUserRef(){
        return getUserRef(getUid());
    }

    public DatabaseReference getUsersRef(){
        return FirebaseDatabase.getInstance().getReference("users");
    }

    public int convertBeforeHour(long longDate){
        long diff = System.currentTimeMillis() - longDate;

        return (int) ( diff / (24 * 60 * 1000) );
    }

    private List<String> scanDeviceForMp3Files(Context context){
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };
        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";
        List<String> mp3Files = new ArrayList<>();

        Cursor cursor = null;
        try {
            Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            cursor = context.getContentResolver().query(uri, projection, selection, null, sortOrder);
            if( cursor != null){
                cursor.moveToFirst();

                while( !cursor.isAfterLast() ){
                    String title = cursor.getString(0);
                    String artist = cursor.getString(1);
                    String path = cursor.getString(2);
                    String displayName  = cursor.getString(3);
                    String songDuration = cursor.getString(4);
                    cursor.moveToNext();
                    if(path != null && path.endsWith(".mp3")) {
                        mp3Files.add(path);
                    }
                }

            }

            // print to see list of mp3 files
            for( String file : mp3Files) {
                Log.i("Audio", file);
            }

        } catch (Exception e) {
            Log.e("Audio", e.toString());
        }finally{
            if( cursor != null){
                cursor.close();
            }
        }
        return mp3Files;
    }
}
