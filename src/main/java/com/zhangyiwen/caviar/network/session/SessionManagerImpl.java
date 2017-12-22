package com.zhangyiwen.caviar.network.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by zhangyiwen on 2017/12/18.
 * 连接Session管理器实现类
 */
public class SessionManagerImpl implements SessionManager{

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionManagerImpl.class);

    private volatile Map<Long, SessionContext> index2Session = new ConcurrentSkipListMap<>();

    @Override
    public SessionContext getSessionContext(long index) {
        SessionContext session = this.index2Session.get(index);
        LOGGER.info("[session manager] get session succeed. index:{}, session:{}", index, session);
        return session;
    }

    @Override
    public SessionContext bindSessionContext(long index, SessionContext session) {
        session.setIndex(index);
        SessionContext result = this.index2Session.put(index,session);
        LOGGER.info("[session manager] bind session succeed. mapSize:{}, index:{}, session:{}", index2Session.size(), index, session);
        return result;
    }

    @Override
    public void cleanSessionContext(long index) {
        SessionContext session = index2Session.remove(index);
        if (session != null) {
            session.close();
        }
        LOGGER.info("[session manager] clean session by index succeed. mapSize:{}, index:{}, session:{}", index2Session.size(), index, session);
    }

}
