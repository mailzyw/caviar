package com.zhangyiwen.caviar.biz.server;

import com.zhangyiwen.caviar.network.server.CaviarServerBizListener;
import com.zhangyiwen.caviar.network.session.SessionContext;
import com.zhangyiwen.caviar.protocol.CaviarMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyiwen on 2017/12/19.
 * CaviarBizHandler的业务实现
 */
public class CaviarServerBizListenerImpl implements CaviarServerBizListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaviarServerBizListenerImpl.class);

    @Override
    public void CLIENT_LOGIN_REQ(SessionContext session, byte[] msg) {
        LOGGER.info("[CLIENT_LOGIN_REQ] session:{}, msg:{}", session, String.valueOf(msg));
        CaviarMessage resp = CaviarMessage.CLIENT_LOGIN_RESP("login succeed".getBytes());
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        session.writeAndFlush(resp);
        LOGGER.info("[CLIENT_LOGIN_REQ] response to client. msg:{}", resp);
    }

    @Override
    public void CLIENT_MSG_SEND_REQ(SessionContext session, byte[] msg) {
        LOGGER.info("[CLIENT_MSG_SEND_REQ] session:{}, msg:{}",session,String.valueOf(msg));
    }

    @Override
    public void CLIENT_LOGOUT_REQ(SessionContext session, byte[] msg) {
        LOGGER.info("[CLIENT_LOGOUT_REQ] session:{}, msg:{}",session,String.valueOf(msg));
        CaviarMessage resp = CaviarMessage.CLIENT_LOGOUT_RESP("logout succeed".getBytes());
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        session.writeAndFlush(resp);
        LOGGER.info("[CLIENT_LOGOUT_REQ] response to client. msg:{}", resp);
    }
}
