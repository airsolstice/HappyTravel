package com.admin.ht.model;

import com.alibaba.fastjson.JSONObject;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 最近聊天记录实体类
 *
 * Created by Solstice on 3/12/2017.
 */
@Entity
public class RecentMsg {

    /*好友id，主键*/
    @Id
    private String id;
    /*好友昵称*/
    private String name;
    /*好友头像链接*/
    private String url;
    /*消息数目*/
    private int count = 0;
    /*好友说明*/
    private String note;
    /*好友所在分组id*/

    public RecentMsg(){

    }

    @Generated(hash = 1756061048)
    public RecentMsg(String id, String name, String url, int count, String note) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.count = count;
        this.note = note;
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

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}