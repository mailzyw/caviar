package com.zhangyiwen.caviar.network.server;

import com.zhangyiwen.caviar.network.dispatcher.EventDispatcher;
import com.zhangyiwen.caviar.network.dispatcher.ServerEventDispatcher;
import com.zhangyiwen.caviar.network.exception.CaviarNetworkException;
import com.zhangyiwen.caviar.network.handler.NetworkEventHandler;
import com.zhangyiwen.caviar.network.session.NettySessionContext;
import com.zhangyiwen.caviar.protocol.CaviarDecoder;
import com.zhangyiwen.caviar.protocol.CaviarEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyiwen on 2017/12/19.
 * 网络服务端
 */
@Data
public class CaviarServer implements Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaviarServer.class);

    private static final String WORKER_THREAD_NAME_PATTERN = "io-worker-%d";

    private static final String BOSS_THREAD_NAME_PATTERN = "io-boss-%d";

    private static final int READ_WAIT_SECONDS = 10;     // 设置10秒检测chanel是否接受过数据

    private EventLoopGroup boss =
            new NioEventLoopGroup(1, new BasicThreadFactory.Builder().namingPattern(BOSS_THREAD_NAME_PATTERN).build());

    private EventLoopGroup worker =
            new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() + 1, new BasicThreadFactory.Builder().namingPattern(WORKER_THREAD_NAME_PATTERN).build());

    private EventDispatcher eventDispatcher;

    private ChannelHandler eventHandler;

    private ServerBootstrap bootstrap;

    private Channel channel;

    private int port;

    public CaviarServer(long timeout, CaviarServerBizListener caviarBizListener) {
        this.eventDispatcher = new ServerEventDispatcher(caviarBizListener,timeout);
        this.eventHandler = new NetworkEventHandler(eventDispatcher);
    }

    @Override
    public void bind(int port) throws CaviarNetworkException {
        if (port < 5000 || eventHandler == null) {
            throw new RuntimeException("unexpected input argument error port :" + port + " biz is null:" + (null == eventHandler));
        }
        try {
            this.port = port;
            this.bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast("readIdlstate", new IdleStateHandler(READ_WAIT_SECONDS, 0, 0, TimeUnit.SECONDS));
                    p.addLast("connector-encoder", new CaviarEncoder());
                    p.addLast("connector-decoder", new CaviarDecoder());
                    p.addLast("event-handler", eventHandler);
                }
            }).option(ChannelOption.SO_BACKLOG, 4096).childOption(ChannelOption.SO_KEEPALIVE, false);
            LOGGER.info("[CaviarServer] bind start...");
            this.channel = bootstrap.bind(this.port).syncUninterruptibly().channel();
//            this.channel.closeFuture().syncUninterruptibly();
            LOGGER.info("[CaviarServer] bind succeed.");
        } catch (Exception e) {
            LOGGER.error("[CaviarServer] bind exception.", e);
            throw CaviarNetworkException.SERVER_START_FAIL;
        }
    }

    @Override
    public void close() throws IOException {
        LOGGER.info("[CaviarServer] close start...");
        if (channel != null) {
            channel.close().awaitUninterruptibly();
        }
        boss.shutdownGracefully().syncUninterruptibly();
        worker.shutdownGracefully().syncUninterruptibly();
        this.eventDispatcher.close();
        //
        try {
            NettySessionContext.serverCallBackTimeoutExecutor.awaitTermination(5, TimeUnit.SECONDS);
            NettySessionContext.serverCallBackTimeoutExecutor.shutdown();
        } catch (InterruptedException e) {
            LOGGER.error("[CaviarClient] close. error.", e);
        }
        LOGGER.info("[CaviarServer] close end.");
    }

}
