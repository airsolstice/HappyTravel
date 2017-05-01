package com.admin.ht.module;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.admin.ht.R;
import com.admin.ht.adapter.ExpandAdapter;
import com.admin.ht.base.BaseActivity;
import com.admin.ht.base.BaseFragment;
import com.admin.ht.base.Constant;
import com.admin.ht.db.ChatLogHelper;
import com.admin.ht.db.RecentMsgHelper;
import com.admin.ht.model.ChatLog;
import com.admin.ht.model.Item;
import com.admin.ht.model.RecentMsg;
import com.admin.ht.model.Result;
import com.admin.ht.model.UnsortedGroup;
import com.admin.ht.model.User;
import com.admin.ht.retro.ApiClient;
import com.admin.ht.utils.LogUtils;
import com.google.gson.reflect.TypeToken;

import net.openmob.mobileimsdk.android.ClientCoreSDK;
import net.openmob.mobileimsdk.android.event.ChatTransDataEvent;
import net.openmob.mobileimsdk.android.event.MessageQoSEvent;
import net.openmob.mobileimsdk.server.protocal.Protocal;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * 个人分组碎片类
 * <p>
 * Created by Solstice on 3/12/2017.
 */
public class SubContactFragment extends BaseFragment implements
        ExpandableListView.OnChildClickListener, ChatTransDataEvent, MessageQoSEvent {

    private ExpandableListView mListView = null;
    private ExpandAdapter mAdapter = null;
    private List<List<Item>> mData = new ArrayList<>();
    private List<String> mGroupData = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册IM回调
        //ClientCoreSDK.getInstance().setChatTransDataEvent(this);
        //ClientCoreSDK.getInstance().setMessageQoSEvent(this);
        //配置当前用户信息
        if (mUser == null) {
            mUser = getUser();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.layout_contact, null);
        mListView = (ExpandableListView) v.findViewById(R.id.fri_list);
        //mListView.setGroupIndicator(getResources().getDrawable(R.drawable.expander_floder));
        mListView.setDescendantFocusability(ExpandableListView.FOCUS_AFTER_DESCENDANTS);
        mListView.setOnChildClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        listGroupSvc(mUser.getId());
    }

    @Override
    protected String getTAG() {
        return "Sub Contact";
    }

    @Override
    public boolean setDebug() {
        return true;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Item entity = mAdapter.getChild(groupPosition, childPosition);
        Intent intent = new Intent(getActivity(), SingleChatActivity.class);
        intent.putExtra(BaseActivity.TARGET_USER, entity);
        getActivity().startActivity(intent);

        return true;
    }

    private void listGroupSvc(String id) {
        ApiClient.service.getGroupList(id)
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
                            mGroupData.clear();
                            Type type = new TypeToken<ArrayList<UnsortedGroup>>() {}.getType();
                            List<UnsortedGroup> list = ApiClient.gson.fromJson(result.getModel().toString(), type);

                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i) == null) {
                                    continue;
                                }
                                UnsortedGroup group = list.get(i);
                                mGroupData.add(group.getGroupName());
                                List<Item> items = new ArrayList<>();

                                Item item = new Item();
                                item.setId(group.getFid());
                                items.add(item);

                                for (int j = i + 1; j < list.size(); j++) {

                                    if (list.get(j) == null) {
                                        continue;
                                    }

                                    if (group.getGroupName().equals(list.get(j).getGroupName())) {
                                        item = new Item();
                                        item.setId(list.get(j).getFid());
                                        items.add(item);
                                        list.set(j, null);
                                    }
                                }
                                mData.add(items);
                            }

                            Activity a = getActivity();
                            if (a == null) {
                                return;
                            }
                            a.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter = new ExpandAdapter(getActivity(), mGroupData, mData);
                                    mListView.setAdapter(mAdapter);
                                    SharedPreferences.Editor editor = mPreferences.edit();
                                    LogUtils.e(TAG, mGroupData.size() + "");
                                    editor.putString(Constant.GROUP_NAME_LIST, mGroupData.toString());
                                    editor.commit();
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

    public void saveMsg(User user) {
        boolean flag = true;
        List<RecentMsg> list = RecentMsgHelper.queryById(mUser.getId());
        for (RecentMsg entity : list) {
            if (entity.getId().equals(user.getId())) {
                entity.setCount(entity.getCount() + 1);
                RecentMsgHelper.update(entity);
                flag = false;
                break;
            }
        }
        if (flag) {
            RecentMsg entity = new RecentMsg();
            entity.setOwner(mUser.getId());
            entity.setId(user.getId());
            entity.setCount(1);
            entity.setUrl(user.getUrl());
            entity.setName(user.getName() + "(" + user.getId() + ")");
            entity.setNote(user.getNote());
            RecentMsgHelper.insert(entity);
        }
    }

    private void getUserInfoSvc(final int chatId, final String content) {
        ApiClient.service.getUserInfoByChatId(chatId)
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
                            str = "获取用户信息";
                            User user = ApiClient.gson.fromJson(result.getModel().toString(), User.class);
                            saveMsg(user);
                            ChatLog entity = new ChatLog();
                            entity.setLogno(user.getChatId() + "-" + mUser.getChatId());
                            entity.setName(user.getName() + "(" + user.getId() + ")");
                            entity.setContent(content);
                            entity.setType(2);
                            entity.setDate(new Date(System.currentTimeMillis()));
                            entity.setUrl(user.getUrl());
                            ChatLogHelper.insert(entity);
                            List<ChatLog> list = ChatLogHelper.queryAll();
                            LogUtils.d(TAG, "数据记录长度增到[" + list.size() + "]");
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "获取失败";
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
    public void onTransBuffer(String fingerPrintOfProtocol, int userId, String content) {
        Log.d(TAG, "收到来自用户[" + userId + "]的消息:" + content + "," + fingerPrintOfProtocol);
        getUserInfoSvc(userId, content);
    }

    @Override
    public void onErrorResponse(int errorCode, String errorMsg) {
        Log.d(TAG, "收到服务端错误消息，errorCode=" + errorCode + ", errorMsg=" + errorMsg);
    }

    @Override
    public void messagesLost(ArrayList<Protocal> lostMessages) {
        Log.d(TAG, "收到系统的未实时送达事件通知，当前共有" + lostMessages.size() + "个包QoS保证机制结束，判定为【无法实时送达】！");
    }

    @Override
    public void messagesBeReceived(String theFingerPrint) {
        if (theFingerPrint != null) {
            Log.d(TAG, "收到对方已收到消息事件的通知，fp=" + theFingerPrint);
        }
    }
}


