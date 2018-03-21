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
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.kwoncheolhyeok.core.Entity.CoreListItem;
import com.example.kwoncheolhyeok.core.Entity.CorePost;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.Exception.ChildSizeMaxException;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.AlarmUtil;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.example.kwoncheolhyeok.core.Util.FirebaseSendPushMsg;
import com.example.kwoncheolhyeok.core.Util.GlideApp;
import com.example.kwoncheolhyeok.core.Util.UiUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by KwonCheolHyeok on 2017-01-17.
 */

public class CoreListAdapter extends RecyclerView.Adapter<CoreListAdapter.CorePostHolder> {

    private final DatabaseReference postsRef;
    private List<CoreListItem> coreListItems;
    private Context context;
    private String cUuid;
    private Handler threadHandler = new Handler();

    private MediaPlayer mediaPlayer;

    private CorePostHolder currentHolder;
    private int currentSeekBarPosition;

    private String currentPlayUrl = "";

    CoreListAdapter(List<CoreListItem> coreListItems, Context context, String cUuid) {
        this.coreListItems = coreListItems;
        this.context = context;
        this.cUuid = cUuid;
        this.mediaPlayer = new MediaPlayer();
        currentHolder = new CorePostHolder(new View(context));
        postsRef = FirebaseDatabase.getInstance().getReference().child("posts");
    }

    @Override
    public CorePostHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.core_list_item, viewGroup, false);
        return new CorePostHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final CorePostHolder holder, @SuppressLint("RecyclerView") final int i) {

        final CoreListItem coreListItem = coreListItems.get(i);
        final CorePost corePost = coreListItem.getCorePost();
        final String mUuid = DataContainer.getInstance().getUid();

        User user = coreListItem.getUser();
        if (user != null) {  // 주인글
            setMasterPost(holder, corePost, user);
        } else {    // 타인글
            setAnonymousPost(holder, coreListItem, corePost, mUuid);
        }


        if (corePost.getUuid().equals(mUuid)) {   // 본인 게시물
            // 수정 삭제 가능
            setPostMenu(holder, coreListItem, R.menu.core_post_normal_menu);

            // cloud
            // TODO : cloud
            holder.core_cloud.setVisibility(View.VISIBLE);
//            FirebaseDatabase.getInstance().putCloudCore()

        } else if (cUuid.equals(mUuid)) { // Core 주인이 뷰어일 경우
            // 삭제 가능, Edit은 불가능
            setPostMenu(holder, coreListItem, R.menu.core_post_master_menu);
            holder.core_cloud.setVisibility(View.INVISIBLE);
        } else {
            holder.core_setting.setVisibility(View.GONE);
            holder.core_cloud.setVisibility(View.INVISIBLE);
        }

        holder.core_date.setText(DataContainer.getInstance().convertBeforeFormat(corePost.getWriteDate()));
        holder.core_contents.setText(corePost.getText());

        holder.core_heart_count.setText(Integer.toString(corePost.getLikeUsers().size()));
        holder.core_heart_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, CoreHeartCountActivity.class);
                i.putExtra("cUuid", cUuid);
                i.putExtra("postKey", coreListItem.getPostKey());
                context.startActivity(i);
            }
        });

        holder.core_heart_btn.setLiked(corePost.getLikeUsers().containsKey(mUuid));

        holder.core_heart_btn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                postsRef.child(cUuid)
                        .child(coreListItem.getPostKey())
                        .child("likeUsers").child(mUuid).setValue(System.currentTimeMillis()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        FireBaseUtil.getInstance().queryBlockWithMe(corePost.getUuid(), new FireBaseUtil.BlockListener() {
                            @Override
                            public void isBlockCallback(boolean isBlockWithMe) {
                                if(isBlockWithMe) return;
                                if (!corePost.getUuid().equals(mUuid)) {
                                    final String NickName = DataContainer.getInstance().getUser().getId();
                                    AlarmUtil.getInstance().sendAlarm("Like",NickName,corePost).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            FirebaseSendPushMsg.sendPostToFCM("Like", corePost.getUuid(), NickName, context.getString(R.string.alertLike));
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                postsRef.child(cUuid)
                        .child(coreListItem.getPostKey())
                        .child("likeUsers").child(mUuid).setValue(null);
            }
        });

        // seekBar Sync
        if (currentSeekBarPosition == i) {
            resetCurrentHolder(holder);
            currentHolder.seekBar.setMax(this.mediaPlayer.getDuration());
        }

        holder.startAndPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                CoreActivity coreActivity = (CoreActivity) context;
                if (b) {
                    doStart((CorePostHolder) coreActivity.getHolder(holder.getAdapterPosition()), corePost.getSoundUrl());
                } else {
                    doPause();
                }
            }
        });

        holder.rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRewind(holder.getAdapterPosition());
            }
        });

        holder.fastForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doFastForward(holder.getAdapterPosition());
            }
        });

        holder.seekBar.setClickable(false);
        holder.seekBar.setEnabled(false);
    }

    private void resetCurrentHolder(CorePostHolder holder) {
        currentHolder.textView_currentPosion = holder.textView_currentPosion;
        currentHolder.seekBar = holder.seekBar;
        currentSeekBarPosition = holder.getAdapterPosition();
        currentHolder.textView_maxTime = holder.textView_maxTime;
        currentHolder.startAndPause = holder.startAndPause;
    }

    private void setAnonymousPost(CorePostHolder holder, CoreListItem coreListItem, CorePost corePost, String mUuid) {
        holder.replyBtnLayout.setVisibility(View.VISIBLE);
        holder.core_img.setVisibility(View.GONE);
        holder.core_media.setVisibility(View.GONE);

        holder.core_pic.setImageResource(R.drawable.a);
        holder.core_id.setText(R.string.unknown);
        holder.core_subProfile.setText("");

        if (cUuid.equals(mUuid)) {   // 주인이 봤을때
            holder.btn_yes.setOnCheckedChangeListener(null);
            holder.btn_pass.setOnCheckedChangeListener(null);
            holder.btn_no.setOnCheckedChangeListener(null);
        } else {
            holder.btn_yes.setClickable(false);
            holder.btn_pass.setClickable(false);
            holder.btn_no.setClickable(false);
        }

        if (corePost.getReply() == null) {
            holder.btn_yes.setChecked(false);
            holder.btn_pass.setChecked(false);
            holder.btn_no.setChecked(false);
        } else if (corePost.getReply().equals("yes")) {
            holder.btn_yes.setChecked(true);
            holder.btn_pass.setChecked(false);
            holder.btn_no.setChecked(false);
        } else if (corePost.getReply().equals("pass")) {
            holder.btn_yes.setChecked(false);
            holder.btn_pass.setChecked(true);
            holder.btn_no.setChecked(false);
        } else if (corePost.getReply().equals("no")) {
            holder.btn_yes.setChecked(false);
            holder.btn_pass.setChecked(false);
            holder.btn_no.setChecked(true);
        }

        if (cUuid.equals(mUuid)) {   // 주인이 봤을때
            holder.btn_yes.setOnCheckedChangeListener(getListener(coreListItem, "yes"));
            holder.btn_pass.setOnCheckedChangeListener(getListener(coreListItem, "pass"));
            holder.btn_no.setOnCheckedChangeListener(getListener(coreListItem, "no"));
        }
    }

    private void setMasterPost(CorePostHolder holder, CorePost corePost, User user) {
        holder.replyBtnLayout.setVisibility(View.GONE);
        holder.core_img.setVisibility(View.VISIBLE);

        // Sound
        if (corePost.getSoundUrl() != null) {
            holder.core_media.setVisibility(View.VISIBLE);

            // 미디어 플레이어를 수정했을 경우 초기화
            if (currentSeekBarPosition == holder.getAdapterPosition() && !currentPlayUrl.equals(corePost.getSoundUrl())) {
                mediaPlayer.seekTo(0);
                if (currentHolder.textView_maxTime != null) {
                    currentHolder.textView_maxTime.setText("");
                }
            }

        } else {
            holder.core_media.setVisibility(View.GONE);
        }

        // Picture
        GlideApp.with(context /* context */)
                .load(user.getPicUrls().getThumbNail_picUrl1())
                .placeholder(R.drawable.a)
                .into(holder.core_pic);
        holder.core_id.setText(user.getId());
        holder.core_subProfile.setText(UiUtil.getInstance().setSubProfile(user));

        if (holder.core_img != null) Glide.with(context /* context */)
                .load(corePost.getPictureUrl())
                .into(holder.core_img);
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

                        switch (i) {
                            case R.id.edit:
                                Intent intent = new Intent(context, CoreWriteActivity.class);
                                intent.putExtra("cUuid", cUuid);
                                intent.putExtra("postKey", coreListItem.getPostKey());
                                context.startActivity(intent);
                                break;
                            case R.id.delete:
                                deletePost(coreListItem);
                                break;
                            case R.id.block:
                                // 다이얼로그
                                UiUtil.getInstance().showDialog(context, "유저 차단", "해당 유저를 차단하시겠습니까?", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        final User mUser = DataContainer.getInstance().getUser();
                                        if (mUser.getBlockUsers().size() >= DataContainer.ChildrenMax) {
                                            Toast.makeText(context, DataContainer.ChildrenMax + "명을 초과할 수 없습니다", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        UiUtil.getInstance().startProgressDialog((Activity) context);
                                        // blockUsers 추가
                                        try {
                                            FireBaseUtil.getInstance().block(coreListItem.getCorePost().getUuid()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(context, "차단되었습니다", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    UiUtil.getInstance().stopProgressDialog();
                                                }
                                            });
                                        } catch (ChildSizeMaxException e) {
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            UiUtil.getInstance().stopProgressDialog();
                                        }
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                });
                                break;
                            default:
                                break;
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

//                        UiUtil.getInstance().startProgressDialog((Activity) context);

                        FireBaseUtil.getInstance().deletePostExcution(coreListItem, postsRef, cUuid);


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
                     boolean isReplyFirst = false;
                    if(coreListItem.getCorePost().getReply() == null) {
                        isReplyFirst = true;
                    }
                    final boolean finalIsReplyFirst = isReplyFirst;
                    postsRef.child(cUuid).child(coreListItem.getPostKey())
                            .child("reply").setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            if(!finalIsReplyFirst || DataContainer.getInstance().isBlockWithMe(coreListItem.getCorePost().getUuid())) return;

                            final String NickName = DataContainer.getInstance().getUser().getId();
                            AlarmUtil.getInstance().sendAlarm("Answer",NickName,coreListItem.getCorePost()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseSendPushMsg.sendPostToFCM("Answer",coreListItem.getCorePost().getUuid(),NickName,"당신이 작성한 질문글에 답이 왔네요!");
                                }
                            });

                        }
                    });
                }

                else {
                    //취소 못하게
                    buttonView.setChecked(true);
                }
            }
        };
    }

    @Override
    public int getItemCount() {
        return coreListItems.size();
    }

    class CorePostHolder extends RecyclerView.ViewHolder {
        ImageView core_pic, core_img;
        //        ImageButton Core_heart;
        ImageButton core_setting;
        TextView core_id, core_subProfile, core_date, core_contents, core_heart_count;
        LikeButton core_heart_btn;
        LinearLayout replyBtnLayout;
        ToggleButton btn_yes, btn_pass, btn_no;

        RelativeLayout core_media;
        ToggleButton startAndPause;
        ImageButton rewind, fastForward;
        SeekBar seekBar;
        TextView textView_maxTime, textView_currentPosion;
        ImageButton core_cloud;

        CorePostHolder(View itemView) {
            super(itemView);

            core_pic = itemView.findViewById(R.id.core_pic);
            core_img = itemView.findViewById(R.id.core_img);

            core_id = itemView.findViewById(R.id.core_id);
            core_subProfile = itemView.findViewById(R.id.sub_profile);
            core_date = itemView.findViewById(R.id.core_date);
            core_contents = itemView.findViewById(R.id.core_contents);

            core_setting = itemView.findViewById(R.id.setting);

            core_heart_count = itemView.findViewById(R.id.heart_count_txt);
//            core_heart= itemView.findViewById(R.id.heart_count);
            core_heart_btn = itemView.findViewById(R.id.core_heart_btn);
            replyBtnLayout = itemView.findViewById(R.id.reply_btn_layout);

            btn_yes = itemView.findViewById(R.id.btn_yes);
            btn_pass = itemView.findViewById(R.id.btn_pass);
            btn_no = itemView.findViewById(R.id.btn_no);

            core_media = itemView.findViewById(R.id.media_player_layout);
            startAndPause = itemView.findViewById(R.id.button_start_pause);
            rewind = itemView.findViewById(R.id.button_rewind);
            fastForward = itemView.findViewById(R.id.button_fastForward);
            seekBar = itemView.findViewById(R.id.seekBar);
            textView_maxTime = itemView.findViewById(R.id.textView_maxTime);
            textView_currentPosion = itemView.findViewById(R.id.textView_currentPosion);
            core_cloud = itemView.findViewById(R.id.core_cloud);
        }
    }


    // Convert millisecond to string.
    private String millisecondsToString(int milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes((long) milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds((long) milliseconds);
        return minutes + ":" + seconds;
    }


    private void doStart(CorePostHolder holder, String url) {

        // 예외처리
        if (holder == null) {
            return;
        }

        // 다른 아이템의 플레이어를 중단
        if (currentSeekBarPosition != holder.getAdapterPosition() && currentHolder.startAndPause.isChecked())
            currentHolder.startAndPause.performClick();

        // syncHolder
        resetCurrentHolder(holder);

        if (!currentPlayUrl.equals(url) || currentPlayUrl.equals("") ||
                holder.textView_currentPosion.getText().equals(holder.textView_maxTime.getText())) {
            try {
                this.mediaPlayer.reset();
                this.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                this.mediaPlayer.setDataSource(url);
                currentPlayUrl = url;
                this.mediaPlayer.prepare(); // 필연적으로 지연됨 (버퍼채움)

                mediaPlayer.seekTo(holder.seekBar.getProgress());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // The duration in milliseconds
        int duration = this.mediaPlayer.getDuration();

        int currentPosition = this.mediaPlayer.getCurrentPosition();

        if (currentPosition == 0) {

            holder.seekBar.setMax(duration);
            String maxTimeString = this.millisecondsToString(duration);
            holder.textView_maxTime.setText(maxTimeString);
        }

        this.mediaPlayer.start();

        // Create a thread to update position of SeekBar.
        UpdateSeekBarThread updateSeekBarThread = new UpdateSeekBarThread();
        threadHandler.postDelayed(updateSeekBarThread, 50);
    }

    // Thread to Update position for SeekBar.
    class UpdateSeekBarThread implements Runnable {

        public void run() {
            int currentPosition = mediaPlayer.getCurrentPosition();
            String currentPositionStr = millisecondsToString(currentPosition);
            currentHolder.textView_currentPosion.setText(currentPositionStr);

            if (context == null || currentHolder.textView_currentPosion.getText().equals(currentHolder.textView_maxTime.getText())) {
                // 사운드 재생 끝
                currentHolder.startAndPause.setChecked(false);  // 버튼 Stop
                currentHolder.textView_currentPosion.setText("0:0");
                currentHolder.seekBar.setProgress(0);   // SeekBar Init
                return;
            }
            currentHolder.seekBar.setProgress(currentPosition);
            // Delay thread 50 milisecond.
            threadHandler.postDelayed(this, 50);
        }
    }

    // When user click to "Pause".
    private void doPause() {
        this.mediaPlayer.pause();
    }

    void clickPause() {
        CoreActivity coreActivity = (CoreActivity) context;
        CorePostHolder corePostHolder = (CorePostHolder) coreActivity.getHolder(currentSeekBarPosition);
        if (corePostHolder == null) return;
        corePostHolder.startAndPause.setChecked(false);
    }

    // When user click to "Rewind".
    private void doRewind(int position) {
        if (position != currentSeekBarPosition) return;
        int currentPosition = this.mediaPlayer.getCurrentPosition();
        // 5 seconds.
        int SUBTRACT_TIME = 5000;

        if (currentPosition - SUBTRACT_TIME > 0) {
            this.mediaPlayer.seekTo(currentPosition - SUBTRACT_TIME);
        }
    }

    // When user click to "Fast-Forward".
    private void doFastForward(int position) {
        if (position != currentSeekBarPosition) return;
        int currentPosition = this.mediaPlayer.getCurrentPosition();
        int duration = this.mediaPlayer.getDuration();
        // 5 seconds.
        int ADD_TIME = 5000;

        if (currentPosition + ADD_TIME < duration) {
            this.mediaPlayer.seekTo(currentPosition + ADD_TIME);
        }
    }

}