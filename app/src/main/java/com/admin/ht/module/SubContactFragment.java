package com.admin.ht.module;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.admin.ht.R;
import com.admin.ht.adapter.GroupViewAdapter;
import com.admin.ht.base.BaseFragment;
import com.admin.ht.base.Constant;
import com.admin.ht.model.Group;
import com.admin.ht.model.GroupItem;
import com.admin.ht.model.Result;
import com.admin.ht.model.UnsortedGroup;
import com.admin.ht.retro.ApiClient;
import com.admin.ht.utils.LogUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by Spec_Inc on 3/4/2017.
 */

public class SubContactFragment extends BaseFragment {


    @Override
    protected String getTAG() {
        return "Sub Contact";
    }

    @Override
    public boolean setDebug() {
        return true;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mUser == null) {
            mUser = getUser();
        }
        if (isDebug) {
            LogUtils.e(TAG, mUser.toString());
        }

        View v = inflater.inflate(R.layout.layout_contact, null);
        final ListView list = (ListView) v.findViewById(R.id.fri_list);

        ApiClient.service.getGroupList(mUser.getId())
                .subscribeOn(Schedulers.newThread())
                //.observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;

                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "获取群组列表";
                            Gson gson = new Gson();
                            UnsortedGroup[] unsortedGroup = gson.fromJson(result.getModel().toString(), UnsortedGroup[].class);

                            ArrayList<UnsortedGroup> ls = new ArrayList<>();
                            for (UnsortedGroup ug : unsortedGroup) {
                                ls.add(ug);
                            }

                            final List<Group> groups = new ArrayList<>();

                            for (int i = 0; i < ls.size(); i++) {
                                if (ls.get(i) == null) {
                                    continue;
                                }

                                UnsortedGroup ug = ls.get(i);
                                Group group = new Group(i + 1 + "", ug.getGroupName());
                                List<GroupItem> items = new ArrayList<>();

                                GroupItem item = new GroupItem();
                                item.setId(ug.getFid());

                                item.setName("user" + i);
                                item.setNote("note" + i);
                                item.setStatus(1);
                                item.setUrl("http://");
                                items.add(item);

                                for (int j = i + 1; j < ls.size(); j++) {

                                    if (ls.get(j) == null) {
                                        continue;
                                    }

                                    if (group.getGroupName().equals(ls.get(j).getGroupName())) {
                                        item.setId(ls.get(j).getFid());
                                        item.setName("user");
                                        item.setNote("");
                                        item.setStatus(1);
                                        item.setUrl("http://");
                                        items.add(item);
                                        ls.set(j, null);
                                    }
                                }

                                group.setGroupItems(items);
                                groups.add(group);
                            }

                            Activity a = getActivity();
                            a.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    GroupViewAdapter adapter = new GroupViewAdapter(getContext(), getGroupData());
                                    list.setAdapter(adapter);
                                }
                            });

                        } else if (result.getCode() == Constant.FAIL) {
                            str = "更新失败";
                        } else if (result.getCode() == Constant.EXECUTING) {
                            str = "服务器繁忙";
                        } else {
                            str = "未知异常";
                        }

                        if (isDebug) {
                            LogUtils.i(TAG, str);
                        }
                    }

                    @Override
                    public void onNext(Result result) {
                        this.result = result;
                        if (isDebug) {
                            LogUtils.i(TAG, result.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isDebug) {
                            LogUtils.i(TAG, e.toString());
                        }
                        e.printStackTrace();
                    }
                });


        getGroupData();
        return v;
    }

    private List<Group> getGroupData() {

        List<Group> groups = new ArrayList<>();
        List<GroupItem> items1 = new ArrayList<>();

        String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=" +
                "1488723551780&di=9ac9726620e9f72d6e473ab97e847be0&imgtype=0" +
                "&src=http%3A%2F%2Fimg.qqai.net%2Fuploads%2Fi_1_3783854548x2374077175_21.jpg";

        for (int i = 0; i < 5; i++) {
            GroupItem item = new GroupItem("100" + i, "friend" + i, url, 1, "note" + i);
            items1.add(item);
        }
        Group group1 = new Group("好友", items1);
        groups.add(group1);

        List<GroupItem> items2 = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            GroupItem item = new GroupItem("100" + i, "family" + i, url, 1, "note" + i);
            items2.add(item);
        }
        Group group2 = new Group("家人", items2);
        groups.add(group2);


        List<GroupItem> items3 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            GroupItem item = new GroupItem("100" + i, "workmate" + i, url, 1, "note" + i);
            items3.add(item);
        }
        Group group3 = new Group("同事", items3);
        groups.add(group3);

        return groups;

    }
}


