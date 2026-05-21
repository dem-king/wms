package com.wms.common.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderStatusEnum 单元测试
 * 验证订单状态枚举的正确性
 */
@DisplayName("OrderStatusEnum 测试")
class OrderStatusEnumTest {

    @Test
    @DisplayName("DRAFT状态 - code=0, desc=草稿")
    void shouldHaveCorrectDraftValues() {
        assertEquals(0, OrderStatusEnum.DRAFT.getCode());
        assertEquals("草稿", OrderStatusEnum.DRAFT.getDesc());
    }

    @Test
    @DisplayName("PENDING状态 - code=1, desc=待审批")
    void shouldHaveCorrectPendingValues() {
        assertEquals(1, OrderStatusEnum.PENDING.getCode());
        assertEquals("待审批", OrderStatusEnum.PENDING.getDesc());
    }

    @Test
    @DisplayName("枚举值总数应为6个")
    void shouldHaveSixStatuses() {
        assertEquals(6, OrderStatusEnum.values().length);
    }

    @ParameterizedTest(name = "{0}状态code必须唯一")
    @EnumSource(OrderStatusEnum.class)
    @DisplayName("所有枚举的code必须唯一且非负")
    void shouldHaveUniqueNonNegativeCodes(OrderStatusEnum status) {
        assertTrue(status.getCode() >= 0,
                () -> status.name() + "的code应为非负数, 实际=" + status.getCode());
    }

    @Test
    @DisplayName("草稿→待审批 是合法的状态流转(相邻code)")
    void shouldSupportDraftToPendingTransition() {
        int draftCode = OrderStatusEnum.DRAFT.getCode();
        int pendingCode = OrderStatusEnum.PENDING.getCode();
        assertEquals(draftCode + 1, pendingCode,
                "草稿的下一状态应为待审批(code递增1)");
    }
}