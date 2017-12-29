package com.example.kwoncheolhyeok.core.FriendsActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.messageRecyclerAdapter;
import com.example.kwoncheolhyeok.core.PeopleFragment.FullImageActivity;
import com.example.kwoncheolhyeok.core.PeopleFragment.ImageAdapter;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.example.kwoncheolhyeok.core.Util.UiUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kimbyeongin on 2017-12-16.
 */

public class userListAdapter extends RecyclerView.Adapter<userListAdapter.userHolder> {

    private List<Item> items;
    private int itemMenu;
    private Context context;
    userListAdapter(Context context, List<Item> items){
        this.context = context;
        this.items = items;
    }
    void setItemMenu(int itemMenu){
        this.itemMenu = itemMenu;
    }

    @Override
    public userHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_list_item,viewGroup,false);
        return new userHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final userHolder userHolder, int i) {
        final Item item = items.get(i);
        final User user = item.getUser();
        Glide.with(userHolder.profilePicImage.getContext()).load(user.getPicUrls().getPicUrl1()).into(userHolder.profilePicImage);
        userHolder.idText.setText(user.getId());
        userHolder.subProfileText.setText(TextUtils.join("/", new String[]{Integer.toString(user.getAge()), Integer.toString(user.getHeight()),
                Integer.toString(user.getWeight()), user.getBodyType()}));
        SimpleDateFormat dateFormat = DataContainer.dateFormat;
        userHolder.dateText.setText( dateFormat.format(new Date(item.getDate())));

        userHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent p = new Intent(view.getContext(), FullImageActivity.class);

                p.putExtra("item", new ImageAdapter.Item(0, item.getUuid(), user, ""));

                view.getContext().startActivity(p);
            }
        });
        userHolder.itemMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu popup = new PopupMenu(view.getContext(), view);
                popup.getMenuInflater().inflate(itemMenu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int i = menuItem.getItemId();
                        if (i == R.id.follow) {
                            // 팔로우
                            UiUtil.getInstance().showDialog(context, "팔로우 신청", "해당 유저 팔로우 신청하시겠습니까?", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    UiUtil.getInstance().startProgressDialog((Activity) context);
                                    Task<Void> task = FireBaseUtil.getInstance().follow(user, item.getUuid(), false);
                                    if(task == null){
                                        Toast.makeText(context, "팔로우 신청 되어있습니다", Toast.LENGTH_SHORT).show();
                                        UiUtil.getInstance().stopProgressDialog();
                                        return;
                                    }
                                    task.addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            UiUtil.getInstance().stopProgressDialog();
                                        }
                                    });
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            return true;
                        }
                        else if (i == R.id.followCancel){
                            // 팔로우 취소
                            UiUtil.getInstance().showDialog(context, "팔로우 취소", "해당 유저 팔로우 취소하시겠습니까?", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    UiUtil.getInstance().startProgressDialog((Activity) context);
                                    Task<Void> task = FireBaseUtil.getInstance().follow(user, item.getUuid(), true);
                                    if(task == null){
                                        Toast.makeText(context, "팔로우 취소 상태입니다", Toast.LENGTH_SHORT).show();
                                        UiUtil.getInstance().stopProgressDialog();
                                        return;
                                    }
                                    task.addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            UiUtil.getInstance().stopProgressDialog();
                                        }
                                    });
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            return true;
                        }
                        else if (i == R.id.navigation_friends) {
                            //do something
                            return true;
                        }
                        else {
                            return onMenuItemClick(menuItem);
                        }
                    }
                });

                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class userHolder extends messageRecyclerAdapter.ViewHolder {
        ImageView profilePicImage;
        TextView idText;
        TextView subProfileText;
        TextView dateText;
        ImageView itemMenuBtn;
        userHolder(View itemView) {
            super(itemView);
            profilePicImage = itemView.findViewById(R.id.profile_image);
            idText = itemView.findViewById(R.id.lblListItem);
            subProfileText = itemView.findViewById(R.id.sub_profile);
            dateText = itemView.findViewById(R.id.date);
            itemMenuBtn = itemView.findViewById(R.id.item_menu_btn);
        }
    }

    public static class Item {
        User user;
        long date;
        String uuid;

        public Item(User user, long date, String uuid) {
            this.user = user;
            this.date = date;
            this.uuid = uuid;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }
    }

}
