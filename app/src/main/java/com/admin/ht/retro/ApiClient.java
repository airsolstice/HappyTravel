package com.admin.ht.retro;

import com.admin.ht.model.RespForgotPwd;
import com.admin.ht.model.RespLoginIn;
import com.admin.ht.model.RespRegister;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2016/11/18 0018.
 */
public class ApiClient {

    public interface ApiService {


        @GET("LoginInServlet")
        Observable<RespLoginIn> loginIn(@Query("count")String count, @Query("pwd") String pwd);


        @GET("RegisterServlet")
        Observable<RespRegister> register(@Query("count")String count, @Query("pwd") String pwd);


        @GET("ForgotPwdServlet")
        Observable<RespForgotPwd> forgotPwd(@Query("count")String count);

    }

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.0.185:8080/HappyTravel/servlet/")//这里最后面必须能带“/”
            .addConverterFactory(GsonConverterFactory.create())//设置将json解析为javabean所用的方式
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();


    public static ApiClient.ApiService service =
            retrofit.create(ApiClient.ApiService.class);


}
