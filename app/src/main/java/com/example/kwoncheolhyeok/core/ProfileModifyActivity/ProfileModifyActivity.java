package com.example.kwoncheolhyeok.core.ProfileModifyActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.kwoncheolhyeok.core.Camera.LoadPicture;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ProfileModifyActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener{

    Toolbar toolbar = null;
    NumberPicker numberpicker1 = null;
    NumberPicker numberpicker2 = null;
    NumberPicker numberpicker3 = null;
    NumberPicker numberpicker4 = null;

    ToggleButton lock1 = null;
    ToggleButton lock2 = null;
    ToggleButton lock3 = null;
    ToggleButton lock4 = null;

    static Dialog d;
    private TextView min_age_filter , max_age_filter, min_height_filter, max_height_filter , min_weight_filter, max_weight_filter, min_bodytype_filter, max_bodytype_filter;

    @Bind(R.id.modify_id)
    EditText _idText;

    @Bind(R.id.image1)
    ImageView profilePic1;

    @Bind(R.id.image2)
    ImageView profilePic2;

    @Bind(R.id.image3)
    ImageView profilePic3;

    @Bind(R.id.image4)
    ImageView profilePic4;

    final String[] values = {"Underweight", "Skinny", "Standard", "Muscular", "Overweight"};

    // 카메라관련 인자
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_GALLERY = 2;
    private static final int REQUEST_CODE_PROFILE_IMAGE_CROP = 3;
    private Uri outputFileUri;
    private LoadPicture loadPicture;
    private ImageView modifingPic;

    // User Info
    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_modify_activity_main);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

//        lock1 = (ToggleButton) findViewById(R.id.lock1);
//        lock1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                if(lock1.isChecked()){
//                    Toast.makeText(new ProfileModifyActivity(), "1번 사진이 잠김", Toast.LENGTH_SHORT).show();
//                }
//                else
//                {Toast.makeText(new ProfileModifyActivity(), "1번 사진이 열림", Toast.LENGTH_SHORT).show();}
//                }
//        });


        numberpicker1 = (NumberPicker) findViewById(R.id.numberPicker1);
        numberpicker1.setMinValue(19);
        numberpicker1.setMaxValue(200);
        numberpicker1.setValue(25);
        numberpicker1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberpicker1.setWrapSelectorWheel(false);
        setDividerColor(numberpicker1, Color.WHITE);
//        numberpicker1.setTextColor(getResources().getColor(R.color.colorPrimary));
//        numberpicker1.setTextColorResource(R.color.colorPrimary);

        numberpicker2 = (NumberPicker) findViewById(R.id.numberPicker2);
        numberpicker2.setMinValue(100);
        numberpicker2.setMaxValue(200);
        numberpicker2.setValue(175);
        numberpicker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberpicker2.setWrapSelectorWheel(false);
        setDividerColor(numberpicker2, Color.WHITE);

        numberpicker3 = (NumberPicker) findViewById(R.id.numberPicker3);
        numberpicker3.setMinValue(40);
        numberpicker3.setMaxValue(150);
        numberpicker3.setValue(65);
        numberpicker3.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberpicker3.setWrapSelectorWheel(false);
        setDividerColor(numberpicker3, Color.WHITE);

        numberpicker4 = (NumberPicker) findViewById(R.id.numberPicker4);
        numberpicker4.setMinValue(0); //from array first value
        numberpicker4.setMaxValue(values.length - 1); //to array last value
        numberpicker4.setValue(values.length - 3);
        numberpicker4.setDisplayedValues(values);
        numberpicker4.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberpicker4.setWrapSelectorWheel(false);
        setDividerColor(numberpicker4, Color.WHITE);



        // 필터 다이얼로그 열기
        min_age_filter = (TextView) findViewById(R.id.min_age_filter);
        min_age_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });
        max_age_filter = (TextView) findViewById(R.id.max_age_filter);
        max_age_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });

        min_height_filter = (TextView) findViewById(R.id.min_height_filter);
        min_height_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show2();
            }
        });
        max_height_filter = (TextView) findViewById(R.id.max_height_filter);
        max_height_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show2();
            }
        });

        min_weight_filter = (TextView) findViewById(R.id.min_weight_filter);
        min_weight_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show3();
            }
        });
        max_weight_filter = (TextView) findViewById(R.id.max_weight_filter);
        max_weight_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show3();
            }
        });

        min_bodytype_filter = (TextView) findViewById(R.id.min_bodytype_filter);
        min_bodytype_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show4();
            }
        });
        max_bodytype_filter = (TextView) findViewById(R.id.max_bodytype_filter);
        max_bodytype_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show4();
            }
        });


        // 개인정보 Setting
        user = DataContainer.getInstance().getUser();
        _idText.setText(user.getId());
        numberpicker1.setValue(Integer.valueOf(user.getAge()));
        numberpicker2.setValue(Integer.valueOf(user.getHeight()));
        numberpicker3.setValue(Integer.valueOf(user.getWeight()));

        loadPicture = new LoadPicture(this, this);

        // Load the image using Glide
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        StorageReference temp = storageRef.child(getParentPath()+"profilePic1.jpg");
        Glide.with(this /* context */)
                //.using(new FirebaseImageLoader())
                .load(temp.getDownloadUrl().toString())
                .into(profilePic1)
        //  onLoadStarted(getResources().getDrawable(R.drawable.progress_dialog_icon_drawable_animation))

        ;

        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(storageRef.child(getParentPath()+"profilePic2.jpg"))
                .into(profilePic2);

        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(storageRef.child(getParentPath()+"profilePic3.jpg"))
                .into(profilePic3);

        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(storageRef.child(getParentPath()+"profilePic4.jpg"))
                .into(profilePic4);

        // Set Event of Getting Picture
        View.OnClickListener onProfilePicClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                modifingPic = (ImageView) v;
                loadPicture.onGallery();
            }
        };
        profilePic1.setOnClickListener(onProfilePicClickListener);
        profilePic2.setOnClickListener(onProfilePicClickListener);
        profilePic3.setOnClickListener(onProfilePicClickListener);
        profilePic4.setOnClickListener(onProfilePicClickListener);

    }


    // 넘버씨커 디바이더 색 바꾸기
    private void setDividerColor(NumberPicker numberpicker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(numberpicker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    //implements 부분 구현
    @Override
    public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
        Log.i("value is", "" + newVal);
    }


    public void show() {

        final Dialog d = new Dialog(ProfileModifyActivity.this);
        d.setContentView(R.layout.profile_modify_age_dialog);

        // Dialog 사이즈 조절 하기
        ViewGroup.LayoutParams params = d.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        d.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        d.show();

        TextView b1 = (TextView) d.findViewById(R.id.button1);
        TextView b2 = (TextView) d.findViewById(R.id.button2);

        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(100); // max value 100
        np.setMinValue(19);   // min value 0
        np.setValue(25);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);  //데이터 선택시 edittext 방지
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);

        final NumberPicker np2 = (NumberPicker) d.findViewById(R.id.numberPicker2);
        np2.setMaxValue(100); // max value 100
        np2.setMinValue(19);   // min value 0
        np2.setValue(25);
        np2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);  //데이터 선택시 edittext 방지
        np2.setWrapSelectorWheel(false);
        np2.setOnValueChangedListener(this);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                min_age_filter.setText(String.valueOf(np.getValue())); //set the value to textview
                d.dismiss();
                max_age_filter.setText(String.valueOf(np2.getValue())); //set the value to textview
                d.dismiss();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    public void show2() {

        final Dialog d = new Dialog(ProfileModifyActivity.this);
        d.setContentView(R.layout.profile_modify_height_dialog);

        // Dialog 사이즈 조절 하기
        ViewGroup.LayoutParams params = d.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        d.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        d.show();

        TextView b1 = (TextView) d.findViewById(R.id.button1);
        TextView b2 = (TextView) d.findViewById(R.id.button2);

        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(200); // max value 100
        np.setMinValue(150);   // min value 0
        np.setValue(175);
        np.setWrapSelectorWheel(false);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);  //데이터 선택시 edittext 방지
        np.setOnValueChangedListener(this);

        final NumberPicker np2 = (NumberPicker) d.findViewById(R.id.numberPicker2);
        np2.setMaxValue(200); // max value 100
        np2.setMinValue(150);   // min value 0
        np2.setValue(175);
        np2.setWrapSelectorWheel(false);
        np2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);  //데이터 선택시 edittext 방지
        np2.setOnValueChangedListener(this);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                min_height_filter.setText(String.valueOf(np.getValue())); //set the value to textview
                d.dismiss();
                max_height_filter.setText(String.valueOf(np2.getValue())); //set the value to textview
                d.dismiss();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    public void show3() {

        final Dialog d = new Dialog(ProfileModifyActivity.this);
        d.setContentView(R.layout.profile_modify_weight_dialog);

        // Dialog 사이즈 조절 하기
        ViewGroup.LayoutParams params = d.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        d.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        d.show();

        TextView b1 = (TextView) d.findViewById(R.id.button1);
        TextView b2 = (TextView) d.findViewById(R.id.button2);

        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(150); // max value 100
        np.setMinValue(40);   // min value 0
        np.setValue(65);
        np.setWrapSelectorWheel(false);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);  //데이터 선택시 edittext 방지
        np.setOnValueChangedListener(this);

        final NumberPicker np2 = (NumberPicker) d.findViewById(R.id.numberPicker2);
        np2.setMaxValue(150); // max value 100
        np2.setMinValue(40);   // min value 0
        np2.setValue(65);
        np2.setWrapSelectorWheel(false);
        np2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);  //데이터 선택시 edittext 방지
        np2.setOnValueChangedListener(this);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                min_weight_filter.setText(String.valueOf(np.getValue())); //set the value to textview
                d.dismiss();
                max_weight_filter.setText(String.valueOf(np2.getValue())); //set the value to textview
                d.dismiss();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    public void show4() {

        final Dialog d = new Dialog(ProfileModifyActivity.this);
        d.setContentView(R.layout.profile_modify_bodytype_dialog);

        // Dialog 사이즈 조절 하기
        ViewGroup.LayoutParams params = d.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        d.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        d.show();

        TextView b1 = (TextView) d.findViewById(R.id.button1);
        TextView b2 = (TextView) d.findViewById(R.id.button2);

        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);

        np.setMinValue(0); //from array first value
        np.setMaxValue(values.length - 1); //to array last value
        np.setValue(values.length - 3);
        np.setDisplayedValues(values);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);

        final NumberPicker np2 = (NumberPicker) d.findViewById(R.id.numberPicker2);
        np2.setMinValue(0); //from array first value
        np2.setMaxValue(values.length - 1); //to array last value
        np2.setValue(values.length - 3);
        np2.setDisplayedValues(values);
        np2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np2.setWrapSelectorWheel(false);
        np2.setOnValueChangedListener(this);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = np.getValue();
                int pos2 = np2.getValue();

                min_bodytype_filter.setText(values[pos]); //set the value to textview
                d.dismiss();
                max_bodytype_filter.setText(values[pos2]); //set the value to textview
                d.dismiss();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();
    }

    //     뒤로가기 버튼 기능
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                outputFileUri = data.getData();

                // 서버에 Upload
                uploadPic(outputFileUri);
            }
        }
    }

    private void uploadPic(final Uri outputFileUri) {

        // Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String profilePicPath = getParentPath();
        if(modifingPic == profilePic1){
            profilePicPath += "profilePic1.jpg";
        } else if(modifingPic == profilePic2){
            profilePicPath += "profilePic2.jpg";
        } else if(modifingPic == profilePic3){
            profilePicPath += "profilePic3.jpg";
        } else if(modifingPic == profilePic4){
            profilePicPath += "profilePic4.jpg";
        }
        final StorageReference spaceRef = storageRef.child(profilePicPath);

        UploadTask uploadTask = spaceRef.putFile(outputFileUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getBaseContext(),exception.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                @SuppressWarnings("VisibleForTests") String url = taskSnapshot.getDownloadUrl().getPath();
                // 로컬에 출력
                showImage(loadPicture.drawFile(outputFileUri));

                Glide.with(getApplicationContext() /* context */)
                        .using(new FirebaseImageLoader())
                        .load(spaceRef)
                        .into(profilePic2);
            }
        });

    }

    @NonNull
    private String getParentPath() {
        return "profile/pic/" + DataContainer.getInstance().getUid() + "/";
    }

    private void showImage(Bitmap bitmap) {
        Drawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);

        modifingPic.setImageDrawable(bitmapDrawable);
//        mProductListener.ProductTabMessageToParent(bitmapDrawable);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
//        return true;
//    }



}