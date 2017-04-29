package com.admin.ht.IM.impl;

import android.util.Log;
import net.openmob.mobileimsdk.android.event.MessageQoSEvent;
import net.openmob.mobileimsdk.server.protocal.Protocal;
import java.util.ArrayList;

/**
 * IM通讯消息回调
 *
 * Created by Solstice on 4/27/2017.
 */
public class MessageQoSEventImpl implements MessageQoSEvent {
    private final static String TAG = MessageQoSEventImpl.class.getSimpleName();

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
