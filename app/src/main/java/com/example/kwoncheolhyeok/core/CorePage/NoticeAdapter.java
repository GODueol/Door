package com.example.kwoncheolhyeok.core.CorePage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kwoncheolhyeok.core.Entity.Notice;
import com.example.kwoncheolhyeok.core.MessageActivity.util.DateUtil;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.GlideApp;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeHolder> {

    private List<Notice> noticeList;
    private Context context;

    public NoticeAdapter(List<Notice> noticeList, Context context) {
        this.noticeList = noticeList;
        this.context = context;
    }

    @Override
    public NoticeHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.setting_notice_item, viewGroup, false);
        return new NoticeHolder(v);
    }

    @Override
    public void onBindViewHolder(NoticeHolder holder, int i) {

        Notice notice = noticeList.get(i);

        if(notice.getPictureUrl() == null) {
            holder.image.setVisibility(View.GONE);
        } else {
            holder.image.setVisibility(View.VISIBLE);
            GlideApp.with(context /* context */)
                    .load(notice.getPictureUrl())
                    .into(holder.image);
        }

        holder.text.setText(notice.getText());
        holder.title.setText(notice.getTitle());
        holder.date.setText(new DateUtil(notice.getWriteDate()).getDateAndTime());

    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    static class NoticeHolder extends RecyclerView.ViewHolder {
        TextView title, text, date;
        ImageView new_dot, image;

        NoticeHolder(View itemView) {
            super(itemView);

            new_dot = itemView.findViewById(R.id.new_dot);
            image = itemView.findViewById(R.id.image);

            title = itemView.findViewById(R.id.title);
            text = itemView.findViewById(R.id.text);
            date = itemView.findViewById(R.id.date);
        }
    }

}