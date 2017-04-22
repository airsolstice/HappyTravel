package com.admin.ht.model;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Spec_Inc on 4/19/2017.
 */

public class UnsortedGroup {

    private int no;

    private String id;

    private String fid;

    private String groupName;

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
