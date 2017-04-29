package com.admin.ht.module;

import android.os.Bundle;
import android.util.Log;
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
import com.admin.ht.model.ChatLog;
import com.admin.ht.model.Item;
import com.admin.ht.model.Result;
import com.admin.ht.model.User;
import com.admin.ht.retro.ApiClient;
import com.admin.ht.utils.LogUtils;

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

    private List<ChatLog> mLogData = new ArrayList<>();

    private ChatLogAdapter mAdapter = null;

    private User mTargetUser;

    private User mHolderUser;

    @Bind(R.id.send)
    ImageView mSend;

    @Bind(R.id.lab_title)
    TextView mTitle;

    @Bind(R.id.log_list)
    ListView mLog;

    @Bind(R.id.edit_text)
    EditText mEdit;


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
        Item item = (Item) getIntent().getExtras().getSerializable(TARGET_USER);
        mTargetUser = new User();
        mTargetUser.setId(item.getId());
        mTargetUser.setName(item.getName());
        mTargetUser.setNote(item.getNote());
        mTargetUser.setUrl(item.getUrl());
        mTargetUser.setStatus(item.getStatus());
        mTargetUser.setChatId(-1);
        mHolderUser = getUser();

        if (isDebug) {
            Log.d(TAG, "目标用户=" + mTargetUser.getId());
        }
        mTitle.setText(mTargetUser.getId());
        initIMListener();

        mAdapter = new ChatLogAdapter(mContext, mLogData);
        //mAdapter = new SimpleChatLogAdapter(mContext, mLogData);
        mLog.setAdapter(mAdapter);
        //移动到尾部
        mLog.smoothScrollToPosition(mLog.getCount() - 1);
    }

    @OnClick(R.id.back)
    public void back() {
        finish();
    }

    @OnClick(R.id.send)
    public void sendMsg() {
        if (mTargetUser.getChatId() == -1) {
            Toast.makeText(mContext, "目标用户的chat_id还未获取，轻稍后重新发送", Toast.LENGTH_SHORT).show();
            return;
        }

        final String msg = mEdit.getText().toString().trim();
        if (msg.length() > 0) {
            new LocalUDPDataSender.SendCommonDataAsync(mContext, msg, mTargetUser.getChatId(), true) {
                @Override
                protected void onPostExecute(Integer code) {
                    if (code == 0) {
                        Log.d(TAG, "数据已成功发出！");
                        ChatLog log = new ChatLog();
                        log.setNo(mTargetUser.getChatId());
                        log.setName("我" + "(" + mHolderUser.getChatId() + "):");
                        log.setContent(msg);
                        log.setUrl(mHolderUser.getUrl());
                        log.setType(1);
                        addLog(log);
                    } else
                        Toast.makeText(mContext, "数据发送失败。错误码是：" + code + "！", Toast.LENGTH_SHORT).show();
                }
            }.execute();
        } else
            Log.e(TAG, "len=" + (msg.length()));
        mEdit.setText("");
    }

    public void addLog(ChatLog log) {
        ChatLogHelper.insert(log);
        List<ChatLog> result = ChatLogHelper.queryAll();
        LogUtils.d(TAG, "数据记录长度增到[" + result.size() + "]");

        mLogData.add(log);
        mAdapter.notifyDataSetChanged();
        //移动到尾部
        mLog.smoothScrollToPosition(mLog.getCount() - 1);
    }


    public User getTargetUser() {
        return mTargetUser;
    }

    private void initIMListener() {
        getTargetChatId(mTargetUser.getId());
        ClientCoreSDK.getInstance().setChatTransDataEvent(this);
        ClientCoreSDK.getInstance().setMessageQoSEvent(this);

    }

    private void getTargetChatId(String id) {
        ApiClient.service.getUserInfo(id)
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
                            str = "获取目标用户的信息";
                            mTargetUser = ApiClient.gson.fromJson(result.getModel().toString(), User.class);
                            List<ChatLog> result = ChatLogHelper.queryById(mTargetUser.getChatId());
                            if(result.size() > 20){
                                mLogData.addAll(result.subList(result.size()-21, result.size() -1));
                            }else {
                                mLogData.addAll(result);
                            }
                            mAdapter.notifyDataSetChanged();
                            //移动到尾部
                            mLog.smoothScrollToPosition(mLog.getCount() - 1);
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
        ChatLog log = new ChatLog();
        log.setNo(userId);
        log.setContent(content);
        log.setUrl(BaseActivity.USER_DEFAULT_HEAD_URL);
        log.setName(userId + "");
        log.setType(2);
        User user = this.getTargetUser();
        if (user != null) {
            log.setNo(user.getChatId());
            log.setName(user.getName() + "(" + user.getId() + ")");
            log.setContent(content);
            log.setType(2);
            log.setDate(new Date(System.currentTimeMillis()));
            log.setUrl(user.getUrl());
        }
        addLog(log);
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
