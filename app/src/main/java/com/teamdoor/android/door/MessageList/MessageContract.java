package com.teamdoor.android.door.MessageList;

import android.os.Bundle;

import com.teamdoor.android.door.Entity.RoomVO;
import com.teamdoor.android.door.Entity.User;
import com.teamdoor.android.door.Util.BaseActivity.BasePresenter;
import com.teamdoor.android.door.Util.BaseActivity.BaseView;

import java.util.List;

// 메세지 보여주기
// 클릭하면 -> 채팅으로 넘어가기
// 뱃지
public interface MessageContract {

    interface View extends BaseView<Presenter> {
        // 메세지리스트뷰 새로고침
        void refreshMessageListView();

        // 채팅방activity 시작
        void startChattingActivity(Bundle bundle);

        // context접근 String
        String getResourceBadge();
        // context접근 String
        String getResourceTeamCore();
    }

    interface Presenter extends BasePresenter {
        void setListItem(List<RoomVO> listrowItem);

        // 메세지 리스트 가져오기
        void setMessageList();

        // 처음 가져올때 데이터 셋팅
        void realTimeMessageListChange(User target, RoomVO roomList);

        // 존재하던 리스트가 변경되었을때
        void realTimeMessageListChange(User target, RoomVO roomList, boolean changeFlag);

        // 접속할 채팅방이 접근가능한지 판단
        void enterChatRoom(RoomVO item);

        // 메세지 리스트 지우기
        void removeMessageList(String target);
    }
}
