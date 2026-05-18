package com.wms.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LabelStatusEnum {

    IN_STOCK(1, "在库"),
    IN_USE(2, "正在使用"),
    RETURNED(3, "已归还"),
    SCRAPPED(4, "报废"),
    IDLE(5, "闲置");

    private final int code;
    private final String desc;
}
