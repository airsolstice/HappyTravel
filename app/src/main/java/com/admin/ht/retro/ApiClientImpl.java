package com.admin.ht.retro;

import com.admin.ht.base.Constant;
import com.admin.ht.model.Result;
import com.admin.ht.utils.LogUtils;
import com.amap.api.maps.model.LatLng;

import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * ApiClient接口调用类
 *
 * Created by Spec_Inc on 5/1/2017.
 */

public class ApiClientImpl {

    /**
     * 获取用户Marker信息
     *
     * @param listener
     * @param id
     */
    public static void getUserMarkerSvc(final RetrofitCallbackListener listener, String id){

        ApiClient.service.getUserMarker(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;

                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "获取用户Marker";
                            if(listener == null){
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "业务请求失败";
                        } else if (result.getCode() == Constant.EXECUTING) {
                            str = "服务器繁忙";
                        } else {
                            str = "未知异常";
                        }

                        LogUtils.i("TAG", str);
                    }

                    @Override
                    public void onNext(Result result) {
                        this.result = result;
                        LogUtils.i("TAG", result.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("TAG", e.toString());
                        e.printStackTrace();
                    }
                });
    }


    /**
     * 获取用户信息
     *
     * @param listener
     * @param id
     */
    public static void getUserInfoSvc(final RetrofitCallbackListener listener, String id){

        ApiClient.service.getUserInfo(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;

                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "获取用户信息";
                            if(listener == null){
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "业务请求失败";
                        } else if (result.getCode() == Constant.EXECUTING) {
                            str = "服务器繁忙";
                        } else {
                            str = "未知异常";
                        }

                        LogUtils.i("TAG", str);
                    }

                    @Override
                    public void onNext(Result result) {
                        this.result = result;
                        LogUtils.i("TAG", result.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("TAG", e.toString());
                        e.printStackTrace();
                    }
                });
    }


    /**
     * 更新用户位置
     *
     * @param listener
     * @param id
     * @param ll
     */
    public static void updatePosSvc(final RetrofitCallbackListener listener, String id, LatLng ll){

        ApiClient.service.updatePos(id,String.valueOf(ll.latitude), String.valueOf(ll.longitude))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;

                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "更新用户位置";
                            if(listener == null){
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "业务请求失败";
                        } else if (result.getCode() == Constant.EXECUTING) {
                            str = "服务器繁忙";
                        } else {
                            str = "未知异常";
                        }

                        LogUtils.i("TAG", str);
                    }

                    @Override
                    public void onNext(Result result) {
                        this.result = result;
                        LogUtils.i("TAG", result.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("TAG", e.toString());
                        e.printStackTrace();
                    }
                });
    }

    /**
     * 获取聊天组列表请求
     *
     * @param listener
     * @param id
     */
    public static void getGroupsSvc(final RetrofitCallbackListener listener, String id) {

        ApiClient.service.getGroups(id)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;

                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "获取聊天群组列表";
                            if(listener == null){
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "业务请求失败";
                        } else if (result.getCode() == Constant.EXECUTING) {
                            str = "服务器繁忙";
                        } else {
                            str = "未知异常";
                        }

                        LogUtils.i("TAG", str);
                    }

                    @Override
                    public void onNext(Result result) {
                        this.result = result;
                        LogUtils.i("TAG", result.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("TAG", e.toString());
                        e.printStackTrace();
                    }
                });
    }


    /**
     * 获取聊天群成员列表请求
     *
     * @param listener
     * @param groupId
     */
    public static void getMembersSvc(final RetrofitCallbackListener listener, int groupId) {

        ApiClient.service.getMembers(groupId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;

                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "获取聊天群组成员列表";
                            if(listener == null){
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "业务请求失败";
                        } else if (result.getCode() == Constant.EXECUTING) {
                            str = "服务器繁忙";
                        } else {
                            str = "未知异常";
                        }
                        LogUtils.i("TAG", str);
                    }

                    @Override
                    public void onNext(Result result) {
                        this.result = result;
                        LogUtils.i("TAG", result.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("TAG", e.toString());
                        e.printStackTrace();
                    }
                });
    }

}
