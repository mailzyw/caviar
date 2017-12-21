package com.zhangyiwen.caviar.network.client;

import com.zhangyiwen.caviar.network.dispatcher.ClientEventDispatcher;
import com.zhangyiwen.caviar.network.dispatcher.EventDispatcher;
import com.zhangyiwen.caviar.network.exception.CaviarNetworkException;
import com.zhangyiwen.caviar.network.handler.NetworkEventHandler;
import com.zhangyiwen.caviar.network.session.NettySessionContext;
import com.zhangyiwen.caviar.network.session.SessionContext;
import com.zhangyiwen.caviar.protocol.CaviarDecoder;
import com.zhangyiwen.caviar.protocol.CaviarEncoder;
import com.zhangyiwen.caviar.protocol.CaviarMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by zhangyiwen on 2017/12/20.
 * 网络客户端
 */
@Data
public class CaviarClient implements Client{

    private static final Logger LOGGER = LoggerFactory.getLogger(CaviarClient.class);

    private static final String WORKER_THREAD_NAME_PATTERN = "io-worker-%d";

    private EventLoopGroup worker =
            new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() + 1, new BasicThreadFactory.Builder().namingPattern(WORKER_THREAD_NAME_PATTERN).build());

    private EventDispatcher eventDispatcher;

    private ChannelHandler eventHandler;

    private Channel channel;

    private String host;

    private int port;

    public CaviarClient(CaviarClientBizListener caviarBizListener) {
        this.eventDispatcher = new ClientEventDispatcher(caviarBizListener);
        this.eventHandler = new NetworkEventHandler(eventDispatcher);
    }

    /**
     * 获取sessionContext
     * @return
     */
    private SessionContext getSessionContext(){
        return this.channel.attr(NettySessionContext.session).get();
    }

    @Override
    public void connect(String host, int port) {
        this.host = host;
        this.port = port;

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker);
        bootstrap.channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast("connector-encoder", new CaviarEncoder());
                p.addLast("connector-decoder", new CaviarDecoder());
                p.addLast("msg-handler", eventHandler);
            }
        });
        LOGGER.info("[CaviarClient] connect start...");
        this.channel = bootstrap.connect(host,port).syncUninterruptibly().channel();
        LOGGER.info("[CaviarClient] connect end.");
    }

    @Override
    public byte[] login(byte[] loginMsg) throws CaviarNetworkException{
        LOGGER.info("[CaviarClient] login start.");
        CaviarMessage caviarMessage = CaviarMessage.CLIENT_LOGIN_REQ(loginMsg);
        SessionContext sessionContext = getSessionContext();
        sessionContext.writeAndFlush(caviarMessage);
        try {
            synchronized (sessionContext){
                LOGGER.info("[CaviarClient] login wait resp...");
                sessionContext.wait();
            }
        } catch (InterruptedException e) {
            throw new CaviarNetworkException("login await done interrupted.");
        }
        LOGGER.info("[CaviarClient] login succeed.");
        return null;
    }

    @Override
    public byte[] logout(byte[] logoutMsg) throws CaviarNetworkException{
        LOGGER.info("[CaviarClient] logout start.");
        CaviarMessage caviarMessage = CaviarMessage.CLIENT_LOGOUT_REQ(logoutMsg);
        SessionContext sessionContext = getSessionContext();
        sessionContext.writeAndFlush(caviarMessage);
        try {
            synchronized (sessionContext){
                LOGGER.info("[CaviarClient] logout wait resp...");
                sessionContext.wait();
            }
        } catch (InterruptedException e) {
            throw new CaviarNetworkException("logout await done interrupted.");
        }
        LOGGER.info("[CaviarClient] logout succeed.");
        return null;
    }

    @Override
    public CaviarMessage sendMsgSync(byte[] msg) throws CaviarNetworkException {
        return null;
    }

    @Override
    public void sendMsgAsync(byte[] msg, CaviarMsgCallback caviarMsgCallback) throws CaviarNetworkException {

    }

    @Override
    public void close() throws IOException {
        LOGGER.info("[CaviarClient] close start...");
        if (channel != null) {
            channel.close().awaitUninterruptibly();
        }
        worker.shutdownGracefully();
        this.eventDispatcher.close();
        LOGGER.info("[CaviarClient] close end.");
    }
}
