package com.zhangyiwen.caviar.network.server;

import java.io.IOException;

/**
 * Created by zhangyiwen on 2017/12/19.
 * 网络服务端
 */
public interface Server {

    /**
     * 启动操作
     * @param port 监听端口
     */
    void bind(int port);

    /**
     * 关闭操作
     * @throws IOException
     */
    void close() throws IOException;
}
