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
import com.example.kwoncheolhyeok.core.Util.IndexedTreeMap;

import java.io.Serializable;
import java.util.Comparator;

public class ImageAdapter extends BaseAdapter {

    private IndexedTreeMap<Item, Boolean> mItems = new IndexedTreeMap<>(new Comparator<Item>() {
        @Override
        public int compare(Item item, Item t1) {
            if(item.distance == t1.distance) return item.getUuid().compareTo(t1.getUuid());
            return (int) (item.distance - t1.distance);
        }
    });

    private final LayoutInflater mInflater;

    ImageAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    void addItem(Item item){
        mItems.put(item,true);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Item getItem(int i) {
        return mItems.getEntry(i).getKey();
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
        fbUtil.setImage(fbUtil.getParentPath(item.getUuid()) + "profilePic1.jpg", picture);

        // 거리 출력
        name.setText(String.format("%.1fkm", item.distance/1000));

        return v;
    }

    void clear() {
        mItems.clear();
    }

    public static class Item implements Serializable {

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