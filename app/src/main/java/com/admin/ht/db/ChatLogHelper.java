package com.admin.ht.db;

import com.admin.ht.base.BaseApplication;
import com.admin.ht.greendao.ChatLogDao;
import com.admin.ht.model.ChatLog;
import java.util.List;

/**
 * 数据库表ChatLog的操作类
 *
 * Created by Solstice on 4/27/2017.
 */
public class ChatLogHelper {

    private static ChatLogDao mDao = BaseApplication.getSession().getChatLogDao();

    public static void insert(ChatLog entity){
        mDao.insert(entity);
    }
    public static void update(ChatLog entity){
        mDao.update(entity);
    }
    public static List<ChatLog> queryAll(){
        return mDao.loadAll();
    }
    public static List<ChatLog> queryByLogno(String key){
        return mDao.queryBuilder().
                where(ChatLogDao.Properties.Logno.eq(key)).list();
    }

    public static void delete(String key) {
        for(ChatLog entity : queryByLogno(key)){
            mDao.delete(entity);
        }
    }
}
