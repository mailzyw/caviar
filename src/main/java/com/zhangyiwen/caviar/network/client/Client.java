package com.zhangyiwen.caviar.network.client;

import com.zhangyiwen.caviar.network.exception.CaviarNetworkException;
import com.zhangyiwen.caviar.protocol.CaviarMessage;

import java.io.IOException;

/**
 * Created by zhangyiwen on 2017/12/20.
 * 网络客户端
 */
public interface Client {

    /**
     * 连接服务端
     * @param host host
     * @param port port
     */
    void connect(String host, int port) throws CaviarNetworkException;

    /**
     * 重连服务端
     */
    void reconnect();

    /**
     * 断连服务端
     */
    void disconnect();

    /**
     * 关闭客户端
     * @throws IOException
     */
    void close() throws IOException;

    /**
     * 设置客户端运行状态
     * @param running running
     */
    void setRunningState(boolean running);

    /**
     * 客户端登录
     * @param loginMsg loginMsg
     * @return 登录响应
     */
    byte[] login(byte[] loginMsg) throws CaviarNetworkException;

    /**
     * 客户端登出
     * @param logoutMsg logoutMsg
     * @return
     */
    byte[] logout(byte[] logoutMsg) throws CaviarNetworkException;

    /**
     * 发送消息——同步
     * @param msg msg
     * @throws CaviarNetworkException
     */
    CaviarMessage sendMsgSync(byte[] msg) throws CaviarNetworkException;

    /**
     * 发送消息——异步
     * @param msg msg
     * @throws CaviarNetworkException
     */
    void sendMsgAsync(byte[] msg, CaviarMsgCallback caviarMsgCallback) throws CaviarNetworkException;
}
