package com.example.kwoncheolhyeok.core.CorePage;

import android.app.Activity;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.R;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;

/**
 * Created by kimbyeongin on 2018-01-06.
 */

public class RecordSelectDialog extends CustomDialog {

    private View.OnClickListener onRecordBtnClickListener;
    private View.OnClickListener onFileBtnClickListener;

    public RecordSelectDialog(@NonNull Context context) {
        super(context);
    }

    public RecordSelectDialog(@NonNull Context context, View.OnClickListener onRecordBtnClickListener, View.OnClickListener onFileBtnClickListener) {
        super(context);
        this.onRecordBtnClickListener = onRecordBtnClickListener;
        this.onFileBtnClickListener = onFileBtnClickListener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_select_record);

//        sizeSet();

        TextView recordBtn = findViewById(R.id.record);
        TextView fileBtn = findViewById(R.id.getByFile);

        recordBtn.setOnClickListener(onRecordBtnClickListener);

        fileBtn.setOnClickListener(onFileBtnClickListener);

    }
}
