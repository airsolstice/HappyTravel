package com.admin.ht.module;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.gson.Gson;
import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends BaseActivity {

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
                            Gson gson = new Gson();
                            User user = gson.fromJson(result.getModel().toString(), User.class);
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

}
