package com.wms.warehouse.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 库房底图信息视图对象
 * 返回底图上传后的库房ID、新版本号和底图访问URL
 */
@Data
@Schema(description = "库房底图信息")
public class WarehouseBackgroundVo {

    /** 库房ID */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 新版本号 */
    @Schema(description = "新版本号")
    private String layoutBackgroundVersion;

    /** 底图访问URL */
    @Schema(description = "底图访问URL")
    private String backgroundUrl;
}