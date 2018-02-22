package com.example.kwoncheolhyeok.core.MessageActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.kwoncheolhyeok.core.MessageActivity.util.ChatMessageView;
import com.example.kwoncheolhyeok.core.MessageActivity.util.DateUtil;
import com.example.kwoncheolhyeok.core.PeopleFragment.FullImageActivity;
import com.example.kwoncheolhyeok.core.PeopleFragment.GridItem;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.GlideApp;
import com.example.kwoncheolhyeok.core.Util.UiUtil;

import java.util.List;


/**
 * Created by Administrator on 2018-01-12.
 */

public class ChattingMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1, MY_IMAGE = 2, OTHER_IMAGE = 3;
    private OnImesageLoadingCallback onImesageLoadingCallback;

    private List<ChatMessage> itemList;
    private GridItem item;
    RequestListener requestListener;
    private Context context;


    public interface OnImesageLoadingCallback {
        void onReady();

        void onRemove(String s, int i);
    }

    public ChattingMessageAdapter(List<ChatMessage> itemList, OnImesageLoadingCallback listener) {
        this.itemList = itemList;
        onImesageLoadingCallback = listener;
        setRequestListener();
    }

    public String getDate(int position) {
        DateUtil dateUtil = new DateUtil(itemList.get(position).getTime());
        String strResult = dateUtil.getDate2();
        return strResult;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = itemList.get(position);
        return message.getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int chatLayoutId = 0;
        context = parent.getContext();
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case MY_MESSAGE:
                chatLayoutId = R.layout.chatting_item_mine_message;
                view = inflater.inflate(chatLayoutId, parent, false);
                viewHolder = new ViewHolder_mine_message(view);
                break;
            case OTHER_MESSAGE:
                chatLayoutId = R.layout.chatting_item_other_message;
                view = inflater.inflate(chatLayoutId, parent, false);
                viewHolder = new ViewHolder_other_message(view);
                break;
            case MY_IMAGE:
                chatLayoutId = R.layout.chatting_item_mine_image;
                view = inflater.inflate(chatLayoutId, parent, false);
                viewHolder = new ViewHolder_mine_image(view);
                break;
            case OTHER_IMAGE:
                chatLayoutId = R.layout.chatting_item_other_image;
                view = inflater.inflate(chatLayoutId, parent, false);
                viewHolder = new ViewHolder_other_image(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        ChatMessage chatMessage = itemList.get(position);
        int viewType = chatMessage.getType();
        String profileImage = chatMessage.getProfileImage();
        String content = chatMessage.getContent();
        String image = chatMessage.getImage();
        Long date = chatMessage.getTime();
        Long date2 = null;
        if (position != 0) {
            date2 = itemList.get(position - 1).getTime();
        }
        int check = chatMessage.getCheck();

        switch (viewType) {
            case MY_MESSAGE:
                ViewHolder_mine_message holder0 = (ViewHolder_mine_message) viewHolder;
                holder0.contentTextView.setText(content);
                holder0.contentTextView.setOnLongClickListener(copyTextListener);
                setDateLineVisiable(holder0.dateLinearLayout, date, date2);
                setDateUtil(holder0.timeTextView, holder0.checkTextView, holder0.dateTextView, date, check);
                break;
            case OTHER_MESSAGE:
                ViewHolder_other_message holder1 = (ViewHolder_other_message) viewHolder;
                item = chatMessage.getItem();
                holder1.contentTextView.setText(content);
                holder1.contentTextView.setOnLongClickListener(copyTextListener);
                setDateLineVisiable(holder1.dateLinearLayout, date, date2);
                setDateUtil(holder1.timeTextView, holder1.checkTextView, holder1.dateTextView, date, check);
                setProfileImage(holder1.profileImageView, profileImage);
                holder1.profileImageView.setOnClickListener(moveProfileListener);
                break;
            case MY_IMAGE:
                ViewHolder_mine_image holder2 = (ViewHolder_mine_image) viewHolder;
                if (!chatMessage.getImage().equals("DELETE")) {
                    holder2.viewImage();
                    setImageMessage(holder2.messageImageView, image, chatMessage.getParent(), position);
                } else {
                    holder2.viewDeleteText();
                }
                setDateLineVisiable(holder2.dateLinearLayout, date, date2);
                setDateUtil(holder2.timeTextView, holder2.checkTextView, holder2.dateTextView, date, check);
                break;
            case OTHER_IMAGE:
                ViewHolder_other_image holder3 = (ViewHolder_other_image) viewHolder;
                item = chatMessage.getItem();
                setProfileImage(holder3.profileImageView, profileImage);
                if (!chatMessage.getImage().equals("DELETE")) {
                    holder3.viewImage();
                    setImageMessage(holder3.messageImageView, image, null, position);
                } else {
                    holder3.viewDeleteText();
                }
                setDateLineVisiable(holder3.dateLinearLayout, date, date2);
                setDateUtil(holder3.timeTextView, holder3.checkTextView, holder3.dateTextView, date, check);
                holder3.profileImageView.setOnClickListener(moveProfileListener);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setRequestListener() {
        requestListener = new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                onImesageLoadingCallback.onReady();
                return false;
            }
        };
    }

    public void deletRequestListener() {
        requestListener = null;
    }


    public static class ViewHolder_mine_message extends RecyclerView.ViewHolder {

        LinearLayout dateLinearLayout;
        TextView dateTextView;
        TextView contentTextView;
        ImageView profileImageView;
        TextView timeTextView;
        TextView checkTextView;
        ImageView messageImageView;

        public ViewHolder_mine_message(View itemView) {
            super(itemView);
            dateLinearLayout = (LinearLayout) itemView.findViewById(R.id.dateLayout);
            dateTextView = (TextView) itemView.findViewById(R.id.dateText);
            contentTextView = (TextView) itemView.findViewById(R.id.chatText);
            profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
            timeTextView = (TextView) itemView.findViewById(R.id.time);
            checkTextView = (TextView) itemView.findViewById(R.id.check);
            messageImageView = (ImageView) itemView.findViewById(R.id.chatImage);
        }
    }

    public static class ViewHolder_other_message extends RecyclerView.ViewHolder {

        LinearLayout dateLinearLayout;
        TextView dateTextView;
        TextView contentTextView;
        ImageView profileImageView;
        TextView timeTextView;
        TextView checkTextView;
        ImageView messageImageView;

        public ViewHolder_other_message(View itemView) {
            super(itemView);
            dateLinearLayout = (LinearLayout) itemView.findViewById(R.id.dateLayout);
            dateTextView = (TextView) itemView.findViewById(R.id.dateText);
            contentTextView = (TextView) itemView.findViewById(R.id.chatText);
            profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
            timeTextView = (TextView) itemView.findViewById(R.id.time);
            checkTextView = (TextView) itemView.findViewById(R.id.check);
            messageImageView = (ImageView) itemView.findViewById(R.id.chatImage);
        }
    }

    public static class ViewHolder_mine_image extends RecyclerView.ViewHolder {

        LinearLayout dateLinearLayout;
        TextView dateTextView;
        ImageView profileImageView;
        TextView timeTextView;
        TextView checkTextView;
        ImageView messageImageView;

        ChatMessageView chatMessageView;
        TextView deleteTextView;

        public ViewHolder_mine_image(View itemView) {
            super(itemView);
            dateLinearLayout = (LinearLayout) itemView.findViewById(R.id.dateLayout);
            dateTextView = (TextView) itemView.findViewById(R.id.dateText);
            profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
            timeTextView = (TextView) itemView.findViewById(R.id.time);
            checkTextView = (TextView) itemView.findViewById(R.id.check);
            messageImageView = (ImageView) itemView.findViewById(R.id.chatImage);

            chatMessageView = (ChatMessageView) itemView.findViewById(R.id.chatMessageView);
            deleteTextView = (TextView) itemView.findViewById(R.id.chatText);
        }

        public void viewImage() {
            messageImageView.setVisibility(View.VISIBLE);
            chatMessageView.setVisibility(View.GONE);
        }

        public void viewDeleteText() {
            messageImageView.setVisibility(View.GONE);
            chatMessageView.setVisibility(View.VISIBLE);
        }
    }

    public static class ViewHolder_other_image extends RecyclerView.ViewHolder {

        LinearLayout dateLinearLayout;
        TextView dateTextView;
        ImageView profileImageView;
        TextView timeTextView;
        TextView checkTextView;
        ImageView messageImageView;

        ChatMessageView chatMessageView;
        TextView deleteTextView;

        public ViewHolder_other_image(View itemView) {
            super(itemView);
            dateLinearLayout = (LinearLayout) itemView.findViewById(R.id.dateLayout);
            dateTextView = (TextView) itemView.findViewById(R.id.dateText);
            profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
            timeTextView = (TextView) itemView.findViewById(R.id.time);
            checkTextView = (TextView) itemView.findViewById(R.id.check);
            messageImageView = (ImageView) itemView.findViewById(R.id.chatImage);

            chatMessageView = (ChatMessageView) itemView.findViewById(R.id.chatMessageView);
            deleteTextView = (TextView) itemView.findViewById(R.id.chatText);

        }

        public void viewImage() {
            messageImageView.setVisibility(View.VISIBLE);
            chatMessageView.setVisibility(View.GONE);
        }

        public void viewDeleteText() {
            messageImageView.setVisibility(View.GONE);
            chatMessageView.setVisibility(View.VISIBLE);
        }
    }


    public void setProfileImage(ImageView imageView, String uri) {
        int width = imageView.getWidth();
        int height = imageView.getHeight();

        GlideApp.with(imageView.getContext())
                .load(uri)
                .override(width, height)
                .centerCrop()
                .placeholder(R.drawable.a)
                .into(imageView);
    }

    public void setImageMessage(final ImageView imageView, final String uri, final String parent, final int position) {
        GlideApp.with(imageView.getContext())
                .asBitmap()
                .placeholder(R.drawable.picture_load)
                .centerCrop()
                .load(uri)
                .override(600, 600)
                .fitCenter()
                .listener(requestListener)
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent p = new Intent(context.getApplicationContext(), ChattingFullImage.class);
                p.putExtra("imageUri", uri);
                p.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.getApplicationContext().startActivity(p);
            }
        });
        if (parent != null) {
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    UiUtil.getInstance().showDialog(context, "사진 삭제", "삭제하시면 상대방도 사진을 볼 수 없으며\n 삭제된 사진은 복구할 수 없습니다.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onImesageLoadingCallback.onRemove(parent, position);
                        }
                    }, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    return false;
                }
            });
        }
    }

    public void setDateLineVisiable(LinearLayout linearLayout, Long date, Long date2) {
        DateUtil dateUtil = new DateUtil(date);
        String dstr = null;
        String dstr2 = null;

        if (date2 != null) {
            dstr = dateUtil.getDate();
            dateUtil.setDate(date2);
            dstr2 = dateUtil.getDate();
        }

        if (date2 == null || !dstr.equals(dstr2)) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
        }

    }

    public void setDateUtil(TextView tTextView, TextView cTextView, TextView dTextView, Long date, int check) {
        DateUtil dateUtil = new DateUtil(date);

        String time = dateUtil.getTime();
        String dateStr = dateUtil.getDate();
        tTextView.setText(time);
        dTextView.setText(dateStr);

        if (check == 0) {
            cTextView.setText("");
        } else {
            cTextView.setText(Integer.toString(check));
        }
    }

    View.OnLongClickListener copyTextListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText("text", ((TextView) v).getText()));
            return false;
        }
    };

    View.OnClickListener moveProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent p = new Intent(context, FullImageActivity.class);
            p.putExtra("item", item);
            context.startActivity(p);
        }
    };


}

