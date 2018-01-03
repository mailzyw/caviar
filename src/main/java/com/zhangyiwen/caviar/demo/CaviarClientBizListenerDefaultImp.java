package com.zhangyiwen.caviar.demo;

import com.zhangyiwen.caviar.network.client.CaviarClientBizListener;
import com.zhangyiwen.caviar.network.request.RequestContext;
import com.zhangyiwen.caviar.network.session.SessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyiwen on 2017/12/27.
 * 网络事件业务处理器——实现示例
 */
public class CaviarClientBizListenerDefaultImp implements CaviarClientBizListener{

    private static final Logger LOGGER = LoggerFactory.getLogger(CaviarClientBizListenerDefaultImp.class);

    @Override
    public void processServerMsg(RequestContext requestContext, SessionContext sessionContext, byte[] msg) {
        LOGGER.info("--->processServerMsg. requestId:{}, msg:{}", requestContext.getRequestId(), new String(msg));
        sessionContext.sendServerRequestResp(requestContext, msg);
    }
}
