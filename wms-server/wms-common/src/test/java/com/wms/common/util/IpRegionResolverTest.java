package com.wms.common.util;

import com.wms.common.config.IpRegionProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("IpRegionResolver 测试")
class IpRegionResolverTest {

    @Test
    @DisplayName("内网地址应返回内网IP")
    void shouldReturnIntranetForPrivateIp() {
        IpRegionResolver resolver = new IpRegionResolver(new IpRegionProperties());

        assertEquals("内网IP", resolver.resolve("127.0.0.1"));
    }

    @Test
    @DisplayName("空IP应返回未知")
    void shouldReturnUnknownForBlankIp() {
        IpRegionResolver resolver = new IpRegionResolver(new IpRegionProperties());

        assertEquals("未知", resolver.resolve(""));
    }

    @Test
    @DisplayName("地区串应格式化为省市区")
    void shouldFormatRegionTextToProvinceCityDistrict() {
        assertEquals("广东省深圳市南山区",
                IpRegionResolver.formatRegion("中国|0|广东省|深圳市|南山区"));
    }

    @Test
    @DisplayName("ip2region 3.x 原始结果应忽略运营商和国家码")
    void shouldIgnoreIspAndIsoCodeForIp2Region3Format() {
        assertEquals("广东省深圳市",
                IpRegionResolver.formatRegion("中国|广东省|深圳市|阿里云|CN"));
    }
}
