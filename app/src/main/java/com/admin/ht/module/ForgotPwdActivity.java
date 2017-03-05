package com.admin.ht.module;

import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.admin.ht.R;
import com.admin.ht.base.BaseActivity;
import com.admin.ht.model.RespForgotPwd;
import com.admin.ht.retro.ApiClient;
import com.admin.ht.utils.LogUtils;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ForgotPwdActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        // TODO: forgot pwd operation
        //forgotPwdSvc(count);
    }

    public void forgotPwdSvc(String count){
        ApiClient.service.forgotPwd(count)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RespForgotPwd>() {
                    boolean isSuccess = false;

                    @Override
                    public void onCompleted() {
                        if (isSuccess) {
                            Toast.makeText(mContext, "get pwd successfully", Toast
                                    .LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(TAG, "error:" + e.getMessage());
                    }

                    @Override
                    public void onNext(RespForgotPwd entity) {
                        LogUtils.e(TAG, entity.toString());
                        if (entity.result.equals("success")) {
                            isSuccess = true;
                        }
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

}
