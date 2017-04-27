package com.admin.ht.module;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.ht.IM.IMClientManager;
import com.admin.ht.R;
import com.admin.ht.base.BaseActivity;
import com.admin.ht.base.Constant;
import com.admin.ht.model.Result;
import com.admin.ht.model.User;
import com.admin.ht.retro.ApiClient;
import com.admin.ht.utils.LogUtils;
import com.admin.ht.utils.NetUtils;
import com.admin.ht.utils.StringUtils;
import com.admin.ht.utils.ToastUtils;

import net.openmob.mobileimsdk.android.ClientCoreSDK;
import net.openmob.mobileimsdk.android.core.LocalUDPDataSender;
import net.openmob.mobileimsdk.android.event.ChatBaseEvent;

import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends BaseActivity implements ChatBaseEvent{

    @Bind(R.id.phone)
    EditText mPhone;
    @Bind(R.id.pwd)
    EditText mPwd;
    @Bind(R.id.is_saved)
    CheckBox mIsSaved;
    @Bind(R.id.network_tip)
    TextView mTip;

    private boolean isSaved;
    private long mExitTime;
    private String mPhoneStr;
    private String mPwdStr;
    private Observer mObserver = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        mPhoneStr = mPreferences.getString(COUNT, "");
        mPwdStr = mPreferences.getString(PWD, "");
        isSaved = mPreferences.getBoolean(IS_SAVED, false);
        mPhone.setText(mPhoneStr);
        mPhone.setSelection(mPhoneStr.length());
        mPwd.setText(mPwdStr);
        mIsSaved.setChecked(isSaved);

    }

    @OnClick(R.id.register)
    public void register() {
        startActivity(new Intent(mContext, RegisterActivity.class));
    }

    @OnClick(R.id.forgot_pwd)
    public void forgotPwd() {
        startActivity(new Intent(mContext, ForgotPwdActivity.class));
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.do_login_in)
    public void login() {
        SharedPreferences.Editor editor = mPreferences.edit();
        mPhoneStr = mPhone.getText().toString();
        mPwdStr = mPwd.getText().toString();

        //输入合法性判断
        Drawable dw = ContextCompat.getDrawable(mContext.getApplicationContext(), R.mipmap.ic_empty);
        if (TextUtils.isEmpty(mPhoneStr) || !StringUtils.isPhone(mPhoneStr)) {
            mPhone.setText("");
            mPhone.setCompoundDrawables(null, null, dw, null);
            return;
        } else if (TextUtils.isEmpty(mPwdStr) || mPwdStr.length() < 6 || mPwdStr.length() > 18) {
            mPwd.setText("");
            mPwd.setCompoundDrawables(null, null, dw, null);
            return;
        }

        if (mIsSaved.isChecked()) {
            editor.putString(COUNT, mPhoneStr);
            editor.putString(PWD, mPwdStr);
        }
        editor.putBoolean(IS_SAVED, mIsSaved.isChecked());
        editor.commit();


        loginInSvc();
    }


    public void loginInSvc() {
        ApiClient.service.loginIn(mPhoneStr, StringUtils.MD5(mPwdStr))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;
                    @Override
                    public void onCompleted() {
                        String str ;
                        if(result == null){
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "正在加载地图组件";
                            User user = ApiClient.gson.fromJson(result.getModel().toString(), User.class);
                            loginIM(user.getId());
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra(BaseActivity.USER, user);
                            startActivity(intent);
                            finish();
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "登入失败";
                        } else if(result.getCode() == Constant.EXECUTING){
                            str = "服务器繁忙";
                        } else {
                            str = "未知异常";
                        }
                        ToastUtils.showShort(mContext, str);
                    }

                    @Override
                    public void onNext(Result result) {
                        if(isDebug){
                            LogUtils.i(TAG, result.toString());
                        }
                        this.result = result;
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.showShort(mContext, "未知异常");
                    }
                });

    }


    private void loginIM(String id) {
        //设置登入回调
        ClientCoreSDK.getInstance().setChatBaseEvent(this);
        //设置登入回调，如果设置了上一行，则此回调方法将被覆盖
        mObserver = new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                // 服务端返回的登陆结果值
                int code = (Integer) data;
                // 登陆成功
                if (code == 0) {
                    int id = ClientCoreSDK.getInstance().getCurrentUserId();
                    Log.d(TAG, "登陆成功！" + id);
                }
                else {
                    Log.d(TAG, "登陆失败，错误码=" + code);
                }
            }
        };
        doLoginImpl(id, "hp");
    }


    private void doLoginImpl(final String targetId, final String pwd) {
        IMClientManager.getInstance(this).getBaseEventListener()
                .setLoginOkForLaunchObserver(mObserver);
        // 异步提交登陆名和密码
        new LocalUDPDataSender.SendLoginDataAsync(mContext, targetId, pwd) {
            @Override
            protected void fireAfterSendLogin(int code) {
                if (code == 0) {
                    Toast.makeText(mContext, "数据发送成功！", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "登陆信息已成功发出！" + targetId + "-" + pwd);
                } else {
                    Toast.makeText(mContext, "数据发送失败。错误码是：" + code + "！",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @OnClick(R.id.network_tip)
    public void setNetwork() {
        LogUtils.e(TAG, "配置网络");
        NetUtils.openSetting(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (NetUtils.isWifi(mContext) || NetUtils.isConnected(mContext)) {
            LogUtils.e(TAG, NetUtils.isConnected(mContext) + "");
            LogUtils.e(TAG, NetUtils.isWifi(mContext) + "");
            mTip.setVisibility(View.GONE);
        } else {
            mTip.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {//
                // 如果两次按键时间间隔大于2000毫秒，则不退出
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();// 更新mExitTime
            } else {
                mApplication.exit();
                System.exit(0);// 否则退出程序
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }


    @Override
    protected String getTAG() {
        return "Login";
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
        return R.layout.activity_login;
    }

    @Override
    public void onLoginMessage(int dwUserId, int dwErrorCode) {
        if (dwErrorCode == 0) {
            Log.i(TAG, "登录成功，当前分配的user_id = " + dwUserId);
        } else {
            Log.e(TAG, "登录失败，错误代码：" + dwErrorCode);
        }
    }

    @Override
    public void onLinkCloseMessage(int dwErrorCode) {
        Log.e(TAG, "网络连接出错关闭了，error：" + dwErrorCode);
    }
}
