package com.example.kwoncheolhyeok.core.CorePage;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

import java.util.ArrayList;

/**
 * Created by Kwon on 2017-12-14.
 */

public class CoreWriteActivity  extends AppCompatActivity {

    Toolbar toolbar = null;

    FloatingActionButton fab, picture_fab, audio_fab;
    LinearLayout pictureFab_layout, audioFab_layout;
    View fabBGLayout;
    boolean isFABOpen=false;
    private LoadPicture loadPicture;
    private static final int REQUEST_GALLERY = 2;

    ImageView editImage;
    TextView saveBtn;
    private String mUuid;

    Uri editImageUri;
    TextView textContents;
    private String cUuid;
    private TextView editAudio;
    private String postKey;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.core_write_activity);
        
        cUuid = getIntent().getStringExtra("cUuid");

        mUuid = DataContainer.getInstance().getUid();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 툴바 뒤로가기 버튼
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        pictureFab_layout= findViewById(R.id.fabLayout2);
        audioFab_layout= findViewById(R.id.fabLayout3);
        fab = findViewById(R.id.fab);
        picture_fab= findViewById(R.id.fab2);
        audio_fab = findViewById(R.id.fab3);
        fabBGLayout=findViewById(R.id.fabBGLayout);
        editImage = findViewById(R.id.edit_img);
        saveBtn = findViewById(R.id.save);
        textContents = findViewById(R.id.edit_txt);
        editAudio = findViewById(R.id.edit_audio);

        // 본인, 타인 구분
        if(!cUuid.equals(mUuid)){    // 타인
            fab.setVisibility(View.GONE);
            editImage.setVisibility(View.GONE);
            editAudio.setVisibility(View.GONE);
            textContents.setHint("질문해주세요 익명입니다");
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
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
                loadPicture.onGallery();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ArrayList<Task> tasks = new ArrayList<>();

                UiUtil.getInstance().startProgressDialog(CoreWriteActivity.this);
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                String key;
                if(!isEdit()) key = mDatabase.child("posts").push().getKey();
                else key = postKey;

                final CorePost corePost = new CorePost(mUuid);

                corePost.setText(textContents.getText().toString());

                final DatabaseReference postRef = mDatabase.child("posts").child(cUuid).child(key);
                Task<Void> postUploadTask = postRef.setValue(corePost);
                tasks.add(postUploadTask);

                postUploadTask.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DatabaseReference corePostCountRef = DataContainer.getInstance().getUserRef(cUuid).child("corePostCount");
                        // addCorePostCount
                        if(isEdit()) FireBaseUtil.getInstance().addCorePostCount(corePostCountRef);
                    }
                });

                // Picture Upload
                if(editImageUri != null) {
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    final StorageReference spaceRef = storageRef.child("posts").child(cUuid).child(key);
                    UploadTask uploadTask = spaceRef.putFile(editImageUri);
                    tasks.add(uploadTask);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            postRef.child("pictureUrl").setValue(taskSnapshot.getDownloadUrl().toString());
                        }
                    });
                }

                // 모든 비동기 호출이 다 끝낫을 때
                for(Task task : tasks){
                    task.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task taskRtn) {
                            for(Task task : tasks )
                                if(!task.isComplete()) return;
                            UiUtil.getInstance().stopProgressDialog();  // 프로그레스바 중단
                            finish();
                        }
                    });
                }

            }
        });

        audioFab_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 다이얼로그 녹음, 파일
                UiUtil.getInstance().showDialog(CoreWriteActivity.this,
                        "Audio", "선택하세요", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // TODO : 녹음

                                closeFABMenu();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // TODO : 음성파일 가져오기

                                closeFABMenu();
                            }
                        }, "녹음", "음성파일가져오기"
                );
            }
        });

        // edit
        postKey = getIntent().getStringExtra("postKey");    // edit 일 경우 값이 있음
        if(isEdit()){
            FirebaseDatabase.getInstance().getReference().child("posts")
                    .child(cUuid).child(postKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    CorePost corePost = dataSnapshot.getValue(CorePost.class);
                    textContents.setText(corePost.getText());
                    Glide.with(editImage.getContext()).load(corePost.getPictureUrl()).into(editImage);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private boolean isEdit() {
        return postKey != null;
    }

    private void showFABMenu(){
        isFABOpen=true;
        pictureFab_layout.setVisibility(View.VISIBLE);
        audioFab_layout.setVisibility(View.VISIBLE);
        fabBGLayout.setVisibility(View.VISIBLE);

        fab.animate().rotationBy(135);
        pictureFab_layout.animate().translationY(-getResources().getDimension(R.dimen.standard_75));
        audioFab_layout.animate().translationY(-getResources().getDimension(R.dimen.standard_150));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotationBy(-135);
        pictureFab_layout.animate().translationY(0);
        audioFab_layout.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if(!isFABOpen){
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
        if(isFABOpen){
            closeFABMenu();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                Uri outputFileUri = data.getData();
                editImage.setImageURI(outputFileUri);   // Local Set
                editImageUri = outputFileUri;
                closeFABMenu();
            }
        }
    }
}