package com.admin.ht.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.admin.ht.R;
import com.admin.ht.model.User;
import com.admin.ht.utils.LogUtils;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import butterknife.ButterKnife;

/**
 * Activity基类
 *
 * Created by Solstice on 2016/10/31 0031.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static String FILE_NAME = "user_login_info_file";
    public static String COUNT = "user_count";
    public static String PWD = "user_pwd";
    public static String IS_SAVED = "is_saved_flag";
    public static String TARGET_USER = "target_user";
    public static String USER = "user";
    public static String USER_ID = "user_id";
    public static String USER_HEAD_URL = "user_url";
    public static String USER_EMAIL = "user_email";
    public static String USER_NAME = "user_name";
    public static String USER_CHAT_ID = "user_chat_id";
    public static String USER_DEFAULT_HEAD_URL = "http://img1.imgtn.bdimg.com/it/u=3995083595,771039139&fm=214&gp=0.jpg";

    protected String TAG = getTAG();
    /**
     * 设置控制台目标对象
     */
    protected abstract String getTAG();
    /**
     * 设置是否执行Status Bar自适应
     */
    public abstract boolean setTranslucent();
    private boolean isTranslucent = setTranslucent();
    public abstract boolean setDebug();
    protected boolean isDebug = setDebug();

    /**
     * 设置布局id
     */
    public abstract int setLayoutId();
    /**
     * activity关联布局
     */
    protected int layoutId = setLayoutId();
    /**
     * 上下文引用
     */
    protected Context mContext = null;
    private SystemBarTintManager mTintManager;
    private View mDecorView;
    protected BaseApplication mApplication;
    protected SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isDebug) LogUtils.v(TAG, "onCreate");
        mContext = this;
        mApplication = (BaseApplication) mContext.getApplicationContext();
        mApplication.addActivity(this);
        setStatusBar();
        setContentView(layoutId);
        ButterKnife.bind(this);
        mPreferences = mContext.getSharedPreferences(FILE_NAME, 0);
    }

    public boolean putUser(User user){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(USER_ID, user.getId());
        editor.putString(USER_HEAD_URL, user.getUrl());
        editor.putString(USER_EMAIL, user.getEmail());
        editor.putString(USER_NAME, user.getName());
        editor.putInt(USER_CHAT_ID, user.getChatId());
        return editor.commit();
    }


    public User getUser(){
        String id = mPreferences.getString(USER_ID,"");
        String name = mPreferences.getString(USER_NAME,"");
        String url = mPreferences.getString(USER_HEAD_URL,USER_DEFAULT_HEAD_URL);
        String email = mPreferences.getString(USER_EMAIL, "");
        int chatId = mPreferences.getInt(USER_CHAT_ID, 0);
        User user = new User(id,name, email, url, chatId);
        return user;
    }

    public void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDecorView = getWindow().getDecorView();
        if (mDecorView != null) {
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
        if (mTintManager != null) {
            mTintManager.setStatusBarTintEnabled(false);
        }
    }

    protected void setStatusBar() {
        if (isTranslucent) {
            //高于4.4
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                mTintManager = new SystemBarTintManager(this);
                mTintManager.setStatusBarTintColor(mContext.getResources().getColor(R.color
                        .colorPrimary));
                mTintManager.setStatusBarTintEnabled(true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isDebug) LogUtils.v(TAG, "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isDebug) LogUtils.v(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isDebug) LogUtils.v(TAG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isDebug) LogUtils.v(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isDebug) LogUtils.v(TAG, "onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isDebug) LogUtils.v(TAG, "onPause");
    }
}
