package com.example.kwoncheolhyeok.core.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwoncheolhyeok.core.CorePage.CoreActivity;
import com.example.kwoncheolhyeok.core.Entity.AlarmSummary;
import com.example.kwoncheolhyeok.core.Exception.NotSetAutoTimeException;
import com.example.kwoncheolhyeok.core.MessageActivity.util.DateUtil;
import com.example.kwoncheolhyeok.core.R;

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

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AlarmSummary item = items.get(position);


        // 날짜 설정
        DateUtil dateUtil = new DateUtil(item.getTime());
        String preTime = null;
        try {
            preTime = dateUtil.getPreTime(context);
        } catch (NotSetAutoTimeException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            ActivityCompat.finishAffinity((Activity) context);
        }
        holder.alarmTime.setText(preTime);
        setAlarmText(holder.alarmText, item);
        // 이미지 설정
        setAlarmImg(holder.alarmImg, item.getType());

        holder.alarmLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent p = new Intent(context.getApplicationContext(),CoreActivity.class);
                p.putExtra("uuid",item.getcUuid());
                p.putExtra("postId",item.getPostId());
                context.startActivity(p);
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
                str = item.getNickname() + "이 " + "\"" + Text + "\"" + " 포스트를 좋아합니다.";
                textView.setText(str);
                break;
            case "Post":
                str = "누군가 당신에게 " + "\"" + Text + "\"" + " 익명 포스트를 남겼습니다..";
                textView.setText(str);
                break;
            default:
                str = item.getNickname() + " 님이 " + "\"" + Text + "\"" + " 포스트에 답변이 달렸습니다.";
                textView.setText(str);
                break;
        }
    }

    private void setAlarmImg(ImageView imageView, String Type) {
        switch (Type) {
            case "Like":
                imageView.setBackgroundResource(R.drawable.new_alarm_heart);
                break;
            case "Post":
                imageView.setBackgroundResource(R.drawable.new_alarm_question);
                break;
            default:
                imageView.setBackgroundResource(R.drawable.new_alarm_answer);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.navAlarmlayout)
        RelativeLayout alarmLayout;

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
