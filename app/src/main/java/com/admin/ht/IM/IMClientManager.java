package com.admin.ht.IM;

import android.content.Context;

import com.admin.ht.IM.impl.ChatBaseEventImpl;
import com.admin.ht.IM.impl.ChatTransDataEventImpl;
import com.admin.ht.IM.impl.MessageQoSEventImpl;

import net.openmob.mobileimsdk.android.ClientCoreSDK;
import net.openmob.mobileimsdk.android.conf.ConfigEntity;

/**
 * IM客户端管理类
 * <p>
 * Created by Solstice on 4/27/2017.
 */
public class IMClientManager {
    private static IMClientManager instance = null;
    private boolean init = false;
    private ChatBaseEventImpl baseEventListener = null;
    private ChatTransDataEventImpl transDataListener = null;
    private MessageQoSEventImpl messageQoSListener = null;
    private Context context = null;

    public static IMClientManager getInstance(Context context) {
        if (instance == null)
            instance = new IMClientManager(context);
        return instance;
    }

    private IMClientManager(Context context) {
        this.context = context;
        initMobileIMSDK();
    }

    public void initMobileIMSDK() {
        if (!init) {
            // 设置AppKey
            ConfigEntity.appKey = "5418023dfd98c579b6001741";

            // 设置服务器ip和服务器端口
            //ConfigEntity.serverIP = "rbcore.openmob.net";
            ConfigEntity.serverIP = "192.168.2.227";
            ConfigEntity.serverUDPPort = 7901;

            // MobileIMSDK核心IM框架的敏感度模式设置
            ConfigEntity.setSenseMode(ConfigEntity.SenseMode.MODE_10S);

            // 开启/关闭DEBUG信息输出
            ClientCoreSDK.DEBUG = false;

            // 进行核心库的初始化
            ClientCoreSDK.getInstance().init(this.context);

            // 设置事件回调
            baseEventListener = new ChatBaseEventImpl();
            transDataListener = new ChatTransDataEventImpl();
            messageQoSListener = new MessageQoSEventImpl();
            ClientCoreSDK.getInstance().setChatBaseEvent(baseEventListener);
            ClientCoreSDK.getInstance().setChatTransDataEvent(transDataListener);
            ClientCoreSDK.getInstance().setMessageQoSEvent(messageQoSListener);
            init = true;
        }
    }

    public void release() {
        ClientCoreSDK.getInstance().release();
    }

    public ChatTransDataEventImpl getTransDataListener() {
        return transDataListener;
    }

    public ChatBaseEventImpl getBaseEventListener() {
        return baseEventListener;
    }

    public MessageQoSEventImpl getMessageQoSListener() {
        return messageQoSListener;
    }
}
