package com.admin.ht.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.admin.ht.model.ChatLog;
import com.admin.ht.model.RecentMsg;

import com.admin.ht.greendao.ChatLogDao;
import com.admin.ht.greendao.RecentMsgDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig chatLogDaoConfig;
    private final DaoConfig recentMsgDaoConfig;

    private final ChatLogDao chatLogDao;
    private final RecentMsgDao recentMsgDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        chatLogDaoConfig = daoConfigMap.get(ChatLogDao.class).clone();
        chatLogDaoConfig.initIdentityScope(type);

        recentMsgDaoConfig = daoConfigMap.get(RecentMsgDao.class).clone();
        recentMsgDaoConfig.initIdentityScope(type);

        chatLogDao = new ChatLogDao(chatLogDaoConfig, this);
        recentMsgDao = new RecentMsgDao(recentMsgDaoConfig, this);

        registerDao(ChatLog.class, chatLogDao);
        registerDao(RecentMsg.class, recentMsgDao);
    }
    
    public void clear() {
        chatLogDaoConfig.clearIdentityScope();
        recentMsgDaoConfig.clearIdentityScope();
    }

    public ChatLogDao getChatLogDao() {
        return chatLogDao;
    }

    public RecentMsgDao getRecentMsgDao() {
        return recentMsgDao;
    }

}