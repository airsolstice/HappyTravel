package com.admin.ht.module;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.admin.ht.base.BaseActivity;
import com.admin.ht.R;
import com.admin.ht.model.ForgotPwdResponse;
import com.admin.ht.model.LoginInResponse;
import com.admin.ht.model.RegisterResponse;
import com.admin.ht.retro.ApiClient;
import com.admin.ht.utils.KeyBoardUtils;
import com.admin.ht.utils.LogUtils;
import com.admin.ht.utils.NetUtils;
import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends BaseActivity {

    @Bind(R.id.count)
    EditText mCount;
    @Bind(R.id.pwd)
    EditText mPwd;
    @Bind(R.id.is_saved)
    CheckBox mIsSaved;
    @Bind(R.id.network_tip)
    TextView mTip;

    private boolean isSaved;
    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        String countStr = mPreferences.getString(COUNT, "");
        String pwdStr = mPreferences.getString(PWD, "");
        isSaved = mPreferences.getBoolean(IS_SAVED, false);
        if (!TextUtils.isEmpty(countStr) && !TextUtils.isEmpty(pwdStr)) {
            mCount.setText(countStr);
            mCount.setSelection(countStr.length());
            mPwd.setText(pwdStr);
            mIsSaved.setChecked(isSaved);
        }
    }

    @OnClick(R.id.register)
    public void register(){
        ApiClient.service.register(mCount.getText().toString(), mPwd.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RegisterResponse>() {
                    boolean isSuccess = false;

                    @Override
                    public void onCompleted() {
                        if (isSuccess) {
                            Toast.makeText(LoginActivity.this, "register successfully", Toast
                                    .LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(TAG, "error:" + e.getMessage());
                    }

                    @Override
                    public void onNext(RegisterResponse entity) {
                        LogUtils.e(TAG, entity.toString());
                        if (entity.result.equals("success")) {
                            isSuccess = true;
                        }
                    }
                });


        //TODO: new a RegisterActivity
    }
    @OnClick(R.id.forgot_pwd)
    public void forgotPwd(){

        ApiClient.service.forgotPwd(mCount.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ForgotPwdResponse>() {
                    boolean isSuccess = false;
                    @Override
                    public void onCompleted() {
                        if (isSuccess) {
                            Toast.makeText(LoginActivity.this, "get pwd successfully", Toast
                                    .LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(TAG, "error:" + e.getMessage());
                    }

                    @Override
                    public void onNext(ForgotPwdResponse entity) {
                        LogUtils.e(TAG, entity.toString());
                        if (entity.result.equals("success")) {
                            isSuccess = true;

                        }
                    }
                });

        // TODO: new a ForgotPwdActivity
    }


    @OnClick(R.id.login_in)
    public void login() {
        SharedPreferences.Editor editor = mPreferences.edit();
        if (mIsSaved.isChecked()) {
            editor.putString(COUNT, mCount.getText().toString());
            editor.putString(PWD, mPwd.getText().toString());
        }
        editor.putBoolean(IS_SAVED, mIsSaved.isChecked());
        editor.commit();
        ApiClient.service.loginIn(mCount.getText().toString(), mPwd.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LoginInResponse>() {
                    boolean isSuccess = false;
                    @Override
                    public void onCompleted() {
                        if (isSuccess) {
                            Toast.makeText(LoginActivity.this, "login in successfully", Toast
                                    .LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(TAG, "error:" + e.getMessage());
                    }

                    @Override
                    public void onNext(LoginInResponse entity) {
                        LogUtils.e(TAG, entity.toString());
                        if (entity.getResult().equals("success")) {
                            isSuccess = true;
                        }

                    }
                });
    }



    @OnClick(R.id.network_tip)
    public void setNetwork() {
        LogUtils.e(TAG, "set network");
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
        return true;
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_login;
    }

}
