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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_alarm_activity_main);


    }

}