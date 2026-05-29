package com.wms.common.datascope;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 数据范围SQL片段构建器。
 */
public class DataScopeSqlBuilder {

    private static final String DENY_ALL_SQL = "1 = 0";

    private static final Map<String, TableRule> TABLE_RULES = Map.ofEntries(
            Map.entry("sys_user", new TableRule(List.of("dept_id"), List.of("id"))),
            Map.entry("wms_outbound_order", new TableRule(List.of("dept_id"), List.of("applicant_id"))),
            Map.entry("wms_scrap_order", new TableRule(List.of(), List.of("applicant_id"))),
            Map.entry("wms_transfer_order", new TableRule(List.of(), List.of("applicant_id"))),
            Map.entry("wms_inbound_order", new TableRule(List.of(), List.of("operator_id"))),
            Map.entry("wms_electronic_label", new TableRule(List.of("current_dept_id"), List.of("current_user_id"))),
            Map.entry("sys_oper_log", new TableRule(List.of(), List.of("user_id"))),
            Map.entry("sys_login_log", new TableRule(List.of(), List.of("user_id"))),
            Map.entry("approval_record", new TableRule(List.of(), List.of("approver_id")))
    );

    /**
     * 根据表名、别名和数据范围条件构建SQL过滤片段。
     *
     * @param tableName 表名
     * @param tableAlias 表别名
     * @param condition 数据范围条件
     * @return SQL过滤片段；无需过滤时返回null
     */
    public String buildSqlSegment(String tableName, String tableAlias, DataScopeCondition condition) {
        if (condition == null || condition.isAllData()) {
            return null;
        }
        TableRule rule = TABLE_RULES.get(normalize(tableName));
        if (rule == null) {
            return null;
        }
        if (!condition.hasRestriction()) {
            return DENY_ALL_SQL;
        }
        List<String> segments = new ArrayList<>();
        if (!condition.getDeptIds().isEmpty() && !rule.deptColumns().isEmpty()) {
            String values = condition.getDeptIds().stream()
                    .sorted(Comparator.naturalOrder())
                    .map(String::valueOf)
                    .reduce((left, right) -> left + "," + right)
                    .orElse("");
            for (String column : rule.deptColumns()) {
                segments.add(qualify(tableAlias, column) + " IN (" + values + ")");
            }
        }
        if (condition.isSelfScope() && condition.getUserId() != null && !rule.userColumns().isEmpty()) {
            for (String column : rule.userColumns()) {
                segments.add(qualify(tableAlias, column) + " = " + condition.getUserId());
            }
        }
        if (segments.isEmpty()) {
            return DENY_ALL_SQL;
        }
        if (segments.size() == 1) {
            return segments.get(0);
        }
        return "(" + String.join(" OR ", segments) + ")";
    }

    private String normalize(String tableName) {
        return tableName == null ? "" : tableName.toLowerCase(Locale.ROOT);
    }

    private String qualify(String tableAlias, String column) {
        if (tableAlias == null || tableAlias.isBlank()) {
            return column;
        }
        return tableAlias + "." + column;
    }

    private record TableRule(List<String> deptColumns, List<String> userColumns) {
    }
}
