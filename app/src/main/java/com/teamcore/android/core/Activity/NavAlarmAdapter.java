package com.teamcore.android.core.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamcore.android.core.CorePage.CoreActivity;
import com.teamcore.android.core.Entity.AlarmSummary;
import com.teamcore.android.core.Exception.NotSetAutoTimeException;
import com.teamcore.android.core.MessageActivity.util.DateUtil;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.GlideApp;
import com.teamcore.android.core.Util.UiUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018-03-25.
 */

public class NavAlarmAdapter extends RecyclerView.Adapter<NavAlarmAdapter.ViewHolder> {

    private List<AlarmSummary> items;
    private Context context;
    private boolean isPlus;
    private RewardedVideoAd mRewardedVideoAd;

    NavAlarmAdapter(Context context, List<AlarmSummary> items, boolean isPlus) {
        this.context = context;
        this.items = items;
        this.isPlus = isPlus;
        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
        loadRewardedVideoAd();

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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final AlarmSummary item = items.get(position);

        DateUtil dateUtil = new DateUtil(item.getTime());
        // 날짜 설정
        String time = dateUtil.getTime();
        String dateStr = dateUtil.msgDate();
        holder.alarmTime.setText(time);

        // 날짜선 위한  이전날짜 가져오기
        if (position != 0) {
            dateUtil.setDate(items.get(position - 1).getTime());
            String dateStr2 = dateUtil.msgDate();
            if (!dateStr.equals(dateStr2)) {
                holder.dTextview.setText(dateStr);
                holder.linearLayout.setVisibility(View.VISIBLE);
            } else {
                holder.linearLayout.setVisibility(View.GONE);
            }
        } else {
            holder.dTextview.setText(dateStr);
            holder.linearLayout.setVisibility(View.VISIBLE);
        }
        setAlarmText(holder.alarmText, item);


        // 이미지 설정
        setAlarmImg(holder.alarmImg, item.getType());
        // 읽음 레이아웃 처리
        if (item.getViewTime() == null || item.getTime() >= item.getViewTime()) {
            holder.alarmItemLayout.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.alarmItemLayout.setBackgroundColor(Color.WHITE);
        }

        holder.alarmItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (mRewardedVideoAd.isLoaded()) {

                mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {


                    @Override
                    public void onRewardedVideoAdLoaded() {
                        Log.d("test", "onRewardedVideoAdLoaded");
                    }

                    @Override
                    public void onRewardedVideoAdOpened() {
                        Log.d("test", "onRewardedVideoAdOpened" +
                                "");

                    }

                    @Override
                    public void onRewardedVideoStarted() {
                        Log.d("test", "onRewardedVideoStarted");
                    }

                    @Override
                    public void onRewardedVideoAdClosed() {
                        loadRewardedVideoAd();
                        FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid()).child(context.getString(R.string.navAlarmCount)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    int value = Integer.valueOf(dataSnapshot.getValue().toString());
                                    Log.d("test", "몇개 : " + value);
                                    if (value > 0) {
                                        FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid()).child(context.getString(R.string.navAlarmCount)).setValue(value - 1);
                                        showCorePost(item, position);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        Log.d("test", "onRewardedVideoAdClosed");
                    }

                    @Override
                    public void onRewarded(RewardItem rewardItem) {
                        FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid()).child(context.getString(R.string.navAlarmCount)).setValue(rewardItem.getAmount());
                    }

                    @Override
                    public void onRewardedVideoAdLeftApplication() {

                        Log.d("test", "onRewardedVideoAdLeftApplication");
                    }

                    @Override
                    public void onRewardedVideoAdFailedToLoad(int i) {
                        Log.d("test", "onRewardedVideoAdFailedToLoad" + i);
                    }
                });
                if (!isPlus) {
                    FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid()).child(context.getString(R.string.navAlarmCount)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int value;
                            try {
                                value = Integer.valueOf(dataSnapshot.getValue().toString());
                            } catch (Exception e) {
                                value = 0;
                            }
                            Log.d("test", "몇개 : " + value);
                            if (value > 0) {
                                FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid()).child(context.getString(R.string.navAlarmCount)).setValue(value - 1);
                                showCorePost(item, position);
                            } else {
                                mRewardedVideoAd.show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    showCorePost(item, position);
                }
            }
        });

    }

    private void setAlarmText(TextView textView, AlarmSummary item) {
        String str;
        String Type = item.getType();
        String Text = item.getText();
        Text = Text.trim();

        if (Text.length() > 10) {
            Text = Text.substring(0, 10) + "...";
        }
        switch (Type) {
            case "Like":
                str = item.getNickname() + "이 당신의 " + "<b>" + Text + "</b> " + " 포스트를 좋아합니다.";
                textView.setText(Html.fromHtml(str));
                break;
            case "Post":
                str = "누군가 당신에게 " + "<b>" + Text + "</b> " + " 익명 질문을 남겼습니다.";
                textView.setText(Html.fromHtml(str));
                break;
            default:
                str = item.getNickname() + " 님이 당신의 " + "<b>" + Text + "</b> " + " 질문에 답변을 달았습니다.";
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


    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(context.getString(R.string.adsNavAlarm),
                new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .addTestDevice("0D525D9C92269D80384121978C3C4267")
                        .build());
    }

    private void showCorePost(AlarmSummary item, int position) {
        if (DataContainer.getInstance().isBlockWithMe(item.getcUuid())) {
            Toast.makeText(context, "포스트를 볼 수 없습니다.", Toast.LENGTH_SHORT).show();
            FirebaseDatabase.getInstance().getReference(context.getString(R.string.alarm)).child(DataContainer.getInstance().getUid()).child(item.getKey()).removeValue();
            items.remove(position);
            notifyDataSetChanged();
            return;
        }
        try {
            Long time = UiUtil.getInstance().getCurrentTime(context);
            item.setViewTime(time);
            FirebaseDatabase.getInstance().getReference(context.getString(R.string.alarm)).child(DataContainer.getInstance().getUid()).child(item.getKey()).child("alarmSummary").child("viewTime").setValue(time);
        } catch (NotSetAutoTimeException e) {
            e.printStackTrace();
        }

        Intent p = new Intent(context.getApplicationContext(), CoreActivity.class);
        p.putExtra("uuid", item.getcUuid());
        p.putExtra("postId", item.getPostId());
        context.startActivity(p);
        notifyDataSetChanged();
    }
}