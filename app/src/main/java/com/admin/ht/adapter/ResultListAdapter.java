package com.admin.ht.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import com.admin.ht.R;
import com.admin.ht.base.BaseAdapter;
import com.admin.ht.base.Constant;
import com.admin.ht.base.ViewHolder;
import com.admin.ht.model.Result;
import com.admin.ht.model.User;
import com.admin.ht.retro.ApiClient;
import com.admin.ht.utils.LogUtils;
import com.admin.ht.utils.ToastUtils;
import java.util.List;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Spec_Inc on 4/29/2017.
 */

public class ResultListAdapter extends BaseAdapter<User> {

    private String[] mGroups = null;
    private String id;

    public ResultListAdapter(Context context, List data, String id, String[] mGroups) {
        super(context, data);
        this.mGroups = mGroups;
        this.id = id;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_result;
    }

    @Override
    public void convert(ViewHolder holder, final User user) {
        holder.setText(R.id.item_name, user.getName())
                .setText(R.id.item_detail, user.getNote())
                .setImageURL(R.id.item_img, user.getUrl());
        ImageView add = holder.getView(R.id.item_add);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new  AlertDialog.Builder(mContext)
                        .setTitle("请选择分组" )
                        .setIcon(R.mipmap.ic_list_48)
                        .setSingleChoiceItems(mGroups, 0, new  DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,  int  which) {
                                        add2Group(id, user.getId(), mGroups[which]);
                                        dialog.dismiss();
                                    }
                                }
                        )
                        .setNegativeButton("取消" ,  null )
                        .show();
            }
        });
    }

    public void add2Group(String id, String fid, String groupName) {
        ApiClient.service.add(id, fid, groupName)
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
                            str = "成功添加好友";
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

}
