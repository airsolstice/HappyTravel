package com.admin.ht.adapter;

import android.content.Context;
import com.admin.ht.R;
import com.admin.ht.base.BaseAdapter;
import com.admin.ht.base.ViewHolder;
import com.admin.ht.model.ChatLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 聊天记录列表适配器
 *
 * Created by Solstice on 3/12/2017.
 */

public class ChatLogAdapter extends BaseAdapter<ChatLog> {

    public ChatLogAdapter(Context context, List data) {
        super(context, data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_chat_left;
    }

    @Override
    public void convert(ViewHolder holder, ChatLog chatLog) {
        holder.setText(R.id.content, chatLog.getContent());
        holder.setText(R.id.name, chatLog.getName());
        holder.setImageURL(R.id.img, chatLog.getUrl());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date =  new Date(System.currentTimeMillis());
        holder.setText(R.id.time, format.format(date));
    }
}
