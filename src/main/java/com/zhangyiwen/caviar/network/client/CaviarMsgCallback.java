package com.zhangyiwen.caviar.network.client;

import com.zhangyiwen.caviar.protocol.CaviarMessage;

/**
 * Created by zhangyiwen on 2017/12/19.
 * 网络事件业务回调接口
 */
public interface CaviarMsgCallback {

    /**
     * 消息发送响应Callback
     */
    void CLIENT_MSG_SEND_RESP(CaviarMessage msg);

}
