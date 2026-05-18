package com.wms.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/** 供应商实体，对应供应商表sys_supplier */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_supplier")
public class SysSupplier extends BaseEntity {

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
}
