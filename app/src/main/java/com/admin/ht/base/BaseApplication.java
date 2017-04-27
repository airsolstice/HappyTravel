package com.admin.ht.base;

import android.app.Activity;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.admin.ht.IM.IMClientManager;
import com.admin.ht.greendao.DaoMaster;
import com.admin.ht.greendao.DaoSession;
import com.admin.ht.utils.ImageUtils;
import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.backends.pipeline.Fresco;

import net.openmob.mobileimsdk.android.core.LocalUDPDataSender;

import java.util.LinkedList;
import java.util.List;

import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2016/11/1 0001.
 */
public class BaseApplication extends Application {

    private List<Activity> mList = new LinkedList<>();

    private static DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        SDKInitializer.initialize(this);
        SMSSDK.initSDK(this, "1cfbdcca823d8", "94fe30c8bcd8d100387ca96c57dbc398");
        IMClientManager.getInstance(this).initMobileIMSDK();
        ImageUtils.initImageLoader(this);
        setupDataBase();
    }

    private void setupDataBase() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "hp.db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster master = new DaoMaster(db);
        mDaoSession = master.newSession();
    }

    public static DaoSession getSession(){
        return mDaoSession;
    }

    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    private void doLogout() {
        // 发出退出登陆请求包（Android系统要求必须要在独立的线程中发送哦）
        new AsyncTask<Object, Integer, Integer>() {
            @Override
            protected Integer doInBackground(Object... params) {
                int code = -1;
                try {
                    code = LocalUDPDataSender.getInstance(getApplicationContext()).sendLoginout();
                } catch (Exception e) {
                    Log.w("Application", e);
                }
                return code;
            }

            @Override
            protected void onPostExecute(Integer code) {
                if (code == 0)
                    Log.d("Application", "注销登陆请求已完成！");
                else
                    Toast.makeText(getApplicationContext(), "注销登陆请求发送失败。错误码是："
                            + code + "！", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }


    public void exit() {
        doLogout();
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
