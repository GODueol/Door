package com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

import com.example.kwoncheolhyeok.core.MessageActivity.ChattingActivity;
import com.example.kwoncheolhyeok.core.MessageActivity.MessageActivity;
import com.example.kwoncheolhyeok.core.R;

import java.util.List;

/**
 * Created by Administrator on 2017-12-04.
 */

public class messageRecyclerAdapter extends  RecyclerView.Adapter<messageRecyclerAdapter.ViewHolder> implements View.OnClickListener{

    private List<MessageVO> messageList;
    private int itemLayout;

    public messageRecyclerAdapter(List<MessageVO> items, int itemLayout){
        this.messageList = items;
        this.itemLayout = itemLayout;
    }

    @Override
    public messageRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout,parent,false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(messageRecyclerAdapter.ViewHolder holder, int position) {
        MessageVO message = messageList.get(position);
        holder.nickname.setText(message.getNickname());
        holder.content.setText(message.getContent());
        holder.editImg.setBackgroundResource(message.getEditimg());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(view.getContext(), ChattingActivity.class);
        view.getContext().startActivity(i);
    }

    /**
     * 뷰 재활용을 위한 viewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView img;
        public TextView nickname;
        public TextView content;
        public  ImageView editImg;


        public ViewHolder(View itemView){
            super(itemView);

            content = (TextView) itemView.findViewById(R.id.message_content);
            nickname = (TextView) itemView.findViewById(R.id.name);
            editImg = (ImageView) itemView.findViewById(R.id.edit_message);
        }

    }
}
