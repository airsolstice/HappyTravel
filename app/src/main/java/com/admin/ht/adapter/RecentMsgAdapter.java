package com.admin.ht.adapter;

import android.content.Context;

import com.admin.ht.R;
import com.admin.ht.base.BaseAdapter;
import com.admin.ht.base.ViewHolder;
import com.admin.ht.model.RecentMsg;

import java.util.List;

/**
 * Created by Spec_Inc on 3/12/2017.
 */

public class RecentMsgAdapter extends BaseAdapter<RecentMsg> {

    public RecentMsgAdapter(Context context, List data) {
        super(context, data);
    }

    @Override
    public void convert(ViewHolder holder, RecentMsg item) {
        //设置默认头像
        holder.setImageURL(R.id.item_img, item.getUrl());
        //设置默认用户信息
        holder.setText(R.id.item_name, item.getName());
        holder.setText(R.id.item_detail, item.getNote());
        holder.setText(R.id.item_badge, item.getCount()+ "");
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_msg;
    }



}
