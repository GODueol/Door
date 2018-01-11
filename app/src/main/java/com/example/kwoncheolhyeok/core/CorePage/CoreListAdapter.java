package com.example.kwoncheolhyeok.core.CorePage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.kwoncheolhyeok.core.Entity.CoreListItem;
import com.example.kwoncheolhyeok.core.Entity.CorePost;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.example.kwoncheolhyeok.core.Util.UiUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by KwonCheolHyeok on 2017-01-17.
 */

public class CoreListAdapter extends RecyclerView.Adapter<CoreListAdapter.CorePostHolder> {

    private List<CoreListItem> posts;
    private Context context;
    private String cUuid;
    private Handler threadHandler = new Handler();

    private MediaPlayer mediaPlayer;

    private CorePostHolder currentHolder;
    private int currentSeekBarPosition;

    private UpdateSeekBarThread updateSeekBarThread;
    private String currentPlayUrl = "";


    CoreListAdapter(List<CoreListItem> posts, Context context, String cUuid) {
        this.posts = posts;
        this.context = context;
        this.cUuid = cUuid;
        this.mediaPlayer=  new MediaPlayer();
        currentHolder = new CorePostHolder(new View(context));
    }

    @Override
    public CorePostHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.core_list_item, viewGroup, false);
        return new CorePostHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final CorePostHolder holder, final int i) {

        final CoreListItem coreListItem = posts.get(i);
        final CorePost corePost = coreListItem.getCorePost();
        final String mUuid = DataContainer.getInstance().getUid();

        User user = coreListItem.getUser();
        if(user != null) {  // 주인글
            holder.replyBtnLayout.setVisibility(View.GONE);
            holder.core_img.setVisibility(View.VISIBLE);

            if(corePost.getSoundUrl() != null)
                holder.core_media.setVisibility(View.VISIBLE);
            else
                holder.core_media.setVisibility(View.GONE);

            Glide.with(context /* context */)
                    .load(user.getPicUrls().getPicUrl1())
                    .into(holder.core_pic);
            holder.core_id.setText(user.getId());
            holder.core_subprofile.setText(UiUtil.getInstance().setSubProfile(user));
            Glide.with(context /* context */)
                    .load(corePost.getPictureUrl())
                    .into(holder.core_img);

            // 미디어 셋

        } else {    // 타인글
            holder.replyBtnLayout.setVisibility(View.VISIBLE);
            holder.core_img.setVisibility(View.GONE);
            holder.core_media.setVisibility(View.GONE);

            holder.core_pic.setImageResource(R.drawable.a);
            holder.core_id.setText("Unknown");
            holder.core_subprofile.setText("");

            if(cUuid.equals(mUuid)) {   // 주인이 봤을때
                holder.btn_yes.setOnCheckedChangeListener(null);
                holder.btn_pass.setOnCheckedChangeListener(null);
                holder.btn_no.setOnCheckedChangeListener(null);
            } else {
                holder.btn_yes.setClickable(false);
                holder.btn_pass.setClickable(false);
                holder.btn_no.setClickable(false);
            }

            if(corePost.getReply() == null) {
                holder.btn_yes.setChecked(false);
                holder.btn_pass.setChecked(false);
                holder.btn_no.setChecked(false);
            } else if(corePost.getReply().equals("yes")){
                holder.btn_yes.setChecked(true);
                holder.btn_pass.setChecked(false);
                holder.btn_no.setChecked(false);
            } else if(corePost.getReply().equals("pass")){
                holder.btn_yes.setChecked(false);
                holder.btn_pass.setChecked(true);
                holder.btn_no.setChecked(false);
            } else if(corePost.getReply().equals("no")){
                holder.btn_yes.setChecked(false);
                holder.btn_pass.setChecked(false);
                holder.btn_no.setChecked(true);
            }

            if(cUuid.equals(mUuid)) {   // 주인이 봤을때
                holder.btn_yes.setOnCheckedChangeListener(getListener(coreListItem, "yes"));
                holder.btn_pass.setOnCheckedChangeListener(getListener(coreListItem, "pass"));
                holder.btn_no.setOnCheckedChangeListener(getListener(coreListItem, "no"));
            }

        }


        if(corePost.getUuid().equals(mUuid)){   // 본인 게시물
            // 수정 삭제 가능
            setPostMenu(holder, coreListItem, R.menu.core_post_normal_menu);

        } else if(cUuid.equals(mUuid)){ // Core 주인이 뷰어일 경우
            // 삭제 가능, Edit은 불가능
            setPostMenu(holder, coreListItem, R.menu.core_post_master_menu);

        } else {
            holder.core_setting.setVisibility(View.INVISIBLE);
        }

        holder.core_date.setText(DataContainer.getInstance().convertBeforeFormat(corePost.getWriteDate()));
        holder.core_contents.setText(corePost.getText());

        holder.core_heart_count.setText(Integer.toString(corePost.getLikeUsers().size()));
        holder.core_heart_btn.setLiked(corePost.getLikeUsers().containsKey(mUuid));

        holder.core_heart_btn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                FirebaseDatabase.getInstance().getReference().child("posts")
                        .child(cUuid)
                        .child(coreListItem.getPostKey())
                        .child("likeUsers").child(mUuid).setValue(System.currentTimeMillis());
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                FirebaseDatabase.getInstance().getReference().child("posts")
                        .child(cUuid)
                        .child(coreListItem.getPostKey())
                        .child("likeUsers").child(mUuid).setValue(null);
            }
        });

        // seekBar Sync
        if(currentSeekBarPosition == i) {
            currentHolder = holder;
            currentHolder.seekBar.setMax(this.mediaPlayer.getDuration());
        }

        holder.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doStart(holder, corePost.getSoundUrl(), i);
            }
        });
        holder.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doPause(holder);
            }
        });
        holder.rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRewind(holder);
            }
        });
        holder.fastForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doFastForward(holder);
            }
        });

    }

    private void setPostMenu(CorePostHolder holder, final CoreListItem coreListItem, final int menuId) {
        holder.core_setting.setVisibility(View.VISIBLE);
        holder.core_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.getMenuInflater().inflate(menuId, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int i = menuItem.getItemId();

                        if (i == R.id.edit) {   // 수정
                            Intent intent = new Intent(context, CoreWriteActivity.class);
                            intent.putExtra("cUuid", cUuid);
                            intent.putExtra("postKey", coreListItem.getPostKey());
                            context.startActivity(intent);

                        } else if (i == R.id.delete) {  // 삭제
                            deletePost(coreListItem);
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void deletePost(final CoreListItem coreListItem) {
        UiUtil.getInstance().showDialog(context, "Delete", "게시물을 삭제하시겠습니까?"
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        FirebaseDatabase.getInstance().getReference().child("posts").child(cUuid).child(coreListItem.getPostKey())
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                final ArrayList<Task> deleteTasks = new ArrayList<>();
                                // 갯수 갱신
                                FireBaseUtil.getInstance().syncCorePostCount(cUuid);

                                // Storage Delete
                                StorageReference postStorageRef = FirebaseStorage.getInstance().getReference().child("posts").child(cUuid).child(coreListItem.getPostKey());
                                if(coreListItem.getCorePost().getSoundUrl() != null)
                                    deleteTasks.add(postStorageRef.child("sound").delete());
                                if(coreListItem.getCorePost().getPictureUrl() != null)
                                    deleteTasks.add(postStorageRef.child("picture").delete());
                                UiUtil.getInstance().startProgressDialog((Activity) context);
                                for(Task task : deleteTasks){
                                    task.addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task mTask) {
                                            for(Task t : deleteTasks){
                                                if(!t.isComplete()) return;
                                                UiUtil.getInstance().stopProgressDialog();
                                            }
                                        }
                                    });
                                }

                            }
                        });



                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }
        );
    }

    @NonNull
    private CompoundButton.OnCheckedChangeListener getListener(final CoreListItem coreListItem, final String value) {
        return new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FirebaseDatabase.getInstance().getReference().child("posts").child(cUuid).child(coreListItem.getPostKey())
                            .child("reply").setValue(value);
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("posts").child(cUuid).child(coreListItem.getPostKey())
                            .child("reply").setValue(null);
                }
            }
        };
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class CorePostHolder extends RecyclerView.ViewHolder {
        ImageView core_pic, core_img ;
//        ImageButton Core_heart;
        ImageButton core_setting;
        TextView core_id, core_subprofile, core_date, core_contents, core_heart_count;
        LikeButton core_heart_btn;
        LinearLayout replyBtnLayout;
        ToggleButton btn_yes, btn_pass, btn_no;

        RelativeLayout core_media;
        ImageButton start, pause, rewind, fastForward;
        SeekBar seekBar;
        TextView textView_maxTime, textView_currentPosion;

        CorePostHolder(View itemView) {
            super(itemView);

            core_pic = itemView.findViewById(R.id.core_pic);
            core_img = itemView.findViewById(R.id.core_img);

            core_id = itemView.findViewById(R.id.core_id);
            core_subprofile= itemView.findViewById(R.id.sub_profile);
            core_date = itemView.findViewById(R.id.core_date);
            core_contents = itemView.findViewById(R.id.core_contents);

            core_setting= itemView.findViewById(R.id.setting);

            core_heart_count = itemView.findViewById(R.id.heart_count_txt);
//            core_heart= itemView.findViewById(R.id.heart_count);
            core_heart_btn = itemView.findViewById(R.id.core_heart_btn);
            replyBtnLayout = itemView.findViewById(R.id.reply_btn_layout);

            btn_yes = itemView.findViewById(R.id.btn_yes);
            btn_pass = itemView.findViewById(R.id.btn_pass);
            btn_no = itemView.findViewById(R.id.btn_no);
            core_media = itemView.findViewById(R.id.media_player_layout);
            start = itemView.findViewById(R.id.button_start);
            pause = itemView.findViewById(R.id.button_pause);
            rewind = itemView.findViewById(R.id.button_rewind);
            fastForward = itemView.findViewById(R.id.button_fastForward);
            seekBar = itemView.findViewById(R.id.seekBar);
            textView_maxTime = itemView.findViewById(R.id.textView_maxTime);
            textView_currentPosion = itemView.findViewById(R.id.textView_currentPosion);
        }
    }


    // Convert millisecond to string.
    private String millisecondsToString(int milliseconds)  {
        long minutes = TimeUnit.MILLISECONDS.toMinutes((long) milliseconds);
        long seconds =  TimeUnit.MILLISECONDS.toSeconds((long) milliseconds) ;
        return minutes+":"+ seconds;
    }


    private void doStart(CorePostHolder holder, String url, int position)  {
        // syncHolder
        currentHolder.textView_currentPosion = holder.textView_currentPosion;
        currentHolder.seekBar = holder.seekBar; currentSeekBarPosition = position;
        currentHolder.textView_maxTime = holder.textView_maxTime;
        currentHolder.start = holder.start;
        currentHolder.pause = holder.pause;

        if(!currentPlayUrl.equals(url) || currentPlayUrl.equals("") ||
                holder.textView_currentPosion.getText().equals(holder.textView_maxTime.getText())){
            try {
                this.mediaPlayer.reset();
                this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                this.mediaPlayer.setDataSource(url);
                currentPlayUrl = url;
                this.mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // The duration in milliseconds
        int duration = this.mediaPlayer.getDuration();

        int currentPosition = this.mediaPlayer.getCurrentPosition();

        if(currentPosition== 0)  {

            holder.seekBar.setMax(duration);
            String maxTimeString = this.millisecondsToString(duration);
            holder.textView_maxTime.setText(maxTimeString);
        }

        this.mediaPlayer.start();
        // Create a thread to update position of SeekBar.
        updateSeekBarThread = new UpdateSeekBarThread();
        threadHandler.postDelayed(updateSeekBarThread,50);

        holder.pause.setEnabled(true);
        holder.start.setEnabled(false);
    }

    // Thread to Update position for SeekBar.
    class UpdateSeekBarThread implements Runnable {

        public void run()  {
//            if(!mediaPlayer.isPlaying()) return;    // 중단되면 쓰레드 종료

            int currentPosition = mediaPlayer.getCurrentPosition();
            Log.d("kbj", "currentPosition : " + currentPosition);
            String currentPositionStr = millisecondsToString(currentPosition);
            currentHolder.textView_currentPosion.setText(currentPositionStr);

            if(currentHolder.textView_currentPosion.getText().equals(currentHolder.textView_maxTime.getText())){
                // 끝
                doPause(currentHolder);
                return;
            }
            currentHolder.seekBar.setProgress(currentPosition);
            // Delay thread 50 milisecond.
            threadHandler.postDelayed(this, 50);
        }
    }

    // When user click to "Pause".
    private void doPause(CorePostHolder holder)  {
        this.mediaPlayer.pause();
        holder.pause.setEnabled(false);
        holder.start.setEnabled(true);
    }

    // When user click to "Rewind".
    private void doRewind(CorePostHolder holder)  {
        int currentPosition = this.mediaPlayer.getCurrentPosition();
        int duration = this.mediaPlayer.getDuration();
        // 5 seconds.
        int SUBTRACT_TIME = 5000;

        if(currentPosition - SUBTRACT_TIME > 0 )  {
            this.mediaPlayer.seekTo(currentPosition - SUBTRACT_TIME);
        }
    }

    // When user click to "Fast-Forward".
    private void doFastForward(CorePostHolder holder)  {
        int currentPosition = this.mediaPlayer.getCurrentPosition();
        int duration = this.mediaPlayer.getDuration();
        // 5 seconds.
        int ADD_TIME = 5000;

        if(currentPosition + ADD_TIME < duration)  {
            this.mediaPlayer.seekTo(currentPosition + ADD_TIME);
        }
    }


}