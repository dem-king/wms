package com.wms.business.domain.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderConstants 单元测试
 * 验证单据编号前缀常量的正确性
 */
@DisplayName("OrderConstants 测试")
class OrderConstantsTest {

    @Test
    @DisplayName("入库单号前缀为'RK'")
    void shouldHaveCorrectInboundPrefix() {
        assertEquals("RK", OrderConstants.INBOUND_NO_PREFIX);
    }

    @Test
    @DisplayName("出库单号前缀为'CK'")
    void shouldHaveCorrectOutboundPrefix() {
        assertEquals("CK", OrderConstants.OUTBOUND_NO_PREFIX);
    }

    @Test
    @DisplayName("归还单号前缀为'GH'")
    void shouldHaveCorrectReturnPrefix() {
        assertEquals("GH", OrderConstants.RETURN_NO_PREFIX);
    }

    @Test
    @DisplayName("报废单号前缀为'BF'")
    void shouldHaveCorrectScrapPrefix() {
        assertEquals("BF", OrderConstants.SCRAP_NO_PREFIX);
    }

    @Test
    @DisplayName("调拨单号前缀为'DB'")
    void shouldHaveCorrectTransferPrefix() {
        assertEquals("DB", OrderConstants.TRANSFER_NO_PREFIX);
    }

    @Test
    @DisplayName("所有前缀互不相同")
    void shouldHaveAllDistinctPrefixes() {
        String[] prefixes = {
                OrderConstants.INBOUND_NO_PREFIX,
                OrderConstants.OUTBOUND_NO_PREFIX,
                OrderConstants.RETURN_NO_PREFIX,
                OrderConstants.SCRAP_NO_PREFIX,
                OrderConstants.TRANSFER_NO_PREFIX
        };
        assertEquals(5, java.util.Set.of(prefixes).size(),
                "所有单据编号前缀必须互不相同");
    }
}