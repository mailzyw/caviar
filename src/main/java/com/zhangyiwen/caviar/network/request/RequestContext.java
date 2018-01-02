package com.zhangyiwen.caviar.network.request;

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

    private CaviarMsgCallback caviarMsgCallback;    //响应回调执行器

    private volatile boolean isSync;                //是否为同步请求

    private volatile boolean respHandled;           //响应是否被处理

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

    /**
     * 设置请求上下文被接收的状态
     * @return true:可以接收/false:无法接收
     */
    public boolean markRespHandled(){
        if(respHandled){
            return false;
        }
        synchronized (this){
            if(respHandled){
                return false;
            }
            respHandled = true;
            return true;
        }
    }
}
