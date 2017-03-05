package com.admin.ht.model;

/**
 * Created by Administrator on 2016/11/18 0018.
 */
public class RespRegister {
    public String result;
    public String method;

    public RespRegister(String result, String method) {
        this.result = result;
        this.method = method;
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

    @Override
    public String toString() {
        return "RespRegister{" +
                "result='" + result + '\'' +
                ", method='" + method + '\'' +
                '}';
    }
}
