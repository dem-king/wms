package com.wms.common.util;

import com.wms.common.config.IpRegionProperties;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * IP归属地解析器
 * 使用 ip2region 离线库解析公网地址，并对内网地址和异常场景做统一降级
 */
@Slf4j
@Component
public class IpRegionResolver {

    private static final String UNKNOWN_LOCATION = "未知";
    private static final String INTRANET_LOCATION = "内网IP";
    private static final String CLASSPATH_PREFIX = "classpath:";

    private final IpRegionProperties properties;

    public IpRegionResolver(IpRegionProperties properties) {
        this.properties = properties;
    }

    /**
     * 解析登录地点
     *
     * @param ip IP地址
     * @return 登录地点
     */
    public String resolve(String ip) {
        if (!properties.isEnabled()) {
            return UNKNOWN_LOCATION;
        }
        if (!StringUtils.hasText(ip)) {
            return UNKNOWN_LOCATION;
        }
        String normalizedIp = ip.trim();
        if (isIntranetIp(normalizedIp)) {
            return INTRANET_LOCATION;
        }
        try {
            Searcher searcher = Searcher.newWithFileOnly(Version.IPv4, resolveDbPath());
            try {
                return formatRegion(searcher.search(normalizedIp));
            } finally {
                searcher.close();
            }
        } catch (Exception e) {
            log.warn("IP归属地解析失败, ip={}, error={}", normalizedIp, e.getMessage());
            return UNKNOWN_LOCATION;
        }
    }

    /**
     * 格式化 ip2region 返回结果
     *
     * @param rawRegion 原始地区串
     * @return 省市区格式地点
     */
    public static String formatRegion(String rawRegion) {
        if (!StringUtils.hasText(rawRegion)) {
            return UNKNOWN_LOCATION;
        }
        String[] parts = rawRegion.split("\\|");
        boolean shouldIgnoreTrailingIsp = shouldIgnoreTrailingIsp(parts);
        Set<String> segments = new LinkedHashSet<>();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (!StringUtils.hasText(part)) {
                continue;
            }
            String value = part.trim();
            if ("0".equals(value) || "中国".equals(value)) {
                continue;
            }
            if (INTRANET_LOCATION.equals(value)) {
                return INTRANET_LOCATION;
            }
            // 仅在尾段明确为国家码时，按 ip2region 3.x 结果忽略 ISP 和国家码，避免误伤旧版省市区格式
            if (shouldIgnoreTrailingIsp && (i == parts.length - 2 || i == parts.length - 1)) {
                continue;
            }
            segments.add(value);
        }
        if (segments.isEmpty()) {
            return UNKNOWN_LOCATION;
        }
        return String.join("", segments);
    }

    private static boolean shouldIgnoreTrailingIsp(String[] parts) {
        if (parts.length < 5) {
            return false;
        }
        String lastPart = parts[parts.length - 1];
        if (!StringUtils.hasText(lastPart)) {
            return false;
        }
        String normalizedLastPart = lastPart.trim();
        return normalizedLastPart.matches("(?i)^[a-z]{2}$");
    }

    private boolean isIntranetIp(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            return address.isAnyLocalAddress()
                    || address.isLoopbackAddress()
                    || address.isSiteLocalAddress()
                    || address.isLinkLocalAddress();
        } catch (UnknownHostException e) {
            return false;
        }
    }

    private String resolveDbPath() throws IOException {
        String configuredPath = properties.getXdbPath();
        if (!StringUtils.hasText(configuredPath)) {
            throw new IOException("ip2region xdb 路径不能为空");
        }
        if (configuredPath.startsWith(CLASSPATH_PREFIX)) {
            String classpathLocation = configuredPath.substring(CLASSPATH_PREFIX.length());
            return copyClasspathResourceToTempFile(classpathLocation);
        }
        return configuredPath;
    }

    private String copyClasspathResourceToTempFile(String classpathLocation) throws IOException {
        ClassPathResource resource = new ClassPathResource(classpathLocation);
        if (!resource.exists()) {
            throw new IOException("未找到 ip2region xdb 文件: " + classpathLocation);
        }
        File tempFile = Files.createTempFile("ip2region-", ".xdb").toFile();
        tempFile.deleteOnExit();
        try (InputStream inputStream = resource.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            inputStream.transferTo(outputStream);
        }
        return tempFile.getAbsolutePath();
    }
}
