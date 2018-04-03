package com.example.kwoncheolhyeok.core.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.CorePage.CoreActivity;
import com.example.kwoncheolhyeok.core.Entity.AlarmSummary;
import com.example.kwoncheolhyeok.core.Exception.NotSetAutoTimeException;
import com.example.kwoncheolhyeok.core.MessageActivity.util.DateUtil;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.GlideApp;
import com.example.kwoncheolhyeok.core.Util.UiUtil;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018-03-25.
 */

public class NavAlarmAdapter extends RecyclerView.Adapter<NavAlarmAdapter.ViewHolder> {

    private List<AlarmSummary> items;
    private Context context;

    NavAlarmAdapter(Context context, List<AlarmSummary> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public NavAlarmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.nav_alarm_list_item, parent, false);


        return new ViewHolder(v);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AlarmSummary item = items.get(position);

        DateUtil dateUtil = new DateUtil(item.getTime());
        // 날짜 설정
        String time = dateUtil.getTime();
        String dateStr = dateUtil.getDate();
        holder.alarmTime.setText(time);

        // 날짜선 위한  이전날짜 가져오기
        if(position!=0) {
            dateUtil.setDate(items.get(position-1).getTime());
            String dateStr2 = dateUtil.getDate();
            if(!dateStr.equals(dateStr2)) {
                holder.dTextview.setText(dateStr);
                holder.linearLayout.setVisibility(View.VISIBLE);
            }else{
                holder.linearLayout.setVisibility(View.GONE);
            }
        }else{
            holder.dTextview.setText(dateStr);
            holder.linearLayout.setVisibility(View.VISIBLE);
        }
        setAlarmText(holder.alarmText, item);


        // 이미지 설정
        setAlarmImg(holder.alarmImg, item.getType());
        // 읽음 레이아웃 처리
        if (item.getViewTime()==null || item.getTime()  >= item.getViewTime()) {
            holder.alarmItemLayout.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.alarmItemLayout.setBackgroundColor(Color.WHITE);
        }

        holder.alarmItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                if(DataContainer.getInstance().isBlockWithMe(item.getcUuid())) {
                    Toast.makeText(context, "포스트를 볼 수 없습니다.", Toast.LENGTH_SHORT).show();
                    // TODO : 여기서 알람 삭제

                    return;
                }
                try {
                    Long time = UiUtil.getInstance().getCurrentTime(context);
                    item.setViewTime(time);
                    FirebaseDatabase.getInstance().getReference("Alarm").child(DataContainer.getInstance().getUid()).child(item.getKey()).child("alarmSummary").child("viewTime").setValue(time);
                } catch (NotSetAutoTimeException e) {
                    e.printStackTrace();
                }

                Intent p = new Intent(context.getApplicationContext(),CoreActivity.class);
                p.putExtra("uuid",item.getcUuid());
                p.putExtra("postId",item.getPostId());
                context.startActivity(p);
                notifyDataSetChanged();
            }
        });

    }

    private void setAlarmText(TextView textView, AlarmSummary item) {
        String str;
        String Type = item.getType();
        String Text = item.getText();
        Text = Text.trim();

        if(Text.length()>10){
            Text  = Text.substring(0,10)+"...";
        }
        switch (Type) {
            case "Like":
                str = item.getNickname() + "이 당신의 "+ "<b>" + Text + "</b> "+" 포스트를 좋아합니다.";
                textView.setText(Html.fromHtml(str));
                break;
            case "Post":
                str = "누군가 당신에게 "+ "<b>" + Text + "</b> "+" 익명 질문을 남겼습니다.";
                textView.setText(Html.fromHtml(str));
                break;
            default:
                str = item.getNickname() + " 님이 당신의 "+ "<b>" + Text + "</b> "+ " 질문에 답변을 달았습니다.";
                textView.setText(Html.fromHtml(str));
                break;
        }
    }

    private void setAlarmImg(ImageView imageView, String Type) {
        switch (Type) {
            case "Like":
                GlideApp.with(imageView.getContext()).load(R.drawable.new_alarm_heart).into(imageView);
                break;
            case "Post":
                GlideApp.with(imageView.getContext()).load(R.drawable.new_alarm_question).into(imageView);
                break;
            default:
                GlideApp.with(imageView.getContext()).load(R.drawable.new_alarm_answer).into(imageView);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        @Bind(R.id.dateLayout)
        LinearLayout linearLayout;

        @Bind(R.id.dateText)
        TextView dTextview;

        @Bind(R.id.navAlarmItemlayout)
        LinearLayout alarmItemLayout;

        @Bind(R.id.navAlarmImg)
        ImageView alarmImg;

        @Bind(R.id.navAlarmText)
        TextView alarmText;

        @Bind(R.id.navAlarmTime)
        TextView alarmTime;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}