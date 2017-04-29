package com.admin.ht.IM.impl;

import android.util.Log;
import net.openmob.mobileimsdk.android.event.ChatBaseEvent;
import java.util.Observer;

/**
 * IM通讯登入事件回调
 *
 * Created by Solstice on 4/27/2017.
 */
public class ChatBaseEventImpl implements ChatBaseEvent {
    private final static String TAG = ChatBaseEventImpl.class.getSimpleName();
    private Observer loginOkForLaunchObserver = null;

    @Override
    public void onLoginMessage(int dwUserId, int dwErrorCode) {
        if (dwErrorCode == 0) {
            Log.i(TAG, "登录成功，当前分配的user_id=" + dwUserId);
        } else {
            Log.e(TAG, "登录失败，错误代码：" + dwErrorCode);

        }
        // 此观察者只有开启程序首次使用登陆界面时有用
        if (loginOkForLaunchObserver != null) {
            loginOkForLaunchObserver.update(null, dwErrorCode);
            loginOkForLaunchObserver = null;
        }
    }

    @Override
    public void onLinkCloseMessage(int dwErrorCode) {
        Log.e(TAG, "网络连接出错关闭了，error：" + dwErrorCode);
    }

    public void setLoginOkForLaunchObserver(Observer loginOkForLaunchObserver) {
        this.loginOkForLaunchObserver = loginOkForLaunchObserver;
    }
}
