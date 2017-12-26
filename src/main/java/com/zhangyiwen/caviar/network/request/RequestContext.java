package com.zhangyiwen.caviar.network.request;

import com.zhangyiwen.caviar.network.client.CaviarMsgCallback;
import com.zhangyiwen.caviar.protocol.CaviarMessage;
import lombok.Data;

/**
 * Created by zhangyiwen on 2017/12/21.
 * 请求上下文
 */
@Data
public class RequestContext {

    private long sessionIndex;                      //session唯一标识

    private long requestId;                         //请求唯一标识

    private CaviarMessage requestMessage;           //请求消息

    private CaviarMessage responseMessage;          //响应消息

    private CaviarMsgCallback caviarMsgCallback;    //请求响应回调

    private boolean isSync;                         //是否为同步请求

    public RequestContext(long sessionIndex,long requestId,CaviarMessage requestMessage){
        this.sessionIndex = sessionIndex;
        this.requestId = requestId;
        this.requestMessage = requestMessage;
        this.isSync = true;
    }

    public RequestContext(long sessionIndex,long requestId,CaviarMessage requestMessage,CaviarMsgCallback caviarMsgCallback){
        this.sessionIndex = sessionIndex;
        this.requestId = requestId;
        this.requestMessage = requestMessage;
        this.caviarMsgCallback = caviarMsgCallback;
        this.isSync = false;
    }

}
