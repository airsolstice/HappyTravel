package com.admin.ht.model;

/**
 * Created by Spec_Inc on 3/5/2017.
 */

public class ItemFriEntity {

    /*好友id，主键*/
    private int id;
    /*好友昵称*/
    private String name;
    /*好友头像链接*/
    private String url;
    /*好友状态，1：在线，0：离线*/
    private int status;
    /*好友说明*/
    private String note;
    /*好友所在分组id*/
    private int groupId;
    /*好友所在分组的名称*/
    private String groupName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
