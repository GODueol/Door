package com.example.kwoncheolhyeok.core.LoginActivity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.Activity.MainActivity;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.CoreProgress;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.GPSInfo;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Kwon on 2017-09-27.
 */

public class IntroActivity extends Activity {
    /** Called when the activity is first created. */

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("users");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        getUserInfo(currentUser);

    }

    private void getUserInfo(FirebaseUser user) {
        if (user != null) {

//            CoreProgress.getInstance().startProgressDialog(this);

            // User is signed in
            Log.d(getApplication().getClass().getName(), "onAuthStateChanged:signed_in:" + user.getUid());

            // user 정보 읽어오기
            DatabaseReference userRef = database.getReference("users");
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
            Log.d(getApplication().getClass().getName(), "onAuthStateChanged:signed_out");

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                    startActivity(intent);
                    // 뒤로가기 했을경우 안나오도록 없애주기 >> finish!!
                    finish();
                }
            }, 1800);

        }
    }

    public void onLoginSuccess() {
        Intent i = new Intent(this, MainActivity.class);
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
//        CoreProgress.getInstance().stopProgressDialog();
    }
}
