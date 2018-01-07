package com.example.kwoncheolhyeok.core.MessageActivity;

import android.media.Image;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kwoncheolhyeok.core.MessageActivity.chat_message_view.util.DateUtil;
import com.example.kwoncheolhyeok.core.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by himanshusoni on 06/09/15.
 */
public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {
    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1, MY_IMAGE = 2, OTHER_IMAGE = 3;

    public ChatMessageAdapter(ChattingActivity context, List<ChatMessage> data) {
        super(context, R.layout.message_item_mine_message, data);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (viewType == MY_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item_mine_message, parent, false);

            TextView textView = (TextView) convertView.findViewById(R.id.text);
            textView.setText(getItem(position).getContent());

            DateUtil dateUtil = new DateUtil(getItem(position).getTime());
            String time = dateUtil.getTime();
            textView = (TextView) convertView.findViewById(R.id.time);
            textView.setText(time);

            if(getItem(position).getCheck()!=0){
                textView = (TextView) convertView.findViewById(R.id.check);
                textView.setText(Integer.toString(getItem(position).getCheck()));
            }

        } else if (viewType == OTHER_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item_other_message, parent, false);

            TextView textView = (TextView) convertView.findViewById(R.id.text);
            textView.setText(getItem(position).getContent());
            ImageView imageView = (ImageView) convertView.findViewById(R.id.profile_image);


            DateUtil dateUtil = new DateUtil(getItem(position).getTime());
            String time = dateUtil.getTime();
            textView = (TextView) convertView.findViewById(R.id.time);
            textView.setText(time);

            if(getItem(position).getCheck()!=0){
                textView = (TextView) convertView.findViewById(R.id.check);
                textView.setText(Integer.toString(getItem(position).getCheck()));
            }

            Glide.with(imageView.getContext()).load(getItem(position).getProfileImeage()).into(imageView);

        } else if (viewType == MY_IMAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item_mine_image, parent, false);

        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item_other_image, parent, false);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.profile_image);
            Glide.with(imageView.getContext()).load(getItem(position).getProfileImeage()).into(imageView);
        }

        convertView.findViewById(R.id.chatMessageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "onClick", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
