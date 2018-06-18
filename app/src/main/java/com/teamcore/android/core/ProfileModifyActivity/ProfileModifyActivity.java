package com.teamcore.android.core.ProfileModifyActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.teamcore.android.core.Entity.IntBoundary;
import com.teamcore.android.core.Entity.StringBoundary;
import com.teamcore.android.core.Entity.User;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.BaseActivity.BaseActivity;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.FireBaseUtil;
import com.teamcore.android.core.Util.GalleryPick;
import com.teamcore.android.core.Util.GlideApp;
import com.teamcore.android.core.Util.UiUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.teamcore.android.core.Util.UiUtil.getInstance;


public class ProfileModifyActivity extends BaseActivity implements NumberPicker.OnValueChangeListener {

    Toolbar toolbar = null;

    TextView agePick = null;
    TextView heightPick = null;
    TextView weightPick = null;
    TextView bodyTypePick = null;

    private static int MAX_CONTENT = 130;

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

    @Bind(R.id.content_error)
    TextView content_error;

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

    @Bind(R.id.AGE_FILTER1)
    RelativeLayout ageFilterLayout1;

    @Bind(R.id.HEIGHT_FILTER1)
    RelativeLayout heightFilterLayout1;

    @Bind(R.id.WEIGHT_FILTER1)
    RelativeLayout weightFilterLayout1;

    @Bind(R.id.BODY_TYPE_FILTER1)
    RelativeLayout bodyTypeFilterLayout1;

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

    private GalleryPick galleryPick;

    @Bind(R.id.age_nouse)
    EditText introduce_focus;

    // filter boundary
    enum FILTER {
        AGE, HEIGHT, WEIGHT
    }

    private static final int minBoundary[] = {20, 100, 40};
    private static final int maxBoundary[] = {99, 220, 150};

    private ImageView modifyingPic;

    @SuppressLint("UseSparseArrays")
//    Map<Integer, Uri> uriMap = new HashMap<>();

    // User Info
    User user;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_modify_activity_main);
        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false); //툴바 안의 앱네임 안보이게
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_black_36dp);

        agePick = (TextView) findViewById(R.id.numberPicker1);
        agePick.setOnClickListener(v -> show_numPick());

        heightPick = (TextView) findViewById(R.id.numberPicker2);
        heightPick.setOnClickListener(v -> show_numPick());

        weightPick = (TextView) findViewById(R.id.numberPicker3);
        weightPick.setOnClickListener(v -> show_numPick());

        bodyTypePick = (TextView) findViewById(R.id.numberPicker4);
        bodyTypePick.setOnClickListener(v -> show_numPick());

        ImageView[] profilePics = new ImageView[]{profilePic1, profilePic2, profilePic3, profilePic4};

        // 필터 다이얼로그 열기
        min_age_filter = (TextView) findViewById(R.id.min_age_filter);
        min_age_filter.setOnClickListener(v -> show(min_age_filter, max_age_filter, FILTER.AGE));
        max_age_filter = (TextView) findViewById(R.id.max_age_filter);
        max_age_filter.setOnClickListener(v -> show(min_age_filter, max_age_filter, FILTER.AGE));

        min_height_filter = (TextView) findViewById(R.id.min_height_filter);
        min_height_filter.setOnClickListener(v -> show(min_height_filter, max_height_filter, FILTER.HEIGHT));
        max_height_filter = (TextView) findViewById(R.id.max_height_filter);
        max_height_filter.setOnClickListener(v -> show(min_height_filter, max_height_filter, FILTER.HEIGHT));

        min_weight_filter = (TextView) findViewById(R.id.min_weight_filter);
        min_weight_filter.setOnClickListener(v -> show(min_weight_filter, max_weight_filter, FILTER.WEIGHT));
        max_weight_filter = (TextView) findViewById(R.id.max_weight_filter);
        max_weight_filter.setOnClickListener(v -> show(min_weight_filter, max_weight_filter, FILTER.WEIGHT));

        min_bodytype_filter = (TextView) findViewById(R.id.min_bodytype_filter);
        min_bodytype_filter.setOnClickListener(v -> showBodyType());
        max_bodytype_filter = (TextView) findViewById(R.id.max_bodytype_filter);
        max_bodytype_filter.setOnClickListener(v -> showBodyType());


        // 개인정보 Setting
        user = DataContainer.getInstance().getUser();
        _idText.setText(user.getId());
        agePick.setText(Integer.toString(user.getAge()));
        heightPick.setText(Integer.toString(user.getHeight()));
        weightPick.setText(Integer.toString(user.getWeight()));
        bodyTypePick.setText(user.getBodyType());
        introEditText.setText(user.getIntro());

        // 제재 시, 개인정보 바꾸지 못하도록
        UiUtil.getInstance().checkUserPrevent(ProfileModifyActivity.this,(isRelease, releaseDate) ->{
            _idText.setEnabled(isRelease);
            agePick.setEnabled(isRelease);
            heightPick.setEnabled(isRelease);
            weightPick.setEnabled(isRelease);
            bodyTypePick.setEnabled(isRelease);
            introEditText.setEnabled(isRelease);
        });

        //소개 텍스트 맥스 숫자 제한 표시
        setVerification();

        // 텍스트뷰를 에딧텍스트로 바꿔서 포커스 준 다음에 인풋못하게 막으면 포커스도 잡으면서 수정도 막는 일석이조라고~
        introduce_focus.requestFocus();
        introduce_focus.setInputType(InputType.TYPE_NULL);

        // Load the image using Glide
        ArrayList<String> picUrlList = user.getPicUrls().toArray();
        for (int i = 0; i < profilePics.length; i++) {
            String url = picUrlList.get(i);
            if (url == null) continue;
            Glide.with(getBaseContext()).load(url).into(profilePics[i]);
        }

        // Set Event of Getting Picture
        galleryPick = new GalleryPick(ProfileModifyActivity.this);
        View.OnClickListener onProfilePicClickListener = v -> {
            // 제재 기간인지 확인
            UiUtil.getInstance().checkUserPrevent(ProfileModifyActivity.this,(isRelease, releaseDate) -> {
                if(!isRelease){

                    Toast.makeText(ProfileModifyActivity.this,
                            "프로필 사진 제재 당하셨기 때문에 " +
                                    releaseDate + " 까지 프로필을 업로드 할 수 없습니다"
                            , Toast.LENGTH_SHORT).show();
                    return;
                }

                modifyingPic = (ImageView) v;
                galleryPick.goToGallery();
                profilePic1.setClickable(false);
                profilePic2.setClickable(false);
                profilePic3.setClickable(false);
                profilePic4.setClickable(false);
            });

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

            // 초기 유저라서 아직 셋팅이 안된 케이스
            // 디폴트 값으로 설정
            max_age_filter.setText(Integer.toString(maxBoundary[FILTER.AGE.ordinal()]));
            min_age_filter.setText(Integer.toString(minBoundary[FILTER.AGE.ordinal()]));

            max_weight_filter.setText(Integer.toString(maxBoundary[FILTER.WEIGHT.ordinal()]));
            min_weight_filter.setText(Integer.toString(minBoundary[FILTER.WEIGHT.ordinal()]));

            max_height_filter.setText(Integer.toString(maxBoundary[FILTER.HEIGHT.ordinal()]));
            min_height_filter.setText(Integer.toString(minBoundary[FILTER.HEIGHT.ordinal()]));

            max_bodytype_filter.setText(DataContainer.bodyTypes[4]);
            min_bodytype_filter.setText(DataContainer.bodyTypes[0]);
        }

        /* filter_switch */
        filterSwitch.setChecked(user.isUseFilter());
        setVisibilityFilterLayout(filterSwitch.isChecked());
        filterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> setVisibilityFilterLayout(isChecked));

        /* pic lock */
        lock2Toggle.setChecked(user.getIsLockPics().getIsLockPic2());
        lock3Toggle.setChecked(user.getIsLockPics().getIsLockPic3());
        lock4Toggle.setChecked(user.getIsLockPics().getIsLockPic4());

        /* lock change event */
        CompoundButton.OnCheckedChangeListener listener = (compoundButton, b) -> {
            String msg;
            if(b){  // True 잠금
                msg = "이 사진을 비공개 합니다.";
            } else {
                msg = "이 사진을 전체공개 합니다";
            }
            Toast.makeText(ProfileModifyActivity.this, msg, Toast.LENGTH_SHORT).show();
        };
        lock2Toggle.setOnCheckedChangeListener(listener);
        lock3Toggle.setOnCheckedChangeListener(listener);
        lock4Toggle.setOnCheckedChangeListener(listener);


        /* onClick del btn */
        setOnDelPicBtnClickListener(delete2Image, profilePic2);
        setOnDelPicBtnClickListener(delete3Image, profilePic3);
        setOnDelPicBtnClickListener(delete4Image, profilePic4);


    }


    private void setOnDelPicBtnClickListener(final ImageView btn, final ImageView targetPic) {
        View.OnClickListener onDeleteClickListener = v -> UiUtil.getInstance().showDialog(ProfileModifyActivity.this, "사진 삭제", " 사진을 삭제하시겠습니까?"
                , (dialog, whichButton) -> {
                    // 사진 바로 삭제

                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    final StorageReference spaceRef = storageRef.child(getPicPath(targetPic));
                    final StorageReference thumbNailSpaceRef = storageRef.child(getPicPath(targetPic).replace(".jpg", "_thumbNail.jpg"));

                    // 삭제
                    Task task = spaceRef.delete().addOnSuccessListener(o -> {
                        removeUserPicUrl(targetPic);
                        targetPic.setImageResource(R.drawable.a);
                    });

                    Task thumbNailTask = thumbNailSpaceRef.delete()
                            .addOnSuccessListener(o -> removeUserThumbNailPicUrl(targetPic));

                    startProgressDialog();
                    Tasks.whenAll(task, thumbNailTask).addOnCompleteListener(task1 -> stopProgressDialog());

                }, (dialog, whichButton) -> {
                });

        btn.setOnClickListener(onDeleteClickListener);
    }

    private void removeUserPicUrl(ImageView targetPic) {
        if (targetPic == profilePic1) {
            user.getPicUrls().setPicUrl1(null);
        } else if (targetPic == profilePic2) {
            user.getPicUrls().setPicUrl2(null);
        } else if (targetPic == profilePic3) {
            user.getPicUrls().setPicUrl3(null);
        } else if (targetPic == profilePic4) {
            user.getPicUrls().setPicUrl4(null);
        }
        DataContainer.getInstance().getUsersRef().child(DataContainer.getInstance().getUid()).setValue(user);
    }

    private void setVisibilityFilterLayout(boolean isChecked) {
        int FLAG;
        if (isChecked) FLAG = View.VISIBLE;
        else FLAG = View.GONE;
        ageFilterLayout1.setVisibility(FLAG);
        heightFilterLayout1.setVisibility(FLAG);
        weightFilterLayout1.setVisibility(FLAG);
        bodyTypeFilterLayout1.setVisibility(FLAG);

    }

    //implements 부분 구현
    @SuppressLint("LogNotTimber")
    @Override
    public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
        Log.i("value is", "" + newVal);
    }

    @SuppressLint("SetTextI18n")
    public void show_numPick() {

        final Dialog d = new Dialog(ProfileModifyActivity.this);
        d.setContentView(R.layout.profile_modify_numpick_layout);
        d.setCanceledOnTouchOutside(false);

        // Dialog 사이즈 조절 하기
        ViewGroup.LayoutParams params = d.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        d.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        d.show();

        TextView b1 = d.findViewById(R.id.button1);
        TextView b2 = d.findViewById(R.id.button2);


        // 지금 하려는 부분
        final NumberPicker numberpicker1 = d.findViewById(R.id.numberPicker1);
        numberpicker1.setMinValue(20);
        numberpicker1.setMaxValue(99);
        numberpicker1.setValue(Integer.parseInt(agePick.getText().toString()));
        numberpicker1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberpicker1.setWrapSelectorWheel(false);
        numberpicker1.setOnValueChangedListener(this);

        final NumberPicker numberpicker2 = d.findViewById(R.id.numberPicker2);
        numberpicker2.setMinValue(100);
        numberpicker2.setMaxValue(220);
        numberpicker2.setValue(Integer.parseInt(heightPick.getText().toString()));
        numberpicker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberpicker2.setWrapSelectorWheel(false);
        numberpicker2.setOnValueChangedListener(this);


        final NumberPicker numberpicker3 = d.findViewById(R.id.numberPicker3);
        numberpicker3.setMinValue(40);
        numberpicker3.setMaxValue(140);
        numberpicker3.setValue(Integer.parseInt(weightPick.getText().toString()));
        numberpicker3.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberpicker3.setWrapSelectorWheel(false);
        numberpicker3.setOnValueChangedListener(this);

        final NumberPicker numberpicker4 = d.findViewById(R.id.numberPicker4);
        numberpicker4.setMinValue(0); //from array first value
        numberpicker4.setMaxValue(DataContainer.bodyTypes.length - 1); //to array last value
        numberpicker4.setDisplayedValues(DataContainer.bodyTypes);
        numberpicker4.setValue(Arrays.asList(DataContainer.bodyTypes).indexOf(bodyTypePick.getText()));
        numberpicker4.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberpicker4.setWrapSelectorWheel(false);
        numberpicker4.setOnValueChangedListener(this);

        b1.setOnClickListener((View v) -> {

            agePick.setText(Integer.toString(numberpicker1.getValue()));
            heightPick.setText(Integer.toString(numberpicker2.getValue()));
            weightPick.setText(Integer.toString(numberpicker3.getValue()));
            bodyTypePick.setText(DataContainer.bodyTypes[numberpicker4.getValue()]);
            d.dismiss();
        });

        b2.setOnClickListener(
                v -> d.dismiss());
        d.show();
    }


    public void show(final TextView min_filter, final TextView max_filter, final FILTER filterType) {

        final Dialog d = new Dialog(ProfileModifyActivity.this);
        d.setContentView(R.layout.profile_modify_filter_dialog);
        d.setCanceledOnTouchOutside(false);

        // Dialog 사이즈 조절 하기
        ViewGroup.LayoutParams params = d.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        d.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        d.show();

        TextView b1 = d.findViewById(R.id.button1);
        TextView b2 = d.findViewById(R.id.button2);

        TextView btn_min = d.findViewById(R.id.btn_min);
        TextView btn_max = d.findViewById(R.id.btn_max);

        final NumberPicker np = d.findViewById(R.id.numberPicker1);
        np.setMaxValue(maxBoundary[filterType.ordinal()]); // max value 100
        np.setMinValue(minBoundary[filterType.ordinal()]);   // min value 0
        btn_min.setOnClickListener(view -> np.setValue(minBoundary[filterType.ordinal()]));

        np.setValue(Integer.parseInt(min_filter.getText().toString()));
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);  //데이터 선택시 edittext 방지
        np.setWrapSelectorWheel(false);

        final NumberPicker np2 = d.findViewById(R.id.numberPicker2);
        np2.setMaxValue(maxBoundary[filterType.ordinal()]); // max value 100
        np2.setMinValue(np.getValue());   // min value 0
        btn_max.setOnClickListener(view -> np2.setValue(maxBoundary[filterType.ordinal()]));
        np2.setValue(Integer.parseInt(max_filter.getText().toString()));
        np2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);  //데이터 선택시 edittext 방지
        np2.setWrapSelectorWheel(false);

        // min 값 바뀌면 max의 하한선이 변경
        np.setOnValueChangedListener((picker, oldVal, newVal) -> np2.setMinValue(newVal));

        b1.setOnClickListener(v -> {
            int pos = np.getValue();
            int pos2 = np2.getValue();
            if (pos > pos2) {
                Toast.makeText(getBaseContext(), "범위가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            min_filter.setText(String.valueOf(np.getValue())); //set the value to textview
            d.dismiss();
            max_filter.setText(String.valueOf(np2.getValue())); //set the value to textview
            d.dismiss();
        });

        b2.setOnClickListener(v -> d.dismiss());
        d.show();
    }

    public void showBodyType() {

        final Dialog d = new Dialog(ProfileModifyActivity.this);
        d.setContentView(R.layout.profile_modify_filter_dialog);
        d.setCanceledOnTouchOutside(false);

        // Dialog 사이즈 조절 하기
        ViewGroup.LayoutParams params = d.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        d.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        d.show();

        TextView b1 = d.findViewById(R.id.button1);
        TextView b2 = d.findViewById(R.id.button2);

        TextView btn_min = d.findViewById(R.id.btn_min);
        TextView btn_max = d.findViewById(R.id.btn_max);

        final NumberPicker np = d.findViewById(R.id.numberPicker1);

        np.setMinValue(0); //from array first value
        np.setMaxValue(DataContainer.bodyTypes.length - 1); //to array last value
        np.setValue(Arrays.asList(DataContainer.bodyTypes).indexOf(min_bodytype_filter.getText()));
        btn_min.setOnClickListener(view -> np.setValue(0));
        np.setDisplayedValues(DataContainer.bodyTypes);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);

        final NumberPicker np2 = d.findViewById(R.id.numberPicker2);
        np2.setMinValue(0); //from array first value
        np2.setMaxValue(DataContainer.bodyTypes.length - 1); //to array last value
        btn_max.setOnClickListener(view -> np2.setValue(DataContainer.bodyTypes.length - 1));
        np2.setValue(Arrays.asList(DataContainer.bodyTypes).indexOf(max_bodytype_filter.getText()));
        np2.setDisplayedValues(DataContainer.bodyTypes);
        np2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np2.setWrapSelectorWheel(false);
        np2.setOnValueChangedListener(this);

        b1.setOnClickListener(v -> {
            int pos = np.getValue();
            int pos2 = np2.getValue();
            if (pos > pos2) {
                Toast.makeText(getBaseContext(), "범위가 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            min_bodytype_filter.setText(DataContainer.bodyTypes[pos]); //set the value to textview
            d.dismiss();
            max_bodytype_filter.setText(DataContainer.bodyTypes[pos2]); //set the value to textview
            d.dismiss();
        });

        b2.setOnClickListener(v -> d.dismiss());
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
            if (requestCode == GalleryPick.REQUEST_GALLERY && data != null && data.getData() != null) {
                try {
                    galleryPick.invoke(data);

                    // 사진 파일 업로드, 유저정보 추가
                    Uri uri = galleryPick.getUri();
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    final StorageReference spaceRef = storageRef.child(getPicPath(modifyingPic));
                    final StorageReference thumbNailSpaceRef = storageRef.child(getPicPath(modifyingPic).replace(".jpg", "_thumbNail.jpg"));

                    // 저장
                    galleryPick.upload(spaceRef, uri)
                        .addOnSuccessListener(taskSnapshot -> {
                            saveUserPicUrl(taskSnapshot.getDownloadUrl(), modifyingPic);
                            // Set Local
                            GlideApp.with(modifyingPic.getContext())
                                    .load(taskSnapshot.getDownloadUrl())
                                    .placeholder(R.drawable.pic_load_ani)
                                    .into(modifyingPic);
                        });

                    // make thumbnail
                    StorageTask<UploadTask.TaskSnapshot> thumbnailUploadTask = galleryPick.makeThumbNail(thumbNailSpaceRef, uri);
                    if(thumbnailUploadTask != null) thumbnailUploadTask.addOnSuccessListener(taskSnapshot -> saveUserThumbNailPicUrl(taskSnapshot.getDownloadUrl(), modifyingPic));
                    else removeUserThumbNailPicUrl(modifyingPic);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(ProfileModifyActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    stopProgressDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ProfileModifyActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    stopProgressDialog();
                }

            }
        }
    }


    private void saveUserPicUrl(Uri downloadUrl, ImageView modifyingPic) {
        if (modifyingPic == profilePic1) {
            user.getPicUrls().setPicUrl1(downloadUrl.toString());
        } else if (modifyingPic == profilePic2) {
            user.getPicUrls().setPicUrl2(downloadUrl.toString());
        } else if (modifyingPic == profilePic3) {
            user.getPicUrls().setPicUrl3(downloadUrl.toString());
        } else if (modifyingPic == profilePic4) {
            user.getPicUrls().setPicUrl4(downloadUrl.toString());
        }
        DataContainer.getInstance().getUsersRef().child(DataContainer.getInstance().getUid()).setValue(user);
    }

    private void saveUserThumbNailPicUrl(Uri downloadUrl, ImageView modifyingPic) {
        if (modifyingPic == profilePic1) {
            user.getPicUrls().setThumbNail_picUrl1(downloadUrl.toString());
        } else if (modifyingPic == profilePic2) {
            user.getPicUrls().setThumbNail_picUrl2(downloadUrl.toString());
        } else if (modifyingPic == profilePic3) {
            user.getPicUrls().setThumbNail_picUrl3(downloadUrl.toString());
        } else if (modifyingPic == profilePic4) {
            user.getPicUrls().setThumbNail_picUrl4(downloadUrl.toString());
        }
        DataContainer.getInstance().getUsersRef().child(DataContainer.getInstance().getUid()).setValue(user);
    }

    @NonNull
    private String getPicPath(ImageView targetImage) {
        String profilePicPath = FireBaseUtil.getInstance().getStorageProfilePicPath();
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

    @SuppressLint("LogNotTimber")
    public void save(View view) {
        getInstance().startProgressDialog(this);

        // validation
        try {
            if (_idText.getText().toString().equals("") || _idText.length() < 2) {
                throw new Exception("두 자리 이상의 아이디로 작성해주세요.");
            }

            String minBT = min_bodytype_filter.getText().toString();
            String maxBT = max_bodytype_filter.getText().toString();
            if (Arrays.asList(DataContainer.bodyTypes).indexOf(minBT) > Arrays.asList(DataContainer.bodyTypes).indexOf(maxBT)) {
                throw new Exception("범위가 잘못되었습니다.");
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            getInstance().stopProgressDialog();
            return;
        }

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
        user.setAge(Integer.parseInt(agePick.getText().toString()));
        user.setHeight(Integer.parseInt(heightPick.getText().toString()));
        user.setWeight(Integer.parseInt(weightPick.getText().toString()));
        user.setBodyType(bodyTypePick.getText().toString());
        user.setIntro(introEditText.getText().toString());
        user.setUseFilter(filterSwitch.isChecked());

        user.getIsLockPics().setIsLockPic2(lock2Toggle.isChecked());
        user.getIsLockPics().setIsLockPic3(lock3Toggle.isChecked());
        user.getIsLockPics().setIsLockPic4(lock4Toggle.isChecked());

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            // User is signed in
            Log.d(this.getClass().getName(), "onAuthStateChanged:signed_in:" + firebaseUser.getUid());

            // 파이어베이스 저장
            startProgressDialog();
            DataContainer.getInstance().getUsersRef().child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(task -> {
                onBackPressed();
                stopProgressDialog();
            });

        } else {
            // User is signed out
            Log.d(this.getClass().getName(), "onAuthStateChanged:signed_out");
        }

    }

    private void removeUserThumbNailPicUrl(ImageView targetPic) {
        if (targetPic == profilePic1) {
            user.getPicUrls().setThumbNail_picUrl1(null);
        } else if (targetPic == profilePic2) {
            user.getPicUrls().setThumbNail_picUrl2(null);
        } else if (targetPic == profilePic3) {
            user.getPicUrls().setThumbNail_picUrl3(null);
        } else if (targetPic == profilePic4) {
            user.getPicUrls().setThumbNail_picUrl4(null);
        }
        DataContainer.getInstance().getUsersRef().child(DataContainer.getInstance().getUid()).setValue(user);
    }

    @Override
    protected void onResume() {
        super.onResume();
        profilePic1.setClickable(true);
        profilePic2.setClickable(true);
        profilePic3.setClickable(true);
        profilePic4.setClickable(true);

    }

    // 소개 텍스트 맥스 제한
    private void setVerification() {
        introEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() <= MAX_CONTENT) {
                    content_error.setText(charSequence.length() + "/" + MAX_CONTENT);
                } else {
                    content_error.setText(MAX_CONTENT + "/" + MAX_CONTENT);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

}