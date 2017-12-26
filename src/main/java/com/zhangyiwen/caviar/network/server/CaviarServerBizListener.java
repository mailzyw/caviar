package com.zhangyiwen.caviar.network.server;

import com.zhangyiwen.caviar.network.request.RequestContext;
import com.zhangyiwen.caviar.network.session.SessionContext;

/**
 * Created by zhangyiwen on 2017/12/19.
 * 网络事件业务处理器
 */
public interface CaviarServerBizListener {

    /**
     * 处理客户端登录请求
     */
    void processClientLogin(RequestContext requestContext, SessionContext sessionContext, byte[] msg);

    /**
     * 处理客户端登出请求
     */
    void processClientLogout(RequestContext requestContext, SessionContext sessionContext, byte[] msg);

    /**
     * 处理客户端请求消息
     */
    void processClientMsg(RequestContext requestContext, SessionContext sessionContext, byte[] msg);

}
