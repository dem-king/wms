package com.wms.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperTypeEnum {

    ADD("i", "新增"),
    UPDATE("u", "更新"),
    DELETE("d", "删除");

    private final String code;
    private final String desc;
}
