package com.admin.ht.module;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import com.admin.ht.R;
import com.admin.ht.base.BaseActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;

public class WelcomeActivity extends BaseActivity {

    @Bind(R.id.simple_img)
    SimpleDraweeView mSimpleView;

    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullScreen();

        super.onCreate(savedInstanceState);
        initAnimation();

    }


    private void initAnimation() {

        /* 设置透明度动画*/
        AlphaAnimation anima = new AlphaAnimation(0.0f, 1.0f);
        /* 设置动画时间*/
        anima.setDuration(6000);
        /* 开启动画*/
        mSimpleView.startAnimation(anima);
        /*动画完成时的接口监听事件*/
        anima.setAnimationListener(new LogoAnimation());

    }

    private class LogoAnimation implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
            mSimpleView.setImageURI(Uri.parse("http://pic.90sjimg" +
                    ".com/back_pic/u/00/02/82/06/561f2511e7227.jpg"));
        }

        @Override
        public void onAnimationEnd(Animation animation) {

            skip2LoginActivity();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

    }

    private void skip2LoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
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
        return "Welcome";
    }

    @Override
    public boolean setTranslucent() {
        return false;
    }

    @Override
    public boolean setDebug() {
        return false;
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_welcome;
    }

}
