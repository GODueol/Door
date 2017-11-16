package com.example.kwoncheolhyeok.core.PeopleFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;

import java.util.ArrayList;
import java.util.List;


public class ImageAdapter extends BaseAdapter {


    private Context context = null;
    ImageView imageView = null;

    private final List<Item> mItems = new ArrayList<Item>();
    private final LayoutInflater mInflater;

    public ImageAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void addItem(Item item){
        mItems.add(item);
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;
        TextView name;

        Item item = getItem(i);

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

    public static class Item {
        public float distance;
        public String uuid;

        public Item(float distance, String uuid) {
            this.distance = distance;
            this.uuid = uuid;
        }
    }
}


//mItems.add(new Item("CORE 24", R.drawable.a));
//        mItems.add(new Item("CORE 45", R.drawable.b));
//        mItems.add(new Item("CORE 66", R.drawable.c));
//        mItems.add(new Item("CORE 75", R.drawable.d));
//        mItems.add(new Item("CORE 102", R.drawable.e));
//        mItems.add(new Item("CORE 302", R.drawable.f));
//        mItems.add(new Item("CORE 51", R.drawable.g));
//        mItems.add(new Item("CORE 18", R.drawable.h));
//        mItems.add(new Item("CORE 7", R.drawable.i));
//        mItems.add(new Item("CORE 95", R.drawable.j));
//        mItems.add(new Item("CORE 244", R.drawable.k));
//        mItems.add(new Item("CORE 109", R.drawable.l));
//        mItems.add(new Item("CORE 142", R.drawable.m));
//        mItems.add(new Item("CORE 78", R.drawable.n));
//        mItems.add(new Item("CORE 93", R.drawable.o));
//        mItems.add(new Item("CORE 79", R.drawable.p));
//        mItems.add(new Item("CORE 51", R.drawable.q));
//        mItems.add(new Item("CORE 63", R.drawable.r));
//        mItems.add(new Item("CORE 101", R.drawable.s));
//        mItems.add(new Item("CORE 30", R.drawable.t));
