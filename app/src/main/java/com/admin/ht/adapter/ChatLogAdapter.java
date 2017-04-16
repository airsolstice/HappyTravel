package com.admin.ht.adapter;

import android.content.Context;

import com.admin.ht.R;
import com.admin.ht.base.BaseAdapter;
import com.admin.ht.base.ViewHolder;
import com.admin.ht.model.ChatLog;

import java.util.List;

/**
 * Created by Spec_Inc on 3/12/2017.
 */

public class ChatLogAdapter extends BaseAdapter<ChatLog> {

    public ChatLogAdapter(Context context, List datas) {
        super(context, datas);
    }

    @Override
    public void convert(ViewHolder holder, ChatLog chatLog) {

        holder.setText(R.id.content,chatLog.getContent());

    }

    @Override
    public int getLayoutId() {
        return R.layout.item_chat;
    }
}
