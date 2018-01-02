package com.zhangyiwen.caviar.network.request;


/**
 * Created by zhangyiwen on 2017/12/19.
 * 网络事件业务回调接口
 */
public interface CaviarMsgCallback {

    /**
     * 响应Callback
     */
    void dealRequestCallback(byte[] msg);

    /**
     * 响应超时Callback
     * @param msg
     */
    void dealRequestTimeout(byte[] msg);

}
