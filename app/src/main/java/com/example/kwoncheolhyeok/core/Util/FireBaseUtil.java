package com.example.kwoncheolhyeok.core.Util;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by gimbyeongjin on 2017. 10. 6..
 */

public class FireBaseUtil {
    public static final String currentLocationPath = "location/users";
    private static final FireBaseUtil ourInstance = new FireBaseUtil();

    public static FireBaseUtil getInstance() {
        return ourInstance;
    }

    private FireBaseUtil() {
    }

    public void setImage(String filePath, final ImageView targetImageView) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filePath);
        Glide.with(targetImageView.getContext())
                .load(storageReference)
                .into(targetImageView);
    }

    public void setBackgroundImage(String filePath, final View targetView) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filePath);
        Glide.with(targetView.getContext())
                .load(storageReference)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable drawable, Transition<? super Drawable> transition) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            targetView.setBackground(drawable);
                        }
                    }
                });
    }

    @NonNull
    public String getParentPath(String uuid) {
        return "profile/pic/" + uuid + "/";
    }

    @NonNull
    public String getParentPath() {
        return getParentPath(DataContainer.getInstance().getUid());
    }

}
