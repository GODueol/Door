package com.example.kwoncheolhyeok.core.FriendsActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kwoncheolhyeok.core.Entity.User;
import com.example.kwoncheolhyeok.core.Exception.ChildSizeMaxException;
import com.example.kwoncheolhyeok.core.Exception.NotSetAutoTimeException;
import com.example.kwoncheolhyeok.core.PeopleFragment.FullImageActivity;
import com.example.kwoncheolhyeok.core.PeopleFragment.GridItem;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.DataContainer;
import com.example.kwoncheolhyeok.core.Util.FireBaseUtil;
import com.example.kwoncheolhyeok.core.Util.GlideApp;
import com.example.kwoncheolhyeok.core.Util.UiUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Collections;
import java.util.List;

/**
 * Created by kimbyeongin on 2017-12-16.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserHolder> {

    private List<Item> items;
    private int itemMenu;
    private Context context;
    private String field;
    public boolean isReverse = false;

    private class VIEW_TYPES {
        static final int Header = 1;
        static final int Normal = 2;
        static final int Footer = 3;
    }

    public UserListAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = items;
    }

    public void setItemMenu(int itemMenu, String tabName) {
        this.itemMenu = itemMenu;
        this.field = tabName;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup viewGroup, int Type) {

        View rowView;

        switch (Type) {
            case VIEW_TYPES.Normal:
                rowView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_list_item, viewGroup, false);
                break;
            case VIEW_TYPES.Header:
                rowView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_list_header, viewGroup, false);
                break;
            default:
                rowView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_list_item, viewGroup, false);
                break;
        }
        return new UserHolder(rowView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final UserHolder userHolder, int i) {
        final Item item = items.get(i);
        final User user = item.getUser();

        // setHeader
        if (setHeader(userHolder, user)) return;

        Glide.with(userHolder.profilePicImage.getContext()).load(user.getPicUrls().getThumbNail_picUrl1()).into(userHolder.profilePicImage);
        userHolder.idText.setText(user.getId());
        userHolder.subProfileText.setText(UiUtil.getInstance().setSubProfile(user));
        try {
            userHolder.dateText.setText(DataContainer.getInstance().convertBeforeFormat(item.getDate(), context));
        } catch (NotSetAutoTimeException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            ActivityCompat.finishAffinity((Activity)context);
        }

        if (!field.equals("blockUsers") && !field.equals("Core Heart Count")) {   // block 아닐때만 클릭 가능하도록
            userHolder.profilePicImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent p = new Intent(view.getContext(), FullImageActivity.class);

                    p.putExtra("item", new GridItem(0, item.getUuid(), user.getSummaryUser(), ""));

                    view.getContext().startActivity(p);
                }
            });
        }

        if(field.equals("Core Heart Count")){
            userHolder.itemMenuBtn.setVisibility(View.INVISIBLE);
        } else {
            userHolder.itemMenuBtn.setVisibility(View.VISIBLE);
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
                            } else if (i == R.id.followCancel) {
                                unFollow();
                                return true;
                            } else if (i == R.id.block) {
                                block();
                                return true;
                            } else if (i == R.id.core) {
                                // Go to Core
                                UiUtil.getInstance().goToCoreActivity(context, item.getUuid());
                                return true;
                            } else if (i == R.id.unblock) {
                                unblock();
                                return true;
                            } else {
                                return true;
                            }
                        }

                        private void block() {
                            UiUtil.getInstance().showDialog(context, "유저 차단", "해당 유저를 차단하시겠습니까?", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    final User mUser = DataContainer.getInstance().getUser();
                                    if (mUser.getBlockUsers().size() >= DataContainer.ChildrenMax) {
                                        Toast.makeText(context, DataContainer.ChildrenMax + "명을 초과할 수 없습니다", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    UiUtil.getInstance().startProgressDialog((Activity) context);
                                    try {
                                        FireBaseUtil.getInstance().block(item.getUuid()).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                    Task<Void> task = null;
                                    try {
                                        task = FireBaseUtil.getInstance().follow(context, user, item.getUuid(), true);
                                    } catch (ChildSizeMaxException e) {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        UiUtil.getInstance().stopProgressDialog();
                                        return;
                                    } catch (NotSetAutoTimeException e) {
                                        e.printStackTrace();
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        ActivityCompat.finishAffinity((Activity)context);
                                    }
                                    if (task == null) {
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
                                    Task<Void> task = null;
                                    try {
                                        task = FireBaseUtil.getInstance().follow(context, user, item.getUuid(), false);
                                    } catch (ChildSizeMaxException e) {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        UiUtil.getInstance().stopProgressDialog();
                                        return;
                                    } catch (NotSetAutoTimeException e) {
                                        e.printStackTrace();
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        ActivityCompat.finishAffinity((Activity)context);
                                    }
                                    if (task == null) {
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
    }

    @SuppressLint("SetTextI18n")
    private boolean setHeader(UserHolder userHolder, User user) {
        if (user == null) {
            ViewGroup.LayoutParams params = userHolder.itemView.getLayoutParams();
            if (field.equals("followingUsers") || field.equals("followerUsers") || field.equals("friendUsers")) {  // 헤더 추가할 메뉴
                userHolder.itemView.setVisibility(View.VISIBLE);
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

                TextView header_title = userHolder.itemView.findViewById(R.id.header_title);
                final TextView friends_contents = userHolder.itemView.findViewById(R.id.friends_contents);
                TextView friends_contents2 = userHolder.itemView.findViewById(R.id.friends_contents2);
                TextView header_count = userHolder.itemView.findViewById(R.id.header_count);
                ImageView profile_image = userHolder.itemView.findViewById(R.id.profile_image);
                TextView userNick = userHolder.itemView.findViewById(R.id.userNick);
                TextView userProfile = userHolder.itemView.findViewById(R.id.userProfile);
                LinearLayout setting = userHolder.itemView.findViewById(R.id.setting_layout);
                final TextView list_sequence = userHolder.itemView.findViewById(R.id.list_sequence);

                User mUser = DataContainer.getInstance().getUser();

                if (mUser.getPicUrls() != null && mUser.getPicUrls().getThumbNail_picUrl1() != null)
                    GlideApp.with(context).load(mUser.getPicUrls().getThumbNail_picUrl1()).centerCrop()
                            .into(profile_image);
                userNick.setText(mUser.getId());
                userProfile.setText(UiUtil.getInstance().setSubProfile(mUser));

                setting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final PopupMenu popup = new PopupMenu(view.getContext(), view);
                        popup.getMenuInflater().inflate(R.menu.sort_menu, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {

                                if (menuItem.getItemId() == R.id.old_order) {
                                    if (isReverse) return false;
                                    list_sequence.setText(R.string.oldOrder);
                                    isReverse = true;

                                } else if (menuItem.getItemId() == R.id.new_order) {
                                    if (!isReverse) return false;
                                    list_sequence.setText(R.string.newOrder);
                                    isReverse = false;
                                }

                                // 첫번째만 남기고 모두 Reverse
                                Item header = items.get(0);
                                items.remove(0);
                                Collections.reverse(items);
                                items.add(0, header);
                                notifyDataSetChanged();

                                return false;
                            }
                        });
                        popup.show();

                    }
                });

                switch (field) {
                    case "followingUsers":
                        header_title.setText("팔로잉 목록");
                        friends_contents.setText(mUser.getId() + " 님의 팔로잉 목록입니다");
                        friends_contents2.setText("상대방이 팔로워하면 친구 목록에 추가됩니다");
                        header_count.setText((items.size() - 1) + "");
                        break;
                    case "followerUsers":
                        header_title.setText("팔로워 목록");
                        friends_contents.setText(mUser.getId() + " 님의 팔로워 목록입니다");
                        friends_contents2.setText("팔로잉하시면 친구 목록에 추가됩니다");
                        header_count.setText((items.size() - 1) + "");
                        break;
                    case "friendUsers":
                        header_title.setText("친구 목록");
                        friends_contents.setText(mUser.getId() + " 님은 CORE 일반 회원입니다");
                        friends_contents2.setText("3명의 친구까지 코어를 열어볼 수 있습니다");
                        header_count.setText((items.size() - 1) + "");
                        break;
                }

            } else { // 제외할 메뉴
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

        if (items.get(position).isHeader)
            return VIEW_TYPES.Header;
        else if (items.get(position).isFooter)
            return VIEW_TYPES.Footer;
        else
            return VIEW_TYPES.Normal;

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class UserHolder extends RecyclerView.ViewHolder {
        ImageView profilePicImage;
        TextView idText;
        TextView subProfileText;
        TextView dateText;
        ImageView itemMenuBtn;

        UserHolder(View itemView) {
            super(itemView);
            profilePicImage = itemView.findViewById(R.id.profile_image);
            idText = itemView.findViewById(R.id.message);
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

        public Item(boolean isHeader) {
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
