package com.zhangyiwen.caviar.network.tansporter;

import com.zhangyiwen.caviar.biz.server.CaviarServerBizListenerImpl;
import com.zhangyiwen.caviar.network.dispatcher.EventDispatcher;
import com.zhangyiwen.caviar.network.dispatcher.ServerEventDispatcher;
import com.zhangyiwen.caviar.network.server.CaviarServerBizListener;
import com.zhangyiwen.caviar.network.server.CaviarServer;
import com.zhangyiwen.caviar.network.server.Server;
import com.zhangyiwen.caviar.protocol.CaviarDecoder;
import com.zhangyiwen.caviar.protocol.CaviarEncoder;

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
        CaviarServerBizListener caviarBizListener = new CaviarServerBizListenerImpl();
        int port = 7005;
        Server server = new CaviarServer(caviarBizListener);
        server.bind(port);
    }
}
