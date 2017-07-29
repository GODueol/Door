package com.example.kwoncheolhyeok.core.CorePage;

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
            holder.core_date = (TextView) convertView.findViewById(R.id.core_date);
            holder.core_contents = (TextView) convertView.findViewById(R.id.core_contents);

        }
        else{
            holder = (Core_List_Adapter.Holder) convertView.getTag();
        }

//         Listview 안의 숫자를 text를 표시해줌

//        final String content = String.valueOf(getItem(position));
//
//        holder.listItem.setText(content);
//        convertView.setTag(holder);


        return convertView;
    }

    private class Holder{


        ImageView core_pic, core_img ;

        public TextView core_id, core_date, core_contents ;

    }
}