/*
 * Copyright (C) 2016 即时通讯网(52im.net) The MobileIMSDK Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/MobileIMSDK
 *  
 * 即时通讯网(52im.net) - 即时通讯技术社区! PROPRIETARY/CONFIDENTIAL.
 * Use is subject to license terms.
 * 
 * ChatTransDataEventImpl.java at 2016-2-20 11:20:18, code by Jack Jiang.
 * You can contact author with jack.jiang@52im.net or jb2011@163.com.
 */
package com.admin.ht.IM.impl;

import android.util.Log;

import com.admin.ht.base.BaseActivity;
import com.admin.ht.model.ChatLog;
import com.admin.ht.model.User;
import com.admin.ht.module.SingleChatActivity;

import net.openmob.mobileimsdk.android.event.ChatTransDataEvent;

public class ChatTransDataEventImpl implements ChatTransDataEvent {
    private final static String TAG = ChatTransDataEventImpl.class.getSimpleName();

    @Override
    public void onTransBuffer(String fingerPrintOfProtocol, int userId, String content) {
        Log.d(TAG, "收到来自用户" + userId + "的消息:" + content+","+fingerPrintOfProtocol);
    }


    @Override
    public void onErrorResponse(int errorCode, String errorMsg) {
        Log.d(TAG, "收到服务端错误消息，errorCode=" + errorCode + ", errorMsg=" + errorMsg);
    }
}
