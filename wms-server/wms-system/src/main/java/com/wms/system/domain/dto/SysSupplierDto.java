package com.wms.system.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 供应商新增/编辑DTO
 */
@Data
@Schema(description = "供应商新增/编辑请求")
public class SysSupplierDto {

    /** 供应商名称 */
    @NotBlank(message = "供应商名称不能为空")
    @Schema(description = "供应商名称")
    private String supplierName;

    /** 供应商编码 */
    @NotBlank(message = "供应商编码不能为空")
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
}
