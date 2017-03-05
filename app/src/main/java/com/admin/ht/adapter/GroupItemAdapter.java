package com.admin.ht.adapter;

import android.content.Context;
import android.widget.TextView;

import com.admin.ht.R;
import com.admin.ht.base.BaseAdapter;
import com.admin.ht.base.ViewHolder;
import com.admin.ht.model.GroupItem;
import com.admin.ht.model.ItemFriEntity;

import java.util.List;

/**
 * Created by Spec_Inc on 3/5/2017.
 */

public class GroupItemAdapter extends BaseAdapter <GroupItem>{

    public GroupItemAdapter(Context context, List datas) {
        super(context, datas);
    }

    @Override
    public void convert(ViewHolder holder, GroupItem item) {
        /*绑定好友结构中的name*/
        holder.setText(R.id.name,item.getName()+"("+item.getId()+")");
        /*绑定好友结构中的note*/
        holder.setText(R.id.note,item.getNote());

    }

    @Override
    public int getLayoutId() {
        return R.layout.item_fri;
    }
}
