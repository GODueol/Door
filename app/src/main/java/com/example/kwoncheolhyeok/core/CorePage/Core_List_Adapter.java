package com.example.kwoncheolhyeok.core.CorePage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.R;

import java.util.List;

/**
 * Created by KwonCheolHyeok on 2017-01-17.
 */

public class Core_List_Adapter extends BaseAdapter {

    private List<String> items;
    private Context context;

    public Core_List_Adapter(List<String> items, Context context) {
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

        Core_List_Adapter.Holder holder = null;

        if(convertView == null){
            LayoutInflater inflater  = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.core_list_item, parent, false);

            holder = new Core_List_Adapter.Holder();
            holder.core_pic = (ImageView) convertView.findViewById(R.id.core_pic);
            holder.core_img = (ImageView) convertView.findViewById(R.id.core_img);

            holder.core_id = (TextView) convertView.findViewById(R.id.core_id);
            holder.core_subprofile=(TextView)convertView.findViewById(R.id.sub_profile);
            holder.core_date = (TextView) convertView.findViewById(R.id.core_date);
            holder.core_contents = (TextView) convertView.findViewById(R.id.core_contents);

            holder.core_media=(TextView)convertView.findViewById(R.id.media_player_txt);
            holder.core_setting=(ImageButton)convertView.findViewById(R.id.setting);

            holder.core_heart_count = (TextView) convertView.findViewById(R.id.heart_count_txt);
            holder.core_heart=(ImageButton)convertView.findViewById(R.id.heart_count);

        }
        else{
            holder = (Core_List_Adapter.Holder) convertView.getTag();
        }


        return convertView;
    }

    private class Holder{


        ImageView core_pic, core_img ;
        ImageButton core_setting, core_heart;
        public TextView core_id, core_subprofile, core_date, core_contents, core_media, core_heart_count;

    }
}