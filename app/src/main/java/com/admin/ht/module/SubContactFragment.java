package com.admin.ht.module;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.admin.ht.R;
import com.admin.ht.adapter.ExpandAdapter;
import com.admin.ht.base.BaseActivity;
import com.admin.ht.base.BaseFragment;
import com.admin.ht.base.Constant;
import com.admin.ht.model.Item;
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

public class SubContactFragment extends BaseFragment implements ExpandableListView.OnChildClickListener {

    private ExpandableListView mListView = null;
    private ExpandAdapter mAdapter = null;
    private List<List<Item>> mData = new ArrayList<>();
    private List<String> mGroups = new ArrayList<>();

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
        mListView = (ExpandableListView) v.findViewById(R.id.fri_list);
        //设置群组指针
        //mListView.setGroupIndicator(getResources().getDrawable(R.drawable.expander_floder));
        mListView.setDescendantFocusability(ExpandableListView.FOCUS_AFTER_DESCENDANTS);
        mListView.setOnChildClickListener(this);

        getGroupListSvc();
        return v;
    }


    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        final Item item = mAdapter.getChild(groupPosition, childPosition);

        Intent intent = new Intent(getActivity(), SingleChatActivity.class);
        intent.putExtra(BaseActivity.TARGET_USER, item);
        getActivity().startActivity(intent);

        return true;
    }


    private void getGroupListSvc() {

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
                            mData.clear();
                            Gson gson = new Gson();
                            UnsortedGroup[] unsortedGroup = gson.fromJson(result.getModel().toString(), UnsortedGroup[].class);
                            ArrayList<UnsortedGroup> ls = new ArrayList<>();
                            for (UnsortedGroup ug : unsortedGroup) {
                                ls.add(ug);
                            }

                            for (int i = 0; i < ls.size(); i++) {
                                if (ls.get(i) == null) {
                                    continue;
                                }
                                UnsortedGroup ug = ls.get(i);
                                mGroups.add(ug.getGroupName());
                                List<Item> items = new ArrayList<>();

                                Item item = new Item();
                                item.setId(ug.getFid());
                                item.setName("user");
                                item.setNote("......");
                                item.setStatus(0);
                                item.setUrl("http://");
                                items.add(item);

                                for (int j = i + 1; j < ls.size(); j++) {

                                    if (ls.get(j) == null) {
                                        continue;
                                    }

                                    if (ug.getGroupName().equals(ls.get(j).getGroupName())) {
                                        item = new Item();
                                        item.setId(ls.get(j).getFid());
                                        item.setName("user");
                                        item.setNote("......");
                                        item.setStatus(1);
                                        item.setUrl("http://");
                                        items.add(item);
                                        ls.set(j, null);
                                    }
                                }

                                mData.add(items);
                            }

                            Activity a = getActivity();
                            a.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter = new ExpandAdapter(getActivity(), mGroups, mData);
                                    mListView.setAdapter(mAdapter);
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
    }


    @Override
    protected String getTAG() {
        return "Sub Contact";
    }

    @Override
    public boolean setDebug() {
        return true;
    }



}


