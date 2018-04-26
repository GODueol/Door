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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.kwoncheolhyeok.core.CorePage.CoreCloudPayDialog.DealDialogFragment;
import com.example.kwoncheolhyeok.core.Entity.CoreCloud;
import com.example.kwoncheolhyeok.core.Entity.CoreListItem;
import com.example.kwoncheolhyeok.core.Entity.CorePost;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.Exception.ChildSizeMaxException;
import com.example.kwoncheolhyeok.core.Exception.NotSetAutoTimeException;
import com.example.kwoncheolhyeok.core.PeopleFragment.FullImageActivity;
import com.example.kwoncheolhyeok.core.PeopleFragment.GridItem;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.AlarmUtil;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.example.kwoncheolhyeok.core.Util.GlideApp;
import com.example.kwoncheolhyeok.core.Util.UiUtil;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CoreListAdapter extends RecyclerView.Adapter<CoreListAdapter.CorePostHolder> {

    private final DatabaseReference postsRef;
    private List<CoreListItem> coreListItems;
    private Context context;
    private Handler threadHandler = new Handler();

    private MediaPlayer mediaPlayer;

    private CorePostHolder currentHolder;
    private int currentSeekBarPosition;

    private String currentPlayUrl = "";

    CoreListAdapter(List<CoreListItem> coreListItems, Context context) {
        this.coreListItems = coreListItems;
        this.context = context;
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

        // 코어 클라우드는 일단 빈값으로 순서를 채움 => 클라우드에 한해서 빈값이 허용되도록
        if(context instanceof CoreCloudActivity && (corePost == null || coreListItem.getUser() == null)) return;

        // 보이는 방식 결정
        setPostViewDiff(holder, coreListItem, corePost, mUuid);

        // common set
        try {
            holder.core_date.setText(DataContainer.getInstance().convertBeforeFormat(corePost.getWriteDate(), context));
        } catch (NotSetAutoTimeException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            ActivityCompat.finishAffinity((Activity)context);
        }
        holder.core_contents.setText(corePost.getText());

        holder.core_heart_count.setText(Integer.toString(corePost.getLikeUsers().size()));
        holder.core_heart_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, CoreHeartCountActivity.class);
                i.putExtra("cUuid", coreListItem.getcUuid());
                i.putExtra("postKey", coreListItem.getPostKey());
                context.startActivity(i);
            }
        });

        holder.core_heart_btn.setLiked(corePost.getLikeUsers().containsKey(mUuid));

        holder.core_heart_btn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                try {
                    postsRef.child(coreListItem.getcUuid())
                            .child(coreListItem.getPostKey())
                            .child("likeUsers").child(mUuid).setValue(UiUtil.getInstance().getCurrentTime(context)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // cloud 반영
                            UiUtil.getInstance().noticeModifyToCloud(corePost, coreListItem.getPostKey(),(Activity)context);

                            if (!corePost.getUuid().equals(mUuid)) {    // 자신이 자신의 포스트에 좋아요한 경우를 제외
                                final String NickName = DataContainer.getInstance().getUser().getId();
                                AlarmUtil.getInstance().sendAlarm(context, "Like", NickName, corePost, coreListItem.getPostKey(), corePost.getUuid(), coreListItem.getcUuid());
                            }

                        }
                    });
                } catch (NotSetAutoTimeException e) {
                    e.printStackTrace();
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    ActivityCompat.finishAffinity((Activity)context);
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                postsRef.child(coreListItem.getcUuid())
                        .child(coreListItem.getPostKey())
                        .child("likeUsers").child(mUuid).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // cloud 반영
                        UiUtil.getInstance().noticeModifyToCloud(corePost, coreListItem.getPostKey(),(Activity)context);
                    }
                });

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

        // 클라우드 체크
        if(corePost.isCloud() && !(context instanceof CoreCloudActivity)){
            holder.check_cloud.setVisibility(View.VISIBLE);
        } else {
            holder.check_cloud.setVisibility(View.INVISIBLE);
        }
    }

    private void setPostViewDiff(final CorePostHolder holder, final CoreListItem coreListItem, final CorePost corePost, final String mUuid) {
        holder.core_cloud.setVisibility(View.INVISIBLE);

        // Notice
        if(corePost.getUuid() == null || coreListItem.getcUuid() == null){
            // Picture
            if (holder.core_img != null)
                Glide.with(context /* context */)
                        .load(corePost.getPictureUrl())
                        .into(holder.core_img);

            // 가릴거 가리기
            holder.replyBtnLayout.setVisibility(View.GONE);
            holder.profile_layout.setVisibility(View.GONE);
            holder.core_media.setVisibility(View.GONE);
            holder.heart_btn_layout.setVisibility(View.GONE);
            return;
        } else {
            holder.replyBtnLayout.setVisibility(View.VISIBLE);
            holder.profile_layout.setVisibility(View.VISIBLE);
            holder.core_media.setVisibility(View.VISIBLE);
            holder.heart_btn_layout.setVisibility(View.VISIBLE);
        }
        
        User user = coreListItem.getUser();
        if (user != null) {  // 주인글
            setMasterPost(holder, corePost, user);
        } else {    // 타인글
            setAnonymousPost(holder, coreListItem, corePost, mUuid);
        }

        if (corePost.getUuid().equals(mUuid)) {   // 본인 게시물
            
            // 수정 삭제 가능
            if(user == null && corePost.getReply() != null){    // 답변이 달린 익명글일 때
                setPostMenu(holder, coreListItem, R.menu.core_post_only_delete_menu);
            } else if(context instanceof CoreCloudActivity) {
                setPostMenu(holder, coreListItem, R.menu.core_post_cloud_menu);
            } else {
                setPostMenu(holder, coreListItem, R.menu.core_post_normal_menu);
            }

            // 본인 게시물이 주인일 때만 클라우드 가능
            if(user != null && !(context instanceof CoreCloudActivity)){
                if(context instanceof CoreActivity && ((CoreActivity)context).postId != null) {
                    // 알람에서 들어갔을 경우
                    holder.core_cloud.setVisibility(View.INVISIBLE);
                    return;
                }

                holder.core_cloud.setVisibility(View.VISIBLE);
                holder.core_cloud.setOnClickListener(new View.OnClickListener() {
                    DealDialogFragment dealDialogFragment;
                    @Override
                    public void onClick(final View view) {
                        view.setClickable(false);

                        // 이미 코어가 올라가 있는 게시물인지 확인
                        for(CoreListItem item : coreListItems){
                            if(item.getCorePost().isCloud()) {
                                Toast.makeText(context, "이미 코어 클라우드 게시하였습니다", Toast.LENGTH_SHORT).show();
                                view.setClickable(true);
                                return;
                            }
                        }

                        // cloud
                        // 클라우드 돌면서 100개 인지 확인
                        // 100개면 1일 넘는 리스트 확인, 가장 오래된 날짜를 다이얼로그에 넘기고, 포스트키를 콜백에 넘김(삭제)
                        // 100개고 1일 넘는것도 없으면, 가장 오래된 포스트 키를 다이얼로그에 넘김(언제 이후로 가능한지 출력)
                        DataContainer.getInstance().getCoreCloudRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                long oldestPostDate = Long.MAX_VALUE;   // 올릴수 있으면 MAX
                                String deletePostKey = null;

                                // 코어 클라우드 최대한계 확인
                                if(dataSnapshot.getChildrenCount() >= DataContainer.CoreCloudMax) {

                                    // 순회
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        CoreCloud coreCloud = snapshot.getValue(CoreCloud.class);
                                        if (coreCloud.getAttachDate() < oldestPostDate) {
                                            oldestPostDate = coreCloud.getAttachDate();
                                            deletePostKey = dataSnapshot.getKey();
                                        }
                                    }

                                    // Toast.makeText(context, "100개의 포스트 중 24시간이 지난 포스트가 없어서 클라우드를 올릴수 없습니다.", Toast.LENGTH_SHORT).show();
                                }

                                // 코어클라우드 결제 가능
                                //★☆★☆★☆★☆여기입니다요★☆★☆★☆★☆
                                if (dealDialogFragment != null && dealDialogFragment.getDialog() != null && dealDialogFragment.getDialog().isShowing()) return;
                                final String finalDeletePostKey = (oldestPostDate == Long.MAX_VALUE ? null : deletePostKey);
                                dealDialogFragment = new DealDialogFragment(oldestPostDate, new DealDialogFragment.CallbackListener() {
                                    @Override
                                    public void callback() {
                                        putCloudDialog(finalDeletePostKey);
                                    }
                                });
                                dealDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "");
                                view.setClickable(true);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                view.setClickable(true);
                            }

                        });
                    }

                    private void putCloudDialog(final String deletePostKey) {
                        UiUtil.getInstance().showDialog(context, "Core Cloud", "코어를 클라우드에 추가합니다. 결재하시겠습니까",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        UiUtil.getInstance().startProgressDialog((Activity)context);
                                        try {
                                            FireBaseUtil.getInstance().putCoreCloud(coreListItem.getcUuid(), coreListItem, context, deletePostKey).addOnSuccessListener(new OnSuccessListener() {
                                                @Override
                                                public void onSuccess(Object o) {
                                                    Toast.makeText(context, "코어가 클라우드에 추가되었습니다", Toast.LENGTH_SHORT).show();
                                                    UiUtil.getInstance().stopProgressDialog();
                                                }
                                            });
                                        } catch (NotSetAutoTimeException e) {
                                            e.printStackTrace();
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            ActivityCompat.finishAffinity(((Activity) context).getParent());
                                        }
                                    }
                                }, null
                        );
                    }
                });
            }
        } else if (coreListItem.getcUuid().equals(mUuid)) { // Core 주인이 뷰어일 경우
            // 삭제 가능, Edit은 불가능
            setPostMenu(holder, coreListItem, R.menu.core_post_master_menu);
            holder.core_cloud.setVisibility(View.INVISIBLE);
        } else {
            holder.core_setting.setVisibility(View.GONE);
            holder.core_cloud.setVisibility(View.INVISIBLE);
        }
    }

    private void resetCurrentHolder(CorePostHolder holder) {
        currentHolder.textView_currentPosion = holder.textView_currentPosion;
        currentHolder.seekBar = holder.seekBar;
        currentSeekBarPosition = holder.getAdapterPosition();
        currentHolder.textView_maxTime = holder.textView_maxTime;
        currentHolder.startAndPause = holder.startAndPause;
    }

    private void setAnonymousPost(CorePostHolder holder, CoreListItem coreListItem, CorePost corePost, String mUuid) {
        if(coreListItem.getcUuid() == null){
            // Notice
            return;
        }

        holder.replyBtnLayout.setVisibility(View.VISIBLE);
        holder.core_img.setVisibility(View.GONE);
        holder.core_media.setVisibility(View.GONE);

        holder.core_pic.setImageResource(R.drawable.a);
        holder.core_id.setText(R.string.unknown);
        holder.core_subProfile.setText("");

        if (coreListItem.getcUuid().equals(mUuid)) {   // 주인이 봤을때
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

        if (coreListItem.getcUuid().equals(mUuid)) {   // 주인이 봤을때
            holder.btn_yes.setOnCheckedChangeListener(getListener(coreListItem, "yes"));
            holder.btn_pass.setOnCheckedChangeListener(getListener(coreListItem, "pass"));
            holder.btn_no.setOnCheckedChangeListener(getListener(coreListItem, "no"));
        }
    }

    private void setMasterPost(CorePostHolder holder, final CorePost corePost, final User user) {
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

        if (holder.core_img != null)
            Glide.with(context)
                .load(corePost.getPictureUrl())
                .into(holder.core_img);

        // cloud 일 경우는 프사 클릭시 프로필 액티비티로 들어가지게
        if(context instanceof CoreCloudActivity){
            holder.core_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // block 확인
                    if(DataContainer.getInstance().isBlockWithMe(corePost.getUuid())){
                        return;
                    }

                    Intent p = new Intent(context.getApplicationContext(), FullImageActivity.class);
                    p.putExtra("item", new GridItem(0, corePost.getUuid(), user.getSummaryUser(), ""));
                    context.startActivity(p);

                }
            });
        } else {
            holder.core_pic.setOnClickListener(null);
        }
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
                                intent.putExtra("cUuid", coreListItem.getcUuid());
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
                            case R.id.detach_cloud:
                                // 클라우드 내리기
                                if(context instanceof CoreCloudActivity){
                                    UiUtil.getInstance().showDialog(context, "클라우드 내리기", "정말 클라우드를 내리겠습니까?"
                                        , new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Map<String, Object> childUpdate = new HashMap<>();
                                                // 포스트 isCloud = false
                                                childUpdate.put("posts/" + coreListItem.getcUuid() + "/" + coreListItem.getPostKey() + "/isCloud", false);

                                                // 클라우드에서 포스트 키 null
                                                childUpdate.put("coreCloud/" + coreListItem.getPostKey(), null);

                                                FirebaseDatabase.getInstance().getReference().updateChildren(childUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(context, "코어 포스트를 내렸습니다", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }, null
                                    );
                                }
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
        String msg = "게시물을 삭제하시겠습니까?";
        if(coreListItem.getCorePost().isCloud()){
            msg = "클라우드된 게시물입니다. 정말 게시물을 삭제하시겠습니까?";
        }
        UiUtil.getInstance().showDialog(context, "Delete", msg
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FireBaseUtil.getInstance().deletePostExcution(coreListItem, postsRef, coreListItem.getcUuid());

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
                    postsRef.child(coreListItem.getcUuid()).child(coreListItem.getPostKey())
                            .child("reply").setValue(value).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            if(!finalIsReplyFirst || DataContainer.getInstance().isBlockWithMe(coreListItem.getCorePost().getUuid())) return;

                            final String NickName = DataContainer.getInstance().getUser().getId();
                            AlarmUtil.getInstance().sendAlarm(context,"Answer",NickName,coreListItem.getCorePost(),coreListItem.getPostKey(),coreListItem.getCorePost().getUuid(),coreListItem.getcUuid());
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

    static class CorePostHolder extends RecyclerView.ViewHolder {
        ImageView core_pic, core_img;

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
        TextView check_cloud;

        RelativeLayout profile_layout;
        RelativeLayout heart_btn_layout;

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
            check_cloud = itemView.findViewById(R.id.check_cloud);
            profile_layout = itemView.findViewById(R.id.profile_layout);
            heart_btn_layout = itemView.findViewById(R.id.heart_btn_layout);
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