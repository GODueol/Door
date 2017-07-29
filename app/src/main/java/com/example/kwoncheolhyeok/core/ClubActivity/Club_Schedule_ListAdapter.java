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
 * Created by KwonCheolHyeok on 2017-01-12.
 */

public class Club_Schedule_ListAdapter extends BaseAdapter {

    private List<String> items;
    private Context context;

    public Club_Schedule_ListAdapter(List<String> items, Context context) {
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

        Club_Schedule_ListAdapter.Holder holder = null;

        if(convertView == null){
            LayoutInflater inflater  = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.club_schedule_list_item, parent, false);

            holder = new Club_Schedule_ListAdapter.Holder();
            holder.schedule_pic = (ImageView) convertView.findViewById(R.id.schedule_pic);
            holder.schedule_name = (TextView) convertView.findViewById(R.id.schedule_name);
            holder.schedule_id_date_watch = (TextView) convertView.findViewById(R.id.schedule_id_date_watch);

        }
        else{
            holder = (Club_Schedule_ListAdapter.Holder) convertView.getTag();
        }

//         Listview 안의 숫자를 text를 표시해줌

//        final String content = String.valueOf(getItem(position));
//
//        holder.listItem.setText(content);
//        convertView.setTag(holder);


        return convertView;
    }

    private class Holder{


        ImageView schedule_pic;

        public TextView schedule_name;

        public TextView schedule_id_date_watch;

    }
}