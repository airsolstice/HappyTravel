package com.admin.ht.module;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.admin.ht.R;
import com.admin.ht.base.BaseFragment;
import com.admin.ht.base.Constant;
import com.admin.ht.model.Item;
import com.admin.ht.model.Result;
import com.admin.ht.model.UnsortedGroup;
import com.admin.ht.model.User;
import com.admin.ht.retro.ApiClient;
import com.admin.ht.utils.LocationUtils;
import com.admin.ht.utils.LogUtils;
import com.admin.ht.utils.StringUtils;
import com.admin.ht.utils.ToastUtils;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * 地图碎片类
 *
 * Created by Solstice on 3/12/2017.
 */
public class MapFragment extends BaseFragment {

    private  AMapLocationClient mLocationClient = null;
    private MapView mMapView;
    private AMap aMap;
    private List<List<Item>> mData = new ArrayList<>();
    private List<String> mGroups = new ArrayList<>();



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getGroupSvc();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) view.findViewById(R.id.map_view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();
        initLocation();
    }


    private void initLocation() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.interval(1000*60*3);
        //设置定位蓝点的Style
        aMap.setMyLocationStyle(myLocationStyle);
        // 设置默认定位按钮是否显示，非必需设置。
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setMyLocationEnabled(true);

        //初始化client
        mLocationClient = new AMapLocationClient(getActivity());
        //设置定位参数
        mLocationClient.setLocationOption(getDefaultOption());
        //设置定位监听
        mLocationClient.setLocationListener(locationListener);
        //启动定位
        mLocationClient.startLocation();
        
    }
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc) {
                String result = LocationUtils.getLocationStr(loc);
                LogUtils.e(TAG, result);
                putUserLoc(loc.getLatitude(), loc.getLongitude());

            } else {
                LogUtils.e(TAG, "定位失败，loc is null");
            }
        }
    };


    private void destroyLocation(){
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setGpsFirst(false);
        //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setHttpTimeOut(30000);
        //可选，设置定位间隔。默认为2秒
        mOption.setInterval(1000*60);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        destroyLocation();
    }

    @Override
    protected String getTAG() {
        return "Map Fragment";
    }

    @Override
    public boolean setDebug() {
        return true;
    }


    public void getGroupSvc(){
        if(mUser == null){
            mUser = getUser();
        }
        if (isDebug) {
            LogUtils.e(TAG, mUser.toString());
        }

        ApiClient.service.getGroupList(mUser.getId())
                .subscribeOn(Schedulers.newThread())
                //.observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;
                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "地图模块，获取群组列表";
                            mData.clear();
                            Gson gson = new Gson();
                            UnsortedGroup[] unsortedGroup = gson.fromJson(result.getModel().toString(), UnsortedGroup[].class);
                            ArrayList<UnsortedGroup> ls = new ArrayList<>();
                            for (UnsortedGroup ug : unsortedGroup) {
                                ls.add(ug);
                            }

                            for (int i = 0; i < ls.size(); i++) {
                                if (ls.get(i) == null) {
                                    continue;
                                }
                                UnsortedGroup ug = ls.get(i);
                                mGroups.add(ug.getGroupName());
                                List<Item> items = new ArrayList<>();

                                Item item = new Item();
                                item.setId(ug.getFid());
                                item.setName("user");
                                item.setNote("......");
                                item.setStatus(0);
                                item.setUrl("http://");
                                items.add(item);

                                for (int j = i + 1; j < ls.size(); j++) {

                                    if (ls.get(j) == null) {
                                        continue;
                                    }

                                    if (ug.getGroupName().equals(ls.get(j).getGroupName())) {
                                        item = new Item();
                                        item.setId(ls.get(j).getFid());
                                        item.setName("user");
                                        item.setNote("......");
                                        item.setStatus(1);
                                        item.setUrl("http://");
                                        items.add(item);
                                        ls.set(j, null);
                                    }
                                }

                                mData.add(items);
                            }

                            Activity a = getActivity();
                            a.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });

                        } else if (result.getCode() == Constant.FAIL) {
                            str = "更新失败";
                        } else if (result.getCode() == Constant.EXECUTING) {
                            str = "服务器繁忙";
                        } else {
                            str = "未知异常";
                        }

                        if(isDebug){
                            LogUtils.i(TAG, str);
                        }
                    }

                    @Override
                    public void onNext(Result result) {
                        this.result = result;
                        if (isDebug) {
                            LogUtils.i(TAG, result.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(isDebug){
                            LogUtils.i(TAG, e.toString());
                        }
                        e.printStackTrace();
                    }
                });

    }



        public void putUserLoc(double lat, double lng) {
        if (mUser == null) {
            mUser = getUser();
        }

        if (StringUtils.isEmpty(mUser.getId()) || !StringUtils.isPhone(mUser.getId())) {
            if (isDebug) {
                LogUtils.e(TAG, "上传用户位置失败");
            }
            ToastUtils.showShort(getContext(), "上传用户位置失败");
            return;
        }

        updateSvc(mUser, lat, lng);
    }

    public void updateSvc(User user, double lat, double lng) {

        ApiClient.service.updatePosition(user.getId(), String.valueOf(lat), String.valueOf(lng))
                .subscribeOn(Schedulers.newThread())
                //.observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;

                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "用户位置更新成功";
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "更新失败";
                        } else if (result.getCode() == Constant.EXECUTING) {
                            str = "服务器繁忙";
                        } else {
                            str = "未知异常";
                        }

                        LogUtils.i(TAG, str);
                    }

                    @Override
                    public void onNext(Result result) {
                        if (isDebug) {
                            LogUtils.i(TAG, result.toString());
                        }
                        this.result = result;
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.i(TAG, e.toString());
                        e.printStackTrace();
                    }
                });
    }

}
