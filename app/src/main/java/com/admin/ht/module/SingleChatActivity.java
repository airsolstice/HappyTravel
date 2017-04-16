package com.admin.ht.module;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.ht.IM.IMClientManager;
import com.admin.ht.R;
import com.admin.ht.adapter.ChatLogAdapter;
import com.admin.ht.base.BaseActivity;
import com.admin.ht.model.ChatLog;
import com.admin.ht.utils.NetUtils;

import net.openmob.mobileimsdk.android.ClientCoreSDK;
import net.openmob.mobileimsdk.android.core.LocalUDPDataSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;
import butterknife.OnClick;


public class SingleChatActivity extends BaseActivity {

    private Observer mObserver = null;

    private List<ChatLog> mLogData = new ArrayList<>();

    ChatLogAdapter mLogAdapter = null;

    @Bind(R.id.progress)
    ProgressBar mBar;

    @Bind(R.id.send)
    ImageView mSend;

    @Bind(R.id.lab_title)
    TextView mTitle;

    @Bind(R.id.log_list)
    ListView mLog;

    @Bind(R.id.edit_text)
    EditText mEdit;

    @Bind(R.id.edit_to)
    EditText mEditTo;

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
        return false;
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_single_chat;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        initLogin();
        mLogAdapter = new ChatLogAdapter(mContext, mLogData);
        mLog.setAdapter(mLogAdapter);

    }

    @OnClick(R.id.send)
    public void sendMsg() {
        final String msg = mEdit.getText().toString().trim();
        if (msg.length() > 0) {
            int friendId = Integer.parseInt(mEditTo.getText().toString().trim());

            // 发送消息（Android系统要求必须要在独立的线程中发送哦）
            new LocalUDPDataSender.SendCommonDataAsync(mContext, msg, friendId, true) {
                @Override
                protected void onPostExecute(Integer code) {
                    if (code == 0) {
                        Log.d(TAG, "数据已成功发出！");
                        ChatLog log = new ChatLog();
                        log.setContent(msg);
                        addLog(log);
                    } else
                        Toast.makeText(getApplicationContext(), "数据发送失败。错误码是：" + code + "！", Toast.LENGTH_SHORT).show();
                }
            }.execute();
        } else
            Log.e(TAG, "len=" + (msg.length()));

    }

    public void addLog(ChatLog log) {
        mLogData.add(log);
        mLogAdapter.notifyDataSetChanged();
    }


    private void initLogin() {
        Log.d(TAG, "初始化");

        IMClientManager.getInstance(this).getTransDataListener().setGUI(this);
        IMClientManager.getInstance(this).getBaseEventListener().setMainGUI(this);
        IMClientManager.getInstance(this).getMessageQoSListener().setMainGUI(this);
        mObserver = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                // 服务端返回的登陆结果值
                int code = (Integer) data;
                // 登陆成功
                if (code == 0) {
                    //startActivity(new Intent(mContext, HomeActivity.class));
                    int mId = ClientCoreSDK.getInstance().getCurrentUserId();
                    Log.d(TAG, "登陆成功！" + mId);
                    mBar.setVisibility(View.GONE);
                    mSend.setClickable(true);
                }
                // 登陆失败
                else {
                    new AlertDialog.Builder(mContext)
                            .setTitle("友情提示")
                            .setMessage("Sorry，登陆失败，错误码=" + code)
                            .setPositiveButton("知道了", null)
                            .show();
                }
            }
        };


        mSend.setClickable(false);
        String id = getIntent().getStringExtra(BaseActivity.CHAT2WHO);
        // mTitle.setText(id);
        doLogin(id);
    }


    /**
     * 登陆处理。
     */

    public void doLogin(String id) {
        if (!NetUtils.isWifi(mContext) && !NetUtils.isConnected(mContext)) {
            return;
        }

        //从服务器映射响应的聊天服务id --- 异步任务处理
        String chatId = getChatId(id);

        // 发送登陆数据包
        if (id != null && id.length() > 0) {
            doLoginImpl(chatId, "123");
        }
    }

    /**
     * 真正的登陆信息发送实现方法。
     */
    private void doLoginImpl(final String chatId, final String pwd) {
        // 设置好服务端反馈的登陆结果观察者（当客户端收到服务端反馈过来的登陆消息时将被通知）
        IMClientManager.getInstance(this).getBaseEventListener()
                .setLoginOkForLaunchObserver(mObserver);

        // 异步提交登陆名和密码
        new LocalUDPDataSender.SendLoginDataAsync(
                mContext
                , chatId
                , pwd) {
            /**
             * 登陆信息发送完成后将调用本方法（注意：此处仅是登陆信息发送完成
             * ，真正的登陆结果要在异步回调中处理哦）。
             *
             * @param code 数据发送返回码，0 表示数据成功发出，否则是错误码
             */
            @Override
            protected void fireAfterSendLogin(int code) {
                if (code == 0) {
                    Toast.makeText(getApplicationContext(), "数据发送成功！", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "登陆信息已成功发出！" + chatId + "-" + pwd);
                } else {
                    Toast.makeText(getApplicationContext(), "数据发送失败。错误码是：" + code + "！", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }


    private String getChatId(String chat2id) {
        if (TextUtils.isEmpty(chat2id)) {
            return null;
        }
        //访问服务器，获取好友id对应的聊天id，此处暂定为10000
        return "18099";
    }

    private void doLogout() {
        // 发出退出登陆请求包（Android系统要求必须要在独立的线程中发送哦）
        new AsyncTask<Object, Integer, Integer>() {
            @Override
            protected Integer doInBackground(Object... params) {
                int code = -1;
                try {
                    code = LocalUDPDataSender.getInstance(mContext).sendLoginout();
                } catch (Exception e) {
                    Log.w(TAG, e);
                }

                return code;
            }

            @Override
            protected void onPostExecute(Integer code) {
                if (code == 0)
                    Log.d(TAG, "注销登陆请求已完成！");
                else
                    Toast.makeText(getApplicationContext(), "注销登陆请求发送失败。错误码是：" + code + "！", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // ** 注意：Android程序要么就别处理，要处理就一定
        //			要退干净，否则会有意想不到的问题哦！
        // 退出登陆
        doLogout();
    }

    protected void onDestroy() {

        super.onDestroy();
    }


}
