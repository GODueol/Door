package com.example.kwoncheolhyeok.core.LoginActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.Activity.MainActivity;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.CoreProgress;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.GPSInfo;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    // auth
    private FirebaseAuth mAuth;

    // database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("users");

    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.btn_login)
    TextView _loginButton;
    @Bind(R.id.link_signup)
    TextView _signupLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
              login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);

                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }


    public void login() {
        Log.d(TAG, "Login Start");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        CoreProgress.getInstance().startProgressDialog(this);

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // firebase login
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.

                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            getUserInfo(user);

                        } else {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());

                            Toast.makeText(getBaseContext(), task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            onLoginFailed();
                        }

                    }
                });

    }

    private void getUserInfo(FirebaseUser user) {
        if (user != null) {

            CoreProgress.getInstance().startProgressDialog(this);

            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

            // user 정보 읽어오기
            userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    DataContainer.getInstance().setUser(user);
                    onLoginSuccess();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),"Getting UserInfo Cancelled",Toast.LENGTH_SHORT).show();
                    Log.d(getApplication().getClass().getName(),databaseError.getMessage());

                }
            });

        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivityForResult(i, 0);

        // Get GPS
        Location location = GPSInfo.getmInstance(this).getGPSLocation();

        // 데이터베이스에 저장
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("path/to/geofire");
        GeoFire geoFire = new GeoFire(ref);


        geoFire.setLocation(userRef.getKey(), new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    Toast.makeText(getBaseContext(),"There was an error saving the location to GeoFire: " + error,Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(),"Location saved on server successfully!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 프로그레스 바 종료
        CoreProgress.getInstance().stopProgressDialog();
    }

    public void onLoginFailed() {
        _loginButton.setEnabled(true);
        CoreProgress.getInstance().stopProgressDialog();
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            _emailText.setError("enter a valid email address");
            Toast.makeText(getBaseContext(), "이메일이 틀렸습니다.", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (password.isEmpty() || password.length() < 6 || password.length() > 12) {
//            _passwordText.setError("between 6 and 12 alphanumeric characters");
            Toast.makeText(getBaseContext(), "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        getUserInfo(currentUser);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
