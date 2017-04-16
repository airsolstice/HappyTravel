package com.admin.ht.base;

import android.app.Activity;
import android.app.Application;

import com.admin.ht.IM.IMClientManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.baidu.mapapi.SDKInitializer;
import java.util.LinkedList;
import java.util.List;

import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2016/11/1 0001.
 */
public class BaseApplication extends Application {

    private List<Activity> mList = new LinkedList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        SDKInitializer.initialize(this);
        SMSSDK.initSDK(this, "1cfbdcca823d8", "94fe30c8bcd8d100387ca96c57dbc398");
        IMClientManager.getInstance(this).initMobileIMSDK();
    }

    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

}
