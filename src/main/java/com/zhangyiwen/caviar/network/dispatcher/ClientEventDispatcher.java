package com.zhangyiwen.caviar.network.dispatcher;

import com.zhangyiwen.caviar.network.client.CaviarClientBizListener;
import com.zhangyiwen.caviar.network.client.Client;
import com.zhangyiwen.caviar.network.enu.NetworkEvent;
import com.zhangyiwen.caviar.network.request.RequestContext;
import com.zhangyiwen.caviar.network.request.RequestContextManager;
import com.zhangyiwen.caviar.network.session.NettySessionContext;
import com.zhangyiwen.caviar.network.session.SessionContext;
import com.zhangyiwen.caviar.network.session.SessionManagerFactory;
import com.zhangyiwen.caviar.protocol.CaviarMessage;
import com.zhangyiwen.caviar.protocol.enu.MsgTypeEnum;
import com.zhangyiwen.caviar.util.NetworkUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyiwen on 2017/12/15.
 * 网络事件派发器——客户端
 */
public class ClientEventDispatcher implements EventDispatcher{

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientEventDispatcher.class);

    private static final ExecutorService executor =
            Executors.newFixedThreadPool(128, new BasicThreadFactory.Builder().namingPattern("server-event-dispatcher-%d").build());

    private CaviarClientBizListener caviarBizListener;      //消息监听器,业务方实现

    private Client client;                                  //网络客户端

    public ClientEventDispatcher(CaviarClientBizListener caviarBizListener, Client client) {
        this.caviarBizListener = caviarBizListener;
        this.client = client;
    }

    /**
     * 网络事件分发
     * @param networkEvent 事件类型
     * @param ctx 连接上下文
     * @param msg 消息体
     */
    public void dispatch(NetworkEvent networkEvent, ChannelHandlerContext ctx, Object msg){

        try {
            switch (networkEvent){
                case onConnected:
                    dealWithConnected(networkEvent, ctx);
                    break;
                case onDisconnect:
                    dealWithDisconnected(networkEvent, ctx);
                    break;
                case onError:
                    dealWithError(networkEvent, ctx, (Throwable)msg);
                    break;
                case onWriteIdle:
                    dealWithWriteIdle(networkEvent, ctx);
                    break;
                case onMessage:
                    executor.execute(()->{
                        dealWithMessage(networkEvent, ctx, (CaviarMessage)msg);
                    });
            }
        } catch (Exception e) {
            LOGGER.error("server dispatch network event error.",e);
            this.dealWithError(NetworkEvent.onError,ctx,e);
        }
    }

    /**
     * 关闭方法
     * @throws IOException
     */
    public void close() throws IOException {
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
            LOGGER.info("server dispatch close. succeed.");
        } catch (InterruptedException e) {
            LOGGER.error("server dispatch close. error.", e);
        }
    }

    private Long getSessionIndex(Channel channel){
        InetSocketAddress localAddress = (InetSocketAddress)channel.localAddress();
        Long index = NetworkUtil.InetSocketAddress2Long(localAddress);
        return index;
    }

    /**
     * 网络建连事件处理
     * 1 生成SessionContext
     * 2 绑定SessionContext到当前Channel
     * 3 调用SessionManager绑定连接与服务端映射关系
     */
    private void dealWithConnected(NetworkEvent networkEvent, ChannelHandlerContext ctx){
        LOGGER.info("[EventDispatcher] dealWithConnected");
        Channel channel = ctx.channel();
        SessionContext session = new NettySessionContext(channel, (InetSocketAddress)ctx.channel().remoteAddress(), (InetSocketAddress)ctx.channel().localAddress());
        Long index = getSessionIndex(ctx.channel());
        SessionManagerFactory.getClientSessionMananger().bindSessionContext(index,session);
        synchronized (client){
            client.setRunningState(true);
            client.notifyAll();
        }
    }

    /**
     * 网络断连事件处理
     * 1 清除连接Session
     * 2 客户端断连重连
     */
    private void dealWithDisconnected(NetworkEvent networkEvent, ChannelHandlerContext ctx) {
        LOGGER.info("[EventDispatcher] dealWithDisconnected");
        destroySession(ctx);
        reconnect(ctx);
    }

    /**
     * 异常事件处理
     * 1 清除连接Session
     * 2 客户端断连重连
     */
    private void dealWithError(NetworkEvent networkEvent, ChannelHandlerContext ctx, Throwable e){
        LOGGER.info("[EventDispatcher] dealWithError. e:{}", e);
        destroySession(ctx);
        reconnect(ctx);
    }

    /**
     * 客户端写心跳触发事件处理
     * 1 发送Ping消息给服务端
     */
    private void dealWithWriteIdle(NetworkEvent networkEvent, ChannelHandlerContext ctx) {
        LOGGER.info("[EventDispatcher] dealWithWriteIdle");
        SessionContext sessionContext = ctx.channel().attr(NettySessionContext.session).get();
        sendPing(sessionContext);
    }

    /**
     * 消息事件处理
     */
    private void dealWithMessage(NetworkEvent networkEvent, ChannelHandlerContext ctx, CaviarMessage msg) {
        SessionContext sessionContext = ctx.channel().attr(NettySessionContext.session).get();

        if(msg.getMsgType().equals(MsgTypeEnum.CLIENT_LOGIN_RESP)){
            setRequestContextAndNotify(msg);
        }
        if(msg.getMsgType().equals(MsgTypeEnum.CLIENT_MSG_SEND_RESP)){
            setRequestContextAndNotify(msg);
        }
        if(msg.getMsgType().equals(MsgTypeEnum.CLIENT_LOGOUT_RESP)){
            setRequestContextAndNotify(msg);
        }
        if(msg.getMsgType().equals(MsgTypeEnum.PONG)){
            LOGGER.info("[PONG] received pong msg:{}", String.valueOf(msg));
        }
    }

    /**
     * 网络异常事件,销毁连接
     * 1 调用SessionManager解除连接与客户端映射关系
     * 2 清理SessionContext
     * 3 销毁RequestContextMap
     * 3 断连Client
     */
    private void destroySession(ChannelHandlerContext ctx){
        SessionContext session = ctx.channel().attr(NettySessionContext.session).get();
        long sessionIndex = session.getIndex();
        SessionManagerFactory.getClientSessionMananger().cleanSessionContext(sessionIndex);

        RequestContextManager.getClientRequestContextManager().cleanRequestContextAll();

        this.client.disconnect();
    }

    /**
     * 客户端断连重连
     */
    private void reconnect(ChannelHandlerContext ctx){
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                client.reconnect();
            }
        }, 2, TimeUnit.SECONDS);
    }

    /**
     * 发送Ping包
     * @param sessionContext sessionContext
     */
    private void sendPing(SessionContext sessionContext){
        sessionContext.writeAndFlush(CaviarMessage.PING());
        LOGGER.debug("[EventDispatcher] send Ping end.");
    }

    private void setRequestContextAndNotify(CaviarMessage msg){
        long requestId = msg.getRequestId();
        RequestContext requestContext = RequestContextManager.getClientRequestContextManager().getRequestContext(requestId);
        requestContext.setResponseMessage(msg);
        synchronized (requestContext){
            requestContext.notifyAll();
        }
    }

}
