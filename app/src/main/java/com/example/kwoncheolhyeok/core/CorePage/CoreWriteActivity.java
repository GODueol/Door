package com.example.kwoncheolhyeok.core.CorePage;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.ArrayList;

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
    LinearLayout pictureFab_layout, audioFab_layout;
    View fabBGLayout;
    boolean isFABOpen = false;
    private LoadPicture loadPicture;
    private static final int REQUEST_GALLERY = 2;
    private static final int REQUEST_RECORD = 0;

    ImageView editImage;
    ImageButton saveBtn;
    private String mUuid;

    TextView textContents;
    private String cUuid;
    private String postKey;
    private String recordFilePath;
    private RecordSelectDialog recordSelectDialog;
    private ArrayList<Task> tasks;
    private boolean isEdit = true;
    private DatabaseReference postRef;
    private Uri editImageUri;
    private Uri soundUri;
    private StorageReference storageRef;
    private CorePost corePost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.core_write_activity);
        storageRef = FirebaseStorage.getInstance().getReference();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        tasks = new ArrayList<>();

        cUuid = getIntent().getStringExtra("cUuid");

        mUuid = DataContainer.getInstance().getUid();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        pictureFab_layout = findViewById(R.id.fabLayout2);
        audioFab_layout = findViewById(R.id.fabLayout3);
        fab = findViewById(R.id.fab);
        picture_fab = findViewById(R.id.fab2);
        audio_fab = findViewById(R.id.fab3);
        fabBGLayout = findViewById(R.id.fabBGLayout);
        editImage = findViewById(R.id.edit_img);
        saveBtn = findViewById(R.id.save);
        textContents = findViewById(R.id.edit_txt);
        TextView editAudio = findViewById(R.id.edit_audio);

        // 본인, 타인 구분
        if (!cUuid.equals(mUuid)) {    // 타인
            fab.setVisibility(View.GONE);
            editImage.setVisibility(View.GONE);
            editAudio.setVisibility(View.GONE);
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
        loadPicture = new LoadPicture(this, this);
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
                                Log.d("kbj", result.toString());
                                AlbumFile albumFile = result.get(0);

                                editImageUri = Uri.fromFile(new File(albumFile.getPath()));
                                editImage.setImageURI(editImageUri);   // Local Set
                                closeFABMenu();
                            }
                        })
                        .onCancel(new Action<String>() {
                            @Override
                            public void onAction(int requestCode, @NonNull String result) {
                                Log.d("kbj", result);
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
                // 다이얼로그 녹음, 파일

                recordSelectDialog = new RecordSelectDialog(CoreWriteActivity.this
                        , new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 직접 녹음
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

                        recordSelectDialog.dismiss();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO : 음성 파일 가져오기
                        recordSelectDialog.dismiss();
                    }
                }
                );
                recordSelectDialog.show();

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
                    if(corePost.getPictureUrl() != null) Glide.with(CoreWriteActivity.this).load(corePost.getPictureUrl()).into(editImage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private void saveCore() {
        UiUtil.getInstance().startProgressDialog(CoreWriteActivity.this);

        if(corePost == null) corePost = new CorePost(mUuid);

        corePost.setText(textContents.getText().toString());
        Task<Void> postUploadTask = postRef.setValue(corePost);
        tasks.add(postUploadTask);

        postUploadTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // addCorePostCount
                if (!isEdit) FireBaseUtil.getInstance().syncCorePostCount(cUuid);
            }
        });

        if(editImageUri != null){
            uploadPicture();
        }
        if(soundUri != null){
            uploadSound();
        }

        // 모든 비동기 호출이 다 끝낫을 때
        for (Task task : tasks) {
            task.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task taskRtn) {
                    for (Task task : tasks)
                        if (!task.isComplete()) return;
                    UiUtil.getInstance().stopProgressDialog();  // 프로그레스바 중단

                    setResult(Activity.RESULT_OK);
                    finish();
                }
            });
        }
    }

    private void showFABMenu() {
        isFABOpen = true;
        pictureFab_layout.setVisibility(View.VISIBLE);
        audioFab_layout.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);

        fab.animate().rotation(135);
        fab.animate().setDuration(200);
        pictureFab_layout.animate().translationY(-getResources().getDimension(R.dimen.standard_75));
        audioFab_layout.animate().translationY(-getResources().getDimension(R.dimen.standard_150));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotation(0);
        fab.animate().setDuration(200);
        pictureFab_layout.animate().translationY(0);
        audioFab_layout.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    pictureFab_layout.setVisibility(View.GONE);
                    audioFab_layout.setVisibility(View.GONE);
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
                closeFABMenu();
            }
        }
    }

    private void uploadPicture() {
        final StorageReference spaceRef = storageRef.child("posts").child(cUuid).child(postKey).child("picture");
        UploadTask uploadTask = spaceRef.putFile(editImageUri);
        tasks.add(uploadTask);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                postRef.child("pictureUrl").setValue(taskSnapshot.getDownloadUrl().toString());
            }
        });
    }

    private void uploadSound() {
        final StorageReference spaceRef = storageRef.child("posts").child(cUuid).child(postKey).child("sound");
        UploadTask uploadTask = spaceRef.putFile(soundUri);
        tasks.add(uploadTask);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                postRef.child("soundUrl").setValue(taskSnapshot.getDownloadUrl().toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pictureFab_layout.setClickable(true);
    }
}