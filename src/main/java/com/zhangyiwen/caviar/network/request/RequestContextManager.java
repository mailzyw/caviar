package com.zhangyiwen.caviar.network.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by zhangyiwen on 2017/12/21.
 * RequestContext管理器
 */
public class RequestContextManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestContextManager.class);

    private volatile Map<Long, RequestContext> index2Session = new ConcurrentSkipListMap<>();

    static class InstanceHolder{
        private static RequestContextManager serverInstance=new RequestContextManager();
        private static RequestContextManager clientInstance=new RequestContextManager();
    }

    /**
     * 获取服务端请求上下文管理器
     * @return RequestContextManager
     */
    public static RequestContextManager getServerRequestContextManager(){
        return InstanceHolder.serverInstance;
    }

    /**
     * 获取客户端请求上下文管理器
     * @return RequestContextManager
     */
    public static RequestContextManager getClientRequestContextManager(){
        return InstanceHolder.clientInstance;
    }

    private RequestContextManager(){

    }

    /**
     * 绑定请求上下文
     * @param requestId 请求唯一标识
     * @param context 请求上下文
     */
    public void bindRequestContext(long requestId, RequestContext context){
        context.setRequestId(requestId);
        RequestContext result = this.index2Session.put(requestId,context);
        LOGGER.debug("[request manager] bind context succeed. mapSize:{}, requestId:{}, context:{}", index2Session.size(), requestId, context);
    }

    /**
     * 根据requestId获取请求上下文
     * @param requestId 请求唯一标识
     * @return RequestContext
     */
    public RequestContext getRequestContext(long requestId){
        RequestContext context = this.index2Session.get(requestId);
        LOGGER.debug("[request manager] get context succeed. requestId:{}, context:{}", requestId, context);
        return context;
    }

    /**
     * 根据requestId清除请求上下文
     * @param requestId 请求唯一标识
     */
    public void cleanRequestContext(long requestId){
        RequestContext context = this.index2Session.remove(requestId);
        LOGGER.debug("[request manager] clean context succeed. mapSize:{}, requestId:{}, context:{}", index2Session.size(), requestId, context);
    }

    /**
     * 销毁请求上下文
     */
    public void cleanRequestContextAll(){
        index2Session = new ConcurrentSkipListMap<>();
        LOGGER.debug("[request manager] clean context all.");
    }

}
