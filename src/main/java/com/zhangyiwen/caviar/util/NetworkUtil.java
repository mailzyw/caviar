package com.zhangyiwen.caviar.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UncheckedIOException;
import java.net.*;
import java.util.Enumeration;

/**
 * Created by zhangyiwen on 2017/12/15.
 * 网络地址转换工具类
 */
public class NetworkUtil {

    private static final Logger logger = LoggerFactory.getLogger(NetworkUtil.class);

    private static final String MAC_NETWORK_INTERFACE_NAME = "en0";

    private static final String LINUX_NETWORK_INTERFACE_NAME = "eth0";

    public static final long networkMask = 0xffffffffffffL;

    /**
     * 获取本机Ip
     * @return String
     */
    public static String getLocalHostIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if ((networkInterface.getName().equals(LINUX_NETWORK_INTERFACE_NAME)
                         || networkInterface.getName().equals(MAC_NETWORK_INTERFACE_NAME))
                        && address instanceof Inet4Address && !address.isLoopbackAddress()
                        && !address.isLinkLocalAddress() && address.getHostAddress().indexOf(":") == -1) {
                        return address.getHostAddress();
                    }
                }
            }
            String localIp = InetAddress.getLocalHost().getHostAddress();
            if ("127.0.0.1".equals(localIp) || StringUtils.isEmpty(localIp)) {
                logger.error("can't get real ip!");
            }
            return localIp;
        } catch (SocketException | UnknownHostException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * InetSocketAddress --> Long
     * @param address InetSocketAddress
     * @return  Long
     */
    public static long InetSocketAddress2Long(InetSocketAddress address) {
        int port = address.getPort();
        if (port > (-1L ^ (-1L << 16))) {
            throw new RuntimeException("the port must between 0 and 65535");
        }
        byte[] bytes = address.getAddress().getAddress();
        logger.debug(Long.toBinaryString((long) bytes[0] & 0xff));
        logger.debug(Long.toBinaryString((long) bytes[1] & 0xff));
        logger.debug(Long.toBinaryString((long) (bytes[2] & 0xff)));
        logger.debug(Long.toBinaryString((long) bytes[3] & 0xff));
        logger.debug(Long.toBinaryString((port & 0xffff)));
        logger.debug(Long.toBinaryString(
                  (((long) (bytes[0] & 0xff)) << 40) | (((long) (bytes[1] & 0xff)) << 32) | (((long) (bytes[2] & 0xff))
                                                                                             << 24) | (
                            ((long) (bytes[3] & 0xff)) << 16)));
        return (((long) (bytes[0] & 0xff)) << 40) | (((long) (bytes[1] & 0xff)) << 32) | (((long) (bytes[2] & 0xff))
                                                                                          << 24) | (
                         ((long) (bytes[3] & 0xff)) << 16) | (port & 0xffff);
    }

    /**
     * Long --> InetSocketAddress
     * @param address InetSocketAddress
     * @return Long
     */
    public static InetSocketAddress Long2InetSocketAddress(long address) {
        long port = address & 0xffff;
        byte[] ipBytes = new byte[4];
        ipBytes[0] = (byte) (address >> 40);
        ipBytes[1] = (byte) (address >> 32);
        ipBytes[2] = (byte) (address >> 24);
        ipBytes[3] = (byte) (address >> 16);
        return new InetSocketAddress(numericToTextFormat(ipBytes), (int) port);
    }

    public static String numericToTextFormat(byte[] src) {
        return (src[0] & 0xff) + "." + (src[1] & 0xff) + "." + (src[2] & 0xff) + "." + (src[3] & 0xff);
    }
}
