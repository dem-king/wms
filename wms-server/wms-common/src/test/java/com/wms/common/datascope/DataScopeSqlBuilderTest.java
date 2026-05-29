package com.wms.common.datascope;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * 数据范围SQL构建测试。
 */
@DisplayName("DataScopeSqlBuilder 测试")
class DataScopeSqlBuilderTest {

    private final DataScopeSqlBuilder builder = new DataScopeSqlBuilder();

    @Test
    @DisplayName("全部数据范围不追加过滤条件")
    void shouldNotBuildConditionForAllDataScope() {
        DataScopeCondition condition = DataScopeCondition.all();

        String sql = builder.buildSqlSegment("wms_outbound_order", "o", condition);

        assertNull(sql);
    }

    @Test
    @DisplayName("部门范围按表的部门字段生成IN条件")
    void shouldBuildDepartmentCondition() {
        DataScopeCondition condition = DataScopeCondition.restricted(Set.of(10L, 11L), null, false);

        String sql = builder.buildSqlSegment("wms_outbound_order", "o", condition);

        assertEquals("o.dept_id IN (10,11)", sql);
    }

    @Test
    @DisplayName("仅本人范围按表的用户字段生成等值条件")
    void shouldBuildSelfCondition() {
        DataScopeCondition condition = DataScopeCondition.restricted(Set.of(), 7L, true);

        String sql = builder.buildSqlSegment("wms_outbound_order", "o", condition);

        assertEquals("o.applicant_id = 7", sql);
    }

    @Test
    @DisplayName("Deny all when restricted scope has no effective condition")
    void shouldDenyAllWhenRestrictedScopeHasNoEffectiveCondition() {
        DataScopeCondition condition = DataScopeCondition.restricted(Set.of(), 7L, false);

        String sql = builder.buildSqlSegment("wms_outbound_order", "o", condition);

        assertEquals("1 = 0", sql);
    }

    @Test
    @DisplayName("多角色范围按部门和本人生成OR条件")
    void shouldMergeDepartmentAndSelfConditionWithOr() {
        DataScopeCondition condition = DataScopeCondition.restricted(new LinkedHashSet<>(Set.of(10L, 11L)), 7L, true);

        String sql = builder.buildSqlSegment("wms_outbound_order", "o", condition);

        assertEquals("(o.dept_id IN (10,11) OR o.applicant_id = 7)", sql);
    }

    @Test
    @DisplayName("Deny all when restricted scope cannot match table columns")
    void shouldDenyAllWhenRestrictedScopeCannotMatchTableColumns() {
        DataScopeCondition condition = DataScopeCondition.restricted(Set.of(10L), 7L, false);

        String sql = builder.buildSqlSegment("sys_oper_log", "l", condition);

        assertEquals("1 = 0", sql);
    }

    @Test
    @DisplayName("不支持的表不追加过滤条件")
    void shouldSkipUnsupportedTable() {
        DataScopeCondition condition = DataScopeCondition.restricted(Set.of(10L), 7L, true);

        String sql = builder.buildSqlSegment("sys_role", "r", condition);

        assertNull(sql);
    }
}
