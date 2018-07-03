package com.teamdoor.android.door.Activity;

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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamdoor.android.door.CorePage.CoreActivity;
import com.teamdoor.android.door.Entity.AlarmSummary;
import com.teamdoor.android.door.Exception.NotSetAutoTimeException;
import com.teamdoor.android.door.MessageActivity.util.DateUtil;
import com.teamdoor.android.door.R;
import com.teamdoor.android.door.Util.BaseActivity.BaseActivity;
import com.teamdoor.android.door.Util.DataContainer;
import com.teamdoor.android.door.Util.GlideApp;
import com.teamdoor.android.door.Util.UiUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.teamdoor.android.door.Util.RemoteConfig.CorePossibleOldFriendCount;

/**
 * Created by Administrator on 2018-03-25.
 */

public class NavAlarmAdapter extends RecyclerView.Adapter<NavAlarmAdapter.ViewHolder> {

    private List<AlarmSummary> items;
    private Context context;
    private boolean isPlus;
    private RewardedVideoAd mRewardedVideoAd;
    private InterstitialAd noFillInterstitialAd;
    boolean isFillReward = false;
    private AlarmSummary alarmItem;
    private int positionItem;

    NavAlarmAdapter(Context context, List<AlarmSummary> items, boolean isPlus) {
        this.context = context;
        this.items = items;
        this.isPlus = isPlus;
        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
        loadRewardedVideoAd();
        mRewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener);
        setnoFillInterstitialAd();
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
                alarmItem = item;
                positionItem = position;
                if (!isPlus) {

                    if(!isPossibleViewPost(item.getcUuid())){
                        FirebaseDatabase.getInstance().getReference(context.getString(R.string.alarm)).child(DataContainer.getInstance().getUid(context)).child(item.getKey()).removeValue();
                        items.remove(position);
                        notifyDataSetChanged();
                        return;
                    }

                    FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid(context)).child(context.getString(R.string.navAlarmCount)).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid(context)).child(context.getString(R.string.navAlarmCount)).setValue(value - 1);
                                showCorePost(item, position);
                            } else {
                                if (isFillReward) {
                                    showCorePost(item, position);
                                    noFillInterstitialAd.show();
                                } else {
                                    if(mRewardedVideoAd.isLoaded()) {
                                        mRewardedVideoAd.show();
                                    }
                                }
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


    RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {


        @Override
        public void onRewardedVideoAdLoaded() {
            Log.d("test", "onRewardedVideoAdLoaded");
        }

        @Override
        public void onRewardedVideoAdOpened() {
            Log.d("test", "onRewardedVideoAdOpened");
        }

        @Override
        public void onRewardedVideoStarted() {
            Log.d("test", "onRewardedVideoStarted");
        }

        @Override
        public void onRewardedVideoAdClosed() {
            loadRewardedVideoAd();
            FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid(context)).child(context.getString(R.string.navAlarmCount)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int value = Integer.valueOf(dataSnapshot.getValue().toString());
                        Log.d("test", "몇개 : " + value);
                        if (value > 0) {
                            FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid(context)).child(context.getString(R.string.navAlarmCount)).setValue(value - 1);
                            showCorePost(alarmItem, positionItem);
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
            loadRewardedVideoAd();
            FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid(context)).child(context.getString(R.string.navAlarmCount)).setValue(rewardItem.getAmount());
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {

            Log.d("test", "onRewardedVideoAdLeftApplication");
        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int i) {
            //Toast.makeText(context,"에러코드nav"+String.valueOf(i),Toast.LENGTH_LONG).show();
            switch (i) {
                case 0:
                    // 에드몹 내부서버에러
                    Toast.makeText(context, "내부서버에 문제가 있습니다.", Toast.LENGTH_LONG).show();
                    loadRewardedVideoAd();
                    break;
                case 2:
                    // 네트워크 연결상태 불량
                    Toast.makeText(context, "네트워크 연결상태가 좋지 않습니다.", Toast.LENGTH_LONG).show();
                    loadRewardedVideoAd();
                    break;
                case 3:
                    // 에드몹 광고 인벤토리 부족
                    isFillReward = true;
                    break;
            }
        }

        @Override
        public void onRewardedVideoCompleted() {

        }
    };


    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(context.getString(R.string.adsNavAlarm),
                new AdRequest.Builder()
                        .build());
    }

    public void setnoFillInterstitialAd() {
        noFillInterstitialAd = new InterstitialAd(context);
        noFillInterstitialAd.setAdUnitId(context.getString(R.string.noFillReward));
        noFillInterstitialAd.loadAd(new AdRequest.Builder().build());
        noFillInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                noFillInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.dateLayout)
        LinearLayout linearLayout;

        @BindView(R.id.dateText)
        TextView dTextview;

        @BindView(R.id.navAlarmItemlayout)
        LinearLayout alarmItemLayout;

        @BindView(R.id.navAlarmImg)
        ImageView alarmImg;

        @BindView(R.id.navAlarmText)
        TextView alarmText;

        @BindView(R.id.navAlarmTime)
        TextView alarmTime;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void showCorePost(AlarmSummary item, int position) {
        if (DataContainer.getInstance().isBlockWithMe(item.getcUuid())) {
            Toast.makeText(context, "포스트를 볼 수 없습니다.", Toast.LENGTH_SHORT).show();
            FirebaseDatabase.getInstance().getReference(context.getString(R.string.alarm)).child(DataContainer.getInstance().getUid(context)).child(item.getKey()).removeValue();
            items.remove(position);
            notifyDataSetChanged();
            return;
        }
        try {
            Long time = UiUtil.getInstance().getCurrentTime(context);
            item.setViewTime(time);
            FirebaseDatabase.getInstance().getReference(context.getString(R.string.alarm)).child(DataContainer.getInstance().getUid(context)).child(item.getKey()).child("alarmSummary").child("viewTime").setValue(time);
        } catch (NotSetAutoTimeException e) {
            e.printStackTrace();
        }

        Intent p = new Intent(context.getApplicationContext(), CoreActivity.class);
        p.putExtra("uuid", item.getcUuid());
        p.putExtra("postId", item.getPostId());
        context.startActivity(p);
        notifyDataSetChanged();
    }

    private boolean isPossibleViewPost(String cUuid){
        if (DataContainer.getInstance().isBlockWithMe(cUuid)) {
            Toast.makeText(context, "포스트를 볼 수 없습니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if( ((BaseActivity) context).isOldFriends(cUuid)){
            Toast.makeText(context, "일반 회원은 " + CorePossibleOldFriendCount + "명의 오래된 친구까지 도어 열람이 가능합니다 :(", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    public void Resume() {
        if(mRewardedVideoAd!=null) {
            mRewardedVideoAd.resume(context);
            mRewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener);
        }
    }

    public void Pause() {
        if(mRewardedVideoAd!=null) {
            mRewardedVideoAd.pause(context);
        }
    }

    public void Destroy() {
        if(mRewardedVideoAd!=null) {
            mRewardedVideoAd.destroy(context);
            mRewardedVideoAd.setRewardedVideoAdListener(null);
        }
    }
}