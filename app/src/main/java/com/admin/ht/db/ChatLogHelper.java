package com.admin.ht.db;

import com.admin.ht.base.BaseApplication;
import com.admin.ht.greendao.ChatLogDao;
import com.admin.ht.model.ChatLog;

import java.util.List;

/**
 * Created by Spec_Inc on 4/27/2017.
 */

public class ChatLogHelper {

    private static ChatLogDao mDao = BaseApplication.getSession().getChatLogDao();

    public static void insert(ChatLog entity){
        mDao.insert(entity);
    }
    public static void update(ChatLog entity){
        mDao.update(entity);
    }

    public static List<ChatLog> queryById(String key){
        return mDao.queryBuilder().
                where(ChatLogDao.Properties.No.eq(key)).list();
    }

    public static List<ChatLog> queryAll(){
        return mDao.loadAll();
    }

}
