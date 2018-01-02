package com.zhangyiwen.caviar.network.client;

import com.zhangyiwen.caviar.network.request.RequestContext;
import com.zhangyiwen.caviar.network.session.SessionContext;

/**
 * Created by zhangyiwen on 2017/12/19.
 * 网络事件业务处理器
 */
public interface CaviarClientBizListener {

    /**
     * 处理服务端请求消息
     */
    void processServerMsg(RequestContext requestContext, SessionContext sessionContext, byte[] msg);

}
