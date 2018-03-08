package com.example.kwoncheolhyeok.core.MessageActivity;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.MessageActivity.util.DateUtil;
import com.example.kwoncheolhyeok.core.MessageActivity.util.RoomVO;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.GlideApp;

import java.util.List;

/**
 * Created by Administrator on 2017-12-04.
 */

public class messageRecyclerAdapter extends RecyclerView.Adapter<messageRecyclerAdapter.ViewHolder> {

    private RecyclerViewClickListener mListener;
    private List<RoomVO> roomList;
    private int itemLayout;
    private OnRemoveChattingListCallback onRemoveChattingListCallback;

    public interface OnRemoveChattingListCallback {
        void onRemove(String s);
    }

    public messageRecyclerAdapter(List<RoomVO> items, int itemLayout, RecyclerViewClickListener listener, OnRemoveChattingListCallback onRemoveChattingListCallback) {
        this.roomList = items;
        this.itemLayout = itemLayout;
        this.onRemoveChattingListCallback = onRemoveChattingListCallback;
        mListener = listener;
    }

    @Override
    public messageRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(messageRecyclerAdapter.ViewHolder holder, int position) {
        final RoomVO room = roomList.get(position);
        holder.content.setText(room.getLastChat());
        holder.nickname.setText(room.getTargetNickName());
        holder.profile.setText(room.getTargetProfile());
        holder.badge.setText(Integer.toString(room.getBadgeCount()));
        Long lastChatTime = room.getLastChatTime();
        Long lastViewTime = room.getLastViewTime();
        if (lastViewTime >= lastChatTime) {
            holder.layout.setBackgroundColor(Color.WHITE);
        } else {
            holder.layout.setBackgroundColor(Color.GRAY);
        }
        DateUtil dateUtil = new DateUtil(lastChatTime);
        String preTime = dateUtil.getPreTime();
        holder.date.setText(preTime);

        int width = holder.img.getWidth();
        int height = holder.img.getHeight();

        GlideApp.with(holder.img.getContext())
                .load(room.getTargetUrl())
                .override(width, height)
                .placeholder(R.drawable.a)
                .centerCrop()
                .into(holder.img);

        holder.edit_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popup = new PopupMenu(view.getContext(), view);
                popup.getMenuInflater().inflate(R.menu.chatting_list_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int i = menuItem.getItemId();
                        if (i == R.id.remove) {
                            onRemoveChattingListCallback.onRemove(room.getTargetUuid());
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public RoomVO getItemRoomVO(int position) {
        return roomList.get(position);
    }

    /**
     * 뷰 재활용을 위한 viewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public RelativeLayout layout;
        public ImageView img;
        public TextView content;
        public TextView nickname;
        public TextView profile;
        public TextView date;
        public ImageView edit_message;
        public TextView badge;
        private RecyclerViewClickListener mListener;


        public ViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);

            content = (TextView) itemView.findViewById(R.id.chat_content);
            nickname = (TextView) itemView.findViewById(R.id.userNick);
            profile = (TextView) itemView.findViewById(R.id.userProfile);
            date = (TextView) itemView.findViewById(R.id.date);
            img = (ImageView) itemView.findViewById(R.id.profile_image);
            layout = (RelativeLayout) itemView.findViewById(R.id.layout);
            edit_message = (ImageView) itemView.findViewById(R.id.edit_message);
            badge = (TextView) itemView.findViewById(R.id.badge_chat_row);
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
