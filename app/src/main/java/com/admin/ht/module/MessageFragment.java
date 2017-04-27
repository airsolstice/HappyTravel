package com.admin.ht.module;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.admin.ht.R;
import com.admin.ht.adapter.RecentMsgAdapter;
import com.admin.ht.base.BaseFragment;
import com.admin.ht.base.Constant;
import com.admin.ht.db.ChatLogHelper;
import com.admin.ht.model.ChatLog;
import com.admin.ht.model.RecentMsg;
import com.admin.ht.model.Result;
import com.admin.ht.model.User;
import com.admin.ht.retro.ApiClient;
import com.admin.ht.utils.LogUtils;
import net.openmob.mobileimsdk.android.ClientCoreSDK;
import net.openmob.mobileimsdk.android.event.ChatTransDataEvent;
import net.openmob.mobileimsdk.android.event.MessageQoSEvent;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by Spec_Inc on 3/4/2017.
 */

public class MessageFragment extends BaseFragment implements  ChatTransDataEvent, MessageQoSEvent {

    private ListView mRecentList = null;

    private List<RecentMsg> mData = new ArrayList<>();

    private RecentMsgAdapter mAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ClientCoreSDK.getInstance().setChatTransDataEvent(this);
        ClientCoreSDK.getInstance().setMessageQoSEvent(this);

        View v = inflater.inflate(R.layout.layout_message, null);
        mRecentList = (ListView) v.findViewById(R.id.list_view);
        mAdapter = new RecentMsgAdapter(getActivity(), mData);
        mRecentList.setAdapter(mAdapter);

        return v;
    }

    public void addMsg(User user){
        boolean flag = true;
        for(RecentMsg item : mData){
           if(item.getId().equals(user.getId())){
               item.setCount(item.getCount() + 1);
               flag = false;
               break;
           }
        }

        if(flag){
            RecentMsg msg = new RecentMsg();
            msg.setId(user.getId());
            msg.setCount(1);
            msg.setUrl(user.getUrl());
            msg.setName(user.getName()+"("+user.getId()+")");
            msg.setNote(user.getNote());
            mData.add(msg);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
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
                            ChatLog entity = new ChatLog();
                            entity.setNo(chatId);
                            entity.setName(user.getName()+"("+user.getId()+")");
                            entity.setContent(content);
                            entity.setType(2);
                            entity.setDate(new Date(System.currentTimeMillis()));
                            entity.setUrl(user.getUrl());
                            addMsg(user);
                            ChatLogHelper.insert(entity);
                            List<ChatLog> result = ChatLogHelper.queryAll();
                            LogUtils.d(TAG, "数据记录长度增到[" + result.size() + "]");
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

    @Override
    protected String getTAG() {
        return "Message Fragment";
    }

    @Override
    public boolean setDebug() {
        return false;
    }

}
