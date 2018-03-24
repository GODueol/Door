package com.example.kwoncheolhyeok.core.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

import com.example.kwoncheolhyeok.core.CorePage.CustomDialog;
import com.example.kwoncheolhyeok.core.PeopleFragment.BlockReportSelectDialog;
import com.example.kwoncheolhyeok.core.R;

/**
 * Created by Kwon on 2018-03-16.
 */

public class NewAlarmDialog extends CustomDialog {

    interface NewAlarmDialogListener {
        void show_alarm_dialog();
    }

    private NewAlarmDialog.NewAlarmDialogListener NewAlarmDialogListener;

    NewAlarmDialog(@NonNull Context context, NewAlarmDialog.NewAlarmDialogListener NewAlarmDialogListener) {
        super(context);
        this.NewAlarmDialogListener = NewAlarmDialogListener;
    }

    // 포스트 아이디로 받아온다면? (
    // 1. 내 코어에 누군가가 익명포스트를 남겼을떄
    // 2. 다른사람 코어의 익명포스트에 답변이 달렸을때
    // 3. 내 코어의 내글 나의 좋아요가 눌렸을떄

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_alarm_activity_main);
    }

}