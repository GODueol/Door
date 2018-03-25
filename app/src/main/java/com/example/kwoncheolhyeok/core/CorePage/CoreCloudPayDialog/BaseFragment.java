package com.example.kwoncheolhyeok.core.CorePage.CoreCloudPayDialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {

    protected View parentView;
    protected FragmentActivity activity;
    protected LayoutInflater inflater;
    protected Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {

        parentView = inflater.inflate(getLayoutResId(), container, false);
        mContext = activity;
        this.inflater = inflater;

        parentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        return parentView;
    }

    @LayoutRes
    public abstract int getLayoutResId();

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    public void onResume() {
        super.onResume();
    }
    public void onPause() {
        super.onPause();
    }

}
