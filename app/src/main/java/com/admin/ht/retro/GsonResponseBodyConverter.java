package com.admin.ht.retro;


import com.admin.ht.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 自定义Response Gson解析器
 *
 * Created by Spec_Inc on 4/16/2017.
 */
public class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final Type type;

    public GsonResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        Reader reader = value.charStream();
        try {
            JsonReader jsonReader = new JsonReader(reader);
            //设置gson宽松解析
            jsonReader.setLenient(true);
            return gson.fromJson(jsonReader, type);
            //默认解析方式
            //return gson.fromJson(reader, type);
        } finally {
            closeQuietly(reader);
        }
    }

    static void closeQuietly(Closeable closeable) {
        if (closeable == null) 
            return;
        try {
            closeable.close();
        } catch (IOException ignored) {
            
        }
    }
}
