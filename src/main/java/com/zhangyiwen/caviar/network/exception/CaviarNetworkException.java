package com.zhangyiwen.caviar.network.exception;

/**
 * Created by zhangyiwen on 2017/12/20.
 * 网络异常
 */
public class CaviarNetworkException extends RuntimeException{

    public static CaviarNetworkException SERVER_START_FAIL = new CaviarNetworkException("caviar server strat failed");
    public static CaviarNetworkException CLIENT_NOT_RUNNING = new CaviarNetworkException("caviar client is not running");
    public static CaviarNetworkException CLIENT_REQ_TIMEOUT = new CaviarNetworkException("caviar client req timeoout");
    public static CaviarNetworkException CLIENT_CONNECT_FAIL = new CaviarNetworkException("caviar client connect failed");
    public static CaviarNetworkException CLIENT_CONNECT_TIMEOUT = new CaviarNetworkException("caviar client connect timeout");
    public static CaviarNetworkException SERVER_REQ_TIMEOUT = new CaviarNetworkException("caviar server req timeoout");

    public CaviarNetworkException(String message) {
        super(message);
    }
}
