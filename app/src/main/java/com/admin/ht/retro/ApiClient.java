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
 *
 * Created by Administrator on 2016/11/18 0018.
 */
public class ApiClient {
    /**
     * 服务地址：192.168.2.227
     * 端口：8080
     */
    public interface ApiService {
        /* 登入 */
        @FormUrlEncoded
        @POST("user/login")
        Observable<Result> loginIn(@Field("id") String id, @Field("pwd") String pwd);
        /* 注册 */
        @FormUrlEncoded
        @POST("user/regist")
        Observable<Result> register(@Field("id") String id, @Field("email") String email, @Field("pwd") String pwd);
        /* 重置密码 */
        @FormUrlEncoded
        @POST("user/retrievePwd")
        Observable<Result> retrievePwd(@Field("id") String id, @Field("pwd") String pwd);
        /* 更新用户位置 */
        @FormUrlEncoded
        @POST("loc/update")
        Observable<Result> updatePosition(@Field("id") String id, @Field("lat") String lat, @Field("lng") String lng);
        /* 获得群组列表 */
        @FormUrlEncoded
        @POST("group/list")
        Observable<Result> getGroupList(@Field("id") String id);
        /* 通过id获取用户信息 */
        @FormUrlEncoded
        @POST("user/info/get")
        Observable<Result> getUserInfo(@Field("id") String id);
        /* 通过chat id反向获取用户信息 */
        @FormUrlEncoded
        @POST("user/info/bychatid/get")
        Observable<Result> getUserInfoByChatId(@Field("chatId") int chatId);
        /* 通过id获取chat id */
        @FormUrlEncoded
        @POST("user/chatid/get")
        Observable<Result> getChatId(@Field("id") String id);
        /* 通过id获取用户行走轨迹 */
        @FormUrlEncoded
        @POST("loc/trace")
        Observable<Result> getTrace(@Field("id") String id);
        /* 注销 */
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
            //.addConverterFactory(GsonConverterFactory.create(gson)
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
