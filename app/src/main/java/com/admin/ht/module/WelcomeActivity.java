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
import com.admin.ht.base.Constant;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;

/**
 * 欢迎Activity,程序入口
 *
 * Created by Solstice on 3/12/2017.
 */
public class WelcomeActivity extends BaseActivity implements Animation.AnimationListener{

    @Bind(R.id.simple_img)
    SimpleDraweeView mSimpleView;
    private long mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullScreen();
        super.onCreate(savedInstanceState);
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(6000);
        mSimpleView.startAnimation(animation);
        animation.setAnimationListener(this);
    }

    @Override
    public void onAnimationStart(Animation animation) {
        mSimpleView.setImageURI(Uri.parse(Constant.URL_WELCOME_PICTURE));
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mTime) > 2000) {//
                // 如果两次按键时间间隔大于2000毫秒，则不退出
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                // 更新mExitTime
                mTime = System.currentTimeMillis();
            } else {
                mApplication.exit();
                // 否则退出程序
                System.exit(0);
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
