package com.zhangyiwen.caviar.network.dispatcher;

import com.zhangyiwen.caviar.network.enu.NetworkEvent;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

/**
 * Created by zhangyiwen on 2017/12/19.
 * 网络事件派发器
 */
public interface EventDispatcher {

    void dispatch(NetworkEvent networkEvent, ChannelHandlerContext ctx, Object msg);

    void close() throws IOException;
}
