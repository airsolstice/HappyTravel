package com.admin.ht.module;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.ht.R;
import com.admin.ht.adapter.ChatLogAdapter;
import com.admin.ht.base.BaseActivity;
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
import com.admin.ht.utils.ToastUtils;

import net.openmob.mobileimsdk.android.ClientCoreSDK;
import net.openmob.mobileimsdk.android.core.LocalUDPDataSender;
import net.openmob.mobileimsdk.android.event.ChatTransDataEvent;
import net.openmob.mobileimsdk.android.event.MessageQoSEvent;
import net.openmob.mobileimsdk.server.protocal.Protocal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 单聊Activity
 *
 * Created by Solstice on 3/12/2017.
 */
public class SingleChatActivity extends BaseActivity implements ChatTransDataEvent, MessageQoSEvent {

    private List<ChatLog> mData = null;
    private ChatLogAdapter mAdapter = null;
    private User mTagUser;
    private User mUser;

    @Bind(R.id.send)
    ImageView mSend;
    @Bind(R.id.lab_title)
    TextView mTitle;
    @Bind(R.id.log_list)
    ListView mLog;
    @Bind(R.id.edit_text)
    EditText mEdit;
    @Bind(R.id.menu)
    ImageView mMenu;

    @Override
    protected String getTAG() {
        return "Single Chat";
    }

    @Override
    public boolean setTranslucent() {
        return true;
    }

    @Override
    public boolean setDebug() {
        return true;
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_single_chat;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //注册IM回调
        ClientCoreSDK.getInstance().setChatTransDataEvent(this);
        ClientCoreSDK.getInstance().setMessageQoSEvent(this);
        //配置当前用户信息
        if (mUser == null) {
            mUser = getUser();
        }

        Item item = (Item) getIntent().getExtras().getSerializable(Constant.TARGET_USER);
        mTagUser = new User();
        mTagUser.setId(item.getId());
        mTagUser.setName(item.getName());
        mTagUser.setNote(item.getNote());
        mTagUser.setUrl(item.getUrl());
        mTagUser.setStatus(item.getStatus());
        mTagUser.setChatId(-1);
        getTagUserChatIdSvc(mTagUser.getId());

        mData = new ArrayList<>();
        mAdapter = new ChatLogAdapter(mContext, mData);
        mLog.setAdapter(mAdapter);

    }

    @OnClick(R.id.menu)
    public void menu() {
        PopupMenu popup = new PopupMenu(mContext, mMenu);
        popup.getMenuInflater().inflate(R.menu.menu_single_chat, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.clean_log:
                        if(mTagUser.getChatId() == -1){
                            break;
                        }
                        ChatLogHelper.delete(mTagUser.getChatId()+"-"+mUser.getChatId());
                        mData.clear();
                        mAdapter.notifyDataSetChanged();
                        break;

                    case R.id.delete_fri:
                         new AlertDialog.Builder(mContext)
                                 .setMessage("确认解除关系？")
                                .setCancelable(false)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ApiClientImpl.removeShipSvc(new RetrofitCallbackListener() {
                                            @Override
                                            public void receive(Result result) {
                                                ToastUtils.showShort(mContext, "解除成功，返回上一页");
                                            }
                                        }, mUser.getId(), mTagUser.getId());

                                        SingleChatActivity.this.finish();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                }).create().show();
                        break;
                }
                return true;
            }
        });
        popup.show();

    }


    @OnClick(R.id.back)
    public void back() {
        finish();
    }

    @OnClick(R.id.send)
    public void sendMsg() {

        //在连不上IM服务器时的异常处理
        if(!ClientCoreSDK.getInstance().isLocalDeviceNetworkOk()){
            ToastUtils.showShort(mContext, "网络异常，请重新启动应用");
            return;
        }

        if(!ClientCoreSDK.getInstance().isLoginHasInit()){
            ToastUtils.showShort(mContext, "登录异常，请重新启动应用");
            return;
        }

        if(!ClientCoreSDK.getInstance().isConnectedToServer()){
            ToastUtils.showShort(mContext, "IM服务器异常，请重新启动应用");
            return;
        }

        if (mTagUser.getChatId() == -1) {
            Toast.makeText(mContext, "目标用户的chat_id还未获取，轻稍后重新发送", Toast.LENGTH_SHORT).show();
            return;
        }

        final String msg = mEdit.getText().toString().trim();
        if (msg.length() > 0) {
            new LocalUDPDataSender.SendCommonDataAsync(mContext, msg, mTagUser.getChatId(), true) {
                @Override
                protected void onPostExecute(Integer code) {
                    if (code == 0) {
                        Log.d(TAG, "数据已成功发出！");
                        ChatLog log = new ChatLog();
                        log.setLogno(mTagUser.getChatId() +"-"+ mUser.getChatId());
                        log.setName("我" + "(" + mUser.getChatId() + "):");
                        log.setContent(msg);
                        log.setUrl(mUser.getUrl());
                        log.setType(1);
                        saveLog(log, null);
                    } else
                        Toast.makeText(mContext, "数据发送失败。错误码是：" + code + "！", Toast.LENGTH_SHORT).show();
                }
            }.execute();
        } else
            Log.e(TAG, "len=" + (msg.length()));
        mEdit.setText("");
    }

    public void saveLog(ChatLog log, User user) {
        ChatLogHelper.insert(log);
        List<ChatLog> result = ChatLogHelper.queryAll();
        LogUtils.d(TAG, "数据记录长度增到[" + result.size() + "]");

        if(log.getLogno().equals(mTagUser.getChatId() +"-"+ mUser.getChatId())){
            mData.add(log);
            mAdapter.notifyDataSetChanged();
            //移动到尾部
            mLog.smoothScrollToPosition(mLog.getCount() - 1);
        } else {
            boolean flag = true;
            List<RecentMsg> list = RecentMsgHelper.queryById(mUser.getId());
            for (RecentMsg entity : list) {
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
                RecentMsgHelper.insert(entity);
            }

        }

    }

    private void  getTagUserChatIdSvc(String id) {
        ApiClientImpl.getUserInfoSvc(new RetrofitCallbackListener() {
            @Override
            public void receive(Result result) {
                mTagUser = ApiClient.gson.fromJson(result.getModel().toString(), User.class);
                List<ChatLog> data =
                        ChatLogHelper.queryByLogno(mTagUser.getChatId()+"-"+mUser.getChatId());
                if(data.size() > 20){
                    mData.addAll(data.subList(data.size()-21, data.size() -1));
                }else {
                    mData.addAll(data);
                }

                Activity a = (Activity) mContext;
                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTitle.setText(mTagUser.getName());
                        mAdapter.notifyDataSetChanged();
                        //移动到尾部
                        mLog.smoothScrollToPosition(mLog.getCount() - 1);
                    }
                });

            }
        }, id);

    }

    public void getTagUserInfo(int chatId, final String content){
        ApiClientImpl.getUserInfoSvc(new RetrofitCallbackListener() {
            @Override
            public void receive(Result result) {
                User user = ApiClient.gson.fromJson(result.getModel().toString(), User.class);
                ChatLog log = new ChatLog();
                log.setLogno(user.getChatId() +"-"+ mUser.getChatId());
                log.setName(user.getName() + "(" + user.getId() + ")");
                log.setContent(content);
                log.setType(2);
                log.setDate(new Date(System.currentTimeMillis()));
                log.setUrl(user.getUrl());
                saveLog(log, user);
            }
        }, chatId);
    }


    @Override
    public void onTransBuffer(String fingerPrintOfProtocol, int userId, String content) {
        Log.d(TAG, "收到来自用户[" + userId + "]的消息:" + content + "," + fingerPrintOfProtocol);
        TipUtils.tipMsg(mContext);
        ChatLog log = new ChatLog();
        //设置信息为填充时的obj
        log.setLogno(userId +"-"+ mUser.getChatId());
        log.setContent(content);
        log.setUrl(Constant.USER_DEFAULT_HEAD_URL);
        log.setName(userId + "");
        log.setType(2);

        //判断是否为本好友发送的消息
        if(userId == mTagUser.getChatId()){
            log.setLogno(mTagUser.getChatId() +"-"+ mUser.getChatId());
            log.setName(mTagUser.getName() + "(" + mTagUser.getId() + ")");
            log.setContent(content);
            log.setType(2);
            log.setDate(new Date(System.currentTimeMillis()));
            log.setUrl(mTagUser.getUrl());
            saveLog(log, null);
        } else{
            getTagUserInfo(userId, content);
        }

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
