package com.admin.ht.model;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by Spec_Inc on 3/5/2017.
 */

public class Group {

    /*群组id，主键*/
    private String groupId;
    /*分组名称*/
    private String groupName;
    /*分组成员*/
    private List<GroupItem> groupItems;

    public Group(String groupId, String groupName){
        this.groupId = groupId;
        this.groupName = groupName;
    }


    /*在群组列表中需要通过groupId来进入聊天群中*/
    public Group(String groupId, String groupName, List<GroupItem> groupItems) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupItems = groupItems;
    }

    /*在好友列表中不需要groupId这个属性*/
    public Group(String groupName, List<GroupItem> groupItems) {
        this.groupName = groupName;
        this.groupItems = groupItems;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String mGroupName) {
        this.groupName = mGroupName;
    }

    public List<GroupItem> getGroupItems() {
        return groupItems;
    }

    public void setGroupItems(List<GroupItem> mItems) {
        this.groupItems = mItems;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

}
