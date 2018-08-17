package com.teamdoor.android.door.Chatting;

import android.location.Location;

import com.teamdoor.android.door.Entity.ChatMessage;
import com.teamdoor.android.door.Entity.MessageVO;
import com.teamdoor.android.door.Entity.User;
import com.teamdoor.android.door.PeopleFragment.GridItem;
import com.teamdoor.android.door.Util.BaseActivity.BasePresenter;
import com.teamdoor.android.door.Util.BaseActivity.BaseView;
import com.teamdoor.android.door.Util.GalleryPick;

import java.util.List;

import io.reactivex.Observable;

public interface ChattingContract {

    interface View extends BaseView<Presenter> {
        void ToastMessage(String msg);

        // 변조되지 않은 시스템 시간 가져오기
        Long getCleanTime();

        // 채팅 리스트 새로고침
        void refreshChatLogView();

        // 메세지 송신시 스크롤 컨트롤
        void setScrollControl(String message, boolean isMine, boolean isImage);

        // 스크롤 set
        void scrollToPosition(int position);

        // 리스트 보여지는 첫번째 아이템 포지션
        int findFirstCompletelyVisibleItemPosition();

        // 리스트 보여지는 마지막 아이템 포지션
        int findLastCompletelyVisibleItemPosition();

        Location getLocation();

        // context접근 String
        String getResourceAlert();

        void finish();

        List<ChatMessage> getChatList();

        List<String> getChatKeyList();

        List<ChatMessage> getUnCheckList();

        /********************임시*******************/
        GridItem getGridItem();

        void setGridItemDistance(float distance);
    }

    interface Presenter extends BasePresenter {
        // 메세지 룸 초기화
        Observable<String> setchatRoom(String userUuid, String targetUuid);

        // 읽은 메세지 처리
        void checkReadChat(String Room, String userUuid);

        // 채팅 로그를 가져옴
        void getChattingLog(String Room, String userUuid);

        // 상대방 정보 가져오기
        GridItem setUserInfo(User targetUser, String targetUuid);

        // 메세지 보내기
        void sendMessage(String Room, String id, String userUuid, String targetUuid, MessageVO message);

        // 이미지 메세지 보내기
        void sendImageMessage(String Room, String id, String userUuid, String targetUuid, GalleryPick galleryPick);

        // 메세지 지우기
        void removeImeageMessage(String Room, final String parent, final int position);

        // 채팅 지우기
        void clearChatLog(String Room, String userUuid, String targetUuid);

        // 마지막 채팅을 본시간 갱신
        void setLastChatView(String userUuid, String targetUuid);

        // 과거 채팅기록 가져오기(페이징)
        void getPastChattingLog(String Room, String userUuid);

        // disposable disepose
        void removeDisposable();
    }
}
