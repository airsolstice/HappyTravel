package com.admin.ht.db;

import com.admin.ht.base.BaseApplication;
import com.admin.ht.greendao.RecentMsgDao;
import com.admin.ht.model.RecentMsg;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * 数据库表RecentMsg操作类
 *
 * Created by Solstice on 4/27/2017.
 */
public class RecentMsgHelper {

    private static RecentMsgDao mDao = BaseApplication.getSession().getRecentMsgDao();

    public static void insert(RecentMsg entity){
        mDao.insert(entity);
    }
    public static List<RecentMsg> queryAll(){
        return mDao.loadAll();
    }
    public static void  update(RecentMsg entity){
        QueryBuilder qb = mDao.queryBuilder();
        mDao.update(entity);
    }
    public static List<RecentMsg> queryById(String key){
        return mDao.queryBuilder().
                where(RecentMsgDao.Properties.Owner.eq(key)).list();
    }
}
