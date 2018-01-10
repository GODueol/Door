package com.example.kwoncheolhyeok.core.MessageActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.ChatFirebaseUtil;
import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.DateUtil;
import com.example.kwoncheolhyeok.core.PeopleFragment.FullImageActivity;
import com.example.kwoncheolhyeok.core.R;
import com.example.kwoncheolhyeok.core.Util.GlideApp;

import java.util.List;

/**
 * Created by himanshusoni on 06/09/15.
 */
public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {
    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1, MY_IMAGE = 2, OTHER_IMAGE = 3;
    private OnCallbackList onCallbackList;
    RequestListener requestListener;

    public interface OnCallbackList {
        public void onEvent();
    }

    public ChatMessageAdapter(ChattingActivity context, List<ChatMessage> data, OnCallbackList listener) {
        super(context, R.layout.message_item_mine_message, data);
        onCallbackList = listener;
        setRequestListener();
    }

    @Override
    public int getViewTypeCount() {
        // my message, other message, my image, other image
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = getItem(position);
        if (item.isMine() && !item.isImage()) return MY_MESSAGE;
        else if (!item.isMine() && !item.isImage()) return OTHER_MESSAGE;
        else if (item.isMine() && item.isImage()) return MY_IMAGE;
        else return OTHER_IMAGE;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);

        if (viewType == MY_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item_mine_message, parent, false);

            TextView textView = (TextView) convertView.findViewById(R.id.text);
            TextView dTextView = (TextView) convertView.findViewById(R.id.time);
            TextView cTextView = (TextView) convertView.findViewById(R.id.check);

            String content = getItem(position).getContent();
            Long date = getItem(position).getTime();
            int check = getItem(position).getCheck();

            textView.setText(content);
            setDateUtil(dTextView, cTextView, date, check);

        } else if (viewType == OTHER_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item_other_message, parent, false);

            TextView textView = (TextView) convertView.findViewById(R.id.text);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.profile_image);
            TextView dTextView = (TextView) convertView.findViewById(R.id.time);
            TextView cTextView = (TextView) convertView.findViewById(R.id.check);

            String profile = getItem(position).getProfileImage();
            String content = getItem(position).getContent();
            Long date = getItem(position).getTime();
            int check = getItem(position).getCheck();

            textView.setText(content);
            setDateUtil(dTextView, cTextView, date, check);
            setProfileImage(imageView, profile);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent p = new Intent(getContext(), FullImageActivity.class);
                    p.putExtra("item", getItem(position).getItem());
                    getContext().startActivity(p);
                }
            });

        } else if (viewType == MY_IMAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item_mine_image, parent, false);

            ImageView messageimage = (ImageView) convertView.findViewById(R.id.chatMessageView);
            TextView dTextView = (TextView) convertView.findViewById(R.id.time);
            TextView cTextView = (TextView) convertView.findViewById(R.id.check);

            String image = getItem(position).getImage();
            Long date = getItem(position).getTime();
            int check = getItem(position).getCheck();

            setImageMessage(messageimage, image);
            setDateUtil(dTextView, cTextView, date, check);

        } else if (viewType == OTHER_IMAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item_other_image, parent, false);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.profile_image);
            ImageView messageimage = (ImageView) convertView.findViewById(R.id.chatMessageView);
            TextView cTextView = (TextView) convertView.findViewById(R.id.check);
            TextView dTextView = (TextView) convertView.findViewById(R.id.time);

            String profile = getItem(position).getProfileImage();
            String image = getItem(position).getImage();
            Long date = getItem(position).getTime();
            int check = getItem(position).getCheck();

            setProfileImage(imageView, profile);
            setImageMessage(messageimage, image);
            setDateUtil(dTextView, cTextView, date, check);
        }
        return convertView;
    }

    public void setDateUtil(TextView dTextView, TextView cTextView, Long date, int check) {
        DateUtil dateUtil = new DateUtil(date);
        String time = dateUtil.getTime();
        dTextView.setText(time);

        if (check != 0) {
            cTextView.setText(Integer.toString(check));
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

    public void setImageMessage(ImageView imageView, String uri) {
        GlideApp.with(imageView.getContext())
                .load(uri)
                .override(600, 600)
                .fitCenter()
                .listener(requestListener)
                .into(imageView);
    }

    public void setRequestListener(){
        requestListener = new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                onCallbackList.onEvent();
                return false;
            }
        };
    }

    public void deletRequestListener(){
        requestListener = null;
    }



}
