package com.teamcore.android.core.FriendsActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamcore.android.core.Activity.FindUserActivity;
import com.teamcore.android.core.Entity.User;
import com.teamcore.android.core.Exception.ChildSizeMaxException;
import com.teamcore.android.core.Exception.NotSetAutoTimeException;
import com.teamcore.android.core.PeopleFragment.FullImageActivity;
import com.teamcore.android.core.PeopleFragment.GridItem;
import com.teamcore.android.core.R;
import com.teamcore.android.core.Util.BaseActivity.BaseActivity;
import com.teamcore.android.core.Util.DataContainer;
import com.teamcore.android.core.Util.FireBaseUtil;
import com.teamcore.android.core.Util.GlideApp;
import com.teamcore.android.core.Util.SharedPreferencesUtil;
import com.teamcore.android.core.Util.UiUtil;

import java.util.Collections;
import java.util.List;

import static com.teamcore.android.core.Util.RemoteConfig.CorePossibleOldFriendCount;

/**
 * Created by kimbyeongin on 2017-12-16.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserHolder> {

    private List<Item> items;
    private int itemMenu;
    private BaseActivity context;
    private String field;
    public boolean isReverse = false;
    private boolean isPlus = false;
    private RewardedVideoAd mRewardedVideoAd;
    private RewardedVideoAd mRewardedVideoAd2;

    private InterstitialAd mInterstitialAd;
    private InterstitialAd noFillInterstitialAd;
    private SharedPreferencesUtil SPUtil;
    boolean isFillReward = false;

    boolean isFillReward2 = false;
    private Item ad_Item;
    private View ad_View;
    private Intent ad_i;

    private class VIEW_TYPES {
        static final int Header = 1;
        static final int Normal = 2;
        static final int Footer = 3;
    }

    public UserListAdapter(BaseActivity context, List<Item> items) {
        this.context = context;
        this.items = items;
        if (context instanceof FriendsActivity) {
            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
            loadRewardedVideoAd();
        }

        // Use an activity context to get the rewarded video instance.
        if (context instanceof FindUserActivity) {
            mRewardedVideoAd2 = MobileAds.getRewardedVideoAdInstance(context);
            loadRewardedVideoAd2();
        }

        setmInterstitialAd();
        setnoFillInterstitialAd();
        SPUtil = new SharedPreferencesUtil(context);
    }

    public UserListAdapter(BaseActivity context, List<Item> items, Boolean isPlus) {
        this(context, items);
        this.isPlus = isPlus;
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
            ActivityCompat.finishAffinity((Activity) context);
        }

        if (!field.equals("blockUsers") && !field.equals("Core Heart Count")) {   // block 아닐때만 클릭 가능하도록
            userHolder.profilePicImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent p = new Intent(view.getContext(), FullImageActivity.class);
                    p.putExtra("item", new GridItem(0, item.getUuid(), user.getSummaryUser(), ""));
                    ad_i = p;
                    ad_View = view;
                    if (context instanceof FindUserActivity) {

                        if (!isPlus) {
                            FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid(context)).child(context.getString(R.string.findUserCount)).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                        FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid(context)).child(context.getString(R.string.findUserCount)).setValue(value - 1);
                                        view.getContext().startActivity(p);
                                    } else {
                                        if (isFillReward2) {
                                            view.getContext().startActivity(p);
                                            noFillInterstitialAd.show();
                                        } else {
                                            mRewardedVideoAd2.show();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            view.getContext().startActivity(p);
                        }

                    } else if (context instanceof FriendsActivity) {
                        view.getContext().startActivity(p);
                        if (!isPlus) {
                            SPUtil.increaseAds(mInterstitialAd, "Friends");
                        }
                    } else {
                        view.getContext().startActivity(p);
                    }


                }
            });
        }

        if (field.equals("Core Heart Count")) {
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
                            UiUtil.getInstance().showDialog(context, "회원 차단", "이 회원을 차단합니다.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    final User mUser = context.getUser();
                                    if (mUser.getBlockUsers().size() >= DataContainer.ChildrenMax) {
                                        Toast.makeText(context, "차단 가능한 회원 수를 초과하였습니다", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    ad_Item = item;
                                    if (!isPlus) {
                                        FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid(context)).child(context.getString(R.string.blockCount)).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                                    FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid(context)).child(context.getString(R.string.blockCount)).setValue(value - 1);

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
                                                } else {
                                                    if (isFillReward) {
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

                                                        noFillInterstitialAd.show();
                                                    } else {
                                                        mRewardedVideoAd.show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    } else {
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
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                        }

                        private void unblock() {
                            UiUtil.getInstance().showDialog(context, "회원 차단 해제", "이 회원을 차단 해제합니다.", new DialogInterface.OnClickListener() {
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
                            UiUtil.getInstance().showDialog(context, "팔로잉 취소", "이 회원을 팔로잉 하지않습니다.", new DialogInterface.OnClickListener() {
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
                                        ActivityCompat.finishAffinity((Activity) context);
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
                            UiUtil.getInstance().showDialog(context, "팔로잉", "이 회원을 팔로잉 합니다. 서로 팔로잉하면 친구가 됩니다.", new DialogInterface.OnClickListener() {
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
                                        ActivityCompat.finishAffinity((Activity) context);
                                    }
                                    if (task == null) {
                                        Toast.makeText(context, "이미 팔로잉 중입니다", Toast.LENGTH_SHORT).show();
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
                TextView core_open_count = userHolder.itemView.findViewById(R.id.core_open_count);
                final TextView friends_contents = userHolder.itemView.findViewById(R.id.friends_contents);
                TextView friends_contents2 = userHolder.itemView.findViewById(R.id.friends_contents2);
                TextView header_count = userHolder.itemView.findViewById(R.id.header_count);
                ImageView profile_image = userHolder.itemView.findViewById(R.id.profile_image);
                TextView userNick = userHolder.itemView.findViewById(R.id.userNick);
                TextView userProfile = userHolder.itemView.findViewById(R.id.userProfile);
                LinearLayout setting = userHolder.itemView.findViewById(R.id.setting_layout);
                final TextView list_sequence = userHolder.itemView.findViewById(R.id.list_sequence);

                User mUser = context.getUser();

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
                        core_open_count.setText("");
                        friends_contents2.setText("상대방이 팔로워하면 친구 목록에 추가됩니다");
                        header_count.setText((items.size() - 1) + "");
                        break;
                    case "followerUsers":
                        header_title.setText("팔로워 목록");
                        friends_contents.setText(mUser.getId() + " 님의 팔로워 목록입니다");
                        core_open_count.setText("");
                        friends_contents2.setText("팔로잉하시면 친구 목록에 추가됩니다");
                        header_count.setText((items.size() - 1) + "");
                        break;
                    case "friendUsers":
                        header_title.setText("친구 목록");
                        friends_contents.setText(mUser.getId() + " 님은 CORE 일반 회원입니다");
                        core_open_count.setText("" + CorePossibleOldFriendCount);
                        friends_contents2.setText(" 명의 오래된 친구까지 코어를 열어볼 수 있습니다");
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


    public void setmInterstitialAd() {
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getString(R.string.adsFriendsTab));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(context.getString(R.string.adsBlockUser),
                new AdRequest.Builder()
                        .build());
        mRewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener);
    }

    RewardedVideoAdListener rewardedVideoAdListener = new RewardedVideoAdListener() {

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
            FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid(context)).child(context.getString(R.string.blockCount)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int value = Integer.valueOf(dataSnapshot.getValue().toString());
                        Log.d("test", "몇개 : " + value);
                        if (value > 0) {
                            FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid(context)).child(context.getString(R.string.blockCount)).setValue(value - 1);

                            UiUtil.getInstance().startProgressDialog((Activity) context);
                            try {
                                FireBaseUtil.getInstance().block(ad_Item.getUuid()).addOnCompleteListener(new OnCompleteListener<Void>() {
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
            FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid(context)).child(context.getString(R.string.blockCount)).setValue(rewardItem.getAmount());
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {

            Log.d("test", "onRewardedVideoAdLeftApplication");
        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int i) {
            Toast.makeText(context, "에러코드block" + String.valueOf(i), Toast.LENGTH_LONG).show();
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
    };

    private void loadRewardedVideoAd2() {
        mRewardedVideoAd2.loadAd(context.getString(R.string.adsUserSearching),
                new AdRequest.Builder()
                        .build());
        mRewardedVideoAd2.setRewardedVideoAdListener(rewardedVideoAdListener2);
    }

    RewardedVideoAdListener rewardedVideoAdListener2 = new RewardedVideoAdListener() {


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
            loadRewardedVideoAd2();
            FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid(context)).child(context.getString(R.string.findUserCount)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int value = Integer.valueOf(dataSnapshot.getValue().toString());
                        Log.d("test", "몇개 : " + value);
                        if (value > 0) {
                            FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid(context)).child(context.getString(R.string.findUserCount)).setValue(value - 1);
                            ad_View.getContext().startActivity(ad_i);
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
            FirebaseDatabase.getInstance().getReference(context.getString(R.string.admob)).child(DataContainer.getInstance().getUid(context)).child(context.getString(R.string.findUserCount)).setValue(rewardItem.getAmount());
        }

        @Override
        public void onRewardedVideoAdLeftApplication() {

            Log.d("test", "onRewardedVideoAdLeftApplication");
        }

        @Override
        public void onRewardedVideoAdFailedToLoad(int i) {
            Toast.makeText(context, "에러코드UserSearching" + String.valueOf(i), Toast.LENGTH_LONG).show();
            switch (i) {
                case 0:
                    // 에드몹 내부서버에러
                    Toast.makeText(context, "내부서버에 문제가 있습니다.", Toast.LENGTH_LONG).show();
                    loadRewardedVideoAd2();
                    break;
                case 2:
                    // 네트워크 연결상태 불량
                    Toast.makeText(context, "네트워크 연결상태가 좋지 않습니다.", Toast.LENGTH_LONG).show();
                    loadRewardedVideoAd2();
                    break;
                case 3:
                    // 에드몹 광고 인벤토리 부족
                    isFillReward2 = true;
                    break;
            }

        }
    };

    public void Resume() {
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.resume(context);
            mRewardedVideoAd.setRewardedVideoAdListener(rewardedVideoAdListener);
        }

        if (mRewardedVideoAd2 != null) {
            mRewardedVideoAd2.resume(context);
            mRewardedVideoAd2.setRewardedVideoAdListener(rewardedVideoAdListener2);
        }
    }

    public void Pause() {
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.pause(context);
            mRewardedVideoAd.setRewardedVideoAdListener(null);
        }
        if (mRewardedVideoAd2 != null) {
            mRewardedVideoAd2.pause(context);
            mRewardedVideoAd2.setRewardedVideoAdListener(null);
        }
    }

    public void Destroy() {
        if (mRewardedVideoAd != null) {
            mRewardedVideoAd.destroy(context);
            mRewardedVideoAd.setRewardedVideoAdListener(null);
        }
        if (mRewardedVideoAd2 != null) {
            mRewardedVideoAd2.destroy(context);
            mRewardedVideoAd2.setRewardedVideoAdListener(null);
        }
    }

}
