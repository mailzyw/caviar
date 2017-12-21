package com.zhangyiwen.caviar.network.client;

import com.zhangyiwen.caviar.network.session.SessionContext;
import com.zhangyiwen.caviar.protocol.CaviarMessage;

/**
 * Created by zhangyiwen on 2017/12/19.
 * 网络事件业务回调接口
 */
public interface CaviarClientBizListener {

    /**
     * 客户端消息发送响应
     */
    void CLIENT_MSG_SEND_RESP(SessionContext session, byte[] msg);

}
