package com.zhangyiwen.caviar.network.session;

import com.zhangyiwen.caviar.network.request.RequestContext;
import com.zhangyiwen.caviar.protocol.CaviarMessage;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by zhangyiwen on 2017/12/15.
 * 连接Session上下文
 */
public interface SessionContext {

    /**
     * 获取Channel
     * @return Channel
     */
    Channel getChannel();

    /**
     * 向通道写数据
     * @param message caviarMessage
     */
    void writeAndFlush(CaviarMessage message);

    /**
     * 发送响应消息——客户端登录
     * @param requestContext 请求上下文
     * @param respMessage 响应消息
     */
    void sendClientLoginResp(RequestContext requestContext,byte[] respMessage);

    /**
     * 发送响应消息——客户端登出
     * @param requestContext 请求上下文
     * @param respMessage 响应消息
     */
    void sendClientLogoutResp(RequestContext requestContext,byte[] respMessage);

    /**
     * 发送响应消息——客户端请求消息
     * @param requestContext 请求上下文
     * @param respMessage 响应消息
     */
    void sendClientRequestResp(RequestContext requestContext,byte[] respMessage);

    /**
     * 获取连接的远程地址
     * @return SocketAddress
     */
    InetSocketAddress getRemoteAddress();

    /**
     * 获取连接的本地地址
     * @return SocketAddress
     */
    InetSocketAddress getLocalAddress();

    /**
     * 设置SessionContext对应连接唯一标识
     * @param index 连接唯一标识
     */
    void setIndex(long index);

    /**
     * 获取SessionContext对应连接唯一标识
     * @return index 连接唯一标识
     */
    long getIndex();

    /**
     * 清除SessionContext,释放连接资源
     */
    void close();

}
