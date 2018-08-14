package com.teamdoor.android.door.Chatting;

import android.support.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamdoor.android.door.Entity.MessageVO;
import com.teamdoor.android.door.Entity.RoomVO;
import com.teamdoor.android.door.Util.RemoteConfig;
import com.teamdoor.android.door.Util.SharedPreferencesUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChattingPresenter implements ChattingContract.Presenter {
    private final static String chatRoomList = "chatRoomList";
    private final static String chat = "chat";
    private final static String image = "image";
    private final static String strDelete = "DELETE";

    private SharedPreferencesUtil SPUtil;
    ChattingContract.View mChattingView;
    private DatabaseReference databaseRef;

    ChattingPresenter(ChattingContract.View view, SharedPreferencesUtil SPUtil) {
        view.setPresenter(this);

        mChattingView = view;
        this.SPUtil = SPUtil;

        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void setChattingRoom(String userUuid, String targetUuid) {

    }


    @Override
    public void getChatLog() {

    }

    @Override
    public void start() {

    }
}
