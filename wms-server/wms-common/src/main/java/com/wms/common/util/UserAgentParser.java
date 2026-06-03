package com.wms.common.util;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User-Agent 轻量解析工具
 * 提取浏览器与操作系统摘要，避免将整段 UA 直接写入日志字段
 */
@Component
public class UserAgentParser {

    private static final String UNKNOWN = "Unknown";
    /** UA 截取最大长度（写入日志/指纹时防爆） */
    private static final int TRUNCATE_MAX_LENGTH = 200;
    private static final Pattern EDGE_PATTERN = Pattern.compile("Edg/(\\d+)");
    private static final Pattern OPERA_PATTERN = Pattern.compile("OPR/(\\d+)");
    private static final Pattern CHROME_PATTERN = Pattern.compile("Chrome/(\\d+)");
    private static final Pattern FIREFOX_PATTERN = Pattern.compile("Firefox/(\\d+)");
    private static final Pattern SAFARI_PATTERN = Pattern.compile("Version/(\\d+)(?:\\.\\d+)?[\\s\\S]*Safari/");
    private static final Pattern IOS_PATTERN = Pattern.compile("(?:CPU (?:iPhone )?OS|iPhone OS) (\\d+)[_\\.]");
    private static final Pattern ANDROID_PATTERN = Pattern.compile("Android (\\d+)");
    private static final Pattern MAC_OS_PATTERN = Pattern.compile("Mac OS X (\\d+)[_\\.](\\d+)");

    public UserAgentParser() {
    }

    /**
     * 解析浏览器与操作系统
     *
     * @param userAgent 原始 User-Agent
     * @return 解析结果
     */
    public static ParsedUserAgent parse(String userAgent) {
        if (!StringUtils.hasText(userAgent)) {
            return new ParsedUserAgent(UNKNOWN, UNKNOWN);
        }
        return new ParsedUserAgent(resolveBrowser(userAgent), resolveOs(userAgent));
    }

    private static String resolveBrowser(String userAgent) {
        String browser = extractVersion(userAgent, EDGE_PATTERN, "Edge ");
        if (browser != null) {
            return browser;
        }
        browser = extractVersion(userAgent, OPERA_PATTERN, "Opera ");
        if (browser != null) {
            return browser;
        }
        browser = extractVersion(userAgent, CHROME_PATTERN, "Chrome ");
        if (browser != null) {
            return browser;
        }
        browser = extractVersion(userAgent, FIREFOX_PATTERN, "Firefox ");
        if (browser != null) {
            return browser;
        }
        browser = extractVersion(userAgent, SAFARI_PATTERN, "Safari ");
        if (browser != null) {
            return browser;
        }
        return UNKNOWN;
    }

    private static String resolveOs(String userAgent) {
        if (userAgent.contains("Windows NT 10.0")) {
            return "Windows 10";
        }
        if (userAgent.contains("Windows NT 6.3")) {
            return "Windows 8.1";
        }
        if (userAgent.contains("Windows NT 6.2")) {
            return "Windows 8";
        }
        if (userAgent.contains("Windows NT 6.1")) {
            return "Windows 7";
        }
        String ios = extractVersion(userAgent, IOS_PATTERN, "iOS ");
        if (ios != null) {
            return ios;
        }
        String android = extractVersion(userAgent, ANDROID_PATTERN, "Android ");
        if (android != null) {
            return android;
        }
        Matcher macMatcher = MAC_OS_PATTERN.matcher(userAgent);
        if (macMatcher.find()) {
            return "macOS " + macMatcher.group(1) + "." + macMatcher.group(2);
        }
        if (userAgent.contains("Linux")) {
            return "Linux";
        }
        return UNKNOWN;
    }

    private static String extractVersion(String userAgent, Pattern pattern, String prefix) {
        Matcher matcher = pattern.matcher(userAgent);
        if (!matcher.find()) {
            return null;
        }
        return prefix + matcher.group(1);
    }

    /**
     * 截断 User-Agent 字符串到安全长度
     * 用于日志或指纹等场景，避免长 UA 撑爆存储/摘要字段
     *
     * @param userAgent 原始 User-Agent
     * @return 截断后的字符串（为空时返回 Unknown）
     */
    public String truncate(String userAgent) {
        if (!StringUtils.hasText(userAgent)) {
            return UNKNOWN;
        }
        if (userAgent.length() <= TRUNCATE_MAX_LENGTH) {
            return userAgent;
        }
        return userAgent.substring(0, TRUNCATE_MAX_LENGTH);
    }

    /**
     * 解析结果
     *
     * @param browser 浏览器摘要
     * @param os      操作系统摘要
     */
    public record ParsedUserAgent(String browser, String os) {
    }
}
