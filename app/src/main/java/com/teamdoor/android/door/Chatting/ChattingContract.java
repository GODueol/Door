package com.teamdoor.android.door.Chatting;

import com.teamdoor.android.door.Util.BaseActivity.BasePresenter;
import com.teamdoor.android.door.Util.BaseActivity.BaseView;

public interface ChattingContract {

    interface View extends BaseView<Presenter> {
        void refreshChatLogView();

        // context접근 String
        String getResourceCurrentRoom();
    };

    interface Presenter extends BasePresenter {
        void setChattingRoom(String userUuid, String targetUuid);
        void getChatLog();
    }
}
