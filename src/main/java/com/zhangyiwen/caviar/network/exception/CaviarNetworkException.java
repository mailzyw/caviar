package com.zhangyiwen.caviar.network.exception;

/**
 * Created by zhangyiwen on 2017/12/20.
 * 网络异常
 */
public class CaviarNetworkException extends RuntimeException{

    public static CaviarNetworkException CLIENT_NOT_RUNNING = new CaviarNetworkException("caviar client is not running");
    public static CaviarNetworkException CLIENT_EXEC_TIMEOUT = new CaviarNetworkException("caviar client exec timeoout");
    public static CaviarNetworkException CLIENT_CONNECT_FAIL = new CaviarNetworkException("caviar client connect failed");
    public static CaviarNetworkException CLIENT_CONNECT_TIMEOUT = new CaviarNetworkException("caviar client connect timeout");

    public CaviarNetworkException(String message) {
        super(message);
    }
}
