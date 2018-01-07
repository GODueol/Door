package com.example.kwoncheolhyeok.core.CorePage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

/**
 * Created by KwonCheolHyeok on 2017-01-17.
 */

public class CoreListAdapter extends RecyclerView.Adapter<CoreListAdapter.CorePostHolder> {

    private List<CoreListItem> posts;
    private Context context;
    private String cUuid;

    CoreListAdapter(List<CoreListItem> posts, Context context, String cUuid) {
        this.posts = posts;
        this.context = context;
        this.cUuid = cUuid;
    }

    @Override
    public CorePostHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.core_list_item, viewGroup, false);
        return new CorePostHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(CorePostHolder holder, int i) {

        final CoreListItem coreListItem = posts.get(i);
        CorePost corePost = coreListItem.getCorePost();
        final String mUuid = DataContainer.getInstance().getUid();

        User user = coreListItem.getUser();
        if(user != null) {  // 주인글
            holder.replyBtnLayout.setVisibility(View.GONE);
            holder.core_img.setVisibility(View.VISIBLE);
            holder.core_media.setVisibility(View.VISIBLE);

            Glide.with(context /* context */)
                    .load(user.getPicUrls().getPicUrl1())
                    .into(holder.core_pic);
            holder.core_id.setText(user.getId());
            holder.core_subprofile.setText(TextUtils.join("/", new String[]{Integer.toString(user.getAge()), Integer.toString(user.getHeight()),
                    Integer.toString(user.getWeight()), user.getBodyType()}));
            Glide.with(context /* context */)
                    .load(corePost.getPictureUrl())
                    .into(holder.core_img);

            // 미디어 셋

        } else {    // 타인글
            holder.replyBtnLayout.setVisibility(View.VISIBLE);
            holder.core_img.setVisibility(View.GONE);
            holder.core_media.setVisibility(View.GONE);
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

                                FireBaseUtil.getInstance().syncCorePostCount(cUuid);
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
        ImageButton core_setting, core_heart;
        TextView core_id, core_subprofile, core_date, core_contents, core_media, core_heart_count;
        LikeButton core_heart_btn;
        LinearLayout replyBtnLayout;
        ToggleButton btn_yes, btn_pass, btn_no;
        CorePostHolder(View itemView) {
            super(itemView);

            core_pic = itemView.findViewById(R.id.core_pic);
            core_img = itemView.findViewById(R.id.core_img);

            core_id = itemView.findViewById(R.id.core_id);
            core_subprofile= itemView.findViewById(R.id.sub_profile);
            core_date = itemView.findViewById(R.id.core_date);
            core_contents = itemView.findViewById(R.id.core_contents);

            core_media= itemView.findViewById(R.id.media_player_txt);
            core_setting= itemView.findViewById(R.id.setting);

            core_heart_count = itemView.findViewById(R.id.heart_count_txt);
            core_heart= itemView.findViewById(R.id.heart_count);
            core_heart_btn = itemView.findViewById(R.id.core_heart_btn);
            replyBtnLayout = itemView.findViewById(R.id.reply_btn_layout);

            btn_yes = itemView.findViewById(R.id.btn_yes);
            btn_pass = itemView.findViewById(R.id.btn_pass);
            btn_no = itemView.findViewById(R.id.btn_no);
        }
    }

}