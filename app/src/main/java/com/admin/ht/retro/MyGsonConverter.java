package com.admin.ht.retro;

import com.google.gson.Gson;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 
 * Created by Solstice on 4/16/2017.
 */
public class MyGsonConverter extends Converter.Factory {

    private final Gson gson;

    public static MyGsonConverter create() {
        return create(new Gson());
    }

    public static MyGsonConverter create(Gson gson) {
        return new MyGsonConverter(gson);
    }

    private MyGsonConverter(Gson gson) {
        if (gson == null) throw new NullPointerException("gson is null");
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new GsonResponseBodyConverter<>(gson, type);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new GsonRequestBodyConverter<>(gson, type);
    }

    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return super.stringConverter(type, annotations, retrofit);
    }
}
