package com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kwoncheolhyeok.core.R;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017-12-04.
 */

public class messageRecyclerAdapter extends  RecyclerView.Adapter<messageRecyclerAdapter.ViewHolder>{

    private RecyclerViewClickListener mListener;
    private List<RoomVO> roomList;
    private int itemLayout;



    public messageRecyclerAdapter(List<RoomVO> items, int itemLayout, RecyclerViewClickListener listener){
        this.roomList = items;
        this.itemLayout = itemLayout;
        mListener = listener;
    }

    @Override
    public messageRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout,parent,false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(messageRecyclerAdapter.ViewHolder holder, int position) {
        RoomVO room = roomList.get(position);
        holder.content.setText(room.getLastChat());
        holder.nickname.setText(room.getTargetNickName());
        holder.profile.setText(room.getTargetProfile());

        Long lastChatTime = room.getLastChatTime();
        Long lastViewTime = room.getLastViewTime();
        if(lastViewTime >= lastChatTime){
            holder.layout.setBackgroundColor(Color.WHITE);
        }else{
            holder.layout.setBackgroundColor(Color.GRAY);
        }
        DateUtil dateUtil = new DateUtil(lastChatTime);
        String preTime = dateUtil.getPreTime();
        holder.date.setText(preTime);
        Glide.with(holder.img.getContext()).load(room.getTargetUri()).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public RoomVO getItemRoomVO(int position){
        return roomList.get(position);
    }

    /**
     * 뷰 재활용을 위한 viewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public RelativeLayout layout;
        public ImageView img;
        public TextView content;
        public TextView nickname;
        public TextView profile;
        public TextView date;
        private RecyclerViewClickListener mListener;


        public ViewHolder(View itemView, RecyclerViewClickListener listener){
            super(itemView);

            content = (TextView) itemView.findViewById(R.id.chat_content);
            nickname = (TextView) itemView.findViewById(R.id.userNick);
            profile = (TextView)itemView.findViewById(R.id.userProfile);
            date = (TextView) itemView.findViewById(R.id.date);
            img = (ImageView) itemView.findViewById(R.id.profile_image);
            layout = (RelativeLayout) itemView.findViewById(R.id.layout);

            mListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }

    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }
}
