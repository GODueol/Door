package com.example.kwoncheolhyeok.core.LoginActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.Activity.MainActivity;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
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

    @Bind(R.id.btn_signup)
    Button _signupButton;

    @Bind(R.id.link_login)
    TextView _loginLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_signup_activity);
        ButterKnife.bind(this);

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

    public void signup() {
        Log.d(TAG, "Signup");



        if (!validate()) {
            onSignupFailed();
            return;
        }


        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
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

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getBaseContext(), "올바른 이메일 양식으로 작성해주세요.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        else if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            Toast.makeText(getBaseContext(), "비밀번호는 4자리 이상 10자리 이하로 설정해주세요.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        else if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            Toast.makeText(getBaseContext(), "패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        else if (ID.isEmpty() || ID.length() < 3) {
            Toast.makeText(getBaseContext(), "최소 세자리 이상의 아이디로 작성해주세요.", Toast.LENGTH_SHORT).show();
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

        else if (Weight.isEmpty() || Weight.length()!=2){
            Toast.makeText(getBaseContext(), "올바른 몸무게로 작성해주세요.", Toast.LENGTH_SHORT).show();
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
}