package com.wms.warehouse.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 存放柜布局保存结果 VO
 * 返回区域下已保存的存放柜布局结果
 */
@Data
@Schema(description = "存放柜布局保存结果")
public class CabinetLayoutSaveVo {

    /** 区域ID */
    @Schema(description = "区域ID")
    private Long areaId;

    /** 已保存的存放柜列表 */
    @Schema(description = "已保存的存放柜列表")
    private List<CabinetVo> cabinets;
}
