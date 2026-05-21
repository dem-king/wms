package com.wms.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BizTypeEnum {

    INBOUND(1, "入库"),
    OUTBOUND(2, "出库/领用"),
    SCRAP(3, "报废"),
    TRANSFER(4, "调拨"),
    RETURN(5, "归还");

    private final int code;
    private final String desc;

    public static BizTypeEnum of(int code) {
        for (BizTypeEnum e : values()) {
            if (e.code == code) return e;
        }
        return null;
    }
}
