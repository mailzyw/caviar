package com.zhangyiwen.caviar.protocol;

import com.zhangyiwen.caviar.network.request.RequestIdGenerator;
import com.zhangyiwen.caviar.protocol.enu.CodecTypeEnum;
import com.zhangyiwen.caviar.protocol.enu.MsgTypeEnum;
import com.zhangyiwen.caviar.util.IoUtil;
import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * Created by zhangyiwen on 2017/12/14.
 * 通信框架消息协议
 */
@Data
public class CaviarMessage {

    public static final int MSG_HEAD_BYTE_SIZE = 8*2 + 4*4; //消息头长度

    public static final long DEFAULT_INVALID_ADDRESS = -1L;

    public static final int DEFAULT_CRC_CODE = 1;

    private Long            requestId;              //请求消息唯一ID
    private Long            address;                //消息发送端IP转换为的Long
    private Integer         crcCode;                //版本号
    private MsgTypeEnum     msgType;                //消息类型
    private CodecTypeEnum   codecType;              //消息体序列化方式
    private Integer         msgBodyLength;          //消息体长度
    private byte[]          msgBody;                //消息体


    /**
     * PING构造器
     */
    public static CaviarMessage PING(){
        byte[] msgBody = "ping".getBytes();
        CaviarMessage caviarMessage = new CaviarMessage();
        caviarMessage.setRequestId(RequestIdGenerator.getRequestIdGenerator().nextGeneralizedId());
        caviarMessage.setAddress(DEFAULT_INVALID_ADDRESS);
        caviarMessage.setCrcCode(DEFAULT_CRC_CODE);
        caviarMessage.setMsgType(MsgTypeEnum.PING);
        caviarMessage.setCodecType(CodecTypeEnum.JSON);
        caviarMessage.setMsgBodyLength(msgBody.length);
        caviarMessage.setMsgBody(msgBody);
        return caviarMessage;
    }

    /**
     * PONG构造器
     */
    public static CaviarMessage PONG(){
        byte[] msgBody = "pong".getBytes();
        CaviarMessage caviarMessage = new CaviarMessage();
        caviarMessage.setRequestId(RequestIdGenerator.getRequestIdGenerator().nextGeneralizedId());
        caviarMessage.setAddress(DEFAULT_INVALID_ADDRESS);
        caviarMessage.setCrcCode(DEFAULT_CRC_CODE);
        caviarMessage.setMsgType(MsgTypeEnum.PONG);
        caviarMessage.setCodecType(CodecTypeEnum.JSON);
        caviarMessage.setMsgBodyLength(msgBody.length);
        caviarMessage.setMsgBody(msgBody);
        return caviarMessage;
    }

    /**
     * CLIENT_LOGIN_REQ构造器
     */
    public static CaviarMessage CLIENT_LOGIN_REQ(byte[] msgBody){
        CaviarMessage caviarMessage = new CaviarMessage();
        caviarMessage.setRequestId(RequestIdGenerator.getRequestIdGenerator().nextGeneralizedId());
        caviarMessage.setAddress(DEFAULT_INVALID_ADDRESS);
        caviarMessage.setCrcCode(DEFAULT_CRC_CODE);
        caviarMessage.setMsgType(MsgTypeEnum.CLIENT_LOGIN_REQ);
        caviarMessage.setCodecType(CodecTypeEnum.JSON);
        caviarMessage.setMsgBodyLength(msgBody.length);
        caviarMessage.setMsgBody(msgBody);
        return caviarMessage;
    }

    /**
     * CLIENT_LOGIN_RESP构造器
     */
    public static CaviarMessage CLIENT_LOGIN_RESP(byte[] msgBody){
        CaviarMessage caviarMessage = new CaviarMessage();
        caviarMessage.setRequestId(RequestIdGenerator.getRequestIdGenerator().nextGeneralizedId());
        caviarMessage.setAddress(DEFAULT_INVALID_ADDRESS);
        caviarMessage.setCrcCode(DEFAULT_CRC_CODE);
        caviarMessage.setMsgType(MsgTypeEnum.CLIENT_LOGIN_RESP);
        caviarMessage.setCodecType(CodecTypeEnum.JSON);
        caviarMessage.setMsgBodyLength(msgBody.length);
        caviarMessage.setMsgBody(msgBody);
        return caviarMessage;
    }

    /**
     * CLIENT_LOGOUT_REQ构造器
     */
    public static CaviarMessage CLIENT_LOGOUT_REQ(byte[] msgBody){
        CaviarMessage caviarMessage = new CaviarMessage();
        caviarMessage.setRequestId(RequestIdGenerator.getRequestIdGenerator().nextGeneralizedId());
        caviarMessage.setAddress(DEFAULT_INVALID_ADDRESS);
        caviarMessage.setCrcCode(DEFAULT_CRC_CODE);
        caviarMessage.setMsgType(MsgTypeEnum.CLIENT_LOGOUT_REQ);
        caviarMessage.setCodecType(CodecTypeEnum.JSON);
        caviarMessage.setMsgBodyLength(msgBody.length);
        caviarMessage.setMsgBody(msgBody);
        return caviarMessage;
    }

    /**
     * CLIENT_LOGOUT_RESP构造器
     */
    public static CaviarMessage CLIENT_LOGOUT_RESP(byte[] msgBody){
        CaviarMessage caviarMessage = new CaviarMessage();
        caviarMessage.setRequestId(RequestIdGenerator.getRequestIdGenerator().nextGeneralizedId());
        caviarMessage.setAddress(DEFAULT_INVALID_ADDRESS);
        caviarMessage.setCrcCode(DEFAULT_CRC_CODE);
        caviarMessage.setMsgType(MsgTypeEnum.CLIENT_LOGOUT_RESP);
        caviarMessage.setCodecType(CodecTypeEnum.JSON);
        caviarMessage.setMsgBodyLength(msgBody.length);
        caviarMessage.setMsgBody(msgBody);
        return caviarMessage;
    }

    /**
     * CLIENT_MSG_SEND_REQ构造器
     */
    public static CaviarMessage CLIENT_MSG_SEND_REQ(byte[] msgBody){
        CaviarMessage caviarMessage = new CaviarMessage();
        caviarMessage.setRequestId(RequestIdGenerator.getRequestIdGenerator().nextGeneralizedId());
        caviarMessage.setAddress(DEFAULT_INVALID_ADDRESS);
        caviarMessage.setCrcCode(DEFAULT_CRC_CODE);
        caviarMessage.setMsgType(MsgTypeEnum.CLIENT_MSG_SEND_REQ);
        caviarMessage.setCodecType(CodecTypeEnum.JSON);
        caviarMessage.setMsgBodyLength(msgBody.length);
        caviarMessage.setMsgBody(msgBody);
        return caviarMessage;
    }

    /**
     * CLIENT_MSG_SEND_RESP构造器
     */
    public static CaviarMessage CLIENT_MSG_SEND_RESP(byte[] msgBody){
        CaviarMessage caviarMessage = new CaviarMessage();
        caviarMessage.setRequestId(RequestIdGenerator.getRequestIdGenerator().nextGeneralizedId());
        caviarMessage.setAddress(DEFAULT_INVALID_ADDRESS);
        caviarMessage.setCrcCode(DEFAULT_CRC_CODE);
        caviarMessage.setMsgType(MsgTypeEnum.CLIENT_MSG_SEND_RESP);
        caviarMessage.setCodecType(CodecTypeEnum.JSON);
        caviarMessage.setMsgBodyLength(msgBody.length);
        caviarMessage.setMsgBody(msgBody);
        return caviarMessage;
    }

    /**
     * CLIENT_MSG_SEND_ASYNC_REQ构造器
     */
    public static CaviarMessage CLIENT_MSG_SEND_ASYNC_REQ(byte[] msgBody){
        CaviarMessage caviarMessage = new CaviarMessage();
        caviarMessage.setRequestId(RequestIdGenerator.getRequestIdGenerator().nextGeneralizedId());
        caviarMessage.setAddress(DEFAULT_INVALID_ADDRESS);
        caviarMessage.setCrcCode(DEFAULT_CRC_CODE);
        caviarMessage.setMsgType(MsgTypeEnum.CLIENT_MSG_SEND_ASYNC_REQ);
        caviarMessage.setCodecType(CodecTypeEnum.JSON);
        caviarMessage.setMsgBodyLength(msgBody.length);
        caviarMessage.setMsgBody(msgBody);
        return caviarMessage;
    }

//    public static byte[] encode(CaviarMessage caviarMessage){
//        int msgBodyLength = caviarMessage.getMsgBody().length;
//        int msgFullLength = MSG_HEAD_BYTE_SIZE + msgBodyLength;
//        byte[] bytes = new byte[msgFullLength];
//        int pos = 0;
//        pos += IoUtil.writeLong(caviarMessage.getRequestId(), bytes, pos);
//        pos += IoUtil.writeLong(caviarMessage.getAddress(), bytes, pos);
//        pos += IoUtil.writeInt(caviarMessage.getCrcCode(), bytes, pos);
//        pos += IoUtil.writeInt(caviarMessage.getMsgType().getCode(),bytes,pos);
//        pos += IoUtil.writeInt(caviarMessage.getCodecType().getCode(),bytes,pos);
//        pos += IoUtil.writeInt(msgBodyLength,bytes,pos);
//        IoUtil.write(caviarMessage.getMsgBody(), bytes, pos);
//        return bytes;
//    }
//
//    public static CaviarMessage decode(byte[] bytes){
//        int pos = 0;
//        Long requestId = IoUtil.readLong(bytes, pos);
//        pos += 8;
//        Long address = IoUtil.readLong(bytes, pos);
//        pos += 8;
//        Integer crcCode = IoUtil.readInt(bytes, pos);
//        pos += 4;
//        Integer msgType = IoUtil.readInt(bytes, pos);
//        pos += 4;
//        Integer codecType = IoUtil.readInt(bytes, pos);
//        pos += 4;
//        Integer length = IoUtil.readInt(bytes, pos);
//        pos += 4;
//
//        CaviarMessage caviarMessage = new CaviarMessage();
//        caviarMessage.setRequestId(requestId);
//        caviarMessage.setAddress(address);
//        caviarMessage.setCrcCode(crcCode);
//        caviarMessage.setMsgType(MsgTypeEnum.getByCode(msgType));
//        caviarMessage.setCodecType(CodecTypeEnum.getByCode(codecType));
//        caviarMessage.setMsgBodyLength(length);
//        if(length>0){
//            byte[] msgBody = new byte[length];
//            IoUtil.read(bytes,pos,msgBody);
//            caviarMessage.setMsgBody(msgBody);
//        }
//        return caviarMessage;
//    }

}
