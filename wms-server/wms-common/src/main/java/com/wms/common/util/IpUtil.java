package com.wms.common.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP工具类
 * 从HttpServletRequest中获取客户端真实IP地址
 */
public final class IpUtil {

    private IpUtil() {
    }

    private static final String UNKNOWN = "unknown";
    private static final int IP_MAX_LOOP = 15;

    /**
     * 获取客户端真实IP地址
     * 依次从X-Forwarded-For、X-Real-IP、Proxy-Client-IP等Header中获取
     *
     * @param request HTTP请求
     * @return 客户端IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (isValidIp(ip)) {
            // 多次反向代理后取第一个IP
            int index = ip.indexOf(',');
            if (index != -1) {
                ip = ip.substring(0, index);
            }
            return ip.trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (isValidIp(ip)) {
            return ip;
        }
        ip = request.getHeader("Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }
        ip = request.getRemoteAddr();
        return ip;
    }

    /**
     * 判断IP是否有效
     *
     * @param ip IP地址
     * @return 是否有效
     */
    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip);
    }
}
