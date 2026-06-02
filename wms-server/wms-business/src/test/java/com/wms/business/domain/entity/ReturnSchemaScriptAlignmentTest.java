package com.wms.business.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Return and stock-moving detail schema alignment tests.
 * Prevents initialization SQL from drifting away from fields used by order code.
 */
@DisplayName("Return and stock-moving detail schema alignment")
class ReturnSchemaScriptAlignmentTest {

    @Test
    @DisplayName("initial SQL should contain columns used by return and bin-level stock-moving details")
    void shouldContainReturnOrderAndDetailColumnsUsedByCode() throws IOException {
        List<Path> existingScripts = List.of(
                Path.of("..", "..", "database", "wms_complete_init.sql"),
                Path.of("..", "..", "database", "wms_full_init.sql"),
                Path.of("..", "..", "database", "wms_ddl.sql")
        ).stream().filter(Files::exists).toList();

        assertFalse(existingScripts.isEmpty(), "missing database initialization SQL script");
        for (Path script : existingScripts) {
            assertScriptContainsRequiredColumns(script);
        }
    }

    private void assertScriptContainsRequiredColumns(Path scriptPath) throws IOException {
        String content = Files.readString(scriptPath);

        assertTrue(content.contains("`outbound_order_id`"), scriptPath + " missing outbound_order_id");
        assertTrue(content.contains("`receiver`"), scriptPath + " missing receiver");
        assertTrue(content.contains("`order_status`"), scriptPath + " missing order_status");
        assertTrue(content.contains("`condition_status`"), scriptPath + " missing condition_status");
        assertTrue(content.contains("`abnormal_remark`"), scriptPath + " missing abnormal_remark");
        assertTrue(content.contains("`actual_quantity`"), scriptPath + " missing actual_quantity");
        assertTrue(content.contains("`bin_id`"), scriptPath + " missing bin_id");
        assertTrue(content.contains("`from_bin_id`"), scriptPath + " missing from_bin_id");
        assertTrue(content.contains("`to_bin_id`"), scriptPath + " missing to_bin_id");
    }
}
