package com.zhangyiwen.caviar.demo;

import com.zhangyiwen.caviar.demo.CaviarServerBizListenerDefaultImp;
import com.zhangyiwen.caviar.network.server.CaviarServerBizListener;
import com.zhangyiwen.caviar.network.server.CaviarServer;
import com.zhangyiwen.caviar.network.server.Server;

/**
 * Created by zhangyiwen on 2017/12/19.
 * Caviar服务端启动类
 */
public class CaviarServerBootstrap {

    /**
     * 业务方的入参
     * CaviarServerBizListener caviarBizListener
     * int port
     */
    public static void main(String[] args) throws Exception {
        CaviarServerBizListener caviarBizListener = new CaviarServerBizListenerDefaultImp();
        int port = 7005;
        Server server = new CaviarServer(5000L,caviarBizListener);
        server.bind(port);

        Thread.sleep(50*1000L);

        server.close();
    }
}
