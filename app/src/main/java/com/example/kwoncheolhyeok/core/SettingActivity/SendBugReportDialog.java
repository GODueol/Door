package com.example.kwoncheolhyeok.core.SettingActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.example.kwoncheolhyeok.core.CorePage.CustomDialog;
import com.example.kwoncheolhyeok.core.R;

/**
 * Created by Kwon on 2018-03-16.
 */

public class SendBugReportDialog extends CustomDialog {

    interface SendBugReportDialogListener {
        void show_sendBugReport_dialog();
    }

    private SendBugReportDialog.SendBugReportDialogListener SendBugReportDialogListener;

    SendBugReportDialog(@NonNull Context context, SendBugReportDialog.SendBugReportDialogListener SendBugReportDialogListener) {
        super(context);
        this.SendBugReportDialogListener = SendBugReportDialogListener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_send_bugreport);


    }

}