package com.wms.business.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 归还单相关建表脚本对齐测试
 * 防止初始化 SQL 与当前归还单代码模型继续偏离
 */
@DisplayName("归还单建表脚本对齐测试")
class ReturnSchemaScriptAlignmentTest {

    @Test
    @DisplayName("初始化 SQL 应声明归还单代码模型所需字段")
    void shouldContainReturnOrderAndDetailColumnsUsedByCode() throws IOException {
        assertScriptContainsRequiredColumns(Path.of("..", "..", "database", "wms_complete_init.sql"));
        assertScriptContainsRequiredColumns(Path.of("..", "..", "database", "wms_full_init.sql"));
        assertScriptContainsRequiredColumns(Path.of("..", "..", "database", "wms_ddl.sql"));
    }

    private void assertScriptContainsRequiredColumns(Path scriptPath) throws IOException {
        String content = Files.readString(scriptPath);

        assertTrue(content.contains("`outbound_order_id`"), scriptPath + " 缺少 outbound_order_id");
        assertTrue(content.contains("`receiver`"), scriptPath + " 缺少 receiver");
        assertTrue(content.contains("`order_status`"), scriptPath + " 缺少 order_status");
        assertTrue(content.contains("`condition_status`"), scriptPath + " 缺少 condition_status");
        assertTrue(content.contains("`abnormal_remark`"), scriptPath + " 缺少 abnormal_remark");
        assertTrue(content.contains("`actual_quantity`"), scriptPath + " 缺少 actual_quantity");
    }
}
