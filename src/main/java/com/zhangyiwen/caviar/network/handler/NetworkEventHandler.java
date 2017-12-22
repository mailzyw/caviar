package com.zhangyiwen.caviar.network.handler;

import com.zhangyiwen.caviar.network.enu.NetworkEvent;
import com.zhangyiwen.caviar.network.dispatcher.EventDispatcher;
import com.zhangyiwen.caviar.protocol.CaviarMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangyiwen on 2017/12/19.
 * 网络事件ChannelHandler
 */
@AllArgsConstructor
@ChannelHandler.Sharable
public class NetworkEventHandler extends SimpleChannelInboundHandler<CaviarMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkEventHandler.class);

    private EventDispatcher dispatcher;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CaviarMessage msg) throws Exception {
        dispatcher.dispatch(NetworkEvent.onMessage, ctx, msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        dispatcher.dispatch(NetworkEvent.onConnected, ctx, null);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        dispatcher.dispatch(NetworkEvent.onDisconnect, ctx, null);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        dispatcher.dispatch(NetworkEvent.onError, ctx, cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //心跳超时断连
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                LOGGER.debug("[userEventTriggered] idleStateEvent. remoteAdress:[{}],evt:[{}]", ctx.channel().remoteAddress(), evt);
                dispatcher.dispatch(NetworkEvent.onReadIdle, ctx, null);
            }
            if (e.state() == IdleState.WRITER_IDLE) {
                LOGGER.debug("[userEventTriggered] idleStateEvent. remoteAdress:[{}],evt:[{}]", ctx.channel().remoteAddress(), evt);
                dispatcher.dispatch(NetworkEvent.onWriteIdle, ctx, null);
            }
        }
    }
}
