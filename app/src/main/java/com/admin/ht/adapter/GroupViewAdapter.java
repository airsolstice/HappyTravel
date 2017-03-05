package com.admin.ht.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.admin.ht.R;
import com.admin.ht.base.BaseAdapter;
import com.admin.ht.base.ViewHolder;
import com.admin.ht.model.Group;
import com.admin.ht.module.SingleChatActivity;

import java.util.List;

/**
 * Created by Spec_Inc on 3/5/2017.
 */

public class GroupViewAdapter extends BaseAdapter<Group> {

    private boolean isShowGroupItem = false;

    public GroupViewAdapter(Context context, List<Group> datas) {
        super(context, datas);
    }

    @Override
    public void convert(ViewHolder holder, Group item) {
        //设置分组的名称
        holder.setText(R.id.group_name, item.getGroupName());
        //设置分组容量
        String count = String.valueOf(item.getGroupItems().size());
        holder.setText(R.id.item_count, count);
        //设置分组下的列表
        final ListView subList = holder.getView(R.id.sub_item);
        GroupItemAdapter adapter = new GroupItemAdapter(mContext, item.getGroupItems());
        subList.setAdapter(adapter);
        subList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int index, long id) {

                Intent intent = new Intent(mContext, SingleChatActivity.class);
                mContext.startActivity(intent);


            }
        });
        //设置分组小的列表高度
        setGroupHeight(subList);
        //给分组添加Click事件
        final RelativeLayout layout = holder.getView(R.id.group_item);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShowGroupItem) {
                    subList.setVisibility(View.VISIBLE);
                    // GroupLayout.setBackgroundResource(R.mipmap.ic_empty);
                    isShowGroupItem = true;
                } else {
                    subList.setVisibility(View.GONE);
                    //GroupLayout.setBackgroundResource(R.mipmap.ic_empty);
                    isShowGroupItem = false;
                }
            }
        });
    }

    /*
     * 在所有的View嵌套问题中都需要解决这个问题
     */
    private void setGroupHeight(ListView mListView) {
        int mTotalHeight = 0;
        ListAdapter mAdapter = mListView.getAdapter();
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View ItemView = mAdapter.getView(i, null, mListView);
            ItemView.measure(0, 0);
            mTotalHeight += ItemView.getMeasuredHeight();
        }
        ViewGroup.LayoutParams mParams = mListView.getLayoutParams();
        mParams.height = mTotalHeight;
        mListView.setMinimumHeight(mTotalHeight);
    }


    @Override
    public int getLayoutId() {
        return R.layout.item_group;
    }
}
