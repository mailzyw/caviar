package com.zhangyiwen.caviar.network.server;

import com.zhangyiwen.caviar.network.session.SessionContext;
import com.zhangyiwen.caviar.protocol.CaviarMessage;

/**
 * Created by zhangyiwen on 2017/12/19.
 * 网络事件业务回调接口
 */
public interface CaviarServerBizListener {

    /**
     * 客户端登录
     */
    void CLIENT_LOGIN_REQ(SessionContext session, byte[] msg);

    /**
     * 客户端消息发送
     */
    void CLIENT_MSG_SEND_REQ(SessionContext session, byte[] msg);

    /**
     * 客户端登出
     */
    void CLIENT_LOGOUT_REQ(SessionContext session, byte[] msg);

}
