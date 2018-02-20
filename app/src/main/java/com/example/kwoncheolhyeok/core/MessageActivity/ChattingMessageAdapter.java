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
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.kwoncheolhyeok.core.MessageActivity.util.DateUtil;
import com.example.kwoncheolhyeok.core.PeopleFragment.FullImageActivity;
import com.example.kwoncheolhyeok.core.PeopleFragment.GridItem;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.GlideApp;
import com.example.kwoncheolhyeok.core.Util.UiUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by Administrator on 2018-01-12.
 */

public class ChattingMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1, MY_IMAGE = 2, OTHER_IMAGE = 3;
    private OnImesageLoadingCallback onImesageLoadingCallback;

    private List<ChatMessage> itemList;
    private GridItem item;
    RequestListener requestListener;
    private SimpleDateFormat sdf;
    private Context context;



    public interface OnImesageLoadingCallback {
        void onReady();
        void onRemove(String s,int i);
    }

    public ChattingMessageAdapter(List<ChatMessage> itemList, OnImesageLoadingCallback listener) {
        this.itemList = itemList;
        onImesageLoadingCallback = listener;
        sdf = new SimpleDateFormat( "yyyy. MM. dd. E", Locale.KOREAN );
        setRequestListener();
    }

    public String getDate(int position) {
        Date date = new Date( itemList.get(position).getTime() );
        String strResult = sdf.format( date );
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
        int check = chatMessage.getCheck();

        switch (viewType) {
            case MY_MESSAGE:
                ViewHolder_mine_message holder0 = (ViewHolder_mine_message) viewHolder;
                holder0.contentTextView.setText(content);
                holder0.contentTextView.setOnLongClickListener(copyTextListener);
                setDateUtil(holder0.dateTextView, holder0.checkTextView, date, check);
                break;
            case OTHER_MESSAGE:
                ViewHolder_other_message holder1 = (ViewHolder_other_message) viewHolder;
                item = chatMessage.getItem();
                holder1.contentTextView.setText(content);
                holder1.contentTextView.setOnLongClickListener(copyTextListener);
                setDateUtil(holder1.dateTextView, holder1.checkTextView, date, check);
                setProfileImage(holder1.profileImageView, profileImage);
                holder1.profileImageView.setOnClickListener(moveProfileListener);
                break;
            case MY_IMAGE:
                ViewHolder_mine_image holder2 = (ViewHolder_mine_image) viewHolder;
                setImageMessage(holder2.messageImageView, image,chatMessage.getParent(),position);

                setDateUtil(holder2.dateTextView, holder2.checkTextView, date, check);
                break;
            case OTHER_IMAGE:
                ViewHolder_other_image holder3 = (ViewHolder_other_image) viewHolder;
                item = chatMessage.getItem();
                setProfileImage(holder3.profileImageView, profileImage);
                setImageMessage(holder3.messageImageView, image,null,position);
                setDateUtil(holder3.dateTextView, holder3.checkTextView, date, check);
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

        TextView contentTextView;
        ImageView profileImageView;
        TextView dateTextView;
        TextView checkTextView;
        ImageView messageImageView;

        public ViewHolder_mine_message(View itemView) {
            super(itemView);
            contentTextView = (TextView) itemView.findViewById(R.id.chatText);
            profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
            dateTextView = (TextView) itemView.findViewById(R.id.time);
            checkTextView = (TextView) itemView.findViewById(R.id.check);
            messageImageView = (ImageView) itemView.findViewById(R.id.chatImage);
        }
    }

    public static class ViewHolder_other_message extends RecyclerView.ViewHolder {

        TextView contentTextView;
        ImageView profileImageView;
        TextView dateTextView;
        TextView checkTextView;
        ImageView messageImageView;

        public ViewHolder_other_message(View itemView) {
            super(itemView);
            contentTextView = (TextView) itemView.findViewById(R.id.chatText);
            profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
            dateTextView = (TextView) itemView.findViewById(R.id.time);
            checkTextView = (TextView) itemView.findViewById(R.id.check);
            messageImageView = (ImageView) itemView.findViewById(R.id.chatImage);
        }
    }

    public static class ViewHolder_mine_image extends RecyclerView.ViewHolder {
        TextView contentTextView;
        ImageView profileImageView;
        TextView dateTextView;
        TextView checkTextView;
        ImageView messageImageView;

        public ViewHolder_mine_image(View itemView) {
            super(itemView);
            contentTextView = (TextView) itemView.findViewById(R.id.chatText);
            profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
            dateTextView = (TextView) itemView.findViewById(R.id.time);
            checkTextView = (TextView) itemView.findViewById(R.id.check);
            messageImageView = (ImageView) itemView.findViewById(R.id.chatImage);
        }
    }

    public static class ViewHolder_other_image extends RecyclerView.ViewHolder {

        TextView contentTextView;
        ImageView profileImageView;
        TextView dateTextView;
        TextView checkTextView;
        ImageView messageImageView;

        public ViewHolder_other_image(View itemView) {
            super(itemView);
            contentTextView = (TextView) itemView.findViewById(R.id.chatText);
            profileImageView = (ImageView) itemView.findViewById(R.id.profile_image);
            dateTextView = (TextView) itemView.findViewById(R.id.time);
            checkTextView = (TextView) itemView.findViewById(R.id.check);
            messageImageView = (ImageView) itemView.findViewById(R.id.chatImage);
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
        if(parent!=null) {
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    UiUtil.getInstance().showDialog(context, "이미지 삭제", "해당 이미지를 삭제하시겠습니까?", new DialogInterface.OnClickListener() {
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

    public void setDateUtil(TextView dTextView, TextView cTextView, Long date, int check) {
        DateUtil dateUtil = new DateUtil(date);
        String time = dateUtil.getTime();
        dTextView.setText(time);

        if (check == 0) {
            cTextView.setText("");
        }else{
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

