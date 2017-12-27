package com.example.kwoncheolhyeok.core.CorePage;

import android.animation.Animator;
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

import com.example.kwoncheolhyeok.core.Camera.LoadPicture;
import com.example.kwoncheolhyeok.core.Entity.CorePost;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.UiUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.core_write_activity);

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
                String key = mDatabase.child("posts").push().getKey();

                CorePost corePost = new CorePost(mUuid);
                corePost.setText(textContents.getText().toString());

                final DatabaseReference postRef = mDatabase.child("posts").child(mUuid).child(key);
                Task<Void> postUploadTask = postRef.setValue(corePost);
                tasks.add(postUploadTask);

                // Picture Upload
                if(editImageUri != null) {
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    final StorageReference spaceRef = storageRef.child("posts").child(mUuid).child(key);
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
                        }
                    });
                }

            }
        });

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