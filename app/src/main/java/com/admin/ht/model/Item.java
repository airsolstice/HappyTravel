package com.admin.ht.model;

import java.io.Serializable;

/**
 * Created by Spec_Inc on 4/22/2017.
 */

public class Item implements Serializable{


    /*好友id，主键*/
    private String id;
    /*好友昵称*/
    private String name;
    /*好友头像链接*/
    private String url;
    /*好友状态，1：在线，0：离线*/
    private int status;
    /*好友说明*/
    private String note;

    public Item() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
}
