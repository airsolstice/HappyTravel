package com.admin.ht.module;

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
import com.admin.ht.base.BaseActivity;
import com.admin.ht.base.Constant;
import com.admin.ht.db.ChatLogHelper;
import com.admin.ht.model.ChatLog;
import com.admin.ht.model.ChatMember;
import com.admin.ht.model.Result;
import com.admin.ht.model.User;
import com.admin.ht.retro.ApiClient;
import com.admin.ht.retro.ApiClientImpl;
import com.admin.ht.retro.RetrofitCallbackListener;
import com.admin.ht.utils.LogUtils;
import com.admin.ht.utils.ToastUtils;
import com.google.gson.reflect.TypeToken;

import net.openmob.mobileimsdk.android.ClientCoreSDK;
import net.openmob.mobileimsdk.android.core.LocalUDPDataSender;
import net.openmob.mobileimsdk.android.event.ChatTransDataEvent;
import net.openmob.mobileimsdk.android.event.MessageQoSEvent;
import net.openmob.mobileimsdk.server.protocal.Protocal;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class GroupChatActivity extends BaseActivity implements ChatTransDataEvent, MessageQoSEvent {

    private User mUser;
    private ChatMember mGroupInfo;

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

        mGroupInfo = (ChatMember) getIntent().getExtras().get(Constant.CHAT_GROUP_INFO);
        LogUtils.e(TAG, mGroupInfo.toString());
        mTitle.setText(mGroupInfo.getGroupName());
    }

    @OnClick(R.id.menu)
    public void menu() {
        PopupMenu popup = new PopupMenu(mContext, mMenu);
        popup.getMenuInflater().inflate(R.menu.menu_group_chat, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.clean_log:

                        break;

                    case R.id.group_info:

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
        if (!ClientCoreSDK.getInstance().isLocalDeviceNetworkOk()) {
            ToastUtils.showShort(mContext, "网络异常，请重新启动应用");
            return;
        }

        if (!ClientCoreSDK.getInstance().isLoginHasInit()) {
            ToastUtils.showShort(mContext, "登录异常，请重新启动应用");
            return;
        }

        if (!ClientCoreSDK.getInstance().isConnectedToServer()) {
            ToastUtils.showShort(mContext, "IM服务器异常，请重新启动应用");
            return;
        }
        final String msg = mEdit.getText().toString().trim();
        if (msg.length() > 0) {
            ApiClientImpl.getMembersSvc(new RetrofitCallbackListener() {
                @Override
                public void receive(Result result) {
                    Type type = new TypeToken<ArrayList<ChatMember>>() {
                    }.getType();
                    List<ChatMember> list = ApiClient.gson.fromJson(result.getModel().toString(), type);

                    for (ChatMember cm : list) {
                        if (cm.getMemberId().equals(mUser.getId())) {
                            continue;
                        }

                        ApiClientImpl.getUserInfoSvc(new RetrofitCallbackListener() {
                            @Override
                            public void receive(Result result) {
                                final User user = ApiClient.gson.fromJson(result.getModel().toString(), User.class);
                                new LocalUDPDataSender.SendCommonDataAsync(mContext, msg, user.getChatId(), true) {
                                    @Override
                                    protected void onPostExecute(Integer code) {
                                        LogUtils.d(TAG, String.format("向【%s】发送群消息", user.getId()));
                                    }
                                }.execute();
                            }
                        }, cm.getMemberId());
                    }
                }
            }, mGroupInfo.getGroupId());
        }
    }


    @Override
    protected String getTAG() {
        return "Group Chat";
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
        return R.layout.activity_group_chat;
    }

    @Override
    public void onTransBuffer(String fingerPrintOfProtocol, int userId, String content) {
        Log.d(TAG, "收到来自用户[" + userId + "]的消息:" + content + "," + fingerPrintOfProtocol);
        ChatLog log = new ChatLog();
        log.setLogno(userId + "-" + mUser.getChatId());
        log.setContent(content);
        log.setUrl(Constant.USER_DEFAULT_HEAD_URL);
        log.setName(userId + "");
        log.setType(2);
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
