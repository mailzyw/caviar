package com.zhangyiwen.caviar.network.dispatcher;

import com.zhangyiwen.caviar.network.enu.NetworkEvent;
import com.zhangyiwen.caviar.network.request.CaviarMsgCallback;
import com.zhangyiwen.caviar.network.request.RequestContext;
import com.zhangyiwen.caviar.network.request.RequestContextManager;
import com.zhangyiwen.caviar.network.server.CaviarServerBizListener;
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
 * 网络事件派发器——服务端
 */
public class ServerEventDispatcher implements EventDispatcher{

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerEventDispatcher.class);

    private static final ExecutorService executor =
            Executors.newFixedThreadPool(128, new BasicThreadFactory.Builder().namingPattern("server-event-dispatcher-%d").build());

    private CaviarServerBizListener caviarBizListener;

    private long requestTimeout; //请求超时时间

    public ServerEventDispatcher(CaviarServerBizListener caviarBizListener, long requestTimeout) {
        this.caviarBizListener = caviarBizListener;
        this.requestTimeout = requestTimeout;
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
                case onReadIdle:
                    dealWithReadIdle(networkEvent, ctx);
                    break;
                case onMessage:
                    executor.execute(()->{
                        dealWithMessage(networkEvent, ctx, (CaviarMessage)msg);
                    });
            }
        } catch (Exception e) {
            LOGGER.error("[EventDispatcher] network event error.",e);
            this.dealWithError(NetworkEvent.onError, ctx, e);
        }
    }

    /**
     * 关闭方法
     * @throws IOException
     */
    public void close() throws IOException {
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
            executor.shutdown();
            LOGGER.info("[EventDispatcher] close. succeed.");
        } catch (InterruptedException e) {
            LOGGER.error("[EventDispatcher] close. error.", e);
        }
    }

    private Long getSessionIndex(Channel channel){
        InetSocketAddress remoteAddress = (InetSocketAddress)channel.remoteAddress();
        Long index = NetworkUtil.InetSocketAddress2Long(remoteAddress);
        return index;
    }

    /**
     * 网络建连事件处理
     * 1 生成SessionContext
     * 2 绑定SessionContext到当前Channel
     * 3 调用SessionManager绑定连接与客户端映射关系
     */
    private void dealWithConnected(NetworkEvent networkEvent, ChannelHandlerContext ctx){
        LOGGER.info("[EventDispatcher] dealWithConnected...");
        Channel channel = ctx.channel();
        SessionContext session = new NettySessionContext(channel, (InetSocketAddress)ctx.channel().remoteAddress(), (InetSocketAddress)ctx.channel().localAddress(),requestTimeout);
        Long index = getSessionIndex(ctx.channel());
        SessionManagerFactory.getServerSessionMananger().bindSessionContext(index, session);
    }

    /**
     * 网络断连事件处理
     */
    private void dealWithDisconnected(NetworkEvent networkEvent, ChannelHandlerContext ctx){
        LOGGER.info("[EventDispatcher] dealWithDisconnected...");
        destroySession(ctx);
    }

    /**
     * 异常事件处理
     */
    private void dealWithError(NetworkEvent networkEvent, ChannelHandlerContext ctx, Throwable e){
        LOGGER.info("[EventDispatcher] dealWithError. e:{}",e);
        destroySession(ctx);
    }

    /**
     * 客户端心跳超时事件处理
     */
    private void dealWithReadIdle(NetworkEvent networkEvent, ChannelHandlerContext ctx) {
        LOGGER.info("[EventDispatcher] dealWithReadIdle");
        destroySession(ctx);
    }

    /**
     * 消息事件处理
     */
    private void dealWithMessage(NetworkEvent networkEvent, ChannelHandlerContext ctx, CaviarMessage msg) {
        long index = getSessionIndex(ctx.channel());
        SessionContext sessionContext = SessionManagerFactory.getServerSessionMananger().getSessionContext(index);

        //=====process request=====
        if(msg.getMsgType().equals(MsgTypeEnum.CLIENT_LOGIN_REQ)){
            LOGGER.info("[receive msg] CLIENT_LOGIN_REQ---> msg:{}", msg);
            RequestContext requestContext = new RequestContext(index,msg.getRequestId(),msg);
            caviarBizListener.processClientLogin(requestContext, sessionContext, msg.getMsgBody());
        }
        if(msg.getMsgType().equals(MsgTypeEnum.CLIENT_MSG_SEND_REQ)){
            LOGGER.info("[receive msg] CLIENT_MSG_SEND_REQ---> msg:{}", msg);
            RequestContext requestContext = new RequestContext(index,msg.getRequestId(),msg);
            caviarBizListener.processClientMsg(requestContext, sessionContext, msg.getMsgBody());
        }
        if(msg.getMsgType().equals(MsgTypeEnum.CLIENT_MSG_SEND_ASYNC_REQ)){
            LOGGER.info("[receive msg] CLIENT_MSG_SEND_ASYNC_REQ---> msg:{}", msg);
            RequestContext requestContext = new RequestContext(index,msg.getRequestId(),msg);
            caviarBizListener.processClientMsg(requestContext, sessionContext, msg.getMsgBody());
        }
        if(msg.getMsgType().equals(MsgTypeEnum.CLIENT_LOGOUT_REQ)){
            LOGGER.info("[receive msg] CLIENT_LOGOUT_REQ---> msg:{}", msg);
            RequestContext requestContext = new RequestContext(index,msg.getRequestId(),msg);
            caviarBizListener.processClientLogout(requestContext, sessionContext, msg.getMsgBody());
        }
        //=====process heartbeat=====
        if(msg.getMsgType().equals(MsgTypeEnum.PING)){
            LOGGER.info("[receive msg] PING---> msg:{}", msg);
            sendPong(sessionContext);
        }
        //=====process response=====
        if(msg.getMsgType().equals(MsgTypeEnum.SERVER_MSG_SEND_RESP)){
            LOGGER.info("[receive msg] SERVER_MSG_SEND_RESP---> msg:{}", msg);
            long requestId = msg.getRequestId();
            RequestContext requestContext = RequestContextManager.getServerRequestContextManager().getRequestContext(requestId);
            if(requestContext == null || (!requestContext.markRespHandled())){
                LOGGER.warn("[receive msg] SERVER_MSG_SEND_RESP---> requestContext has bean handled. requestId:{}, requestContext:{}",requestId, requestContext);
                return;
            }
            if(requestContext.isSync()){
                setRequestContextAndNotify(msg);
            }
            else {
                dealCallbackMsg(msg);
            }
        }
    }

    private void setRequestContextAndNotify(CaviarMessage msg){
        long requestId = msg.getRequestId();
        RequestContext requestContext = RequestContextManager.getServerRequestContextManager().getRequestContext(requestId);
        synchronized (requestContext){
            requestContext.setResponseMessage(msg);
            requestContext.notifyAll();
        }
    }

    private void dealCallbackMsg(CaviarMessage msg){
        long requestId = msg.getRequestId();
        RequestContext requestContext = RequestContextManager.getServerRequestContextManager().getRequestContext(requestId);
        requestContext.setResponseMessage(msg);
        CaviarMsgCallback msgCallback = requestContext.getCaviarMsgCallback();
        RequestContextManager.getServerRequestContextManager().cleanRequestContext(requestId);
        msgCallback.dealRequestCallback(msg.getMsgBody());
    }

    /**
     * 网络异常事件,销毁连接
     * 1 调用SessionManager解除连接与客户端映射关系
     * 2 清理SessionContext,关闭Channel
     * 3 关闭ChannelHandlerContext
     */
    private void destroySession(ChannelHandlerContext ctx){
        SessionContext session = ctx.channel().attr(NettySessionContext.session).get();
        long sessionIndex = session.getIndex();
        SessionManagerFactory.getServerSessionMananger().cleanSessionContext(sessionIndex);
        ctx.close();
    }

    /**
     * 发送Pong包
     * @param sessionContext sessionContext
     */
    private void sendPong(SessionContext sessionContext){
        sessionContext.writeAndFlush(CaviarMessage.PONG());
    }
}
