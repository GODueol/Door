package com.example.kwoncheolhyeok.core.Util;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

/**
 * Created by gimbyeongjin on 2017. 10. 6..
 */

public class FireBaseUtil {
    private static final FireBaseUtil ourInstance = new FireBaseUtil();

    public static FireBaseUtil getInstance() {
        return ourInstance;
    }

    private FireBaseUtil() {
    }

    public void setImage(String filePath, final ImageView targetImageView) {
        FirebaseStorage.getInstance().getReference().child(filePath)
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(targetImageView.getContext() /* context */)
                        .load(uri)
                        .into(targetImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d(this.getClass().getName(), exception.getMessage());
            }
        });
    }

    @NonNull
    public String getParentPath() {
        return "profile/pic/" + DataContainer.getInstance().getUid() + "/";
    }

}