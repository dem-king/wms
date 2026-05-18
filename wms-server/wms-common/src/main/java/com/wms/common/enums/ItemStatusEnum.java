package com.wms.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ItemStatusEnum {

    IN_STOCK(1, "在库"),
    IN_USE(2, "使用中"),
    RETURNED(3, "已归还"),
    DAMAGED(4, "损坏"),
    LOST(5, "丢失"),
    SCRAPPED(6, "报废"),
    IDLE(7, "闲置");

    private final int code;
    private final String desc;
}
