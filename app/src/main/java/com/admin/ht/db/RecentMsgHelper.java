package com.admin.ht.db;

import com.admin.ht.base.BaseApplication;
import com.admin.ht.greendao.ChatLogDao;
import com.admin.ht.greendao.RecentMsgDao;
import com.admin.ht.model.ChatLog;
import com.admin.ht.model.RecentMsg;

import java.util.List;

/**
 * Created by Spec_Inc on 4/27/2017.
 */

public class RecentMsgHelper {

    private static RecentMsgDao mDao = BaseApplication.getSession().getRecentMsgDao();

    public static void insert(RecentMsg entity){
        mDao.insert(entity);
    }
    public static void  update(RecentMsg entity){
        mDao.update(entity);
    }

    public static List<RecentMsg> queryByName(String key){
        return mDao.queryBuilder().
                where(RecentMsgDao.Properties.Name.eq(key)).list();
    }

    public static List<RecentMsg> queryAll(){
        return mDao.loadAll();
    }

}
