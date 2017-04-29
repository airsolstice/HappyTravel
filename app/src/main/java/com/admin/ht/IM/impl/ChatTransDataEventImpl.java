package com.admin.ht.IM.impl;

import android.util.Log;
import net.openmob.mobileimsdk.android.event.ChatTransDataEvent;

/**
 * IM通讯数据传输回调
 *
 * Created by Solstice on 4/27/2017.
 */
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
