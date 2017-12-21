package com.zhangyiwen.caviar.biz.client;

import com.zhangyiwen.caviar.network.client.CaviarClientBizListener;
import com.zhangyiwen.caviar.network.session.SessionContext;
import com.zhangyiwen.caviar.protocol.CaviarMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyiwen on 2017/12/19.
 * CaviarBizHandler的业务实现
 */
public class CaviarClientBizListenerImpl implements CaviarClientBizListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaviarClientBizListenerImpl.class);

    @Override
    public void CLIENT_LOGIN_RESP(SessionContext session, byte[] msg) {
        LOGGER.info("[CLIENT_LOGIN_RESP] session:{}, msg:{}", session, String.valueOf(msg));
        synchronized (session){
            session.notifyAll();
        }
    }

    @Override
    public void CLIENT_MSG_SEND_RESP(SessionContext session, byte[] msg) {
        LOGGER.info("[CLIENT_MSG_SEND_RESP] session:{}, msg:{}",session,String.valueOf(msg));
    }

    @Override
    public void CLIENT_LOGOUT_RESP(SessionContext session, byte[] msg) {
        LOGGER.info("[CLIENT_LOGOUT_RESP] session:{}, msg:{}",session,String.valueOf(msg));
        synchronized (session){
            session.notifyAll();
        }
    }
}
