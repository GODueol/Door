package com.example.kwoncheolhyeok.core.FriendsActivity;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.messageRecyclerAdapter;
import com.example.kwoncheolhyeok.core.R;

import java.util.List;

/**
 * Created by kimbyeongin on 2017-12-16.
 */

public class userListAdapter extends RecyclerView.Adapter<userListAdapter.userHolder> {

    private List<User> users;
    userListAdapter(List<User> users){
        this.users = users;
    }

    @Override
    public userHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_list_item,viewGroup,false);
        return new userHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(userHolder userHolder, int i) {
        User user = users.get(i);
        userHolder.idText.setText(user.getId());
        userHolder.subProfileText.setText(user.getIntro());
        userHolder.dateText.setText("" +user.getLoginDate());
    }

    @Override
    public int getItemCount() {
        return users.size();
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

}
