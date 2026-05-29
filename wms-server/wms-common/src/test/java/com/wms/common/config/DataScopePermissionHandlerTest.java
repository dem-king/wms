package com.wms.common.config;

import com.wms.common.context.DataScopeContext;
import com.wms.common.datascope.DataScopeCondition;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 数据权限处理器测试。
 */
@DisplayName("DataScopePermissionHandler 测试")
class DataScopePermissionHandlerTest {

    private final DataScopePermissionHandler handler = new DataScopePermissionHandler();

    @AfterEach
    void tearDown() {
        DataScopeContext.clear();
    }

    @Test
    @DisplayName("没有数据范围上下文时不追加表达式")
    void shouldReturnNullWithoutContext() {
        Expression expression = handler.getSqlSegment(new Table("wms_outbound_order"), null, "testMapper.select");

        assertNull(expression);
    }

    @Test
    @DisplayName("存在数据范围上下文时生成JSqlParser表达式")
    void shouldBuildExpressionFromContext() {
        DataScopeContext.set(DataScopeCondition.restricted(new LinkedHashSet<>(Set.of(10L, 11L)), 7L, true));
        Table table = new Table("wms_outbound_order");
        table.setAlias(new net.sf.jsqlparser.expression.Alias("o"));

        Expression expression = handler.getSqlSegment(table, null, "testMapper.select");

        assertEquals("(o.dept_id IN (10, 11) OR o.applicant_id = 7)", expression.toString());
    }
}
