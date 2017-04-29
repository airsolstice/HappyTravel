package com.admin.ht.module;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Window;
import android.widget.EditText;
import com.admin.ht.R;
import com.admin.ht.base.BaseActivity;
import com.admin.ht.base.Constant;
import com.admin.ht.model.Result;
import com.admin.ht.retro.ApiClient;
import com.admin.ht.utils.LogUtils;
import com.admin.ht.utils.StringUtils;
import com.admin.ht.utils.ToastUtils;
import butterknife.Bind;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 忘记密码Activity
 *
 * Created by Solstice on 3/12/2017.
 */
public class ForgotPwdActivity extends BaseActivity {

    @Bind(R.id.phone)
    EditText mPhone;
    @Bind(R.id.pwd)
    EditText mPwd;
    @Bind(R.id.verify_pwd)
    EditText mVerify;
    @Bind(R.id.input_identifying_code)
    EditText mIdentifyingCode;

    private String mPhoneStr;
    private String mPwdStr;
    private String mVerifyStr;
    private String mCodeStr;
    private EventHandler mHandler = new EventHandler() {
        String str = null;

        @Override
        public void afterEvent(int event, int result, Object data) {
            super.afterEvent(event, result, data);
            System.out.println(data);
            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //验证码验证成功
                    str = "验证码验证成功";
                    retrieveSvc(mPhoneStr, mPwdStr);
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    str = "验证码发送成功";
                }
            } else {
                try {
                    str = "发送异常";
                    throw new Exception((Throwable) data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ForgotPwdActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showShort(mContext, str);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        SMSSDK.registerEventHandler(mHandler);
    }

    @OnClick(R.id.get_identifying_code)
    public void getIdentifyingCode() {
        mPhoneStr = mPhone.getText().toString().trim();

        if (TextUtils.isEmpty(mPhoneStr)) {
            ToastUtils.showShort(mContext, "请输入手机号");
            return;
        }
        SMSSDK.getVerificationCode("86", mPhoneStr);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.do_retrieve)
    public void retrieve() {
        mPhoneStr = mPhone.getText().toString();
        mPwdStr = mPwd.getText().toString().trim();
        mVerifyStr = mVerify.getText().toString().trim();
        mCodeStr = mIdentifyingCode.getText().toString().trim();
        String errStr = "";

        //输入合法性判断
        Drawable dw = ContextCompat.getDrawable(mContext.getApplicationContext(), R.mipmap.ic_empty);
        if (!StringUtils.isPhone(mPhoneStr)) {
            mPhone.setCompoundDrawables(null, null, dw, null);
            errStr = "手机号码格式有误";
            return;
        } else if (TextUtils.isEmpty(mPwdStr) || mPwdStr.length() < 6 || mPwdStr.length() > 18) {
            //对于密码长度和格式的校验，可以参考ValidateUtils，由于测试方便，并未加上验证
            mPwd.setCompoundDrawables(null, null, dw, null);
            errStr = "密码格式不正确";
        } else if (!mVerifyStr.equals(mPwdStr)) {
            mVerify.setCompoundDrawables(null, null, dw, null);
            errStr = "两次密码输入不一致";
        } else if (mCodeStr.length() != 4) {
            mIdentifyingCode.setCompoundDrawables(null, null, dw, null);
            ToastUtils.showShort(mContext, "验证码输入有误");
        }


        if (!TextUtils.isEmpty(errStr)) {
            ToastUtils.showShort(mContext, errStr);
            return;
        } else {
            //SMSSDK.submitVerificationCode("86", mPhoneStr, mCodeStr);
            retrieveSvc(mPhoneStr, mPwdStr);
        }

    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == Constant.DELAY_TASK) {
                finish();
            }
        }
    };

    public void retrieveSvc(String id, String pwd) {
        ApiClient.service.retrievePwd(id, StringUtils.MD5(pwd))
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
                            str = "密码修改成功，请重新登入";
                            handler.sendEmptyMessageDelayed(Constant.DELAY_TASK, 1000);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "密码修改失败";
                        } else if(result.getCode() == Constant.EXECUTING){
                            str = "服务器繁忙";
                        } else {
                            str = "未知异常";
                        }
                        ToastUtils.showShort(mContext, str);
                    }

                    @Override
                    public void onNext(Result result) {
                        LogUtils.i(TAG, result.toString());
                        this.result = result;
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.showShort(mContext, "未知异常");
                    }
                });

    }

    @Override
    protected String getTAG() {
        return "ForgotPwd";
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
        return R.layout.activity_forgot_pwd;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }
}
