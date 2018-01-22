package com.example.kwoncheolhyeok.core.PeopleFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.GlideApp;
import com.example.kwoncheolhyeok.core.Util.IndexedTreeSet;

import java.util.Comparator;
import java.util.HashMap;

public class ImageAdapter extends BaseAdapter {

    private IndexedTreeSet<GridItem> mItems = new IndexedTreeSet<>(new Comparator<GridItem>() {
        @Override
        public int compare(GridItem item1, GridItem item2) {
            if(item1.getUuid().equals(item2.getUuid())) return 0;
            if(item2.getUuid().equals(DataContainer.getInstance().getUid())) return 1;   // 1. 본인계정
            if(item1.getUuid().equals(DataContainer.getInstance().getUid())) return -1;   // 1. 본인계정
            if(item1.distance != item2.distance) {
                float diff = (item1.distance - item2.distance);
                if(diff<0.0) return -1;
                if(diff>0.0) return 1;
                return 0;
            }

            int compReturn = item1.getUuid().compareTo(item2.getUuid());
            if(compReturn<0) return -1;
            if(compReturn>0) return 1;
            return 0;
        }
    });

    private HashMap<String, GridItem> itemHashMap = new HashMap<>();

    private final LayoutInflater mInflater;

    ImageAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public GridItem getItem(int i) {
        return (GridItem) mItems.toArray()[i];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder{
        ImageView imageView;
        TextView textView;
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public View getView(int i, View v, ViewGroup viewGroup) {
        ViewHolder holder;
        GridItem item;
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
        GlideApp.with(mInflater.getContext() /* context */)
                .load(item.getPicUrl())
                .placeholder(R.drawable.a)
                .into(holder.imageView);

        // 거리 출력
        holder.textView.setText(item.getSummaryUser().getCorePostCount()+ " CORE" );
        holder.textView.setTextSize((float) 15.5);

        return v;
    }

    void addItem(GridItem item){
        if(itemHashMap.containsKey(item.getUuid())){
            if(!mItems.remove(itemHashMap.get(item.getUuid()))){
                Log.d("kbj r itemHashMapItem", itemHashMap.get(item.getUuid()).toString());
                Log.d("kbj r mItems", mItems.toString());
            }
        }
        if(!mItems.add(item)){
            Log.d("kbj a item", item.toString());
            Log.d("kbj a mItems", mItems.toString());

        }
        itemHashMap.put(item.getUuid(), item);
    }

    GridItem getItem(String uuid){
        return itemHashMap.get(uuid);
    }

    void remove(String uuid){
        if(!itemHashMap.containsKey(uuid)) return;
        if(mItems.remove(itemHashMap.get(uuid))) {
            itemHashMap.remove(uuid);
        }
    }

    void clear() {
        mItems.clear();
        itemHashMap.clear();
        notifyDataSetChanged();
    }

}