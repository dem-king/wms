package com.wms.business.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 业务单据实体字段映射测试
 * 防止实体字段名与数据库列名不一致时生成错误 SQL
 */
@DisplayName("业务单据实体映射测试")
class OrderEntityMappingTest {

    @Test
    @DisplayName("入库单状态字段应映射到 order_status")
    void shouldMapInboundStatusToOrderStatusColumn() throws NoSuchFieldException {
        Field field = WmsInboundOrder.class.getDeclaredField("status");

        TableField tableField = field.getAnnotation(TableField.class);

        assertNotNull(tableField, "status 字段必须声明 TableField 映射");
        assertEquals("order_status", tableField.value());
    }

    @Test
    @DisplayName("出库单预计归还时间字段应映射到 expected_return")
    void shouldMapOutboundExpectedReturnDateToExpectedReturnColumn() throws NoSuchFieldException {
        Field field = WmsOutboundOrder.class.getDeclaredField("expectedReturnDate");

        TableField tableField = field.getAnnotation(TableField.class);

        assertNotNull(tableField, "expectedReturnDate 字段必须声明 TableField 映射");
        assertEquals("expected_return", tableField.value());
    }

    @Test
    @DisplayName("报废单状态字段应映射到 order_status")
    void shouldMapScrapStatusToOrderStatusColumn() throws NoSuchFieldException {
        Field field = WmsScrapOrder.class.getDeclaredField("status");

        TableField tableField = field.getAnnotation(TableField.class);

        assertNotNull(tableField, "status 字段必须声明 TableField 映射");
        assertEquals("order_status", tableField.value());
    }

    @Test
    @DisplayName("调拨单状态字段应映射到 order_status")
    void shouldMapTransferStatusToOrderStatusColumn() throws NoSuchFieldException {
        Field field = WmsTransferOrder.class.getDeclaredField("status");

        TableField tableField = field.getAnnotation(TableField.class);

        assertNotNull(tableField, "status 字段必须声明 TableField 映射");
        assertEquals("order_status", tableField.value());
    }
}
