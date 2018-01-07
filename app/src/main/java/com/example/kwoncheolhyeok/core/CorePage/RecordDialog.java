package com.example.kwoncheolhyeok.core.CorePage;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.kwoncheolhyeok.core.R;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by kimbyeongin on 2018-01-06.
 */

public class RecordDialog extends CustomDialog {

    private MediaRecorder recorder;
    private String recordFilePath;
    private TextView textMaxTime;
    private TextView textCurrentPosition;
    private ImageButton buttonPause;
    private ImageButton buttonStart;
    private SeekBar seekBar;
    private Handler threadHandler = new Handler();

    private MediaPlayer mediaPlayer;
    private RelativeLayout relativeLayout;


    public RecordDialog(@NonNull Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_record);

//        sizeSet();

        textCurrentPosition = findViewById(R.id.textView_currentPosion);
        textMaxTime= findViewById(R.id.textView_maxTime);
        buttonStart= findViewById(R.id.button_start);
        buttonPause= findViewById(R.id.button_pause);
        buttonPause.setEnabled(false);
        seekBar= findViewById(R.id.seekBar);
        seekBar.setClickable(false);

        relativeLayout = findViewById(R.id.relativeLayout);
        relativeLayout.setVisibility(View.GONE);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doStart(view);
            }
        });

        findViewById(R.id.button_rewind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRewind(view);
            }
        });

        findViewById(R.id.button_pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doPause(view);
            }
        });

        findViewById(R.id.button_fastForward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doFastForward(view);
            }
        });


        // ID of 'mysong' in 'raw' folder.
//        int songId = getRawResIdByName("sample");

        // Create MediaPlayer.
        mediaPlayer= new MediaPlayer();//  MediaPlayer.create(getContext(), songId);

        // 녹음 파일 경로
        File storageDir = Environment.getExternalStorageDirectory();
        recordFilePath = storageDir.getAbsolutePath() + "/" + "recoder.mp3";
        setDataSource();

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
    }

    public void startRec() {

        try {
            relativeLayout.setVisibility(View.GONE);
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC); //마이크 녹음
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); //파일타입 설정 (녹음파일의경우 3gp가 용량 작고 효율적)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);    // 코덱 설정
            recorder.setOutputFile(recordFilePath);   //저장될 파일 설정
            recorder.prepare();
            recorder.start();   // 녹음 시작
            Toast.makeText(getContext(), "start Record", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRec() {
        if(recorder == null) return;
        recorder.stop();
        recorder.release();
        Toast.makeText(getContext(), "stop Record", Toast.LENGTH_LONG).show();
        relativeLayout.setVisibility(View.VISIBLE);
        setDataSource();
//        mediaPlayer.reset();
    }

    // Find ID of resource in 'raw' folder.
    public int getRawResIdByName(String resName)  {
        String pkgName = getContext().getPackageName();
        // Return 0 if not found.
        int resID = getContext().getResources().getIdentifier(resName, "raw", pkgName);
        return resID;
    }

    // Convert millisecond to string.
    private String millisecondsToString(int milliseconds)  {
        long minutes = TimeUnit.MILLISECONDS.toMinutes((long) milliseconds);
        long seconds =  TimeUnit.MILLISECONDS.toSeconds((long) milliseconds) ;
        return minutes+":"+ seconds;
    }


    public void doStart(View view)  {
        // The duration in milliseconds
        int duration = mediaPlayer.getDuration();

        int currentPosition = mediaPlayer.getCurrentPosition();
        if(currentPosition== 0)  {
            seekBar.setMax(duration);
            String maxTimeString = millisecondsToString(duration);
            textMaxTime.setText(maxTimeString);
        } else if(currentPosition== duration)  {
            // Resets the MediaPlayer to its uninitialized state.
            mediaPlayer.reset();
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        mediaPlayer.start();

        // Create a thread to update position of SeekBar.
        UpdateSeekBarThread updateSeekBarThread= new UpdateSeekBarThread();
        threadHandler.postDelayed(updateSeekBarThread,50);

        buttonPause.setEnabled(true);
        buttonStart.setEnabled(false);
    }

    private void setDataSource() {
        try {

            if(recordFilePath.equals("")){
                Toast.makeText(getContext(), "File not exist", Toast.LENGTH_SHORT).show();
                return;
            }
            mediaPlayer.reset(); // mp객체를 초기화합니다.
            mediaPlayer.setDataSource(recordFilePath);
            mediaPlayer.prepare();
//            mediaPlayer.start();
            Toast.makeText(getContext(), "재생할 파일 " + recordFilePath, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Thread to Update position for SeekBar.
    class UpdateSeekBarThread implements Runnable {

        public void run()  {
            int currentPosition = mediaPlayer.getCurrentPosition();
            String currentPositionStr = millisecondsToString(currentPosition);
            textCurrentPosition.setText(currentPositionStr);

            seekBar.setProgress(currentPosition);


            Log.d("kbj", "currentPosition : " + currentPosition + ", mediaPlayer.getDuration() : " + mediaPlayer.getDuration());

            Log.d("kbj", "mediaPlayer.isPlaying() : " + mediaPlayer.isPlaying() + ", mediaPlayer.isLooping() : " + mediaPlayer.isLooping());

            if(!mediaPlayer.isPlaying()){
                // reset
                Log.d("kbj!!!!", "currentPosition : " + currentPosition);
//                mediaPlayer.reset();

                setDataSource();
                return;
            }

            // Delay thread 50 milisecond.
            threadHandler.postDelayed(this, 50);
        }

    }

    // When user click to "Pause".
    public void doPause(View view)  {
        mediaPlayer.pause();
        buttonPause.setEnabled(false);
        buttonStart.setEnabled(true);
    }

    // When user click to "Rewind".
    public void doRewind(View view)  {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();
        // 5 seconds.
        int SUBTRACT_TIME = 5000;

        if(currentPosition - SUBTRACT_TIME > 0 )  {
            mediaPlayer.seekTo(currentPosition - SUBTRACT_TIME);
        }
    }

    // When user click to "Fast-Forward".
    public void doFastForward(View view)  {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();
        // 5 seconds.
        int ADD_TIME = 5000;

        if(currentPosition + ADD_TIME < duration)  {
            mediaPlayer.seekTo(currentPosition + ADD_TIME);
        }
    }
}
