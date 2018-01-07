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
import com.example.kwoncheolhyeok.core.PeopleFragment.FullImageActivity;
import com.example.kwoncheolhyeok.core.PeopleFragment.ImageAdapter;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.example.kwoncheolhyeok.core.Util.UiUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

/**
 * Created by kimbyeongin on 2017-12-16.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserHolder> {

    private List<Item> items;
    private int itemMenu;
    private Context context;
    private String field;

    private class VIEW_TYPES {
        public static final int Header = 1;
        public static final int Normal = 2;
        public static final int Footer = 3;
    }

    public UserListAdapter(Context context, List<Item> items){
        this.context = context;
        this.items = items;
    }
    public void setItemMenu(int itemMenu, String tabName){
        this.itemMenu = itemMenu;
        this.field = tabName;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View rowView;

        switch (i)
        {
            case VIEW_TYPES.Normal:
                rowView=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_list_item, viewGroup, false);
                break;
            case VIEW_TYPES.Header:
                rowView=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_friends_list, viewGroup, false);
                break;
            default:
                rowView=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_list_item, viewGroup, false);
                break;
        }
        return new UserHolder (rowView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final UserHolder userHolder, int i) {
        final Item item = items.get(i);
        final User user = item.getUser();

        // setHeader
        if (setHeader(userHolder, user)) return;

        Glide.with(userHolder.profilePicImage.getContext()).load(user.getPicUrls().getPicUrl1()).into(userHolder.profilePicImage);
        userHolder.idText.setText(user.getId());
        userHolder.subProfileText.setText(TextUtils.join("/", new String[]{Integer.toString(user.getAge()), Integer.toString(user.getHeight()),
                Integer.toString(user.getWeight()), user.getBodyType()}));
        userHolder.dateText.setText( DataContainer.getInstance().convertBeforeFormat(item.getDate()));

        userHolder.profilePicImage.setOnClickListener(new View.OnClickListener() {
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
                            follow();
                            return true;
                        }
                        else if (i == R.id.followCancel){
                            unFollow();
                            return true;
                        }
                        else if (i == R.id.block) {
                            block();
                            return true;
                        }
                        else if(i == R.id.core){
                            // Go to Core
                            UiUtil.getInstance().goToCoreActivity(context, item.getUuid());
                            return true;
                        }
                        else if (i == R.id.unblock) {
                            unblock();
                            return true;
                        }
                        else {
                            return true;
                        }
                    }

                    private void block() {
                        UiUtil.getInstance().showDialog(context, "유저 차단", "해당 유저를 차단하시겠습니까?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                UiUtil.getInstance().startProgressDialog((Activity) context);
                                FireBaseUtil.getInstance().block(item.getUuid()).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                    }

                    private void unblock() {
                        UiUtil.getInstance().showDialog(context, "유저 차단해제", "해당 유저를 차단해제하시겠습니까?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                UiUtil.getInstance().startProgressDialog((Activity) context);
                                FireBaseUtil.getInstance().unblock(item.getUuid()).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                    }

                    private void unFollow() {
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
                    }

                    private void follow() {
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
                    }
                });

                popup.show();
            }
        });
    }

    private boolean setHeader(UserHolder userHolder, User user) {
        if(user == null) {
            ViewGroup.LayoutParams params = userHolder.itemView.getLayoutParams();
            if(field.equals("followingUsers") || field.equals("followerUsers") || field.equals("friendUsers") ) {  // 헤더 추가할 메뉴
                userHolder.itemView.setVisibility(View.VISIBLE);
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
            else { // 제외할 메뉴
                userHolder.itemView.setVisibility(View.INVISIBLE);
                params.height = 0;
            }
            userHolder.itemView.setLayoutParams(params);
            return true;
        }
        return false;
    }

    @Override
    public int getItemViewType(int position) {

        if(items.get(position).isHeader)
            return VIEW_TYPES.Header;
        else if(items.get(position).isFooter)
            return VIEW_TYPES.Footer;
        else
            return VIEW_TYPES.Normal;

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class UserHolder extends RecyclerView.ViewHolder {
        ImageView profilePicImage;
        TextView idText;
        TextView subProfileText;
        TextView dateText;
        ImageView itemMenuBtn;
        UserHolder(View itemView) {
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

        boolean isHeader = false, isFooter = false;

        public Item(boolean isHeader){
            this.isHeader = isHeader;
        }

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

    }

}
