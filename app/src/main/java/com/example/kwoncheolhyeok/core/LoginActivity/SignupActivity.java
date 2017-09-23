package com.example.kwoncheolhyeok.core.LoginActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.Activity.MainActivity;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.ProfileModifyActivity.ProfileModifyActivity;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {
    private static final String TAG = "SignupActivity";

    // auth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("users");

    User mUser;

    @Bind(R.id.input_email)
    EditText _emailText;

    @Bind(R.id.input_password)
    EditText _passwordText;

    @Bind(R.id.input_reEnterPassword)
    EditText _reEnterPasswordText;

    @Bind(R.id.input_id)
    EditText _IDText;

    @Bind(R.id.input_age)
    EditText _ageText;

    @Bind(R.id.input_height)
    EditText _heightText;

    @Bind(R.id.input_weight)
    EditText _weightText;

    @Bind(R.id.input_bodytype)
    EditText _bodytype;

    @Bind(R.id.btn_signup)
    Button _signupButton;

    @Bind(R.id.link_login)
    TextView _loginLink;

    static Dialog d;
    private EditText bodytype;
    final String[] values = {"Underweight", "Skinny", "Standard", "Muscular", "Overweight"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_signup_activity);
        ButterKnife.bind(this);


        bodytype = (EditText) findViewById(R.id.input_bodytype);
        bodytype.setFocusable(false);
        bodytype.setClickable(false);
        bodytype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show4();
            }
        });

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    // 선택 정보 입력
                    // Write a message to the database
                    userRef.child(user.getUid()).setValue(mUser);    // 파이어베이스 저장
                    DataContainer.getInstance().setUser(mUser);  // 로컬 저장

                    onSignupSuccess();

                }
                else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }


    public void show4() {

        final Dialog d = new Dialog(SignupActivity.this);
        d.setContentView(R.layout.login_signup_bodytype_dialog);

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
        np.setOnValueChangedListener((NumberPicker.OnValueChangeListener) this);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Value 위치 값을 숫자가 아닌 해당 텍스트로 가져옴
                int pos = np.getValue();
                bodytype.setText(values[pos]); //set the value to textview
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




    public void signup() {
        Log.d(TAG, "Signup");



        if (!validate()) {
            onSignupFailed();
            return;
        }


        _signupButton.setEnabled(false);


        //프로그레스 다이얼로그 이미지만 센터에서 돌아가게
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_dialog_icon_drawable_animation));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.show();


        final String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        final String id = _IDText.getText().toString();
        final String age = _ageText.getText().toString();
        final String height = _heightText.getText().toString();
        final String weight = _weightText.getText().toString();

        mUser = new User(email,id,age,height,weight);

        // TODO: Implement your own signup logic here.

        // firebase 회원가입
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            onSignupFailed();
                        }

                        progressDialog.dismiss();
                    }
                });

        /*
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
        */

    }




    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
//        setResult(RESULT_OK, null);
//        finish();

        Intent i = new Intent(this, MainActivity.class);
        startActivityForResult(i, 0);
    }

    public void onSignupFailed() {
        //Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        String ID = _IDText.getText().toString();
        String Age = _ageText.getText().toString();
        String Height = _heightText.getText().toString();
        String Weight = _weightText.getText().toString();
        String Bodytype = _bodytype.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getBaseContext(), "올바른 이메일 양식으로 작성해주세요.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        else if (password.isEmpty() || password.length() < 4 || password.length() > 12) {
            Toast.makeText(getBaseContext(), "비밀번호는 4자리 이상 12자리 이하로 설정해주세요.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        else if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 12 || !(reEnterPassword.equals(password))) {
            Toast.makeText(getBaseContext(), "패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        else if (ID.isEmpty() || ID.length() < 2) {
            Toast.makeText(getBaseContext(), "두 자리 이상의 아이디로 작성해주세요.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        else if (ID.length()>8){
            Toast.makeText(getBaseContext(), "아이디가 너무 길어요.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        else if (Age.isEmpty() || Age.length()!=2){
            Toast.makeText(getBaseContext(), "올바른 나이를 작성해주세요.", Toast.LENGTH_SHORT).show();
            valid=false;
        }

        else if (Height.isEmpty() || Height.length()!=3){
            Toast.makeText(getBaseContext(), "올바른 키를 작성해 주세요.", Toast.LENGTH_SHORT).show();
            valid=false;
        }

        else if (Weight.isEmpty() || Weight.length()<2 || Weight.length()>3) {
            Toast.makeText(getBaseContext(), "올바른 몸무게로 작성해주세요.", Toast.LENGTH_SHORT).show();
            valid=false;
        }

        else if (Bodytype.isEmpty()){
            Toast.makeText(getBaseContext(), "바디타입을 설정해주세요.", Toast.LENGTH_SHORT).show();
            valid=false;
        }

        return valid;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //implements 부분 구현
    @Override
    public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
        Log.i("value is", "" + newVal);
    }

}