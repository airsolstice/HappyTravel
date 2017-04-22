package com.admin.ht.module;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.admin.ht.R;
import com.admin.ht.base.BaseFragment;
import com.admin.ht.base.Constant;
import com.admin.ht.model.Result;
import com.admin.ht.model.User;
import com.admin.ht.retro.ApiClient;
import com.admin.ht.utils.LogUtils;
import com.admin.ht.utils.StringUtils;
import com.admin.ht.utils.ToastUtils;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by Spec_Inc on 2/19/2017.
 */

public class MapFragment extends BaseFragment {

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationMode mCurrentMode;
    private LocationClient mLocClient;
    private LocationListener myListener = new LocationListener();
    private boolean isFirstLoc = true;


    public void putUserLoc(LatLng ll) {
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

        updateSvc(mUser, ll);
    }


    public void updateSvc(User user, LatLng ll) {

        ApiClient.service.updatePosition(user.getId(), String.valueOf(ll.latitude), String.valueOf(ll.longitude))
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


    /**
     * 定位监听函数
     */
    public class LocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());

            if (isFirstLoc) {
                putUserLoc(ll);
                isFirstLoc = false;
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
    }

    public void initLocation() {
        mCurrentMode = LocationMode.NORMAL;
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                mCurrentMode, true, null));

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(getActivity());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(3 * 60 * 1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        LogUtils.e("Map", "starting location ....");
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
                            str = "获取群组列表";
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


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get group list data
        getGroupSvc();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) view.findViewById(R.id.map_view);
        //init map location
        initLocation();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected String getTAG() {
        return "Map Fragment";
    }

    @Override
    public boolean setDebug() {
        return true;
    }


    @Override
    public void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }


}
