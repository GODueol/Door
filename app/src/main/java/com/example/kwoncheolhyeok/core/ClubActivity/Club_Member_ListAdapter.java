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
 * Created by KwonCheolHyeok on 2016-12-29.
 */

public class Club_Member_ListAdapter extends BaseAdapter {

    private List<String> items;
    private Context context;

    public Club_Member_ListAdapter(List<String> items, Context context) {
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

        Club_Member_ListAdapter.Holder holder = null;

        if(convertView == null){
            LayoutInflater inflater  = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.club_member_list_item, parent, false);

            holder = new Club_Member_ListAdapter.Holder();
            holder.member_image = (ImageView) convertView.findViewById(R.id.member_image);
            holder.member_type = (TextView) convertView.findViewById(R.id.member_type);
            holder.member_id = (TextView) convertView.findViewById(R.id.member_id);
            holder.member_join_date = (TextView) convertView.findViewById(R.id.member_join_date);
            holder.btn_setting = (ImageView) convertView.findViewById(R.id.btn_setting);

        }
        else{
            holder = (Club_Member_ListAdapter.Holder) convertView.getTag();
        }

//         Listview 안의 숫자를 text를 표시해줌
//        final String content = String.valueOf(getItem(position));
//
//        holder.listItem.setText(content);
//        convertView.setTag(holder);


        return convertView;
    }

    private class Holder{

        ImageView member_image;

        public TextView member_type;

        public TextView member_id;

        public TextView member_join_date;

        ImageView btn_setting;


    }
}