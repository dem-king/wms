package com.wms.item.domain.constant;

import com.wms.common.enums.LabelStatusEnum;

import java.util.*;

/**
 * 电子标签常量类
 * 定义标签编号前缀、打印状态、标签状态、标签类型等常量
 */
public final class LabelConstants {

    private LabelConstants() {
    }

    /** 标签编号前缀 */
    public static final String LABEL_NO_PREFIX = "BQ";

    /** 打印状态：未打印 */
    public static final int PRINT_STATUS_NOT = 0;

    /** 打印状态：已打印 */
    public static final int PRINT_STATUS_DONE = 1;

    /** 标签类型：RFID */
    public static final int LABEL_TYPE_RFID = 3;

    /** 闲置判定天数阈值 */
    public static final int IDLE_THRESHOLD_DAYS = 90;

    /** RFID编码随机数下限 */
    public static final int RFID_RANDOM_MIN = 1000;

    /** RFID编码随机数上限(不含) */
    public static final int RFID_RANDOM_MAX = 9000;

    /**
     * 合法状态流转映射
     * key: 当前状态码, value: 允许流转的目标状态码集合
     * 映射关系:
     *   在库(1) → {正在使用(2), 闲置(5)}
     *   正在使用(2) → {已归还(3), 报废(4)}
     *   已归还(3) → {在库(1), 闲置(5)}
     *   闲置(5) → {在库(1)}
     */
    public static final Map<Integer, Set<Integer>> STATUS_TRANSITIONS;

    static {
        Map<Integer, Set<Integer>> transitions = new HashMap<>();
        // 在库 → 正在使用、闲置
        transitions.put(LabelStatusEnum.IN_STOCK.getCode(),
                Set.of(LabelStatusEnum.IN_USE.getCode(), LabelStatusEnum.IDLE.getCode()));
        // 正在使用 → 已归还、报废
        transitions.put(LabelStatusEnum.IN_USE.getCode(),
                Set.of(LabelStatusEnum.RETURNED.getCode(), LabelStatusEnum.SCRAPPED.getCode()));
        // 已归还 → 在库、闲置
        transitions.put(LabelStatusEnum.RETURNED.getCode(),
                Set.of(LabelStatusEnum.IN_STOCK.getCode(), LabelStatusEnum.IDLE.getCode()));
        // 闲置 → 在库
        transitions.put(LabelStatusEnum.IDLE.getCode(),
                Set.of(LabelStatusEnum.IN_STOCK.getCode()));
        STATUS_TRANSITIONS = Collections.unmodifiableMap(transitions);
    }
}
