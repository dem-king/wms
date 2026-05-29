package com.wms.item.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 物品默认库位视图对象
 */
@Data
@Schema(description = "物品默认库位信息")
public class ItemLocationVo {

    /** 库房ID */
    @Schema(description = "库房ID")
    private Long warehouseId;

    /** 库房名称 */
    @Schema(description = "库房名称")
    private String warehouseName;

    /** 区域ID */
    @Schema(description = "区域ID")
    private Long areaId;

    /** 区域名称 */
    @Schema(description = "区域名称")
    private String areaName;

    /** 存放柜ID */
    @Schema(description = "存放柜ID")
    private Long cabinetId;

    /** 存放柜名称 */
    @Schema(description = "存放柜名称")
    private String cabinetName;

    /** 库位ID */
    @Schema(description = "库位ID")
    private Long binId;

    /** 库位编码 */
    @Schema(description = "库位编码")
    private String binCode;

    /** 完整库位路径 */
    @Schema(description = "完整库位路径")
    private String locationText;
}
