package com.admin.ht.retro;

import com.admin.ht.base.Constant;
import com.admin.ht.model.Result;
import com.admin.ht.utils.LogUtils;
import com.amap.api.maps.model.LatLng;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * ApiClient接口调用类
 * <p>
 * Created by Spec_Inc on 5/1/2017.
 */
public class ApiClientImpl {

    /**
     * 解除用户关系
     *
     * @param listener
     * @param id
     * @param fid
     */
    public static void removeShipSvc(final RetrofitCallbackListener listener, String  id, String fid) {

        ApiClient.service.removeShip(id, fid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;

                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "成功解除关系";
                            if (listener == null) {
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "解除失败";
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
     * 搜索用户信息
     *
     * @param listener
     * @param key
     */
    public static void searchUserSvc(final RetrofitCallbackListener listener, String  key) {

        ApiClient.service.searchUser(key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;

                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "搜索用户信息";
                            if (listener == null) {
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "搜索用户信息失败";
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
     * @param chatId
     */
    public static void getUserInfoSvc(final RetrofitCallbackListener listener, int chatId) {

        ApiClient.service.getUserInfo(chatId)
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
                            if (listener == null) {
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "获取用户信息失败";
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
     * 退群
     *
     * @param groupId
     * @param memberId
     */
    public static void quitChatGroupsSvc(final RetrofitCallbackListener listener,int groupId, String memberId) {

        ApiClient.service.quitChatGroup(groupId, memberId)
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
                            str = "退群成功";
                            if (listener == null) {
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "退群失败";
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
     * 获取群列表
     *
     * @param id
     */
    public static void getChatGroupsSvc(final RetrofitCallbackListener listener, String id) {

        ApiClient.service.getChatGroups(id)
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
                            str = "获取群列表成功";
                            if (listener == null) {
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "获取群列表失败";
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
     * 搜索群
     *
     * @param key
     */
    public static void searchGroupSvc(final RetrofitCallbackListener listener, String key) {

        ApiClient.service.searchGroup(key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;

                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "搜索群成功";
                            if (listener == null) {
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "搜索群失败";
                            if (listener == null) {
                                return;
                            }
                            listener.receive(result);
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
     * 创建群
     *
     * @param listener
     * @param memberId
     * @param groupName
     */
    public static void createGroupSvc(final RetrofitCallbackListener listener, String memberId, String groupName) {

        ApiClient.service.createGroup(memberId, groupName, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;

                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "群创建成功";
                            if (listener == null) {
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "群创建失败";
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
     * 修改密码
     *
     * @param listener
     * @param id
     * @param pwd
     */
    public static void retrieveSvc(final RetrofitCallbackListener listener, String id, String pwd) {

        ApiClient.service.retrievePwd(id, pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;

                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "密码修改成功";
                            if (listener == null) {
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "密码修改失败";
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
     * 注册
     *
     * @param listener
     * @param id
     * @param pwd
     */
    public static void registerSvc(final RetrofitCallbackListener listener, String id, String pwd, String email) {

        ApiClient.service.register(id, email, pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;

                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "注册成功";
                            if (listener == null) {
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "注册失败";
                            if (listener == null) {
                                return;
                            }
                            listener.receive(result);
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
     * 登陆验证
     *
     * @param listener
     * @param id
     * @param pwd
     */
    public static void loginInSvc(final RetrofitCallbackListener listener, String id, String pwd) {

        ApiClient.service.loginIn(id, pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    Result result = null;

                    @Override
                    public void onCompleted() {
                        String str;
                        if (result == null) {
                            str = "未知异常";
                        } else if (result.getCode() == Constant.SUCCESS) {
                            str = "登陆成功";
                            if (listener == null) {
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "登陆失败";
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
     * 获取用户Marker信息
     *
     * @param listener
     * @param id
     */
    public static void getUserMarkerSvc(final RetrofitCallbackListener listener, String id) {

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
                            if (listener == null) {
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "获取用户Marker失败";
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
    public static void getUserInfoSvc(final RetrofitCallbackListener listener, String id) {

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
                            if (listener == null) {
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "获取用户信息失败";
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
    public static void updatePosSvc(final RetrofitCallbackListener listener, String id, LatLng ll) {

        ApiClient.service.updatePos(id, String.valueOf(ll.latitude), String.valueOf(ll.longitude))
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
                            if (listener == null) {
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "更新用户位置失败";
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
                            str = "获取好友分组列表";
                            if (listener == null) {
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "获取好友分组列表失败";
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
                            if (listener == null) {
                                return;
                            }
                            listener.receive(result);
                        } else if (result.getCode() == Constant.FAIL) {
                            str = "获取聊天群组成员列表失败";
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
