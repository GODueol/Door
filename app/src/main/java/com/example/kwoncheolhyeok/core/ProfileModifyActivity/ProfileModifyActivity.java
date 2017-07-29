package com.example.kwoncheolhyeok.core.ProfileModifyActivity;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.R;


public class ProfileModifyActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener{

    Toolbar toolbar = null;
    NumberPicker numberpicker1 = null;
    NumberPicker numberpicker2 = null;
    NumberPicker numberpicker3 = null;
    NumberPicker numberpicker4 = null;

    static Dialog d;
    private TextView min_age_filter , max_age_filter, min_height_filter, max_height_filter , min_weight_filter, max_weight_filter, min_bodytype_filter, max_bodytype_filter;

    final String[] values = {"Underweight_0", "Skinny_1", "Standard_2", "Muscular_3", "Overweight_4"};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_modify_activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        getSupportActionBar().setDisplayShowHomeEnabled(true); //홈 아이콘을 숨김처리합니다.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);


        numberpicker1 = (NumberPicker) findViewById(R.id.numberPicker1);
        numberpicker1.setMinValue(19);
        numberpicker1.setMaxValue(100);
        numberpicker1.setValue(25);
        numberpicker1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberpicker1.setWrapSelectorWheel(false);
        setDividerColor(numberpicker1, Color.WHITE);
//        numberpicker1.setTextColor(getResources().getColor(R.color.colorPrimary));
//        numberpicker1.setTextColorResource(R.color.colorPrimary);

        numberpicker2 = (NumberPicker) findViewById(R.id.numberPicker2);
        numberpicker2.setMinValue(150);
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
                min_bodytype_filter.setText(String.valueOf(np.getValue())); //set the value to textview
                d.dismiss();
                max_bodytype_filter.setText(String.valueOf(np2.getValue())); //set the value to textview
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }



}