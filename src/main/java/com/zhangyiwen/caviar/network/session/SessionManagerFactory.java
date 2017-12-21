package com.zhangyiwen.caviar.network.session;

/**
 * Created by zhangyiwen on 2017/12/18.
 * 连接Session管理器工厂
 */
public class SessionManagerFactory {

    private SessionManagerFactory(){
    }

    static class InstanceHolder{
        private static SessionManager serverInstance=new SessionManagerImpl();
        private static SessionManager clientInstance=new SessionManagerImpl();
    }

    /**
     * 获取SessionManager
     * @return SessionManager
     */
    public static SessionManager getServerSessionMananger(){
        return InstanceHolder.serverInstance;
    }

    /**
     * 获取SessionManager
     * @return SessionManager
     */
    public static SessionManager getClientSessionMananger(){
        return InstanceHolder.clientInstance;
    }
}
