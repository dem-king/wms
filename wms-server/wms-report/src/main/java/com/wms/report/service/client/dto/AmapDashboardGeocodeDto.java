package com.wms.report.service.client.dto;

import lombok.Data;

/**
 * 高德逆地理编码结果 DTO
 * 仅保留首页位置天气所需的行政区字段
 */
@Data
public class AmapDashboardGeocodeDto {

    /** 省份名称 */
    private String province;

    /** 城市名称 */
    private String city;

    /** 区县名称 */
    private String district;

    /** 行政区编码 */
    private String adcode;
}
