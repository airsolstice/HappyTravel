package com.admin.ht.module;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
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
import com.admin.ht.model.User;
import com.admin.ht.utils.LogUtils;
import com.admin.ht.utils.ToastUtils;
import com.admin.ht.widget.NoScrollViewPager;

import net.openmob.mobileimsdk.android.ClientCoreSDK;
import net.openmob.mobileimsdk.android.core.LocalUDPDataSender;

import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * 主页Activity
 *
 * Created by Solstice on 3/12/2017.
 */
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
    private  long mExitTime;

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

        User user = (User) getIntent().getExtras().get(Constant.USER);
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
        //mPager.setOffscreenPageLimit(4);
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

    @OnClick(R.id.search)
    public void doLogout() {
        new AlertDialog.Builder(mContext)
                .setMessage("确认注销？")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //在连不上IM服务器时的异常处理
                        if(!ClientCoreSDK.getInstance().isLocalDeviceNetworkOk()){
                            ToastUtils.showShort(mContext, "网络异常，请重新启动应用");
                            startActivity(new Intent(mContext, LoginActivity.class));
                            finish();
                            return;
                        }

                        if(!ClientCoreSDK.getInstance().isLoginHasInit()){
                            ToastUtils.showShort(mContext, "登录异常，请重新启动应用");
                            startActivity(new Intent(mContext, LoginActivity.class));
                            finish();
                            return;
                        }

                        if(!ClientCoreSDK.getInstance().isConnectedToServer()){
                            ToastUtils.showShort(mContext, "IM服务器异常，请重新启动应用");
                            startActivity(new Intent(mContext, LoginActivity.class));
                            finish();
                            return;
                        }

                        new AsyncTask<Object, Integer, Integer>(){
                            @Override
                            protected Integer doInBackground(Object... params) {
                                int code = -1;
                                try{
                                    code = LocalUDPDataSender.getInstance(mContext).sendLoginout();
                                }
                                catch (Exception e){
                                    Log.w(TAG, e);
                                }

                                return code;
                            }

                            @Override
                            protected void onPostExecute(Integer code) {
                                startActivity(new Intent(mContext, LoginActivity.class));
                                finish();
                            }
                        }.execute();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create().show();
    }




    @OnClick(R.id.add)
    public void getMenu(){
        PopupMenu popup = new PopupMenu(mContext, mAdd);
        popup.getMenuInflater().inflate(R.menu.menu_home, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add_personal:
                        startActivityForResult(new Intent(mContext, PersonalAdditionActivity.class), Constant.CODE);
                        break;

                    case R.id.add_group:
                        startActivity(new Intent(mContext, GroupAdditionActivity.class));
                        break;
                }
                return true;
            }
        });
        popup.show();
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
}
