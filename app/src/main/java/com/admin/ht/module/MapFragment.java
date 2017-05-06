package com.admin.ht.module;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.admin.ht.R;
import com.admin.ht.base.BaseActivity;
import com.admin.ht.base.BaseFragment;
import com.admin.ht.base.Constant;
import com.admin.ht.model.ChatMember;
import com.admin.ht.model.MarkerInfo;
import com.admin.ht.model.Result;
import com.admin.ht.model.User;
import com.admin.ht.retro.ApiClient;
import com.admin.ht.retro.ApiClientImpl;
import com.admin.ht.retro.RetrofitCallbackListener;
import com.admin.ht.utils.BitmapUtils;
import com.admin.ht.utils.ImageUtils;
import com.admin.ht.utils.LogUtils;
import com.admin.ht.utils.StringUtils;
import com.admin.ht.utils.ToastUtils;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import net.openmob.mobileimsdk.android.event.ChatTransDataEvent;
import net.openmob.mobileimsdk.android.event.MessageQoSEvent;
import net.openmob.mobileimsdk.server.protocal.Protocal;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 地图业务类
 * <p>
 * Created by Solstice on 3/12/2017.
 */
public class MapFragment extends BaseFragment
        implements View.OnClickListener, ChatTransDataEvent, MessageQoSEvent, AMap.OnMyLocationChangeListener {

    private MapView mMapView;
    private TextView mGroupNameView;
    private AMap aMap;
    private int index = 0;
    private List<ChatMember> mGroupData = new ArrayList<>();
    private List<ChatMember> mData = new ArrayList<>();
    private List<Marker> mMarkerData = new ArrayList<>();
    Marker mMarker = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册IM监听回调
        //ClientCoreSDK.getInstance().setChatTransDataEvent(this);
        //ClientCoreSDK.getInstance().setMessageQoSEvent(this);

        //配置当前用户信息
        if (mUser == null) {
            mUser = getUser();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) view.findViewById(R.id.map_view);
        mGroupNameView = (TextView) view.findViewById(R.id.group_name);
        ImageView left = (ImageView) view.findViewById(R.id.left_move);
        left.setOnClickListener(this);
        ImageView right = (ImageView) view.findViewById(R.id.right_move);
        right.setOnClickListener(this);
        TextView chat = (TextView) view.findViewById(R.id.chat);
        chat.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapView.onCreate(savedInstanceState);
        //获取地图对象
        aMap = mMapView.getMap();
        //初始化地图自带的定位配置
        initLocation();

    }

    private void initLocation() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.interval(1000 * 60 * 3);
        //设置定位蓝点的Style
        aMap.setMyLocationStyle(myLocationStyle);
        //设置缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        //设置默认定位按钮是否显示，非必需设置。
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        //设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.setMyLocationEnabled(true);
        //设置定位回调监听
        aMap.setOnMyLocationChangeListener(this);
    }

    public void addCustomMarker(final MarkerInfo info) {
        final View v = View.inflate(getActivity(), R.layout.view_marker, null);
        final SimpleDraweeView icon = (SimpleDraweeView) v.findViewById(R.id.icon);
        int rw = icon.getMeasuredWidth();
        int rh = icon.getMeasuredHeight();
        ImageSize targetSize = new ImageSize(rw, rh);
        ImageLoader.getInstance().loadImage(info.getUrl(), targetSize, ImageUtils.getDisplayImageOptions(),
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        loadedImage = BitmapUtils.makeRoundCorner(loadedImage);
                        loadedImage = BitmapUtils.zoomBitmap(loadedImage, 50, 50);
                        icon.setImageBitmap(loadedImage);
                        Bitmap bitmap = BitmapUtils.convertViewToBitmap(v);
                        Marker marker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
                                .position(new LatLng(info.getLat(), info.getLng()))
                                .title(info.getName())
                                .snippet("你好！这是地图定位演示")
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                        mMarkerData.add(marker);

                    }
                });
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
        if (mUser == null) {
            mUser = getUser();
        }
        ApiClientImpl.getChatGroupsSvc(new RetrofitCallbackListener() {
            @Override
            public void receive(Result result) {
                mGroupData.clear();
                Type type = new TypeToken<ArrayList<ChatMember>>() {
                }.getType();
                List<ChatMember> list = ApiClient.gson.fromJson(result.getModel().toString(), type);
                mGroupData.addAll(list);
                if (mGroupData.size() == 0) {
                    return;
                }

                Activity a = (Activity) getContext();
                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mGroupNameView.setText(mGroupData.get(index).getGroupName());
                        getMembers(index);
                    }
                });
            }
        }, mUser.getId());

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
    }

    @Override
    protected String getTAG() {
        return "Map Fragment";
    }

    @Override
    public boolean setDebug() {
        return true;
    }


    public void getMembers(int index){
        ApiClientImpl.getMembersSvc(new RetrofitCallbackListener() {
            @Override
            public void receive(Result result) {
                mData.clear();
                Type type = new TypeToken<ArrayList<ChatMember>>() {
                }.getType();
                List<ChatMember> list = ApiClient.gson.fromJson(result.getModel().toString(), type);
                mData.addAll(list);

                for (ChatMember member : mData) {

                    if(mUser != null){
                        if(member.getMemberId().equals(mUser.getId())){
                            continue;
                        }
                    }

                    ApiClientImpl.getUserMarkerSvc(new RetrofitCallbackListener() {
                        @Override
                        public void receive(Result result) {
                            MarkerInfo info = ApiClient.gson.fromJson(result.getModel().toString(),
                                    MarkerInfo.class);
                            addCustomMarker(info);
                        }
                    }, member.getMemberId());
                }
            }
        }, mGroupData.get(index).getGroupId());

    }

    @Override
    public void onClick(View v) {

        if(mGroupData.size() == 0){
            ToastUtils.showShort(getContext(), "请添加群组！");
            return;
        }

        switch (v.getId()) {
            case R.id.left_move:
                //清除旧的用户数据
                for(Marker m : mMarkerData){
                    m.remove();
                }
                mMarkerData.clear();
                //移动指针
                if (--index < 0) {
                    index = mGroupData.size() - 1;
                }
                //设置群组名
                mGroupNameView.setText(mGroupData.get(index).getGroupName());
                //获取成员信息，并配置marker对象到地图图层
                getMembers(index);
                break;
            case R.id.right_move:
                //清除旧的用户数据
                for(Marker m : mMarkerData){
                    m.remove();
                }
                mMarkerData.clear();
                //移动指针
                if (++index >= mGroupData.size()) {
                    index = 0;
                }
                //设置群组名
                mGroupNameView.setText(mGroupData.get(index).getGroupName());
                //获取成员信息，并配置marker对象到地图图层
                getMembers(index);
                break;

            case R.id.chat:
                Intent intent = new Intent(getContext(), GroupChatActivity.class);
                intent.putExtra(Constant.CHAT_GROUP_INFO, mGroupData.get(index));
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onMyLocationChange(Location location) {
        if (location == null) {
            LogUtils.e(TAG, "location为空");
            return;
        }
        LogUtils.e(TAG, "更新位置");

        if (mUser == null) {
            mUser = getUser();
        }
        if (StringUtils.isEmpty(mUser.getId()) || !StringUtils.isPhone(mUser.getId())) {
            if (isDebug) {
                LogUtils.e(TAG, "更新位置失败");
            }
            ToastUtils.showShort(getContext(), "位置更新失败");
            return;
        }

        ApiClientImpl.getUserMarkerSvc(new RetrofitCallbackListener() {
            @Override
            public void receive(Result result) {
                if(mMarker != null){
                    mMarker.remove();
                }

                final MarkerInfo info = ApiClient.gson.fromJson(result.getModel().toString(), MarkerInfo.class);
                final View v = View.inflate(getActivity(), R.layout.view_marker, null);
                final SimpleDraweeView icon = (SimpleDraweeView) v.findViewById(R.id.icon);
                int rw = icon.getMeasuredWidth();
                int rh = icon.getMeasuredHeight();
                ImageSize targetSize = new ImageSize(rw, rh);
                ImageLoader.getInstance().loadImage(info.getUrl(), targetSize, ImageUtils.getDisplayImageOptions(),
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                loadedImage = BitmapUtils.makeRoundCorner(loadedImage);
                                loadedImage = BitmapUtils.zoomBitmap(loadedImage, 50, 50);
                                icon.setImageBitmap(loadedImage);
                                Bitmap bitmap = BitmapUtils.convertViewToBitmap(v);
                                 mMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
                                        .position(new LatLng(info.getLat(), info.getLng()))
                                        .title(info.getName())
                                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                            }
                        });
            }
        }, mUser.getId());

        ApiClientImpl.updatePosSvc(null, mUser.getId(),
                new LatLng(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onTransBuffer(String fingerPrintOfProtocol, int userId, String content) {
        Log.d(TAG, "收到来自用户[" + userId + "]的消息:" + content + "," + fingerPrintOfProtocol);

        User user = new User();
        user.setChatId(userId);
        user.setNote(content);
        //保存信息到本地SQLite数据库
        //saveMsg(user);
        //在地图上展示消息
        //displayMsgInMarker(userId, content);
    }

    @Override
    public void onErrorResponse(int errorCode, String errorMsg) {
        Log.d(TAG, "收到服务端错误消息，errorCode=" + errorCode + ", errorMsg=" + errorMsg);
    }

    @Override
    public void messagesLost(ArrayList<Protocal> lostMessages) {
        Log.d(TAG, "收到系统的未实时送达事件通知，当前共有" + lostMessages.size() + "个包QoS保证机制结束，判定为【无法实时送达】！");
    }

    @Override
    public void messagesBeReceived(String theFingerPrint) {
        if (theFingerPrint != null) {
            Log.d(TAG, "收到对方已收到消息事件的通知，fp=" + theFingerPrint);
        }
    }


}
