package com.admin.ht.module;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.admin.ht.R;
import com.admin.ht.adapter.RecentMsgAdapter;
import com.admin.ht.base.BaseFragment;
import com.admin.ht.base.Constant;
import com.admin.ht.db.ChatLogHelper;
import com.admin.ht.db.RecentMsgHelper;
import com.admin.ht.model.ChatLog;
import com.admin.ht.model.Item;
import com.admin.ht.model.RecentMsg;
import com.admin.ht.model.Result;
import com.admin.ht.model.User;
import com.admin.ht.retro.ApiClient;
import com.admin.ht.retro.ApiClientImpl;
import com.admin.ht.retro.RetrofitCallbackListener;
import com.admin.ht.utils.LogUtils;
import com.admin.ht.utils.TipUtils;

import net.openmob.mobileimsdk.android.ClientCoreSDK;
import net.openmob.mobileimsdk.android.event.ChatTransDataEvent;
import net.openmob.mobileimsdk.android.event.MessageQoSEvent;
import net.openmob.mobileimsdk.server.protocal.Protocal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 消息碎片类
 * <p>
 * Created by Solstice on 3/12/2017.
 */
public class MessageFragment extends BaseFragment
        implements AdapterView.OnItemClickListener, ChatTransDataEvent, MessageQoSEvent {

    private ListView mListView = null;
    private List<RecentMsg> mData = null;
    private RecentMsgAdapter mAdapter = null;
    private Handler mHandler = null;

    public MessageFragment(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //配置当前用户信息
        if (mUser == null) {
            mUser = getUser();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.layout_message, null);
        mListView = (ListView) v.findViewById(R.id.list_view);
        mData = new ArrayList<>();
        List<RecentMsg> list = RecentMsgHelper.queryById(mUser.getId());
        mData.addAll(list);
        mAdapter = new RecentMsgAdapter(getContext(), mData);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        //注册IM回调
        ClientCoreSDK.getInstance().setChatTransDataEvent(this);
        ClientCoreSDK.getInstance().setMessageQoSEvent(this);
        mData.clear();
        List<RecentMsg> list = RecentMsgHelper.queryById(mUser.getId());
        mData.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected String getTAG() {
        return "Message Fragment";
    }

    @Override
    public boolean setDebug() {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RecentMsg msg = mData.get(position);
        msg.setCount(0);
        RecentMsgHelper.update(msg);
        mData.get(position).setCount(1);
        mAdapter.notifyDataSetChanged();

        Item item = new Item();
        item.setId(msg.getId());
        item.setUrl(msg.getUrl());
        item.setStatus(1);
        item.setName(msg.getName());
        item.setNote(msg.getNote());
        Intent intent = new Intent(getContext(), SingleChatActivity.class);
        intent.putExtra(Constant.TARGET_USER, item);
        getContext().startActivity(intent);
    }

    public void saveMsg(User user) {
        boolean flag = true;
        for (RecentMsg entity : mData) {
            if (entity.getId().equals(user.getId())) {
                entity.setCount(entity.getCount() + 1);
                entity.setTime(new Date(System.currentTimeMillis()));
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
            entity.setTime(new Date(System.currentTimeMillis()));
            mData.add(entity);
            RecentMsgHelper.insert(entity);
        }

        Activity a = (Activity) getContext();

        a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onTransBuffer(String fingerPrintOfProtocol, final int userId, final String content) {
        Log.d(TAG, "收到来自用户[" + userId + "]的消息:" + content + "," + fingerPrintOfProtocol);
        ApiClientImpl.getUserInfoSvc(new RetrofitCallbackListener() {
            @Override
            public void receive(Result result) {
                User user = ApiClient.gson.fromJson(result.getModel().toString(), User.class);
                saveMsg(user);
                ChatLog log = new ChatLog();
                log.setLogno(user.getChatId() + "-" + mUser.getChatId());
                log.setName(user.getName() + "(" + user.getId() + ")");
                log.setContent(content);
                log.setType(2);
                log.setDate(new Date(System.currentTimeMillis()));
                log.setUrl(user.getUrl());
                ChatLogHelper.insert(log);
                List<ChatLog> list = ChatLogHelper.queryAll();
                LogUtils.d(TAG, "数据记录长度增到[" + list.size() + "]");
            }
        }, userId);

        TipUtils.tipMsg(getContext());
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
