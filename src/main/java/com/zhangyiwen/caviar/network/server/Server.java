package com.zhangyiwen.caviar.network.server;

/**
 * Created by zhangyiwen on 2017/12/19.
 * 网络服务端
 */
public interface Server {

    /**
     * Bind操作
     * @param port 监听端口
     */
    void bind(int port);
}
