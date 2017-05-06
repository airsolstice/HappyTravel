package com.admin.ht.module;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.admin.ht.R;
import com.admin.ht.base.BaseActivity;
import com.admin.ht.base.Constant;
import com.admin.ht.utils.LocationUtils;
import com.admin.ht.utils.LogUtils;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.facebook.drawee.view.SimpleDraweeView;

import retrofit2.http.Url;

/**
 * 测试Activity
 * <p>
 * Created by Solstice on 3/12/2017.
 */
public class TestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        SimpleDraweeView view = (SimpleDraweeView) this.findViewById(R.id.icon);

        Uri uri = Uri.parse(Constant.USER_DEFAULT_HEAD_URL);
        view.setImageURI(uri);

       // initLocation();

    }

    /**
     * 高德地图定位
     */
    private AMapLocationClient mLocationClient = null;

    public void initLocation() {
        //初始化client
        mLocationClient = new AMapLocationClient(this);
        //设置定位参数
        mLocationClient.setLocationOption(getDefaultOption());
        //设置定位监听
        mLocationClient.setLocationListener(locationListener);
        //启动定位
        mLocationClient.startLocation();
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setGpsFirst(false);
        //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setHttpTimeOut(30000);
        //可选，设置定位间隔。默认为2秒
        mOption.setInterval(1000 * 60);
        //可选，设置是否返回逆地理地址信息。默认是true
        mOption.setNeedAddress(true);
        //可选，设置是否单次定位。默认是false
        mOption.setOnceLocation(false);
        //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        mOption.setOnceLocationLatest(false);
        //可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);
        //可选，设置是否使用传感器。默认是false
        mOption.setSensorEnable(false);
        //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setWifiScan(true);
        //可选，设置是否使用缓存定位，默认为true
        mOption.setLocationCacheEnable(true);
        return mOption;
    }

    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc) {
                String result = LocationUtils.getLocationStr(loc);
                LogUtils.e("Test", result);
            } else {
                LogUtils.e("Test", "定位失败");
            }
        }
    };


    private void destroyLocation() {
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyLocation();
    }
}