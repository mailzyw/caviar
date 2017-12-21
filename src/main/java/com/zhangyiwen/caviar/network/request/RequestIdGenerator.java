package com.zhangyiwen.caviar.network.request;

/**
 * Created by zhangyiwen on 2017/6/20.
 * 分布式泛化ID发号器
 * 0 - 0000000000 0000000000 0000000000 0000000000 - 00000 - 00000000 00000000 - 00000000 00
 * 空缺 - timestamp - nodeId - sequence - shardingKey
 */
public class RequestIdGenerator {

    static class InstanceHolder{
        private static RequestIdGenerator requestIdGenerator = new RequestIdGenerator();
    }

    public static RequestIdGenerator getRequestIdGenerator(){
        return InstanceHolder.requestIdGenerator;
    }

    // ==============================Fields===========================================
    /** 开始时间截 (2015-01-01) */
    private static final long twepoch = 1420041600L;

    /** 机器id所占的位数 */
    private static final long nodeIdBits = 5L;

    /** 序列在id中占的位数 */
    private static final long sequenceBits = 16L;

    /** shardingKey所占的位数 */
    private static final long shardingKeyBits = 10L;

    /** 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
    private static final long maxNodeId = ~(-1L << nodeIdBits);

    /** 生成序列的掩码，这里为65535 */
    private static final long sequenceMask = ~(-1L << sequenceBits);

    /** 时间截向左移31位(5+16+10) */
    private static final long timestampLeftShift = nodeIdBits + sequenceBits + shardingKeyBits;

    /** 数据标识id向左移26位(16+10) */
    private static final long nodeIdShift = sequenceBits + shardingKeyBits;

    /** 序列向左移10位 */
    private static final long sequenceShift = shardingKeyBits;



    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;

    /** 集群节点ID(0~31) */
    private long nodeId = 0L;

    /** 毫秒内序列(0~65535) */
    private long sequence = 0L;

    //==============================Constructors=====================================
    private RequestIdGenerator() {
    }

    // ==============================Methods==========================================
    /**
     * 获得下一个GeneralizedId (该方法是线程安全的)
     * @return GeneralizedId
     */
    public synchronized long nextGeneralizedId() {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift) //
                | (nodeId << nodeIdShift) //
                | (sequence << sequenceShift) //
                | 0L;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis()/1000;
    }

}
