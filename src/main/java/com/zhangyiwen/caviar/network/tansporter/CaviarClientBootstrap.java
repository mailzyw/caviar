package com.zhangyiwen.caviar.network.tansporter;

import com.zhangyiwen.caviar.biz.client.CaviarClientBizListenerImpl;
import com.zhangyiwen.caviar.network.client.CaviarClient;
import com.zhangyiwen.caviar.network.client.CaviarClientBizListener;
import com.zhangyiwen.caviar.network.client.Client;
import com.zhangyiwen.caviar.network.exception.CaviarNetworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyiwen on 2017/12/20.
 * Caviar客户端启动类
 */
public class CaviarClientBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaviarClientBootstrap.class);

    /**
     * 业务方的入参
     * CaviarServerBizListener caviarBizListener
     * int port
     */
    public static void main(String[] args) throws Exception {
        //prepare
        CaviarClientBizListener caviarBizListener = new CaviarClientBizListenerImpl();
        String host = "127.0.0.1";
        int port = 7005;

        //connect
        Client client = new CaviarClient(caviarBizListener,10*1000L);
        try {
            client.connect(host, port);
        } catch (CaviarNetworkException e) {
            e.printStackTrace();
            client.close();
        }

        //login
        client.login("login test".getBytes());
        LOGGER.info("client login end.");

        //logout
        client.logout("logout test".getBytes());
        LOGGER.info("client logout end.");
    }
}
