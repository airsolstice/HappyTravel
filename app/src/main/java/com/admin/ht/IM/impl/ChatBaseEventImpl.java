/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * ChatBaseEventImpl.java at 2016-2-20 11:20:18, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package com.admin.ht.IM.impl;

import android.util.Log;

import com.admin.ht.module.SingleChatActivity;

import net.openmob.mobileimsdk.android.event.ChatBaseEvent;

import java.util.Observer;

public class ChatBaseEventImpl implements ChatBaseEvent {
    private final static String TAG = ChatBaseEventImpl.class.getSimpleName();

    private SingleChatActivity mainGUI = null;

    private Observer loginOkForLaunchObserver = null;

    @Override
    public void onLoginMessage(int dwUserId, int dwErrorCode) {
        if (dwErrorCode == 0) {
            Log.i(TAG, "登录成功，当前分配的user_id=" + dwUserId);

            if (this.mainGUI != null) {
                //this.mainGUI.refreshMyid();
                //this.mainGUI.showIMInfo_green("登录成功,id="+dwUserId);
            }
        } else {
            Log.e(TAG, "登录失败，错误代码：" + dwErrorCode);

            if (this.mainGUI != null) {
                //this.mainGUI.refreshMyid();
                //this.mainGUI.showIMInfo_red("登录失败,code="+dwErrorCode);
            }
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

        if (this.mainGUI != null) {

        }
    }

    public void setLoginOkForLaunchObserver(Observer loginOkForLaunchObserver) {
        this.loginOkForLaunchObserver = loginOkForLaunchObserver;
    }

    public ChatBaseEventImpl setMainGUI(SingleChatActivity mainGUI) {
        this.mainGUI = mainGUI;
        return this;
    }
}
