package com.admin.ht.model;

import com.admin.ht.base.Constant;
import com.alibaba.fastjson.JSONObject;

/**
 * 通讯结果实体类
 * <p>
 * Created by Solstice on 3/12/2017.
 */
public class Result {

    private int code = Constant.SUCCESS;
    private String msg = "";
    private Object model = null;

    public Result() {
    }

    public Result(Object model) {
        this.model = model;
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(int code, String msg, Object model) {
        this.code = code;
        this.msg = msg;
        this.model = model;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getModel() {
        return model;
    }

    public void setModel(Object model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}