package com.zhangyiwen.caviar.protocol.enu;

/**
 * Created by zhangyiwen on 2017/12/14.
 * 消息类型枚举类
 */
public enum MsgTypeEnum {

    //请求消息
    CLIENT_LOGIN_REQ(1001),                 //客户端登录
    CLIENT_LOGOUT_REQ(1002),                //客户端登出
    CLIENT_MSG_SEND_REQ(1003),              //客户端消息发送——同步
    CLIENT_MSG_SEND_ASYNC_REQ(1004),        //客户端消息发送——异步回调
    SERVER_MSG_PUSH_REQ(1005),              //服务端消息推送

    //应答消息
    CLIENT_LOGIN_RESP(2001),                //客户端登录
    CLIENT_LOGOUT_RESP(2002),               //客户端登出
    CLIENT_MSG_SEND_RESP(2003),             //客户端消息发送——同步
    CLIENT_MSG_SEND_ASYNC_RESP(2004),       //客户端消息发送——异步回调
    SERVER_MSG_PUSH_RESP(2005),             //服务端消息推送

    //心跳消息
    PING(9001),                             //客户端Ping
    PONG(9002),                             //服务端Pong
    ;

    private int code;

    MsgTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static MsgTypeEnum getByCode(int code){
        for(MsgTypeEnum msgTypeEnum:MsgTypeEnum.values()){
            if(msgTypeEnum.getCode() == code){
                return msgTypeEnum;
            }
        }
        return null;
    }
}
