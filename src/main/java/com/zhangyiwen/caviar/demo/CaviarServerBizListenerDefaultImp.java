package com.zhangyiwen.caviar.demo;

import com.zhangyiwen.caviar.network.request.CaviarMsgCallback;
import com.zhangyiwen.caviar.network.request.RequestContext;
import com.zhangyiwen.caviar.network.server.CaviarServerBizListener;
import com.zhangyiwen.caviar.network.session.SessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyiwen on 2017/12/26.
 * 网络事件业务处理器——实现示例
 */
public class CaviarServerBizListenerDefaultImp implements CaviarServerBizListener{

    private static final Logger LOGGER = LoggerFactory.getLogger(CaviarServerBizListenerDefaultImp.class);

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
        LOGGER.info("--->processClientMsg. requestId:{}, msg:{}", requestContext.getRequestId(), msg);
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        byte[] reqAsyncMsg = "server msg push async".getBytes();
        sessionContext.sendServerReq("server msg push".getBytes());
        sessionContext.sendServerReqAsync(reqAsyncMsg, new CaviarMsgCallback() {
            @Override
            public void dealRequestCallback(byte[] msg) {
                LOGGER.info("[MsgCallback] get resp. msg:{}", msg);
            }

            @Override
            public void dealRequestTimeout(byte[] msg) {
                LOGGER.info("[MsgCallback] get resp timeout. request:{}", reqAsyncMsg);
            }
        });
        sessionContext.sendClientRequestResp(requestContext, msg);
    }
}
