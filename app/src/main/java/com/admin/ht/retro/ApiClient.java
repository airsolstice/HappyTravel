package com.admin.ht.retro;

import com.admin.ht.model.Result;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import rx.Observable;

/**
 * 后端服务API
 * <p>
 * Created by Solstice on 2016/11/18 0018.
 */
public class ApiClient {
    /**
     * 服务地址：192.168.2.227
     * 端口：8080
     */
    public interface ApiService {

        /**
         * 登入
         *
         * @param id
         * @param pwd
         * @return
         */
        @FormUrlEncoded
        @POST("user/login")
        Observable<Result> loginIn(@Field("id") String id, @Field("pwd") String pwd);

        /**
         * 注册
         *
         * @param id
         * @param email
         * @param pwd
         * @return
         */
        @FormUrlEncoded
        @POST("user/regist")
        Observable<Result> register(@Field("id") String id, @Field("email") String email, @Field("pwd") String pwd);

        /**
         * 重置密码
         *
         * @param id
         * @param pwd
         * @return
         */
        @FormUrlEncoded
        @POST("user/retrievePwd")
        Observable<Result> retrievePwd(@Field("id") String id, @Field("pwd") String pwd);

        /**
         * 更新用户位置
         *
         * @param id
         * @param lat
         * @param lng
         * @return
         */
        @FormUrlEncoded
        @POST("loc/update")
        Observable<Result> updatePos(@Field("id") String id, @Field("lat") String lat, @Field("lng") String lng);

        /**
         * 获得群组列表
         *
         * @param id
         * @return
         */
        @FormUrlEncoded
        @POST("group/list")
        Observable<Result> getGroups(@Field("id") String id);

        /**
         * 通过id获取用户信息
         *
         * @param id
         * @return
         */
        @FormUrlEncoded
        @POST("user/info/get")
        Observable<Result> getUserInfo(@Field("id") String id);

        /**
         * 通过id获取用户marker
         *
         * @param id
         * @return
         */
        @FormUrlEncoded
        @POST("loc/marker")
        Observable<Result> getUserMarker(@Field("id") String id);
        /**
         * 通过chat id反向获取用户信息
         *
         * @param chatId
         * @return
         */
        @FormUrlEncoded
        @POST("user/info/bychatid/get")
        Observable<Result> getUserInfo(@Field("chatId") int chatId);

        /**
         * 通过id获取chat id
         *
         * @param memberId
         * @return
         */
        @FormUrlEncoded
        @POST("chat/group/list")
        Observable<Result> getChatGroups(@Field("memberId") String memberId);

        /**
         * 通过id获取chat id
         *
         * @param id
         * @return
         */
        @FormUrlEncoded
        @POST("user/chatid/get")
        Observable<Result> getChatId(@Field("id") String id);

        /**
         * 搜索用户
         *
         * @param id
         * @return
         */
        @FormUrlEncoded
        @POST("user/search")
        Observable<Result> searchUser(@Field("id") String id);

        /**
         * 删除好友关系
         *
         * @param id
         * @return
         */
        @FormUrlEncoded
        @POST("group/delete")
        Observable<Result> removeShip(@Field("id") String id, @Field("fid") String fid);

        /**
         * 搜索群
         *
         * @param groupId
         * @return
         */
        @FormUrlEncoded
        @POST("chat/group/search")
        Observable<Result> searchGroup(@Field("groupId") String groupId);

        /**
         * 添加到分组
         *
         * @param id
         * @return
         */
        @FormUrlEncoded
        @POST("group/add")
        Observable<Result> add(@Field("id") String id, @Field("fid") String fid, @Field("gname") String gname);

        /**
         * 添加到群组
         *
         * @return
         */
        @FormUrlEncoded
        @POST("chat/group/invite")
        Observable<Result> invite(@Field("groupId") int groupId,
                                  @Field("memberId") String memberId, @Field("groupName") String groupName,
                                  @Field("role") int role);

        /**
         * 添加到群组
         *
         * @return
         */
        @FormUrlEncoded
        @POST("chat/group/createGroup")
        Observable<Result> createGroup(@Field("memberId") String memberId, @Field("groupName") String groupName,
                                       @Field("role") int role);

        /**
         * 退群
         *
         * @return
         */
        @FormUrlEncoded
        @POST("chat/group/quit")
        Observable<Result> quitChatGroup(@Field("groupId") int groupId, @Field("memberId") String memberId);

        /**
         * 获取群组成员
         *
         * @return
         */
        @FormUrlEncoded
        @POST("chat/group/members")
        Observable<Result> getMembers(@Field("groupId") int groupId);

        /**
         * 通过id获取用户行走轨迹
         *
         * @param id
         * @return
         */
        @FormUrlEncoded
        @POST("loc/trace")
        Observable<Result> getTrace(@Field("id") String id);

        /**
         * 注销
         *
         * @param id
         * @return
         */
        @FormUrlEncoded
        @PUT("user/loginOut")
        Observable<Result> loginOut(@Field("id") String id);

    }

    /**
     * Gson配置对象
     */
    public static Gson gson = new GsonBuilder()
            .setLenient()
            .enableComplexMapKeySerialization()//支持Map的key为复杂对象的形式
            .serializeNulls() //智能null
            .setPrettyPrinting()// 调教格式
            .disableHtmlEscaping() //默认是GSON把HTML 转义的
            .create();

    /**
     * OKClient代理对象
     */
    public static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build();

    /**
     * Retrofit单例对象
     */
    public static Retrofit retrofit = new Retrofit.Builder()
            //设置OKClement代理
            .client(client)
            //这里最后面必须能带“/”
            .baseUrl("http://192.168.2.227:8080/frame-study-demo/")
            //设置json默认解析器
            //.addConverterFactory(GsonConverterFactory.createGroup(gson)
            //设置json自定义解析器
            .addConverterFactory(MyGsonConverter.create(gson))
            //设置RxJava响应式回调
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();

    /**
     * API调用入口
     */
    public static ApiClient.ApiService service =
            retrofit.create(ApiClient.ApiService.class);
}
