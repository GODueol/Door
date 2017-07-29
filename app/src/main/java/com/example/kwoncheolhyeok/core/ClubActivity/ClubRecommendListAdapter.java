package com.example.kwoncheolhyeok.core.ClubActivity;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.R;

import java.util.List;

/**
 Board와 Club Tab의 내용이 겹쳐보이지 않는것을 확인하기 위한 TabFragment 2,3 List Adapter
 */
public class ClubRecommendListAdapter extends BaseAdapter {

    private List<String> items;
    private Context context;

    public ClubRecommendListAdapter(List<String> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder = null;

        if(convertView == null){
            LayoutInflater inflater  = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.club_recommend_list_item, parent, false);

            holder = new Holder();
            holder.clubstyle = (TextView) convertView.findViewById(R.id.club_style);
            holder.clubname = (TextView) convertView.findViewById(R.id.club_name);
            holder.clubfilter = (TextView) convertView.findViewById(R.id.club_filter);
            holder.imageView = (ImageView) convertView.findViewById(R.id.lol);

        }
        else{
            holder = (Holder) convertView.getTag();
        }

//         Listview 안의 숫자를 text를 표시해줌
//        final String content = String.valueOf(getItem(position));
//
//        holder.listItem.setText(content);
//        convertView.setTag(holder);


        return convertView;
    }

    private class Holder{
        public TextView clubstyle;
        public TextView clubname;
        public TextView clubfilter;
        ImageView imageView;

    }
}