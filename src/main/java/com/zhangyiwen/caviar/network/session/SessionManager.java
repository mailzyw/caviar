package com.zhangyiwen.caviar.network.session;

/**
 * Created by zhangyiwen on 2017/12/18.
 * 连接Session管理器
 */
public interface SessionManager {

    /**
     * 根据index获取SessionContext
     * @param index index
     * @return SessionContext
     */
    SessionContext getSessionContext(long index);

    /**
     * 绑定SessionContext到index
     * @param index index
     * @param session session
     * @return SessionContext
     */
    SessionContext bindSessionContext(long index, SessionContext session);

    /**
     * 清理index对应的SessionContext
     * @param index index
     */
    void cleanSessionContext(long index);

}
