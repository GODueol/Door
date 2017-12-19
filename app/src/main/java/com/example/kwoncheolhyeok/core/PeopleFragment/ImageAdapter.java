package com.example.kwoncheolhyeok.core.PeopleFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.IndexedTreeMap;

import java.io.Serializable;
import java.util.Comparator;

public class ImageAdapter extends BaseAdapter {

    private IndexedTreeMap<Item, Boolean> mItems = new IndexedTreeMap<>(new Comparator<Item>() {
        @Override
        public int compare(Item item1, Item item2) {
            if(item2.getUuid().equals(DataContainer.getInstance().getUid())) return 1;   // 1. 본인계정
            if(item1.getUuid().equals(DataContainer.getInstance().getUid())) return -1;   // 1. 본인계정
            if(item1.distance != item2.distance) return (int) (item1.distance - item2.distance);    // 2. 거리
            return 1;
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

    static class ViewHolder{
        ImageView imageView;
        TextView textView;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int i, View v, ViewGroup viewGroup) {
        ViewHolder holder;
        Item item;
        try {
            item = getItem(i);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        if (v == null) {
            v = mInflater.inflate(R.layout.square_grid_view, viewGroup, false);

            holder = new ViewHolder();
            holder.imageView = v.findViewById(R.id.picture);
            holder.textView = v.findViewById(R.id.text);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        // 프사 출력

        Glide.with(mInflater.getContext() /* context */)
                .load(item.getPicUrl())
                .into(holder.imageView);

        // 거리 출력
        holder.textView.setText(String.format("%.1fkm", item.distance/1000));
        holder.textView.setTextSize((float) 15.5);

        return v;
    }

    void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public static class Item implements Serializable {

        float distance;
        String uuid;
        User user;
        String picUrl;

        public Item(float distance, String uuid, User user, String picUrl) {
            this.distance = distance;
            this.uuid = uuid;
            this.user = user;
            this.picUrl = picUrl;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        float getDistance() {
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