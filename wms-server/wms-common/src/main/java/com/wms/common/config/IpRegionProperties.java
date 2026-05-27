package com.wms.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * IP归属地解析配置
 * 控制 ip2region 离线库是否启用及 xdb 文件加载路径
 */
@Data
@Component
@ConfigurationProperties(prefix = "wms.ip-region")
public class IpRegionProperties {

    /** 是否启用 IP 归属地解析 */
    private boolean enabled = true;

    /** ip2region xdb 文件路径，支持 classpath: 前缀 */
    private String xdbPath = "classpath:ip2region.xdb";
}
