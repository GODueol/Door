package com.example.kwoncheolhyeok.core.CorePage;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.kwoncheolhyeok.core.Entity.CorePost;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.Camera.LoadPicture;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.example.kwoncheolhyeok.core.Util.UiUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;


/**
 * Created by Kwon on 2017-12-14.
 */

public class CoreWriteActivity extends AppCompatActivity {

    Toolbar toolbar = null;

    FloatingActionButton fab, picture_fab, audio_fab;
    LinearLayout audioFab_layout, pictureFab_layout;

    RelativeLayout image_edit_layout;
    View fabBGLayout;
    boolean isFABOpen = false;
    private static final int REQUEST_GALLERY = 2;
    private static final int REQUEST_RECORD = 0;

    ImageView editImage;
    ImageButton saveBtn, image_x_btn, sound_x_btn;
    private String mUuid;

    TextView textContents;
    private String cUuid;
    private String postKey;
    private String recordFilePath;
    private RecordSelectDialog recordSelectDialog;
    private HashMap<Task, OnSuccessListener> tasks;
    private boolean isEdit = true;
    private DatabaseReference postRef;
    private Uri editImageUri;
    private Uri soundUri;
    private StorageReference storageRef;
    private CorePost corePost;
    private RelativeLayout edit_audio_layout;

    private TextView textMaxTime;
    private TextView textCurrentPosition;
    private ToggleButton startAndPause;
    private SeekBar seekBar;
    private ViewTreeObserver vto;
    private Handler threadHandler = new Handler();

    private MediaPlayer mediaPlayer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.core_write_activity);
        storageRef = FirebaseStorage.getInstance().getReference();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        tasks = new HashMap<>();

        cUuid = getIntent().getStringExtra("cUuid");

        mUuid = DataContainer.getInstance().getUid();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        pictureFab_layout = findViewById(R.id.pictureFab_layout);
        audioFab_layout = findViewById(R.id.audioFab_layout);
        fab = findViewById(R.id.fab);
        picture_fab = findViewById(R.id.fab2);
        audio_fab = findViewById(R.id.fab3);
        fabBGLayout = findViewById(R.id.fabBGLayout);
        editImage = findViewById(R.id.edit_img);
        saveBtn = findViewById(R.id.save);
        textContents = findViewById(R.id.edit_txt);
        edit_audio_layout = findViewById(R.id.edit_audio_layout);
        image_x_btn = findViewById(R.id.image_x_btn);
        image_edit_layout = findViewById(R.id.image_edit_layout);

        textCurrentPosition = findViewById(R.id.textView_currentPosion);
        textMaxTime= findViewById(R.id.textView_maxTime);
        startAndPause = findViewById(R.id.button_start_pause);
        seekBar = findViewById(R.id.seekBar);

        sound_x_btn = findViewById(R.id.sound_x_btn);

        // 본인, 타인 구분
        if (!cUuid.equals(mUuid)) {    // 타인
            fab.setVisibility(View.GONE);
            editImage.setVisibility(View.GONE);
            edit_audio_layout.setVisibility(View.GONE);
            textContents.setHint("질문해주세요 익명입니다");
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        fabBGLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });

        // Get Picture Btn Set
        LoadPicture loadPicture = new LoadPicture(this, this);

        pictureFab_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 사진파일 가져오기 라이브러리 적용
                Album.image(CoreWriteActivity.this) // Image selection.
                        .singleChoice()
//                        .multipleChoice()
                        .requestCode(2)
                        .camera(false)
//                        .columnCount()
//                        .selectCount()
//                        .checkedList(mAlbumFiles)
//                        .filterSize(30) // Filter the file size.
//                        .filterMimeType() // Filter file format.
//                        .afterFilterVisibility() // Show the filtered files, but they are not available.
                        .onResult(new Action<ArrayList<AlbumFile>>() {
                            @Override
                            public void onAction(int requestCode, @NonNull ArrayList<AlbumFile> result) {
                                AlbumFile albumFile = result.get(0);

                                editImageUri = Uri.fromFile(new File(albumFile.getPath()));
                                editImage.setImageURI(editImageUri);   // Local Set
                                image_edit_layout.setVisibility(View.VISIBLE);
                                closeFABMenu();
                            }
                        })
                        .onCancel(new Action<String>() {
                            @Override
                            public void onAction(int requestCode, @NonNull String result) {
                            }
                        })
                        .start();

//                loadPicture.onGallery();
                pictureFab_layout.setClickable(false);

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveCore();
            }
        });

        audioFab_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 다이얼로그 녹음
                recordFilePath = Environment.getExternalStorageDirectory() + "/recorded_audio.wav";
                int color = getResources().getColor(R.color.white);
                int requestCode = 0;
                AndroidAudioRecorder.with(CoreWriteActivity.this)
                        // Required
                        .setFilePath(recordFilePath)
                        .setColor(color)
                        .setRequestCode(requestCode)

                        // Optional
                        .setSource(AudioSource.MIC)
                        .setChannel(AudioChannel.STEREO)
                        .setSampleRate(AudioSampleRate.HZ_48000)
                        .setAutoStart(true)
                        .setKeepDisplayOn(true)

                        // Start recording
                        .record();
            }
        });

        // edit 판별
        postKey = getIntent().getStringExtra("postKey");    // edit 일 경우 값이 있음
        if(postKey == null) {
            isEdit = false;
            postKey = mDatabase.child("posts").push().getKey();
        }
        postRef = mDatabase.child("posts").child(cUuid).child(postKey);

        // Edit인 경우 데이터 받아서 Set
        if (isEdit) {
            mDatabase.child("posts")
                    .child(cUuid).child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    corePost = dataSnapshot.getValue(CorePost.class);
                    if (corePost == null) return;
                    textContents.setText(corePost.getText());
                    if (editImage.getContext() == null) return;
                    if(corePost.getPictureUrl() != null) {
                        Glide.with(CoreWriteActivity.this).load(corePost.getPictureUrl()).into(editImage);
                        image_edit_layout.setVisibility(View.VISIBLE);
                    } else {
                        image_edit_layout.setVisibility(View.GONE);
                    }

                    if(corePost.getSoundUrl() != null){
                        mediaPlayer.reset();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        try {
                            mediaPlayer.setDataSource(corePost.getSoundUrl());
                            mediaPlayer.prepare(); // 필연적으로 지연됨 (버퍼채움)
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        edit_audio_layout.setVisibility(View.VISIBLE);
                    } else {
                        edit_audio_layout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        image_x_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 사진 삭제
                image_edit_layout.setVisibility(View.GONE);
                editImageUri = null;
            }
        });


        // set media player

        seekBar.setClickable(false);
        seekBar.setEnabled(false);

        mediaPlayer = new MediaPlayer();

        startAndPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    doStart();
                } else {
                    doPause();
                }
            }
        });

        sound_x_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 음성 삭제
                startAndPause.setChecked(false);
                edit_audio_layout.setVisibility(View.GONE);
                soundUri = null;
            }
        });

    }

    private void saveCore() {
        UiUtil.getInstance().startProgressDialog(CoreWriteActivity.this);

        if(corePost == null) corePost = new CorePost(mUuid);

        corePost.setText(textContents.getText().toString());
        Task<Void> postUploadTask = postRef.setValue(corePost);
        tasks.put(postUploadTask, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // addCorePostCount
                if (!isEdit) FireBaseUtil.getInstance().syncCorePostCount(cUuid);
            }
        });

        // image
        if(corePost.getPictureUrl() != null && image_edit_layout.getVisibility() == View.GONE) {
            // deletePicture
            deletePicture();
        } else if(editImageUri != null){
            uploadPicture();
        }

        // sound
        if(corePost.getSoundUrl() != null && edit_audio_layout.getVisibility() == View.GONE) {
            // deleteSound
            deleteSound();
        } else if(soundUri != null){
            uploadSound();
        }

        // 모든 비동기 호출이 다 끝낫을 때
        for (Task task : tasks.keySet()) {
            task.addOnSuccessListener(tasks.get(task));
            task.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task taskRtn) {
                    for (Task task : tasks.keySet())
                        if (!task.isComplete()) return;
                    setResult(Activity.RESULT_OK);
                    finish();
                    UiUtil.getInstance().stopProgressDialog();  // 프로그레스바 중단
                }
            });
        }
    }

    private void showFABMenu() {
        isFABOpen = true;
        audioFab_layout.setVisibility(View.VISIBLE);
        pictureFab_layout.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);

        fab.animate().rotation(135);
        fab.animate().setDuration(200);
        audioFab_layout.animate().translationY(-getResources().getDimension(R.dimen.standard_75));
        pictureFab_layout.animate().translationY(-getResources().getDimension(R.dimen.standard_150));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotation(0);
        fab.animate().setDuration(200);
        audioFab_layout.animate().translationY(0);
        pictureFab_layout.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    audioFab_layout.setVisibility(View.GONE);
                    pictureFab_layout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    // 뒤로가기 버튼 기능
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isFABOpen) {
            closeFABMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                editImageUri = data.getData();
                editImage.setImageURI(editImageUri);   // Local Set
                closeFABMenu();
            }
            if(requestCode == REQUEST_RECORD){
                // Great! User has recorded and saved the audio file
                // 파일 저장
                soundUri = Uri.fromFile(new File(recordFilePath));

                // mediaPlayer Set
                this.mediaPlayer.reset();
                this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    this.mediaPlayer.setDataSource(CoreWriteActivity.this, soundUri);
                    this.mediaPlayer.prepare(); // 필연적으로 지연됨 (버퍼채움)
                } catch (IOException e) {
                    e.printStackTrace();
                }
                edit_audio_layout.setVisibility(View.VISIBLE);

                closeFABMenu();
            }
        }
    }

    private void uploadPicture() {
        final StorageReference spaceRef = storageRef.child("posts").child(cUuid).child(postKey).child("picture");
        UploadTask uploadTask = spaceRef.putFile(editImageUri);
        tasks.put(uploadTask, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                postRef.child("pictureUrl").setValue(taskSnapshot.getDownloadUrl().toString());
            }
        });
    }

    private void deletePicture(){
        final StorageReference spaceRef = storageRef.child("posts").child(cUuid).child(postKey).child("picture");
        Task task = spaceRef.delete();
        tasks.put(task, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                postRef.child("pictureUrl").setValue(null);
            }
        });
    }

    private void uploadSound() {
        final StorageReference spaceRef = storageRef.child("posts").child(cUuid).child(postKey).child("sound");
        UploadTask uploadTask = spaceRef.putFile(soundUri);
        tasks.put(uploadTask, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                postRef.child("soundUrl").setValue(taskSnapshot.getDownloadUrl().toString());
            }
        });
    }

    private void deleteSound(){
        final StorageReference spaceRef = storageRef.child("posts").child(cUuid).child(postKey).child("sound");
        Task task = spaceRef.delete();
        tasks.put(task, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                postRef.child("soundUrl").setValue(null);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pictureFab_layout.setClickable(true);
    }

    // Convert millisecond to string.
    private String millisecondsToString(int milliseconds)  {
        long minutes = TimeUnit.MILLISECONDS.toMinutes((long) milliseconds);
        long seconds =  TimeUnit.MILLISECONDS.toSeconds((long) milliseconds) ;
        return minutes+":"+ seconds;
    }


    public void doStart()  {
        // The duration in milliseconds
        int duration = this.mediaPlayer.getDuration();

        int currentPosition = this.mediaPlayer.getCurrentPosition();

        if(currentPosition== 0)  {
            this.seekBar.setMax(duration);
            String maxTimeString = this.millisecondsToString(duration);
            this.textMaxTime.setText(maxTimeString);
        }

        this.mediaPlayer.start();
        // Create a thread to update position of SeekBar.
        UpdateSeekBarThread updateSeekBarThread= new UpdateSeekBarThread();
        threadHandler.postDelayed(updateSeekBarThread,50);

    }

    // Thread to Update position for SeekBar.
    class UpdateSeekBarThread implements Runnable {

        public void run()  {
            int currentPosition = mediaPlayer.getCurrentPosition();
            String currentPositionStr = millisecondsToString(currentPosition);
            textCurrentPosition.setText(currentPositionStr);

            if(CoreWriteActivity.this == null || textCurrentPosition.getText().equals(textMaxTime.getText())){
                // 사운드 재생 끝
                startAndPause.setChecked(false);  // 버튼 Stop
                textCurrentPosition.setText("0:0");
                seekBar.setProgress(0);   // SeekBar Init
                mediaPlayer.seekTo(0);
                return;
            }

            seekBar.setProgress(currentPosition);
            // Delay thread 50 milisecond.
            threadHandler.postDelayed(this, 50);
        }
    }

    // When user click to "Pause".
    public void doPause()  {
        this.mediaPlayer.pause();
    }

    // When user click to "Rewind".
    public void doRewind(View view)  {
        int currentPosition = this.mediaPlayer.getCurrentPosition();
        // 5 seconds.
        int SUBTRACT_TIME = 5000;

        if(currentPosition - SUBTRACT_TIME > 0 )  {
            this.mediaPlayer.seekTo(currentPosition - SUBTRACT_TIME);
        }
    }

    // When user click to "Fast-Forward".
    public void doFastForward(View view)  {
        int currentPosition = this.mediaPlayer.getCurrentPosition();
        int duration = this.mediaPlayer.getDuration();
        // 5 seconds.
        int ADD_TIME = 5000;

        if(currentPosition + ADD_TIME < duration)  {
            this.mediaPlayer.seekTo(currentPosition + ADD_TIME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        startAndPause.setChecked(false);
    }
}