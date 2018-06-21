package com.teamcore.android.core.CorePage;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
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
import com.squareup.otto.Subscribe;
import com.teamcore.android.core.Entity.CorePost;
import com.teamcore.android.core.Event.TargetUserBlocksMeEvent;
import com.teamcore.android.core.Exception.NotSetAutoTimeException;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.AlarmUtil;
import com.teamcore.android.core.Util.BaseActivity.BlockBaseActivity;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.FireBaseUtil;
import com.teamcore.android.core.Util.GalleryPick;
import com.teamcore.android.core.Util.UiUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;
import cafe.adriel.androidaudiorecorder.model.AudioSource;


/**
 * Created by Kwon on 2017-12-14.
 */

public class CoreWriteActivity extends BlockBaseActivity {

    Toolbar toolbar = null;

    FloatingActionButton fab, picture_fab, audio_fab;
    LinearLayout audioFab_layout, pictureFab_layout;

    RelativeLayout image_edit_layout;
    View fabBGLayout;
    boolean isFABOpen = false;
    private static final int REQUEST_RECORD = 0;

    ImageView editImage;
    ImageButton saveBtn, image_x_btn, sound_x_btn;
    private String mUuid;

    TextView textContents;
    private String cUuid;
    private String postKey;
    private String recordFilePath;
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
    private Handler threadHandler = new Handler();

    private MediaPlayer mediaPlayer;
    private GalleryPick galleryPick;

    private RewardedVideoAd mRewardedVideoAd;

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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        pictureFab_layout = (LinearLayout) findViewById(R.id.pictureFab_layout);
        audioFab_layout = (LinearLayout) findViewById(R.id.audioFab_layout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        picture_fab = (FloatingActionButton) findViewById(R.id.fab2);
        audio_fab = (FloatingActionButton) findViewById(R.id.fab3);
        fabBGLayout = findViewById(R.id.fabBGLayout);
        editImage = (ImageView) findViewById(R.id.edit_img);
        saveBtn = (ImageButton) findViewById(R.id.save);
        textContents = (TextView) findViewById(R.id.edit_txt);
        edit_audio_layout = (RelativeLayout) findViewById(R.id.edit_audio_layout);
        image_x_btn = (ImageButton) findViewById(R.id.image_x_btn);
        image_edit_layout = (RelativeLayout) findViewById(R.id.image_edit_layout);

        textCurrentPosition = (TextView) findViewById(R.id.textView_currentPosion);
        textMaxTime = (TextView) findViewById(R.id.textView_maxTime);
        startAndPause = (ToggleButton) findViewById(R.id.button_start_pause);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        sound_x_btn = (ImageButton) findViewById(R.id.sound_x_btn);

        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        loadRewardedVideoAd();
        // 본인, 타인 구분
        if (isAnonymousPost()) {    // 타인
            fab.setVisibility(View.GONE);
            editImage.setVisibility(View.GONE);
            edit_audio_layout.setVisibility(View.GONE);
            textContents.setHint("익명으로 질문을 남깁니다. 매너를 지켜주세요:)");
        }

        fab.setOnClickListener(view -> {
            if (!isFABOpen) {
                showFABMenu();
            } else {
                closeFABMenu();
            }
        });

        fabBGLayout.setOnClickListener(view -> closeFABMenu());

        // Get Picture Btn Set
        pictureFab_layout.setOnClickListener(view -> {
            galleryPick = new GalleryPick(CoreWriteActivity.this).goToGallery();
            pictureFab_layout.setClickable(false);
        });

        saveBtn.setOnClickListener(view -> {

            // 차단관계인 경우 불가능
            if (DataContainer.getInstance().getUser().getBlockMeUsers().containsKey(cUuid)) {// 차단
                Toast.makeText(CoreWriteActivity.this, "차단으로 인해 포스트 업로드가 불가능합니다", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // 답변 검사
            FirebaseDatabase.getInstance().getReference().child("posts").child(cUuid).child(postKey).child("reply").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // 답변이 달린경우
                    if (dataSnapshot.getValue() != null) {
                        if (isAnonymousPost()) {
                            Toast.makeText(CoreWriteActivity.this, "당신의 질문에 답변이 달려 글 내용을 수정할 수 없습니다", Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }
                    }


                    if (!isAnonymousPost()) {
                        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                            @Override
                            public void onRewardedVideoAdLoaded() {

                            }

                            @Override
                            public void onRewardedVideoAdOpened() {

                            }

                            @Override
                            public void onRewardedVideoStarted() {

                            }

                            @Override
                            public void onRewardedVideoAdClosed() {
                                loadRewardedVideoAd();
                                FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid()).child(getString(R.string.mCorePostCount)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            int value = Integer.valueOf(dataSnapshot.getValue().toString());
                                            Log.d("test", "몇개 : " + value);
                                            if (value > 0) {
                                                FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid()).child(getString(R.string.mCorePostCount)).setValue(value - 1);
                                                saveCore();

                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                Log.d("test", "onRewardedVideoAdClosed");
                            }

                            @Override
                            public void onRewarded(RewardItem rewardItem) {
                                FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid()).child(getString(R.string.mCorePostCount)).setValue(rewardItem.getAmount());
                            }

                            @Override
                            public void onRewardedVideoAdLeftApplication() {

                            }

                            @Override
                            public void onRewardedVideoAdFailedToLoad(int i) {

                            }
                        });

                        boolean isPlus = DataContainer.getInstance().isPlus;
                        if (!isPlus) {
                            FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid()).child(getString(R.string.mCorePostCount)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int value;
                                    try {
                                        value = Integer.valueOf(dataSnapshot.getValue().toString());
                                    } catch (Exception e) {
                                        value = 0;
                                    }
                                    Log.d("test", "몇개 : " + value);
                                    if (value > 0) {
                                        FirebaseDatabase.getInstance().getReference(getString(R.string.admob)).child(DataContainer.getInstance().getUid()).child(getString(R.string.mCorePostCount)).setValue(value - 1);
                                        saveCore();
                                    } else {
                                        mRewardedVideoAd.show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            saveCore();
                        }
                    } else {
                        saveCore();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(CoreWriteActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        audioFab_layout.setOnClickListener(view -> {

            // 다이얼로그 녹음
            recordFilePath = Environment.getExternalStorageDirectory() + "/core_recorded_audio.wav";
            int color = getResources().getColor(R.color.white);
            AndroidAudioRecorder.with(CoreWriteActivity.this)
                    // Required
                    .setFilePath(recordFilePath)
                    .setColor(color)
                    .setRequestCode(REQUEST_RECORD)

                    // Optional
                    .setSource(AudioSource.MIC)
                    .setChannel(AudioChannel.MONO)
                    .setSampleRate(AudioSampleRate.HZ_8000)
                    .setAutoStart(true)
                    .setKeepDisplayOn(true)

                    // Start recording
                    .record();
        });

        // edit 판별
        postKey = getIntent().getStringExtra("postKey");    // edit 일 경우 값이 있음
        if (postKey == null) {
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
                    if (corePost.getPictureUrl() != null) {
                        Glide.with(CoreWriteActivity.this).load(corePost.getPictureUrl()).into(editImage);
                        image_edit_layout.setVisibility(View.VISIBLE);
                    } else {
                        image_edit_layout.setVisibility(View.GONE);
                    }

                    if (corePost.getSoundUrl() != null) {
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

        image_x_btn.setOnClickListener(view -> {
            // 사진 삭제
            image_edit_layout.setVisibility(View.GONE);
            editImageUri = null;
        });


        // set media player

        seekBar.setClickable(false);
        seekBar.setEnabled(false);

        mediaPlayer = new MediaPlayer();

        startAndPause.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                doStart();
            } else {
                doPause();
            }
        });

        sound_x_btn.setOnClickListener(view -> {
            // 음성 삭제
            startAndPause.setChecked(false);
            edit_audio_layout.setVisibility(View.GONE);
            soundUri = null;
        });

    }

    private void saveCore() {

        UiUtil.getInstance().startProgressDialog(CoreWriteActivity.this);

        if (corePost == null) try {
            corePost = new CorePost(mUuid, UiUtil.getInstance().getCurrentTime(CoreWriteActivity.this));
        } catch (NotSetAutoTimeException e) {
            e.printStackTrace();
            Toast.makeText(CoreWriteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            ActivityCompat.finishAffinity(CoreWriteActivity.this);
        }

        corePost.setText(textContents.getText().toString());
        Task<Void> postUploadTask = postRef.setValue(corePost);
        tasks.put(postUploadTask, (OnSuccessListener<Void>) aVoid -> {
            // addCorePostCount
            if (!isEdit) FireBaseUtil.getInstance().syncCorePostCount(cUuid);
        });

        // image
        if (corePost.getPictureUrl() != null && image_edit_layout.getVisibility() == View.GONE) {
            // deletePicture
            deletePicture();
        } else if (editImageUri != null) {
            try {
                uploadPicture();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(CoreWriteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        // sound
        if (corePost.getSoundUrl() != null && edit_audio_layout.getVisibility() == View.GONE) {
            // deleteSound
            deleteSound();
        } else if (soundUri != null) {
            uploadSound();
        }

        // 모든 비동기 호출이 다 끝낫을 때
        for (Task task : tasks.keySet()) {
            task.addOnSuccessListener(tasks.get(task));
            task.addOnCompleteListener(taskRtn -> {
                for (Task task1 : tasks.keySet()) {
                    if (!task1.isComplete()) return;
                }
                if (isAnonymousPost()) {
                    // 익명게시글이면
                    AlarmUtil.getInstance().sendAlarm(getApplicationContext(), "Post", "UnKnown", corePost, postKey, cUuid, cUuid);
                }

                // 클라우드
                UiUtil.getInstance().noticeModifyToCloud(corePost, postKey, CoreWriteActivity.this);

                setResult(Activity.RESULT_OK);
                finish();
            });
        }
    }


    private boolean isAnonymousPost() {
        return !cUuid.equals(mUuid);
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
            if (requestCode == REQUEST_RECORD) {
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
                return;
            }
            if (requestCode == GalleryPick.REQUEST_GALLERY && data != null && data.getData() != null) {

                try {
                    galleryPick.invoke(data);
                    galleryPick.setImage(editImage);
                    editImageUri = galleryPick.getUri();
                    image_edit_layout.setVisibility(View.VISIBLE);
                    closeFABMenu();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(CoreWriteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(CoreWriteActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void uploadPicture() throws Exception {
        final StorageReference spaceRef = storageRef.child("posts").child(cUuid).child(postKey).child("picture");
        UploadTask uploadTask = galleryPick.upload(spaceRef);
        tasks.put(uploadTask, (OnSuccessListener<UploadTask.TaskSnapshot>) taskSnapshot -> postRef.child("pictureUrl").setValue(taskSnapshot.getDownloadUrl().toString()));
    }

    private void deletePicture() {
        final StorageReference spaceRef = storageRef.child("posts").child(cUuid).child(postKey).child("picture");
        Task task = spaceRef.delete();
        tasks.put(task, (OnSuccessListener<UploadTask.TaskSnapshot>) taskSnapshot -> postRef.child("pictureUrl").setValue(null));
    }

    private void uploadSound() {
        final StorageReference spaceRef = storageRef.child("posts").child(cUuid).child(postKey).child("sound");
        UploadTask uploadTask = spaceRef.putFile(soundUri);
        tasks.put(uploadTask, (OnSuccessListener<UploadTask.TaskSnapshot>) taskSnapshot -> postRef.child("soundUrl").setValue(taskSnapshot.getDownloadUrl().toString()));
    }

    private void deleteSound() {
        final StorageReference spaceRef = storageRef.child("posts").child(cUuid).child(postKey).child("sound");
        Task task = spaceRef.delete();
        tasks.put(task, (OnSuccessListener<UploadTask.TaskSnapshot>) taskSnapshot -> postRef.child("soundUrl").setValue(null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        pictureFab_layout.setClickable(true);
    }

    // Convert millisecond to string.
    private String millisecondsToString(int milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes((long) milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds((long) milliseconds);
        return minutes + ":" + seconds;
    }


    public void doStart() {
        // The duration in milliseconds
        int duration = this.mediaPlayer.getDuration();

        int currentPosition = this.mediaPlayer.getCurrentPosition();

        if (currentPosition == 0) {
            this.seekBar.setMax(duration);
            String maxTimeString = this.millisecondsToString(duration);
            this.textMaxTime.setText(maxTimeString);
        }

        this.mediaPlayer.start();
        // Create a thread to update position of SeekBar.
        UpdateSeekBarThread updateSeekBarThread = new UpdateSeekBarThread();
        threadHandler.postDelayed(updateSeekBarThread, 50);

    }

    // Thread to Update position for SeekBar.
    class UpdateSeekBarThread implements Runnable {

        public void run() {
            int currentPosition = mediaPlayer.getCurrentPosition();
            String currentPositionStr = millisecondsToString(currentPosition);
            textCurrentPosition.setText(currentPositionStr);

            if (CoreWriteActivity.this == null || textCurrentPosition.getText().equals(textMaxTime.getText())) {
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
    public void doPause() {
        this.mediaPlayer.pause();
    }

    // When user click to "Rewind".
    public void doRewind(View view) {
        int currentPosition = this.mediaPlayer.getCurrentPosition();
        // 5 seconds.
        int SUBTRACT_TIME = 5000;

        if (currentPosition - SUBTRACT_TIME > 0) {
            this.mediaPlayer.seekTo(currentPosition - SUBTRACT_TIME);
        }
    }

    // When user click to "Fast-Forward".
    public void doFastForward(View view) {
        int currentPosition = this.mediaPlayer.getCurrentPosition();
        int duration = this.mediaPlayer.getDuration();
        // 5 seconds.
        int ADD_TIME = 5000;

        if (currentPosition + ADD_TIME < duration) {
            this.mediaPlayer.seekTo(currentPosition + ADD_TIME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        startAndPause.setChecked(false);
    }

    @Subscribe
    public void FinishActivity(TargetUserBlocksMeEvent someoneBlocksMeEvent) {
        finish();
    }

    @Override
    protected void onDestroy() {
        UiUtil.getInstance().stopProgressDialog();
        super.onDestroy();
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(getString(R.string.adsWriteCorePost),
                new AdRequest.Builder()
                        .build());
    }
}