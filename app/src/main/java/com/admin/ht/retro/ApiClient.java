package com.admin.ht.retro;

import com.admin.ht.model.Result;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import rx.Observable;

/**
 * Created by Administrator on 2016/11/18 0018.
 */
public class ApiClient {

    public interface ApiService {

        @FormUrlEncoded
        @POST("user/login")
        Observable<Result> loginIn(@Field("id") String id, @Field("pwd") String pwd);

        @FormUrlEncoded
        @POST("user/regist")
        Observable<Result> register(@Field("id") String id, @Field("email") String email, @Field("pwd") String pwd);

        @FormUrlEncoded
        @POST("user/retrievePwd")
        Observable<Result> retrievePwd(@Field("id") String id, @Field("pwd") String pwd);

        @FormUrlEncoded
        @POST("loc/update")
        Observable<Result> updatePosition(@Field("id") String id, @Field("lat") String lat, @Field("lng") String lng);

        @FormUrlEncoded
        @POST("group/list")
        Observable<Result> getGroupList(@Field("id") String id);

        @FormUrlEncoded
        @POST("loc/trace")
        Observable<Result> getTrace(@Field("id") String id);

        @FormUrlEncoded
        @PUT("user/loginOut")
        Observable<Result> loginOut(@Field("id") String id);


    }

    public static Gson gson = new GsonBuilder()
            .setLenient()
            .enableComplexMapKeySerialization()//支持Map的key为复杂对象的形式
            .serializeNulls() //智能null
            .setPrettyPrinting()// 调教格式
            .disableHtmlEscaping() //默认是GSON把HTML 转义的
            .create();

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.2.227:8080/frame-study-demo/")//这里最后面必须能带“/”
            //.addConverterFactory(GsonConverterFactory.create(gson))//设置将json解析为javabean所用的方式
            .addConverterFactory(MyGsonConverter.create(gson))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();

    public static ApiClient.ApiService service =
            retrofit.create(ApiClient.ApiService.class);


}
