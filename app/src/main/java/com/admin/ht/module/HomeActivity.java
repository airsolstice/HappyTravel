package com.admin.ht.module;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.admin.ht.widget.NoScrollViewPager;
import com.baidu.mapapi.model.LatLng;

import net.openmob.mobileimsdk.android.core.LocalUDPDataSender;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeActivity extends BaseActivity {

    @Bind(R.id.container)
    RelativeLayout mContainer;
    @Bind(R.id.view_pager)
    NoScrollViewPager mPager;
    @Bind(R.id.back)
    ImageView mBack;
    @Bind(R.id.contact)
    ImageView mContact;
    @Bind(R.id.add)
    ImageView mAdd;
    @Bind(R.id.search)
    ImageView mSearch;
    @Bind(R.id.lab_title)
    TextView mTitle;

    private List<Fragment> mFrgs = new ArrayList<>();

    private  long mExitTime ;

    @Override
    protected String getTAG() {
        return "Home";
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
        return R.layout.activity_home;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        User user = (User) getIntent().getExtras().get(BaseActivity.USER);
        if(user != null){
            if (isDebug){
                LogUtils.e(TAG, user.toString());
            }
            putUser(user);
        }

        Fragment contact = new ContactFragment();
        Fragment map = new MapFragment();
        mFrgs.add(map);
        mFrgs.add(contact);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(adapter);
        mPager.setCurrentItem(0);
        mPager.setNoScroll(true);
        mPager.setOffscreenPageLimit(4);
        mPager.addOnPageChangeListener(adapter);
    }

    @OnClick(R.id.contact)
    public void go2Contact() {
        mPager.setCurrentItem(1);
    }

    @OnClick(R.id.back)
    public void go2HomePage() {
        mPager.setCurrentItem(0);
    }


    class PagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {


        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFrgs.get(position);
        }

        @Override
        public int getCount() {
            return mFrgs.size();
        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                if(!mPager.isNoScroll()){
                    mPager.setNoScroll(true);
                }

                mBack.setVisibility(View.GONE);
                mTitle.setText("主页");
                mAdd.setVisibility(View.GONE);
                mSearch.setVisibility(View.VISIBLE);
                mContact.setVisibility(View.VISIBLE);

            } else {
                mTitle.setText("联系人");
                if(mPager.isNoScroll()){
                    mPager.setNoScroll(false);
                }

                mBack.setVisibility(View.VISIBLE);
                mAdd.setVisibility(View.VISIBLE);
                mContact.setVisibility(View.GONE);
                mSearch.setVisibility(View.GONE);
                mPager.setNoScroll(false);

            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
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
    protected void onDestroy() {
        IMClientManager.getInstance(mContext).release();
        super.onDestroy();
    }
}
