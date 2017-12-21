package com.zhangyiwen.caviar.protocol;

import com.zhangyiwen.caviar.protocol.enu.CodecTypeEnum;
import com.zhangyiwen.caviar.protocol.enu.MsgTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Created by zhangyiwen on 2017/12/19.
 * Caviar解码器
 */
public class CaviarDecoder extends LengthFieldBasedFrameDecoder {

    private static final int MAX_FRAME_LENGTH           = 1024 * 1024;
    private static final int LENGTH_FIELD_LENGTH        = 4;
    private static final int LENGTH_FIELD_OFFSET        = 8*2 + 4*3;
    private static final int LENGTH_ADJUSTMENT          = 0;
    private static final int INITIAL_BYTES_TO_STRIP     = 0;

    public CaviarDecoder() {
        super(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH, LENGTH_ADJUSTMENT, INITIAL_BYTES_TO_STRIP);
    }

    public CaviarMessage decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        try {
            CaviarMessage msg = frame == null ? null : decode(frame);
            return msg;
        } finally {
            if (frame != null) {
                frame.release();
            }
        }
    }

    private CaviarMessage decode(ByteBuf in){
        Long requestId = in.readLong();
        Long address = in.readLong();
        Integer crcCode = in.readInt();
        Integer msgType = in.readInt();
        Integer codecType = in.readInt();
        Integer length = in.readInt();

        CaviarMessage caviarMessage = new CaviarMessage();
        caviarMessage.setRequestId(requestId);
        caviarMessage.setAddress(address);
        caviarMessage.setCrcCode(crcCode);
        caviarMessage.setMsgType(MsgTypeEnum.getByCode(msgType));
        caviarMessage.setCodecType(CodecTypeEnum.getByCode(codecType));
        caviarMessage.setMsgBodyLength(length);
        if(length>0){
            byte[] msgBody = new byte[length];
            in.readBytes(msgBody,0,length);
            caviarMessage.setMsgBody(msgBody);
        }
        return caviarMessage;
    }

}
