package com.example.kwoncheolhyeok.core.CorePage;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kwoncheolhyeok.core.Entity.CoreListItem;
import com.example.kwoncheolhyeok.core.Entity.CorePost;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;

import java.util.List;

/**
 * Created by KwonCheolHyeok on 2017-01-17.
 */

public class CoreListAdapter extends BaseAdapter {

    private List<CoreListItem> posts;
    private Context context;

    public CoreListAdapter(List<CoreListItem> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public CoreListItem getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CoreListAdapter.Holder holder = null;

        CoreListItem coreListItem;
        try {
            coreListItem = getItem(position);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        if(convertView == null){
            LayoutInflater inflater  = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.core_list_item, parent, false);

            holder = new CoreListAdapter.Holder();
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

            convertView.setTag(holder);
        }
        else{
            holder = (CoreListAdapter.Holder) convertView.getTag();
        }

        User user = coreListItem.getUser();
        CorePost corePost = coreListItem.getCorePost();

        Glide.with(context /* context */)
                .load(user.getPicUrls().getPicUrl1())
                .into(holder.core_pic);

        Glide.with(context /* context */)
                .load(corePost.getPictureUrl())
                .into(holder.core_img);

        holder.core_id.setText(user.getId());
        holder.core_subprofile.setText(TextUtils.join("/", new String[]{Integer.toString(user.getAge()), Integer.toString(user.getHeight()),
                Integer.toString(user.getWeight()), user.getBodyType()}));
        holder.core_date.setText(DataContainer.getInstance().convertBeforeHour(corePost.getWriteDate()) + "시간 전");
        holder.core_contents.setText(corePost.getText());

        return convertView;
    }

    private class Holder{


        ImageView core_pic, core_img ;
        ImageButton core_setting, core_heart;
        public TextView core_id, core_subprofile, core_date, core_contents, core_media, core_heart_count;

    }

}