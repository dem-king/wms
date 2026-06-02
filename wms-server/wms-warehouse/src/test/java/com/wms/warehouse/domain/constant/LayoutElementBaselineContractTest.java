package com.wms.warehouse.domain.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Task1 基线契约测试
 * 只校验布局元素常量类与数据库脚本基线，避免越界到 Task2 的字段映射实现。
 */
@DisplayName("LayoutElement Task1 基线契约测试")
class LayoutElementBaselineContractTest {

    private static final String COMPLETE_INIT_SQL = "wms_complete_init.sql";

    @Test
    @DisplayName("应提供布局元素类型与形状类型常量基线")
    void shouldProvideLayoutElementConstantsBaseline() throws Exception {
        Class<?> constantsClass = Class.forName("com.wms.warehouse.domain.constant.LayoutElementConstants");

        assertTrue(Modifier.isFinal(constantsClass.getModifiers()));
        assertConstant(constantsClass, "ELEMENT_TYPE_WALL", "wall");
        assertConstant(constantsClass, "ELEMENT_TYPE_AISLE", "aisle");
        assertConstant(constantsClass, "ELEMENT_TYPE_RESERVED", "reserved");
        assertConstant(constantsClass, "ELEMENT_TYPE_DEVICE", "device");
        assertConstant(constantsClass, "ELEMENT_TYPE_TEXT", "text");
        assertConstant(constantsClass, "ELEMENT_TYPE_DIMENSION", "dimension");
        assertConstant(constantsClass, "SHAPE_TYPE_LINE", "line");
        assertConstant(constantsClass, "SHAPE_TYPE_RECT", "rect");
        assertConstant(constantsClass, "SHAPE_TYPE_POLYGON", "polygon");
        assertConstant(constantsClass, "SHAPE_TYPE_CIRCLE", "circle");
        assertConstant(constantsClass, "SHAPE_TYPE_TEXT", "text");
    }

    @Test
    @DisplayName("DDL 与初始化脚本应包含布局底图数据库基线")
    void shouldContainLayoutBlueprintBaselineInSqlScripts() throws IOException {
        Path repoRoot = findRepositoryRoot();
        assertSqlBaseline(repoRoot.resolve("database").resolve(COMPLETE_INIT_SQL));
    }

    private void assertConstant(Class<?> constantsClass, String fieldName, String expectedValue) throws Exception {
        Field field = constantsClass.getDeclaredField(fieldName);
        assertTrue(Modifier.isPublic(field.getModifiers()));
        assertTrue(Modifier.isStatic(field.getModifiers()));
        assertTrue(Modifier.isFinal(field.getModifiers()));
        assertEquals(expectedValue, field.get(null));
    }

    private void assertSqlBaseline(Path sqlFile) throws IOException {
        String content = readFile(sqlFile);
        assertTrue(content.contains("CREATE TABLE `wms_layout_element`"), sqlFile + " 缺少 wms_layout_element 建表语句");
        assertTrue(content.contains("`layout_width`"), sqlFile + " 缺少 layout_width 字段");
        assertTrue(content.contains("`layout_height`"), sqlFile + " 缺少 layout_height 字段");
        assertTrue(content.contains("`layout_scale`"), sqlFile + " 缺少 layout_scale 字段");
        assertTrue(content.contains("`layout_background_version`"), sqlFile + " 缺少 layout_background_version 字段");
        assertTrue(content.contains("`shape_type`"), sqlFile + " 缺少 shape_type 字段");
        assertTrue(content.contains("`polygon_points`"), sqlFile + " 缺少 polygon_points 字段");
        assertTrue(content.contains("`label_x`"), sqlFile + " 缺少 label_x 字段");
        assertTrue(content.contains("`label_y`"), sqlFile + " 缺少 label_y 字段");
        assertTrue(content.contains("`rotation`"), sqlFile + " 缺少 rotation 字段");

        String layoutElementBlock = extractTableBlock(content, "wms_layout_element");
        assertNotNull(layoutElementBlock, sqlFile + " 缺少 wms_layout_element 表定义块");
        assertTrue(layoutElementBlock.contains("`id`              BIGINT"), sqlFile + " 的布局元素表主键不是 BIGINT");
        assertLayoutElementAuditFields(layoutElementBlock, sqlFile);
        assertFalse(layoutElementBlock.contains("AUTO_INCREMENT"), sqlFile + " 的布局元素表主键不应使用 AUTO_INCREMENT");
    }

    private void assertIncrementSqlBaseline(Path sqlFile) throws IOException {
        String content = readFile(sqlFile);
        assertTrue(content.contains("ALTER TABLE `wms_warehouse`"), sqlFile + " 缺少 wms_warehouse 增量脚本");
        assertTrue(content.contains("ALTER TABLE `wms_area`"), sqlFile + " 缺少 wms_area 增量脚本");
        assertTrue(content.contains("ALTER TABLE `wms_cabinet`"), sqlFile + " 缺少 wms_cabinet 增量脚本");
        assertTrue(content.contains("CREATE TABLE `wms_layout_element`"), sqlFile + " 缺少 wms_layout_element 建表语句");
        assertTrue(content.contains("`layout_background_version`"), sqlFile + " 缺少 layout_background_version 字段");
        assertTrue(content.contains("`polygon_points`"), sqlFile + " 缺少 polygon_points 字段");
        assertTrue(content.contains("`rotation`"), sqlFile + " 缺少 rotation 字段");
        String layoutElementBlock = extractTableBlock(content, "wms_layout_element");
        assertNotNull(layoutElementBlock, sqlFile + " 缺少 wms_layout_element 表定义块");
        assertLayoutElementAuditFields(layoutElementBlock, sqlFile);
        assertFalse(content.contains("AUTO_INCREMENT"), sqlFile + " 不应使用 AUTO_INCREMENT");
    }

    private void assertLayoutElementAuditFields(String layoutElementBlock, Path sqlFile) {
        assertTrue(layoutElementBlock.contains("`del_flag`"), sqlFile + " 的布局元素表缺少 del_flag 字段");
        assertTrue(layoutElementBlock.contains("`create_time`"), sqlFile + " 的布局元素表缺少 create_time 字段");
        assertTrue(layoutElementBlock.contains("`create_by`"), sqlFile + " 的布局元素表缺少 create_by 字段");
        assertTrue(layoutElementBlock.contains("`update_time`"), sqlFile + " 的布局元素表缺少 update_time 字段");
        assertTrue(layoutElementBlock.contains("`update_by`"), sqlFile + " 的布局元素表缺少 update_by 字段");
    }

    private String extractTableBlock(String content, String tableName) {
        String marker = "CREATE TABLE `" + tableName + "`";
        int start = content.indexOf(marker);
        if (start < 0) {
            return null;
        }
        int end = content.indexOf("ENGINE=InnoDB", start);
        if (end < 0) {
            return null;
        }
        return content.substring(start, end);
    }

    private String readFile(Path sqlFile) throws IOException {
        assertTrue(Files.exists(sqlFile), "文件不存在: " + sqlFile);
        return Files.readString(sqlFile, StandardCharsets.UTF_8);
    }

    private Path findRepositoryRoot() {
        Path current = Path.of("").toAbsolutePath().normalize();
        while (current != null) {
            if (Files.exists(current.resolve("database").resolve(COMPLETE_INIT_SQL))) {
                return current;
            }
            current = current.getParent();
        }
        throw new IllegalStateException("未找到仓库根目录");
    }
}
