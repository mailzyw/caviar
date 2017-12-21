package com.zhangyiwen.caviar.codec;

/**
 * Created by zhangyiwen on 2017/12/14.
 * 序列化工具
 */
public interface Serialization {

    /**
     * 序列化方法
     * @param obj obj
     * @return
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化方法
     * @param in in
     * @param type type
     * @param <T> T
     * @return
     */
    <T> T deserialize(byte[] in, Class<T> type);
}
