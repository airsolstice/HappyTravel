package com.admin.ht.adapter;

import android.content.Context;
import android.widget.TextView;

import com.admin.ht.R;
import com.admin.ht.base.BaseAdapter;
import com.admin.ht.base.ViewHolder;
import com.admin.ht.model.RecentMsg;
import com.jauker.widget.BadgeView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 最近消息列表适配器
 *
 * Created by Solstice on 3/12/2017.
 */
public class RecentMsgAdapter extends BaseAdapter<RecentMsg> {

    public RecentMsgAdapter(Context context, List data) {
        super(context, data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_msg;
    }

    @Override
    public void convert(ViewHolder holder, RecentMsg item) {
        //设置默认头像
        holder.setImageURL(R.id.item_img, item.getUrl());
        //设置默认用户信息
        holder.setText(R.id.item_name, item.getName());
        SimpleDateFormat format = new SimpleDateFormat("MM-dd hh:mm");
        holder.setText(R.id.item_detail, format.format(new Date(System.currentTimeMillis())));
        holder.setText(R.id.item_badge, "");
        TextView tv = holder.getView(R.id.item_badge);

        BadgeView badgeView = new BadgeView(mContext);
        badgeView.setTargetView(tv);
        badgeView.setBadgeCount(item.getCount());
    }
}
