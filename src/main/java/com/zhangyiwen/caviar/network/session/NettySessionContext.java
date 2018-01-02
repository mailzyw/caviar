package com.zhangyiwen.caviar.network.session;

import com.zhangyiwen.caviar.network.exception.CaviarNetworkException;
import com.zhangyiwen.caviar.network.request.CaviarMsgCallback;
import com.zhangyiwen.caviar.network.request.RequestContext;
import com.zhangyiwen.caviar.network.request.RequestContextManager;
import com.zhangyiwen.caviar.protocol.CaviarMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyiwen on 2017/12/15.
 * 连接Session上下文——Netty实现类
 */
@ToString(exclude = "channel")
public class NettySessionContext implements SessionContext{

    private static final Logger LOGGER = LoggerFactory.getLogger(NettySessionContext.class);

    public static final AttributeKey<SessionContext> session = AttributeKey.valueOf("session");

    public static ScheduledExecutorService serverCallBackTimeoutExecutor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1);     //异步超时监听线程池

    private long index;                             //连接唯一标识,用于标识连接的对端信息(host,port,server/client)
    private final Channel channel;                  //netty Channel
    private InetSocketAddress remoteAddress;        //连接的远程地址
    private InetSocketAddress localAddress;         //连接的本地地址
    private long serverRequestTimeout;              //服务端请求超时时间

    public NettySessionContext(Channel channel, InetSocketAddress remoteAddress, InetSocketAddress localAddress) {
        this.remoteAddress = remoteAddress;
        this.localAddress = localAddress;
        this.channel = channel;
        channel.attr(NettySessionContext.session).set(this);
    }

    public NettySessionContext(Channel channel, InetSocketAddress remoteAddress, InetSocketAddress localAddress, long serverRequestTimeout) {
        this.remoteAddress = remoteAddress;
        this.localAddress = localAddress;
        this.channel = channel;
        this.serverRequestTimeout = serverRequestTimeout;
        channel.attr(NettySessionContext.session).set(this);
    }

    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public void writeAndFlush(CaviarMessage message) {
        channel.writeAndFlush(message).syncUninterruptibly().addListener(future -> {
            if (!future.isSuccess()) {
                LOGGER.error("[sessionContext] write and flush error. message:{}, sessionContext:{}, e:{}",message,session,future.cause());
                channel.close();
            }
        });
    }

    @Override
    public void sendClientLoginResp(RequestContext requestContext, byte[] respMessage) {
        CaviarMessage resp = CaviarMessage.CLIENT_LOGIN_RESP(respMessage);
        response(requestContext, resp);
    }

    @Override
    public void sendClientLogoutResp(RequestContext requestContext, byte[] respMessage) {
        CaviarMessage resp = CaviarMessage.CLIENT_LOGOUT_RESP(respMessage);
        response(requestContext, resp);
    }

    @Override
    public void sendClientRequestResp(RequestContext requestContext, byte[] respMessage) {
        CaviarMessage resp = CaviarMessage.CLIENT_MSG_SEND_RESP(respMessage);
        response(requestContext, resp);
    }

    @Override
    public void sendServerRequestResp(RequestContext requestContext, byte[] respMessage) {
        CaviarMessage resp = CaviarMessage.SERVER_MSG_SEND_RESP(respMessage);
        response(requestContext, resp);
    }

    @Override
    public byte[] sendServerReq(byte[] reqMessage) throws CaviarNetworkException{
        LOGGER.info("[sessionContext] sendMsgSync start.");
        CaviarMessage caviarMessage = CaviarMessage.SERVER_MSG_SEND_REQ(reqMessage);
        long requestId = caviarMessage.getRequestId();
        RequestContext requestContext = new RequestContext(this.getIndex(),requestId,caviarMessage);
        RequestContextManager.getServerRequestContextManager().bindRequestContext(requestId,requestContext);
        this.writeAndFlush(caviarMessage);

        try {
            synchronized (requestContext){
                LOGGER.info("[sessionContext] sendMsgSync wait resp...");
                requestContext.wait(serverRequestTimeout);
            }
        } catch (InterruptedException e) {
            throw new CaviarNetworkException("[sessionContext] sendMsgSync await done interrupted.");
        }

        RequestContext requestContextResp = RequestContextManager.getServerRequestContextManager().getRequestContext(requestId);
        CaviarMessage response = requestContextResp.getResponseMessage();
        RequestContextManager.getServerRequestContextManager().cleanRequestContext(requestId);
        LOGGER.info("[sessionContext] sendMsgSync end. resp:{}",response);
        if(response == null){
            throw CaviarNetworkException.SERVER_REQ_TIMEOUT;
        }
        return response.getMsgBody();
    }

    @Override
    public void sendServerReqAsync(byte[] reqMessage, CaviarMsgCallback callback) {
        LOGGER.info("[sessionContext] sendMsgAsync start.");
        CaviarMessage caviarMessage = CaviarMessage.SERVER_MSG_SEND_ASYNC_REQ(reqMessage);

        long requestId = caviarMessage.getRequestId();
        RequestContext requestContext = new RequestContext(this.getIndex(),requestId,caviarMessage,callback);
        RequestContextManager.getServerRequestContextManager().bindRequestContext(requestId, requestContext);

        this.writeAndFlush(caviarMessage);

        serverCallBackTimeoutExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                RequestContext timeoutRequestContext = RequestContextManager.getServerRequestContextManager().getRequestContext(requestId);
                if (timeoutRequestContext != null && timeoutRequestContext.markRespHandled()) {
                    LOGGER.info("[sessionContext] sendMsgAsync timeout. requestId:{},msg:{}", requestId, reqMessage);
                    timeoutRequestContext.setRespHandled(true);
                    RequestContextManager.getServerRequestContextManager().cleanRequestContext(requestId);
                    callback.dealRequestTimeout(reqMessage);
                }
            }
        }, serverRequestTimeout, TimeUnit.MILLISECONDS);
    }

    private void response(RequestContext requestContext, CaviarMessage message) {
        message.setRequestId(requestContext.getRequestId());
        writeAndFlush(message);
    }

    public InetSocketAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    public InetSocketAddress getLocalAddress() {
        return this.localAddress;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public long getIndex() {
        return this.index;
    }

    @Override
    public void close() {
        this.channel.close();
    }
}
