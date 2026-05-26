-- ============================================================
-- 数据库结构差异修复脚本
-- 生成时间: 2026-05-26
-- 说明: 根据 wms_complete_init.sql 对比实际数据库结构生成的修复脚本
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- 第一部分: 删除多余的表（SQL文件中不存在）
-- ============================================================

-- 删除旧的认证登录日志表（已统一到 sys_login_log）
DROP TABLE IF EXISTS `auth_login_log`;

-- 删除旧的操作日志表（已统一到 sys_oper_log）
DROP TABLE IF EXISTS `auth_oper_log`;

-- 删除审批配置备份表
DROP TABLE IF EXISTS `wms_approval_config_backup`;

-- 删除审批记录备份表
DROP TABLE IF EXISTS `wms_approval_record_backup`;

-- 删除旧的WMS登录日志表（已统一到 sys_login_log）
DROP TABLE IF EXISTS `wms_login_log`;

-- 删除机器备件备份表
DROP TABLE IF EXISTS `wms_machine_spare_backup`;

-- 删除旧的操作日志表（已统一到 sys_oper_log）
DROP TABLE IF EXISTS `wms_operation_log`;

-- ============================================================
-- 第二部分: 修复字段差异
-- ============================================================

-- 2.1 wms_bin 表添加缺少的字段
-- 检查并添加 warehouse_id 字段
SET @exist_wh := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                  WHERE TABLE_SCHEMA = DATABASE() 
                  AND TABLE_NAME = 'wms_bin' 
                  AND COLUMN_NAME = 'warehouse_id');
SET @sql_wh := IF(@exist_wh = 0, 
                  'ALTER TABLE `wms_bin` ADD COLUMN `warehouse_id` BIGINT DEFAULT NULL COMMENT "库房ID" AFTER `bin_status`',
                  'SELECT "warehouse_id 字段已存在" as message');
PREPARE stmt_wh FROM @sql_wh;
EXECUTE stmt_wh;
DEALLOCATE PREPARE stmt_wh;

-- 检查并添加 is_occupied 字段
SET @exist_occ := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_SCHEMA = DATABASE() 
                   AND TABLE_NAME = 'wms_bin' 
                   AND COLUMN_NAME = 'is_occupied');
SET @sql_occ := IF(@exist_occ = 0, 
                   'ALTER TABLE `wms_bin` ADD COLUMN `is_occupied` TINYINT DEFAULT 0 COMMENT "是否占用(0-空闲 1-占用)" AFTER `warehouse_id`',
                   'SELECT "is_occupied 字段已存在" as message');
PREPARE stmt_occ FROM @sql_occ;
EXECUTE stmt_occ;
DEALLOCATE PREPARE stmt_occ;

-- 2.2 wms_approval_config 表字段检查
-- 检查 remark 字段是否存在
SET @exist_remark := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                      WHERE TABLE_SCHEMA = DATABASE() 
                      AND TABLE_NAME = 'wms_approval_config' 
                      AND COLUMN_NAME = 'remark');
SET @sql_remark := IF(@exist_remark = 0, 
                      'ALTER TABLE `wms_approval_config` ADD COLUMN `remark` VARCHAR(512) DEFAULT "" COMMENT "备注" AFTER `config_name`',
                      'SELECT "remark 字段已存在" as message');
PREPARE stmt_remark FROM @sql_remark;
EXECUTE stmt_remark;
DEALLOCATE PREPARE stmt_remark;

-- 2.3 wms_outbound_order 表添加缺少的 receiver 字段
-- 检查并添加 receiver 字段
SET @exist_receiver := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS 
                        WHERE TABLE_SCHEMA = DATABASE() 
                        AND TABLE_NAME = 'wms_outbound_order' 
                        AND COLUMN_NAME = 'receiver');
SET @sql_receiver := IF(@exist_receiver = 0, 
                        'ALTER TABLE `wms_outbound_order` ADD COLUMN `receiver` VARCHAR(64) DEFAULT "" COMMENT "领用人(姓名)" AFTER `applicant_id`',
                        'SELECT "receiver 字段已存在" as message');
PREPARE stmt_receiver FROM @sql_receiver;
EXECUTE stmt_receiver;
DEALLOCATE PREPARE stmt_receiver;

-- ============================================================
-- 第三部分: 修复索引差异
-- ============================================================

-- 3.1 wms_machine_spare 表添加缺少的索引
SET @exist_idx_mc := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
                      WHERE TABLE_SCHEMA = DATABASE() 
                      AND TABLE_NAME = 'wms_machine_spare' 
                      AND INDEX_NAME = 'idx_machine_code');
SET @sql_idx_mc := IF(@exist_idx_mc = 0, 
                      'ALTER TABLE `wms_machine_spare` ADD KEY `idx_machine_code` (`machine_code`)',
                      'SELECT "idx_machine_code 索引已存在" as message');
PREPARE stmt_idx_mc FROM @sql_idx_mc;
EXECUTE stmt_idx_mc;
DEALLOCATE PREPARE stmt_idx_mc;

SET @exist_idx_si := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
                      WHERE TABLE_SCHEMA = DATABASE() 
                      AND TABLE_NAME = 'wms_machine_spare' 
                      AND INDEX_NAME = 'idx_spare_item_id');
SET @sql_idx_si := IF(@exist_idx_si = 0, 
                      'ALTER TABLE `wms_machine_spare` ADD KEY `idx_spare_item_id` (`spare_item_id`)',
                      'SELECT "idx_spare_item_id 索引已存在" as message');
PREPARE stmt_idx_si FROM @sql_idx_si;
EXECUTE stmt_idx_si;
DEALLOCATE PREPARE stmt_idx_si;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 修复完成
-- ============================================================
SELECT '数据库结构修复完成' as result;
