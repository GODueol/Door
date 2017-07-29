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
 * Created by KwonCheolHyeok on 2016-12-28.
 */

public class ClubTimelineContentsListAdapter extends BaseAdapter {

    private List<String> items;
    private Context context;

    public ClubTimelineContentsListAdapter(List<String> items, Context context) {
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

        ClubTimelineContentsListAdapter.Holder holder = null;

        if(convertView == null){
            LayoutInflater inflater  = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.club_activity_timeline_list_item, parent, false);

            holder = new ClubTimelineContentsListAdapter.Holder();
            holder.id_writer = (TextView) convertView.findViewById(R.id.id_writer);
            holder.date_writer = (TextView) convertView.findViewById(R.id.date_writer);
            holder.contents_writer = (TextView) convertView.findViewById(R.id.contents_writer);
            holder.pic_writer = (ImageView) convertView.findViewById(R.id.pic_writer);

            holder.reply_count = (TextView) convertView.findViewById(R.id.reply_count);
            holder.reply_contents = (TextView) convertView.findViewById(R.id.reply_contents);
            holder.reply_id = (TextView) convertView.findViewById(R.id.reply_id);
            holder.reply_date = (TextView) convertView.findViewById(R.id.reply_date);
            holder.pic_reply = (ImageView) convertView.findViewById(R.id.pic_reply);

        }
        else{
            holder = (ClubTimelineContentsListAdapter.Holder) convertView.getTag();
        }

//         Listview 안의 숫자를 text를 표시해줌
//        final String content = String.valueOf(getItem(position));
//
//        holder.listItem.setText(content);
//        convertView.setTag(holder);


        return convertView;
    }

    private class Holder{

        ImageView pic_writer;

        public TextView id_writer;

        public TextView date_writer;

        public TextView contents_writer;

        public TextView reply_count;

        ImageView pic_reply;

        public TextView reply_contents;

        public TextView reply_id;

        public TextView reply_date;



    }
}