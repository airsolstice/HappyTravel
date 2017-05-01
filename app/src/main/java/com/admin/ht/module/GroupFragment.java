package com.admin.ht.module;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.admin.ht.R;
import com.admin.ht.adapter.ChatGroupListAdapter;
import com.admin.ht.base.BaseFragment;
import com.admin.ht.base.Constant;
import com.admin.ht.db.ChatLogHelper;
import com.admin.ht.db.RecentMsgHelper;
import com.admin.ht.model.ChatLog;
import com.admin.ht.model.ChatMember;
import com.admin.ht.model.RecentMsg;
import com.admin.ht.model.Result;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 群组碎片类
 *
 * Created by Solstice on 3/12/2017.
 */
public class GroupFragment extends BaseFragment
        implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener,
        ChatTransDataEvent, MessageQoSEvent {

    private ListView mListView = null;
    private List<ChatMember> mData = new ArrayList<>();
    private ChatGroupListAdapter mAdapter = null;

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
        View v = inflater.inflate(R.layout.layout_group,null);
        mListView = (ListView) v.findViewById(R.id.list_view);
        mAdapter = new ChatGroupListAdapter(getContext(), mData, null);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        listChatGroupSvc(mUser.getId());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), GroupChatActivity.class);
        intent.putExtra(Constant.CHAT_GROUP_INFO, mData.get(position));
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        new AlertDialog.Builder(getActivity())
                .setMessage("是否退出该群？")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        quitSvc(mData.get(position).getGroupId(), mData.get(position).getMemberId());
                        mData.remove(position);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create().show();
        return false;
    }

    @Override
    protected String getTAG() {
        return "Group Fragment";
    }

    @Override
    public boolean setDebug() {
        return true;
    }


    private void listChatGroupSvc(String id) {
        ApiClient.service.getChatGroupList(id)
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
                            str = "获取聊天群组列表";
                            mData.clear();
                            Type type = new TypeToken<ArrayList<ChatMember>>() {}.getType();
                            List<ChatMember> list = ApiClient.gson.fromJson(result.getModel().toString(), type);
                            mData.addAll(list);

                            Activity a = getActivity();
                            if(a == null){
                                return;
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
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



    private void quitSvc(int groupId, String memberId) {
        ApiClient.service.quitGroup(groupId, memberId)
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
                            str = "退出成功";
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "退出失败";
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
