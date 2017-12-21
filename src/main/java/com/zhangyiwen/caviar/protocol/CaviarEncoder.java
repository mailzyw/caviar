package com.zhangyiwen.caviar.protocol;

import com.zhangyiwen.caviar.util.IoUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


/**
 * Created by zhangyiwen on 2017/12/19.
 * Caviar编码器
 */
@ChannelHandler.Sharable
public class CaviarEncoder extends MessageToByteEncoder<CaviarMessage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, CaviarMessage caviarMessage, ByteBuf byteBuf) throws Exception {
        byte[] encodeBytes = encode(caviarMessage);
        byteBuf.writeBytes(encodeBytes);
    }

    private byte[] encode(CaviarMessage caviarMessage){
        int msgBodyLength = caviarMessage.getMsgBody().length;
        int msgFullLength = CaviarMessage.MSG_HEAD_BYTE_SIZE + msgBodyLength;
        byte[] bytes = new byte[msgFullLength];
        int pos = 0;
        pos += IoUtil.writeLong(caviarMessage.getRequestId(), bytes, pos);
        pos += IoUtil.writeLong(caviarMessage.getAddress(), bytes, pos);
        pos += IoUtil.writeInt(caviarMessage.getCrcCode(), bytes, pos);
        pos += IoUtil.writeInt(caviarMessage.getMsgType().getCode(),bytes,pos);
        pos += IoUtil.writeInt(caviarMessage.getCodecType().getCode(),bytes,pos);
        pos += IoUtil.writeInt(msgBodyLength,bytes,pos);
        IoUtil.write(caviarMessage.getMsgBody(), bytes, pos);
        return bytes;
    }
}
