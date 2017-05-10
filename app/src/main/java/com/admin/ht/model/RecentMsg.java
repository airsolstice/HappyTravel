package com.admin.ht.model;

import com.alibaba.fastjson.JSONObject;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;

/**
 * 最近聊天记录实体类
 *
 * Created by Solstice on 3/12/2017.
 */
@Entity
public class RecentMsg {
    @Id
    private Long no;
    /*好友id*/
    private String id;
    /*聊天id，用于区分每个用户的列表*/
    private String owner;
    /*好友昵称*/
    private String name;
    /*好友头像链接*/
    private String url;
    /*消息数目*/
    private int count = 0;
    /*好友说明*/
    private String note;
    /*接收消息的时间*/
    private Date time;

    public RecentMsg(){

    }

    @Generated(hash = 484197876)
    public RecentMsg(Long no, String id, String owner, String name, String url,
            int count, String note, Date time) {
        this.no = no;
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.url = url;
        this.count = count;
        this.note = note;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getNo() {
        return no;
    }

    public void setNo(Long no) {
        this.no = no;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}