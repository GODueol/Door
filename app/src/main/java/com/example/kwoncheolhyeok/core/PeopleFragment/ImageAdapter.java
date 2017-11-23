package com.example.kwoncheolhyeok.core.PeopleFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private List<Item> mItems = new ArrayList<>();
    private final LayoutInflater mInflater;

    ImageAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    void addItem(Item item){
        mItems.add(item);
        // Distance 기준 정렬
        Collections.sort(mItems, new Comparator<Item>() {

            @Override
            public int compare(Item item1, Item item2 ){
                return (int) (item1.distance - item2.distance);
            }
        }) ;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Item getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;
        TextView name;
        Item item;
        try {
            item = getItem(i);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        if (v == null) {
            v = mInflater.inflate(R.layout.square_grid_view, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        picture = (ImageView) v.getTag(R.id.picture);
        name = (TextView) v.getTag(R.id.text);

        // 프사 출력
        FireBaseUtil fbUtil = FireBaseUtil.getInstance();
        fbUtil.setImage(fbUtil.getParentPath(item.uuid) + "profilePic1.jpg", picture);

        // 거리 출력
        name.setText(String.format("%.1fkm", item.distance/1000));

        return v;
    }

    void clear() {
        mItems.clear();

    }

    static class Item implements Serializable {

        float distance;
        String uuid;
        User user;

        Item(float distance, String uuid, User user) {
            this.distance = distance;
            this.uuid = uuid;
            this.user = user;
        }

        public float getDistance() {
            return distance;
        }

        public void setDistance(float distance) {
            this.distance = distance;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }
}