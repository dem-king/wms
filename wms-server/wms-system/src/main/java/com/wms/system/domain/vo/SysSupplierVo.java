package com.wms.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 供应商视图对象
 */
@Data
@Schema(description = "供应商信息")
public class SysSupplierVo {

    /** 供应商ID */
    @Schema(description = "供应商ID")
    private Long id;

    /** 供应商名称 */
    @Schema(description = "供应商名称")
    private String supplierName;

    /** 供应商编码 */
    @Schema(description = "供应商编码")
    private String supplierCode;

    /** 联系人 */
    @Schema(description = "联系人")
    private String contactPerson;

    /** 联系电话 */
    @Schema(description = "联系电话")
    private String contactPhone;

    /** 地址 */
    @Schema(description = "地址")
    private String address;

    /** 年交易金额 */
    @Schema(description = "年交易金额")
    private BigDecimal annualAmount;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
