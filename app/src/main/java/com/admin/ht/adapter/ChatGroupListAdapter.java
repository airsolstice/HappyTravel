package com.admin.ht.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.admin.ht.R;
import com.admin.ht.base.BaseAdapter;
import com.admin.ht.base.Constant;
import com.admin.ht.base.ViewHolder;
import com.admin.ht.model.ChatMember;
import com.admin.ht.model.Result;
import com.admin.ht.retro.ApiClient;
import com.admin.ht.utils.LogUtils;
import com.admin.ht.utils.ToastUtils;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Spec_Inc on 4/30/2017.
 */

public class ChatGroupListAdapter extends BaseAdapter<ChatMember> {

    private String memberId = null;

    public ChatGroupListAdapter(Context context, List data, String memberId) {
        super(context, data);
        this.memberId = memberId;
    }

    @Override
    public void convert(ViewHolder holder, final ChatMember member) {
        holder.setText(R.id.chat_group_name, member.getGroupName()
                + "(" + member.getGroupId() + ")");

        ImageView add = holder.getView(R.id.item_add);

        if(memberId == null){
            add.setVisibility(View.GONE);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add2Group(member.getGroupId(), memberId, member.getGroupName(), 0);
            }
        });

    }

    public void add2Group(int groupId, String memberId, String groupName, int role) {
        ApiClient.service.invite(groupId, memberId, groupName, role)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;
                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "成功添加群";
                        } else if (result.getCode() == Constant.FAIL) {
                            str = result.getModel().toString();
                        } else if (result.getCode() == Constant.EXECUTING) {
                            str = "服务器繁忙";
                        } else {
                            str = "未知异常";
                        }
                        ToastUtils.showShort(mContext, str);
                    }

                    @Override
                    public void onNext(Result result) {
                        this.result = result;
                        LogUtils.e("ResultList", result.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });

    }


    @Override
    public int getLayoutId() {
        return R.layout.item_chat_group;
    }
}
