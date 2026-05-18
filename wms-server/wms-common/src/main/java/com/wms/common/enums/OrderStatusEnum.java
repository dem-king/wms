package com.wms.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    DRAFT(0, "草稿"),
    PENDING(1, "待审批"),
    APPROVING(2, "审批中"),
    APPROVED(3, "已通过"),
    REJECTED(4, "已驳回"),
    COMPLETED(5, "已完成");

    private final int code;
    private final String desc;
}
