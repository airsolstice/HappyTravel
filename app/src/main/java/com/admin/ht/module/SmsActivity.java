package com.admin.ht.module;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.admin.ht.R;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;


public class SmsActivity extends Activity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        //打开注册页面
        RegisterPage registerPage = new RegisterPage();
        registerPage.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                // 解析注册结果
                if (result == SMSSDK.RESULT_COMPLETE) {
                    @SuppressWarnings("unchecked")
                    HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country");
                    String phone = (String) phoneMap.get("phone");

                    // 提交用户信息, 提交的资料将当作“通信录好友”功能的建议资料。
                    registerUser(country, phone);

                    //在验证过后可以处理自己想要的操作
                    Log.v("TAG", "success...");
                }
            }
        });
        registerPage.show(SmsActivity.this);

    }

    //这个方法要自己写
    protected void registerUser(String country, String phone) {
        //提交的资料将当作“通信录好友”功能的建议资料。
        String uid = "1223";
        String nickName = "yj";
        SMSSDK.submitUserInfo(uid, nickName , null, country, phone);
    }

}