package com.admin.ht.model;

/**
 * Created by Administrator on 2016/11/18 0018.
 */
public class RespLoginIn {

    public String result;
    public String method;


    public RespLoginIn(String result, String method) {
        this.result = result;
        this.method = method;
    }

    @Override
    public String toString() {
        return "RespLoginIn{" +
                "result='" + result + '\'' +
                ", method='" + method + '\'' +
                '}';
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
