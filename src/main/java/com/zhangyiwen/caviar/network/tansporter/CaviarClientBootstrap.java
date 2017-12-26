package com.zhangyiwen.caviar.network.tansporter;

import com.zhangyiwen.caviar.network.client.CaviarClient;
import com.zhangyiwen.caviar.network.client.CaviarMsgCallback;
import com.zhangyiwen.caviar.network.client.Client;
import com.zhangyiwen.caviar.network.exception.CaviarNetworkException;
import com.zhangyiwen.caviar.protocol.CaviarMessage;
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
        String host = "127.0.0.1";
        int port = 7005;

        //connect
        Client client = new CaviarClient(5*1000L);
        try {
            client.connect(host, port);
        } catch (CaviarNetworkException e) {
            e.printStackTrace();
            client.close();
        }

        //login
        byte[] loginResp = client.login("login test".getBytes());
        LOGGER.info("==========client login end.=========  loginResp:{}",String.valueOf(loginResp));

        //sendMsgSync
        byte[] sendSyncResp = client.sendMsgSync("send msg sync test".getBytes());
        LOGGER.info("==========client send sync end.==========  sendSyncResp:{}",String.valueOf(sendSyncResp));

        //sendMsgAsync
        for(int i=0;i<3;i++){
            client.sendMsgAsync(("send msg async test" + i).getBytes(), new CaviarMsgCallback() {
                @Override
                public void dealMsgCallback(CaviarMessage msg) {
                    LOGGER.info("[MsgCallback] get resp. msg:{}", msg);
                }
            });
        }
        LOGGER.info("==========client send async end.==========");

        //logout
        byte[] logoutResp = client.logout("logout test".getBytes());
        LOGGER.info("==========client logout end.==========  logoutResp:{}",String.valueOf(logoutResp));
    }
}
