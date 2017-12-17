package com.example.kwoncheolhyeok.core.FriendsActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.messageRecyclerAdapter;
import com.example.kwoncheolhyeok.core.PeopleFragment.FullImageActivity;
import com.example.kwoncheolhyeok.core.PeopleFragment.ImageAdapter;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kimbyeongin on 2017-12-16.
 */

public class userListAdapter extends RecyclerView.Adapter<userListAdapter.userHolder> {

    private List<Item> items;
    userListAdapter(List<Item> items){
        this.items = items;
    }

    @Override
    public userHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_list_item,viewGroup,false);
        return new userHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(userHolder userHolder, int i) {
        final Item item = items.get(i);
        final User user = item.getUser();
        Glide.with(userHolder.profilePicImage.getContext()).load(user.getPicUrls().getPicUrl1()).into(userHolder.profilePicImage);
        userHolder.idText.setText(user.getId());
        userHolder.subProfileText.setText(TextUtils.join("/", new String[]{Integer.toString(user.getAge()), Integer.toString(user.getHeight()),
                Integer.toString(user.getWeight()), user.getBodyType()}));
        SimpleDateFormat dateFormat = DataContainer.dateFormat;
        userHolder.dateText.setText( dateFormat.format(new Date(item.getDate())));

        userHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent p = new Intent(view.getContext(), FullImageActivity.class);

                p.putExtra("item", new ImageAdapter.Item(0, item.getUuid(), user, ""));

                view.getContext().startActivity(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class userHolder extends messageRecyclerAdapter.ViewHolder {
        ImageView profilePicImage;
        TextView idText;
        TextView subProfileText;
        TextView dateText;
        userHolder(View itemView) {
            super(itemView);
            profilePicImage = itemView.findViewById(R.id.profile_image);
            idText = itemView.findViewById(R.id.lblListItem);
            subProfileText = itemView.findViewById(R.id.sub_profile);
            dateText = itemView.findViewById(R.id.date);
        }
    }

    public static class Item {
        User user;
        long date;
        String uuid;

        public Item(User user, long date, String uuid) {
            this.user = user;
            this.date = date;
            this.uuid = uuid;
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

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }
    }

}
