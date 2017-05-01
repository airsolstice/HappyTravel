package com.admin.ht.model;

import org.greenrobot.greendao.annotation.Entity;
import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 聊天记录实体类
 *
 * Created by Solstice on 3/12/2017.
 */
@Entity
public class ChatLog {
    @Id
    private Long id;
    private String logno;
    private String name;
    private String content;
    private String url;
    private int type;
    private Date date;


    @Generated(hash = 1994978153)
    public ChatLog() {
    }


    @Generated(hash = 1981529424)
    public ChatLog(Long id, String logno, String name, String content, String url,
            int type, Date date) {
        this.id = id;
        this.logno = logno;
        this.name = name;
        this.content = content;
        this.url = url;
        this.type = type;
        this.date = date;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogno() {
        return logno;
    }

    public void setLogno(String logno) {
        this.logno = logno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
