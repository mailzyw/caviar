package com.zhangyiwen.caviar.network.session;

import com.zhangyiwen.caviar.network.request.RequestContext;
import com.zhangyiwen.caviar.protocol.CaviarMessage;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by zhangyiwen on 2017/12/15.
 * 连接Session上下文——Netty实现类
 */
@ToString(exclude = "channel")
public class NettySessionContext implements SessionContext{

    private static final Logger LOGGER = LoggerFactory.getLogger(NettySessionContext.class);

    public static final AttributeKey<SessionContext> session = AttributeKey.valueOf("session");

    private long index;                         //连接唯一标识,用于标识连接的对端信息(host,port,server/client)
    private final Channel channel;              //netty Channel
    private InetSocketAddress remoteAddress;        //连接的远程地址
    private InetSocketAddress localAddress;         //连接的本地地址

    public NettySessionContext(Channel channel, InetSocketAddress remoteAddress, InetSocketAddress localAddress) {
        this.remoteAddress = remoteAddress;
        this.localAddress = localAddress;
        this.channel = channel;
        channel.attr(NettySessionContext.session).set(this);
    }

    public Channel getChannel() {
        return this.channel;
    }

    public void writeAndFlush(CaviarMessage message) {
        channel.writeAndFlush(message).syncUninterruptibly().addListener(future -> {
            if (!future.isSuccess()) {
                LOGGER.error("[sessionContext] write and flush error. message:{}, sessionContext:{}, e:{}",message,session,future.cause());
                channel.close();
            }
        });
    }

    @Override
    public void response(RequestContext requestContext, CaviarMessage message) {
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
