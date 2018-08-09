package com.teamdoor.android.door.SettingActivity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.teamdoor.android.door.Entity.Notice;
import com.teamdoor.android.door.Chatting.util.DateUtil;
import com.teamdoor.android.door.R;
import com.teamdoor.android.door.Util.SharedPreferencesUtil;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeHolder> {

    private List<Notice> noticeList;
    private Context context;
    private SharedPreferencesUtil sp;

    public NoticeAdapter(List<Notice> noticeList, Context context) {
        this.noticeList = noticeList;
        this.context = context;
        sp = new SharedPreferencesUtil(context);
    }

    @Override
    public NoticeHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.setting_notice_item, viewGroup, false);
        return new NoticeHolder(v);
    }

    @Override
    public void onBindViewHolder(final NoticeHolder holder, int i) {

        final Notice notice = noticeList.get(i);

        // Image
        if (notice.getPictureUrl() == null) {
            holder.image.setVisibility(View.GONE);
        } else {
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(context /* context */)
                    .load(notice.getPictureUrl())
                    .into(holder.image);
        }

        holder.text.setText(notice.getText());
        holder.title.setText(notice.getTitle());
        holder.image.setVisibility(View.GONE);
        holder.date.setText(new DateUtil(notice.getWriteDate()).getDateAndTime());

        // set New tag
        if (sp.isNoticeRead(notice.getKey())) {
            holder.new_dot.setVisibility(View.INVISIBLE);
        } else {
            holder.new_dot.setVisibility(View.VISIBLE);
        }

        // Text 누르면 펼침, 읽음 저장
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 읽음 저장
                sp.putNoticeRead(notice.getKey());

                // set New tag Invisible
                NoticeActivity noticeActivity = (NoticeActivity) context;
                NoticeHolder noticeHolder = (NoticeHolder) noticeActivity.recyclerView.findViewHolderForAdapterPosition(holder.getAdapterPosition());
                noticeHolder.new_dot.setVisibility(View.INVISIBLE);

                // 펼침
                if (noticeHolder.text.getMaxLines() == 3) {
                    holder.image.setVisibility(View.VISIBLE);
                    noticeHolder.text.setMaxLines(Integer.MAX_VALUE);
                }
                // 접음
                else {
                    holder.image.setVisibility(View.GONE);
                    noticeHolder.text.setMaxLines(3);
                }
                //이미지 클릭 시 펼치고 접음
                holder.image.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        if(noticeHolder.text.getMaxLines()==3){
                            holder.image.setVisibility(View.VISIBLE);
                            noticeHolder.text.setMaxLines(Integer.MAX_VALUE);
                        }
                        else{
                            holder.image.setVisibility(View.GONE);
                            noticeHolder.text.setMaxLines(3);
                        }
                    }
                });
            }
        });

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