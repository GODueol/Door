package com.example.kwoncheolhyeok.core.CorePage;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.kwoncheolhyeok.core.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by kimbyeongin on 2018-01-06.
 */

public class RecordDialog extends CustomDialog {

    private MediaRecorder recorder;

    public RecordDialog(@NonNull Context context) {
        super(context);
    }

    public RecordDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected RecordDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public RecordDialog(Context context, String title,
                        View.OnClickListener singleListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        /*
        this.mTitle = title;
        this.mLeftClickListener = singleListener;
        */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_record);

        // Set Btn
        recorder = new MediaRecorder();
        ToggleButton recordBtn = findViewById(R.id.recordButton);
        recordBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    startRec();
                } else {
                    stopRec();
                }
            }
        });
/*

        mTitleView = (TextView) findViewById(R.id.txt_title);
        mContentView = (TextView) findViewById(R.id.txt_content);
        mLeftButton = (Button) findViewById(R.id.btn_left);
        mRightButton = (Button) findViewById(R.id.btn_right);

        // 제목과 내용을 생성자에서 셋팅한다.
        mTitleView.setText(mTitle);
        mContentView.setText(mContent);

        // 클릭 이벤트 셋팅
        if (mLeftClickListener != null && mRightClickListener != null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
            mRightButton.setOnClickListener(mRightClickListener);
        } else if (mLeftClickListener != null
                && mRightClickListener == null) {
            mLeftButton.setOnClickListener(mLeftClickListener);
        } else {

        }
*/

    }


    public void startRec() {

        try {
            File file = Environment.getExternalStorageDirectory();
            //갤럭시 S4기준으로 /storage/emulated/0/의 경로를 갖고 시작한다.
            String path = file.getAbsolutePath() + "/" + "recoder.mp3";

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //첫번째로 어떤 것으로 녹음할것인가를 설정한다. 마이크로 녹음을 할것이기에 MIC로 설정한다.
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            //이것은 파일타입을 설정한다. 녹음파일의경우 3gp로해야 용량도 작고 효율적인 녹음기를 개발할 수있다.
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            //이것은 코덱을 설정하는 것이라고 생각하면된다.
            recorder.setOutputFile(path);
            //저장될 파일을 저장한뒤
            recorder.prepare();
            recorder.start();
            //시작하면된다.
            Toast.makeText(getContext(), "start Record", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void stopRec() {
        recorder.stop();
        //멈추는 것이다.
        recorder.release();
        Toast.makeText(getContext(), "stop Record", Toast.LENGTH_LONG).show();
    }
}
