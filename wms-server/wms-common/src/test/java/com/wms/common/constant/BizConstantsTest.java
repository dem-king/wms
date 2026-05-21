package com.wms.common.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BizConstants 单元测试
 * 验证业务通用常量的正确性
 */
@DisplayName("BizConstants 测试")
class BizConstantsTest {

    @Test
    @DisplayName("STATUS_ENABLED恒为1 - 启用状态")
    void shouldHaveCorrectEnabledStatus() {
        assertEquals(1, BizConstants.STATUS_ENABLED);
    }

    @Test
    @DisplayName("STATUS_DISABLED恒为0 - 禁用状态")
    void shouldHaveCorrectDisabledStatus() {
        assertEquals(0, BizConstants.STATUS_DISABLED);
    }

    @Test
    @DisplayName("STOCK_SYNC_IN恒为'IN' - 入库同步方向")
    void shouldHaveCorrectStockSyncIn() {
        assertEquals("IN", BizConstants.STOCK_SYNC_IN);
    }

    @Test
    @DisplayName("STOCK_SYNC_OUT恒为'OUT' - 出库同步方向")
    void shouldHaveCorrectStockSyncOut() {
        assertEquals("OUT", BizConstants.STOCK_SYNC_OUT);
    }

    @Test
    @DisplayName("库存同步方向互斥")
    void shouldHaveDistinctSyncDirections() {
        assertNotEquals(BizConstants.STOCK_SYNC_IN, BizConstants.STOCK_SYNC_OUT);
    }
}