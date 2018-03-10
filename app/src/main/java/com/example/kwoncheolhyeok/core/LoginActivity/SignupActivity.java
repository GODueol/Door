package com.example.kwoncheolhyeok.core.LoginActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.Activity.MainActivity;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.UiUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {
    private static final String TAG = "SignupActivity";

    // auth
    private FirebaseAuth mAuth;

    // database
    DatabaseReference userRef = DataContainer.getInstance().getUsersRef();

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
    EditText _bodyType;

    @Bind(R.id.btn_signup)
    Button _signupButton;

    @Bind(R.id.link_login)
    TextView _loginLink;

    private EditText bodytype;
    final String[] values = {"Underweight", "Skinny", "Standard", "Muscular", "Overweight"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_signup_activity);
        ButterKnife.bind(this);


        bodytype = findViewById(R.id.input_bodytype);
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
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    private void setUserInfo(FirebaseUser user) {
        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

            // 선택 정보 입력
            // Write a message to the database
            userRef.child(user.getUid()).setValue(mUser)    // 파이어베이스 저장
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DataContainer.getInstance().setUser(mUser);  // 로컬 저장
                            onSignupSuccess();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            onSignupFailed(e);
                            Log.d(getApplication().getClass().getName(), e.getMessage());
                        }
                    });
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }
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

        TextView b1 = d.findViewById(R.id.button1);
        TextView b2 = d.findViewById(R.id.button2);

        final NumberPicker np = d.findViewById(R.id.numberPicker1);

        np.setMinValue(0); //from array first value
        np.setMaxValue(values.length - 1); //to array last value
        np.setValue(values.length - 3);
        np.setDisplayedValues(values);
        np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);


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

        try {
            validate();
        } catch (Exception e) {
            onSignupFailed(e);
            return;
        }

        _signupButton.setEnabled(false);

        UiUtil.getInstance().startProgressDialog(this);

        final String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        final String id = _IDText.getText().toString();
        final int age = Integer.parseInt(_ageText.getText().toString());
        final int height = Integer.parseInt(_heightText.getText().toString());
        final int weight = Integer.parseInt(_weightText.getText().toString());
        final String bodyType = _bodyType.getText().toString();
        mUser = new User(email, id, age, height, weight, bodyType);

        // firebase 회원가입
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.

                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            setUserInfo(user);
                        } else {
                            onSignupFailed(task.getException());
                        }
                    }
                });
    }

    public void onSignupSuccess() {
        Toast.makeText(getBaseContext(), "Join Success", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void onSignupFailed(Exception e) {
        Toast.makeText(getBaseContext(), "Join Failed : " + e.getMessage(), Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
        UiUtil.getInstance().stopProgressDialog();
    }

    @SuppressLint("SetTextI18n")
    public void validate() throws Exception {

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        String ID = _IDText.getText().toString();
        String Age = _ageText.getText().toString();
        String Height = _heightText.getText().toString();
        String Weight = _weightText.getText().toString();
        String Bodytype = _bodyType.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            throw new Exception("올바른 이메일 양식으로 작성해주세요.");
        } else if (password.isEmpty() || password.length() < 6 || password.length() > 12) {
            throw new Exception("비밀번호는 6자리 이상 12자리 이하로 설정해주세요.");
        } else if (reEnterPassword.isEmpty() || reEnterPassword.length() < 6 || reEnterPassword.length() > 12 || !(reEnterPassword.equals(password))) {
            throw new Exception("패스워드가 일치하지 않습니다.");
        } else if (ID.isEmpty() || ID.length() < 2 || ID.length() > 10) {
            throw new Exception("두 자리 이상의 아이디로 작성해주세요.");
        } else if (Age.isEmpty() || Age.length() != 2 || Integer.parseInt(Age) > 100) {
            throw new Exception("올바른 나이를 작성해주세요.");
        } else if (Integer.parseInt(Age) < 20) {
            throw new Exception("미성년자는 가입할 수 없습니다.");
        } else if (Height.isEmpty() || Height.length() != 3 || Integer.parseInt(Height) < 100 || Integer.parseInt(Height) > 220) {
            throw new Exception("올바른 키를 작성해 주세요.");
        } else if (Weight.isEmpty() || Weight.length() < 2 || Weight.length() > 3 || Integer.parseInt(Weight) < 40 || Integer.parseInt(Weight) > 140) {
            throw new Exception("올바른 몸무게로 작성해주세요.");
        } else if (Bodytype.isEmpty()) {
            _bodyType.setText("Standard");
//            throw new Exception("바디타입을 설정해주세요.");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //implements 부분 구현
    @Override
    public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
        Log.i("value is", "" + newVal);
    }

}