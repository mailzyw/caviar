package com.zhangyiwen.caviar.biz;

import com.zhangyiwen.caviar.network.request.RequestContext;
import com.zhangyiwen.caviar.network.server.CaviarServerBizListener;
import com.zhangyiwen.caviar.network.session.SessionContext;

/**
 * Created by zhangyiwen on 2017/12/26.
 * 网络事件业务处理器——实现示例
 */
public class CaviarServerBizListenerDefaultImp implements CaviarServerBizListener{
    @Override
    public void processClientLogin(RequestContext requestContext, SessionContext sessionContext, byte[] msg) {
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sessionContext.sendClientLoginResp(requestContext,"login succeed".getBytes());
    }

    @Override
    public void processClientLogout(RequestContext requestContext, SessionContext sessionContext, byte[] msg) {
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sessionContext.sendClientLogoutResp(requestContext,"logout succeed".getBytes());
    }

    @Override
    public void processClientMsg(RequestContext requestContext, SessionContext sessionContext, byte[] msg) {
        try {
            Thread.sleep(10*1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sessionContext.sendClientRequestResp(requestContext, msg);
    }
}
