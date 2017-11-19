package com.example.kwoncheolhyeok.core.ProfileModifyActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.kwoncheolhyeok.core.Camera.LoadPicture;
import com.example.kwoncheolhyeok.core.Entity.IntBoundary;
import com.example.kwoncheolhyeok.core.Entity.StringBoundary;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.BusProvider;
import com.example.kwoncheolhyeok.core.Util.CoreProgress;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.example.kwoncheolhyeok.core.Util.PushEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ProfileModifyActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    Toolbar toolbar = null;


    TextView agePick = null;
    TextView heightPick = null;
    TextView weightPick = null;
    TextView bodyTypePick = null;

    private TextView min_age_filter, max_age_filter, min_height_filter, max_height_filter, min_weight_filter, max_weight_filter, min_bodytype_filter, max_bodytype_filter;

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

    @Bind(R.id.modify_introduce)
    EditText introEditText;

    @Bind(R.id.filter_switch)
    Switch filterSwitch;

    @Bind(R.id.AGE_FILTER)
    RelativeLayout ageFilterLayout;

    @Bind(R.id.HEIGHT_FILTER)
    RelativeLayout heightFilterLayout;

    @Bind(R.id.WEIGHT_FILTER)
    RelativeLayout weightFilterLayout;

    @Bind(R.id.BODY_TYPE_FILTER)
    RelativeLayout bodyTypeFilterLayout;

    @Bind(R.id.lock2)
    ToggleButton lock2Toggle;

    @Bind(R.id.lock3)
    ToggleButton lock3Toggle;

    @Bind(R.id.lock4)
    ToggleButton lock4Toggle;

    @Bind(R.id.delete2)
    ImageView delete2Image;

    @Bind(R.id.delete3)
    ImageView delete3Image;

    @Bind(R.id.delete4)
    ImageView delete4Image;


    final String[] values = {"Underweight", "Skinny", "Standard", "Muscular", "Overweight"};

    // filter boundary
    enum FILTER {
        AGE, HEIGHT, WEIGHT
    }

    ;
    private static final int minBoundary[] = {19, 150, 40};
    private static final int maxBoundary[] = {100, 200, 150};

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

//        numberpicker_layout = (LinearLayout) findViewById(R.id.numberPicker_layout);
//        numberpicker_layout.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                show_numPick();
//            }
//        });

        // 지금 하려는 부분
        agePick = (TextView) findViewById(R.id.numberPicker1);
        agePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_numPick();
            }
        });

//        agePick.setMinValue(19);
//        agePick.setMaxValue(200);
//        agePick.setValue(25);
//        agePick.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
//        agePick.setWrapSelectorWheel(false);
//        setDividerColor(agePick, Color.WHITE);
////        agePick.setTextColor(getResources().getColor(R.color.colorPrimary));
////        agePick.setTextColorResource(R.color.colorPrimary);

        heightPick = (TextView) findViewById(R.id.numberPicker2);
        heightPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_numPick();
            }
        });
//        heightPick.setMinValue(100);
//        heightPick.setMaxValue(200);
//        heightPick.setValue(175);
//        heightPick.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
//        heightPick.setWrapSelectorWheel(false);
//        setDividerColor(heightPick, Color.WHITE);

        weightPick = (TextView) findViewById(R.id.numberPicker3);
        weightPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_numPick();
            }
        });
//        weightPick.setMinValue(40);
//        weightPick.setMaxValue(150);
//        weightPick.setValue(65);
//        weightPick.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
//        weightPick.setWrapSelectorWheel(false);
//        setDividerColor(weightPick, Color.WHITE);

        bodyTypePick = (TextView) findViewById(R.id.numberPicker4);
        bodyTypePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_numPick();
            }
        });
//        bodyTypePick.setMinValue(0); //from array first value
//        bodyTypePick.setMaxValue(values.length - 1); //to array last value
//        bodyTypePick.setValue(values.length - 3);
//        bodyTypePick.setDisplayedValues(values);
//        bodyTypePick.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
//        bodyTypePick.setWrapSelectorWheel(false);
//        setDividerColor(bodyTypePick, Color.WHITE);


        // 필터 다이얼로그 열기
        min_age_filter = (TextView) findViewById(R.id.min_age_filter);
        min_age_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show(min_age_filter, max_age_filter, FILTER.AGE);
            }
        });
        max_age_filter = (TextView) findViewById(R.id.max_age_filter);
        max_age_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show(min_age_filter, max_age_filter, FILTER.AGE);
            }
        });

        min_height_filter = (TextView) findViewById(R.id.min_height_filter);
        min_height_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show(min_height_filter, max_height_filter, FILTER.HEIGHT);
            }
        });
        max_height_filter = (TextView) findViewById(R.id.max_height_filter);
        max_height_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show(min_height_filter, max_height_filter, FILTER.HEIGHT);
            }
        });

        min_weight_filter = (TextView) findViewById(R.id.min_weight_filter);
        min_weight_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show(min_weight_filter, max_weight_filter, FILTER.WEIGHT);
            }
        });
        max_weight_filter = (TextView) findViewById(R.id.max_weight_filter);
        max_weight_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show(min_weight_filter, max_weight_filter, FILTER.WEIGHT);
            }
        });

        min_bodytype_filter = (TextView) findViewById(R.id.min_bodytype_filter);
        min_bodytype_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBodyType();
            }
        });
        max_bodytype_filter = (TextView) findViewById(R.id.max_bodytype_filter);
        max_bodytype_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBodyType();
            }
        });


        // 개인정보 Setting
        user = DataContainer.getInstance().getUser();
        _idText.setText(user.getId());
        agePick.setText(user.getAge());
        heightPick.setText(user.getHeight());
        weightPick.setText(user.getWeight());
        bodyTypePick.setText(user.getBodyType());
        introEditText.setText(user.getIntro());

        // Load the image using Glide
        FireBaseUtil fbUtil = FireBaseUtil.getInstance();
        fbUtil.setImage(fbUtil.getParentPath() + "profilePic1.jpg", profilePic1);
        fbUtil.setImage(fbUtil.getParentPath() + "profilePic2.jpg", profilePic2);
        fbUtil.setImage(fbUtil.getParentPath() + "profilePic3.jpg", profilePic3);
        fbUtil.setImage(fbUtil.getParentPath() + "profilePic4.jpg", profilePic4);

        // Set Event of Getting Picture
        loadPicture = new LoadPicture(this, this);
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

        // Set Filter
        try {
            max_age_filter.setText(Integer.toString(user.getAgeBoundary().getMax()));
            min_age_filter.setText(Integer.toString(user.getAgeBoundary().getMin()));

            max_weight_filter.setText(Integer.toString(user.getWeightBoundary().getMax()));
            min_weight_filter.setText(Integer.toString(user.getWeightBoundary().getMin()));

            max_height_filter.setText(Integer.toString(user.getHeightBoundary().getMax()));
            min_height_filter.setText(Integer.toString(user.getHeightBoundary().getMin()));

            max_bodytype_filter.setText(user.getBodyTypeBoundary().getMax());
            min_bodytype_filter.setText(user.getBodyTypeBoundary().getMin());

        } catch (Exception e) {
            e.printStackTrace();
        }

        /* filter_switch */
        filterSwitch.setChecked(user.isUseFilter());
        setVisibilityFilterLayout(filterSwitch.isChecked());
        filterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setVisibilityFilterLayout(isChecked);
            }
        });

        /* pic lock */
        lock2Toggle.setChecked(user.isLockPic2());
        lock3Toggle.setChecked(user.isLockPic3());
        lock4Toggle.setChecked(user.isLockPic4());

        /* onClick del btn */
        setOnDelPicBtnClickListener(delete2Image, profilePic2);
        setOnDelPicBtnClickListener(delete3Image, profilePic3);
        setOnDelPicBtnClickListener(delete4Image, profilePic4);

    }

    private void setOnDelPicBtnClickListener(final ImageView btn, final ImageView targetPic) {
        View.OnClickListener onDeleteClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileModifyActivity.this);
                builder.setTitle("Delete Picture");
                builder.setMessage("Do you want delete Picture?");
                builder.setCancelable(false);
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // 사진 삭제
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                        String picPath = getPicPath(targetPic);
                        StorageReference desertRef = storageRef.child(picPath);

                        // Delete the file
                        CoreProgress.getInstance().startProgressDialog(ProfileModifyActivity.this);
                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // File deleted successfully
                                Log.d(getClass().getName(),"Delete Pic Success");
                                targetPic.setImageResource(R.drawable.a);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Uh-oh, an error occurred!
                                Log.d(getClass().getName(),"Delete Pic Fail");
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                CoreProgress.getInstance().stopProgressDialog();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기
            }
        };

        btn.setOnClickListener(onDeleteClickListener);
    }

    private void setVisibilityFilterLayout(boolean isChecked) {
        int FLAG;
        if (isChecked) FLAG = View.VISIBLE;
        else FLAG = View.GONE;
        ageFilterLayout.setVisibility(FLAG);
        heightFilterLayout.setVisibility(FLAG);
        weightFilterLayout.setVisibility(FLAG);
        bodyTypeFilterLayout.setVisibility(FLAG);
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

    //지금하는거
    public void show_numPick() {

        final Dialog d = new Dialog(ProfileModifyActivity.this);
        d.setContentView(R.layout.profile_modify_numpick_layout);

        // Dialog 사이즈 조절 하기
        ViewGroup.LayoutParams params = d.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        d.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        d.show();

        TextView b1 = (TextView) d.findViewById(R.id.button1);
        TextView b2 = (TextView) d.findViewById(R.id.button2);

//        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
//        np.setMaxValue(100); // max value 100
//        np.setMinValue(19);   // min value 0
//        np.setValue(25);
//        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);  //데이터 선택시 edittext 방지
//        np.setWrapSelectorWheel(false);
//        np.setOnValueChangedListener(this);

//        final NumberPicker np2 = (NumberPicker) d.findViewById(R.id.numberPicker2);
//        np2.setMaxValue(100); // max value 100
//        np2.setMinValue(19);   // min value 0
//        np2.setValue(25);
//        np2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);  //데이터 선택시 edittext 방지
//        np2.setWrapSelectorWheel(false);
//        np2.setOnValueChangedListener(this);

        // 지금 하려는 부분
        final NumberPicker numberpicker1 = (NumberPicker) d.findViewById(R.id.numberPicker1);
        numberpicker1.setMinValue(19);
        numberpicker1.setMaxValue(200);
        numberpicker1.setValue(25);
        numberpicker1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberpicker1.setWrapSelectorWheel(false);
//        setDividerColor(numberpicker1, Color.WHITE);
        numberpicker1.setOnValueChangedListener(this);
//        agePick.setTextColor(getResources().getColor(R.color.colorPrimary));
//        agePick.setTextColorResource(R.color.colorPrimary);

        final NumberPicker numberpicker2 = (NumberPicker) d.findViewById(R.id.numberPicker2);
        numberpicker2.setMinValue(100);
        numberpicker2.setMaxValue(200);
        numberpicker2.setValue(175);
        numberpicker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberpicker2.setWrapSelectorWheel(false);
//        setDividerColor(numberpicker2, Color.WHITE);
        numberpicker2.setOnValueChangedListener(this);


        final NumberPicker numberpicker3 = (NumberPicker) d.findViewById(R.id.numberPicker3);
        numberpicker3.setMinValue(40);
        numberpicker3.setMaxValue(150);
        numberpicker3.setValue(65);
        numberpicker3.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberpicker3.setWrapSelectorWheel(false);
//        setDividerColor(numberpicker3, Color.WHITE);
//        numberpicker3.setOnValueChangedListener(this);

        final NumberPicker numberpicker4 = (NumberPicker) d.findViewById(R.id.numberPicker4);
        numberpicker4.setMinValue(0); //from array first value
        numberpicker4.setMaxValue(values.length - 1); //to array last value
        numberpicker4.setValue(values.length - 3);
        numberpicker4.setDisplayedValues(values);
        numberpicker4.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberpicker4.setWrapSelectorWheel(false);
//        setDividerColor(numberpicker4, Color.WHITE);
//        numberpicker4.setOnValueChangedListener(this);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                agePick.setText(Integer.toString(numberpicker1.getValue()));
                heightPick.setText(Integer.toString(numberpicker2.getValue()));
                weightPick.setText(Integer.toString(numberpicker3.getValue()));
                bodyTypePick.setText(values[numberpicker4.getValue()]);
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


    public void show(final TextView min_filter, final TextView max_filter, FILTER filterType) {

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
        np.setMaxValue(maxBoundary[filterType.ordinal()]); // max value 100
        np.setMinValue(minBoundary[filterType.ordinal()]);   // min value 0
        np.setValue(Integer.parseInt(min_filter.getText().toString()));
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);  //데이터 선택시 edittext 방지
        np.setWrapSelectorWheel(false);

        final NumberPicker np2 = (NumberPicker) d.findViewById(R.id.numberPicker2);
        np2.setMaxValue(maxBoundary[filterType.ordinal()]); // max value 100
        np2.setMinValue(np.getValue());   // min value 0
        np2.setValue(Integer.parseInt(max_filter.getText().toString()));
        np2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);  //데이터 선택시 edittext 방지
        np2.setWrapSelectorWheel(false);

        // min 값 바뀌면 max의 하한선이 변경
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                np2.setMinValue(newVal);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                min_filter.setText(String.valueOf(np.getValue())); //set the value to textview
                d.dismiss();
                max_filter.setText(String.valueOf(np2.getValue())); //set the value to textview
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

    public void showBodyType() {

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
        np.setValue(Arrays.asList(values).indexOf(min_bodytype_filter.getText().toString()));
        np.setDisplayedValues(values);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);

        final NumberPicker np2 = (NumberPicker) d.findViewById(R.id.numberPicker2);
        np2.setMinValue(np.getValue()); //from array first value
        np2.setMaxValue(values.length - 1); //to array last value
        np2.setValue(Arrays.asList(values).indexOf(max_bodytype_filter.getText().toString()));
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

        CoreProgress.getInstance().startProgressDialog(this);

        // Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        String profilePicPath = getPicPath(modifingPic);

        final StorageReference spaceRef = storageRef.child(profilePicPath);

        UploadTask uploadTask = spaceRef.putFile(outputFileUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getBaseContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // 로컬에 출력
                @SuppressWarnings("VisibleForTests") Uri uri = taskSnapshot.getDownloadUrl();

                try {
                    Glide.with(modifingPic.getContext() /* context */)
                            .load(uri)
                            .into(modifingPic);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "Upload Fail", Toast.LENGTH_SHORT).show();
                }

                FireBaseUtil.getInstance().setImage(uri.toString(), modifingPic);
                Toast.makeText(getBaseContext(), "Upload Complete", Toast.LENGTH_SHORT).show();

                // 첫번째 사진일 경우는 프로필 사진 변경 이벤트 발생
                if (modifingPic == profilePic1) {
                    BusProvider.getInstance().post(new PushEvent());
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                // 프로그레스바 중단
                CoreProgress.getInstance().stopProgressDialog();
            }
        });

    }

    @NonNull
    private String getPicPath(ImageView targetImage) {
        String profilePicPath = FireBaseUtil.getInstance().getParentPath();
        if (targetImage == profilePic1) {
            profilePicPath += "profilePic1.jpg";
        } else if (targetImage == profilePic2) {
            profilePicPath += "profilePic2.jpg";
        } else if (targetImage == profilePic3) {
            profilePicPath += "profilePic3.jpg";
        } else if (targetImage == profilePic4) {
            profilePicPath += "profilePic4.jpg";
        } else {
            new Exception("Not Found Picture Path").printStackTrace();
            return null;
        }
        return profilePicPath;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }


    private void showImage(Bitmap bitmap) {
        Drawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
        modifingPic.setImageDrawable(bitmapDrawable);
    }

    public void save(View view) {
        CoreProgress.getInstance().startProgressDialog(this);

        // Save Filter
        user.setAgeBoundary(new IntBoundary(
                Integer.parseInt(max_age_filter.getText().toString()),
                Integer.parseInt(min_age_filter.getText().toString())
        ));
        user.setHeightBoundary(new IntBoundary(
                Integer.parseInt(max_height_filter.getText().toString()),
                Integer.parseInt(min_height_filter.getText().toString())
        ));
        user.setWeightBoundary(new IntBoundary(
                Integer.parseInt(max_weight_filter.getText().toString()),
                Integer.parseInt(min_weight_filter.getText().toString())
        ));
        user.setBodyTypeBoundary(new StringBoundary(
                max_bodytype_filter.getText().toString(),
                min_bodytype_filter.getText().toString()
        ));

        // Save User Info
        user.setId(_idText.getText().toString());
        user.setAge(agePick.getText().toString());
        user.setHeight(heightPick.getText().toString());
        user.setWeight(weightPick.getText().toString());
        user.setBodyType(bodyTypePick.getText().toString());
        user.setIntro(introEditText.getText().toString());
        user.setUseFilter(filterSwitch.isChecked());
        user.setLockPic2(lock2Toggle.isChecked());
        user.setLockPic3(lock3Toggle.isChecked());
        user.setLockPic4(lock4Toggle.isChecked());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // User is signed in
            Log.d(this.getClass().getName(), "onAuthStateChanged:signed_in:" + user.getUid());

            // 파이어베이스 저장
            final User mUser = this.user;
            FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).setValue(mUser)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DataContainer.getInstance().setUser(mUser);  // 로컬 저장
                            Toast.makeText(getApplicationContext(), "Save Success", Toast.LENGTH_SHORT).show();

                            // 성공시 백버튼
                            onBackPressed();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Save Fail", Toast.LENGTH_SHORT).show();
                            Log.d(getApplication().getClass().getName(), e.getMessage());
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            CoreProgress.getInstance().stopProgressDialog();
                        }
                    });
        } else {
            // User is signed out
            Log.d(this.getClass().getName(), "onAuthStateChanged:signed_out");
        }

    }

}