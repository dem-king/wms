package com.wms.report.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 高德接口配置
 * 管理首页位置天气功能所需的 key 与接口地址
 */
@Data
@Component
@ConfigurationProperties(prefix = "wms.amap")
public class AmapProperties {

    /** 高德 WebService Key */
    private String key;

    /** 高德逆地理编码地址 */
    private String geocodeUrl;

    /** 高德实时天气地址 */
    private String weatherUrl;
}
