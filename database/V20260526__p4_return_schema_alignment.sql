-- P4归还单表结构对齐迁移脚本
-- 目标：让现有数据库兼容当前归还单代码模型，避免实体字段与表结构不一致

SET NAMES utf8mb4;

-- 1. 归还单主表补齐代码使用的字段
SET @exist_outbound_order_id := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'wms_return_order'
      AND COLUMN_NAME = 'outbound_order_id'
);
SET @sql_outbound_order_id := IF(
    @exist_outbound_order_id = 0,
    'ALTER TABLE `wms_return_order` ADD COLUMN `outbound_order_id` BIGINT DEFAULT NULL COMMENT "关联出库单ID" AFTER `order_no`',
    'SELECT "outbound_order_id 字段已存在" AS message'
);
PREPARE stmt_outbound_order_id FROM @sql_outbound_order_id;
EXECUTE stmt_outbound_order_id;
DEALLOCATE PREPARE stmt_outbound_order_id;

SET @exist_receiver := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'wms_return_order'
      AND COLUMN_NAME = 'receiver'
);
SET @sql_receiver := IF(
    @exist_receiver = 0,
    'ALTER TABLE `wms_return_order` ADD COLUMN `receiver` VARCHAR(64) DEFAULT "" COMMENT "归还人" AFTER `outbound_order_id`',
    'SELECT "receiver 字段已存在" AS message'
);
PREPARE stmt_receiver FROM @sql_receiver;
EXECUTE stmt_receiver;
DEALLOCATE PREPARE stmt_receiver;

SET @exist_order_status := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'wms_return_order'
      AND COLUMN_NAME = 'order_status'
);
SET @sql_order_status := IF(
    @exist_order_status = 0,
    'ALTER TABLE `wms_return_order` ADD COLUMN `order_status` TINYINT DEFAULT 0 COMMENT "单据状态(0-草稿 1-待审批 2-审批中 3-已通过 4-已驳回 5-已完成)" AFTER `receiver`',
    'SELECT "order_status 字段已存在" AS message'
);
PREPARE stmt_order_status FROM @sql_order_status;
EXECUTE stmt_order_status;
DEALLOCATE PREPARE stmt_order_status;

-- 尝试把旧字段数据迁移到新字段，保持历史数据可读
SET @has_outbound_id := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'wms_return_order'
      AND COLUMN_NAME = 'outbound_id'
);
SET @sql_backfill_outbound := IF(
    @has_outbound_id > 0,
    'UPDATE `wms_return_order` SET `outbound_order_id` = `outbound_id` WHERE `outbound_order_id` IS NULL',
    'SELECT "outbound_id 旧字段不存在，无需回填" AS message'
);
PREPARE stmt_backfill_outbound FROM @sql_backfill_outbound;
EXECUTE stmt_backfill_outbound;
DEALLOCATE PREPARE stmt_backfill_outbound;

SET @has_return_status_main := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'wms_return_order'
      AND COLUMN_NAME = 'return_status'
);
SET @sql_backfill_order_status := IF(
    @has_return_status_main > 0,
    'UPDATE `wms_return_order` SET `order_status` = 5 WHERE `order_status` IS NULL OR `order_status` = 0',
    'SELECT "return_status 旧字段不存在，无需回填 order_status" AS message'
);
PREPARE stmt_backfill_order_status FROM @sql_backfill_order_status;
EXECUTE stmt_backfill_order_status;
DEALLOCATE PREPARE stmt_backfill_order_status;

-- 2. 归还明细表补齐代码使用的字段
SET @exist_condition_status := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'wms_return_detail'
      AND COLUMN_NAME = 'condition_status'
);
SET @sql_condition_status := IF(
    @exist_condition_status = 0,
    'ALTER TABLE `wms_return_detail` ADD COLUMN `condition_status` TINYINT DEFAULT 1 COMMENT "物品状态(1-正常 2-损坏 3-丢失 4-数量不符)" AFTER `quantity`',
    'SELECT "condition_status 字段已存在" AS message'
);
PREPARE stmt_condition_status FROM @sql_condition_status;
EXECUTE stmt_condition_status;
DEALLOCATE PREPARE stmt_condition_status;

SET @exist_abnormal_remark := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'wms_return_detail'
      AND COLUMN_NAME = 'abnormal_remark'
);
SET @sql_abnormal_remark := IF(
    @exist_abnormal_remark = 0,
    'ALTER TABLE `wms_return_detail` ADD COLUMN `abnormal_remark` VARCHAR(500) DEFAULT NULL COMMENT "异常说明" AFTER `condition_status`',
    'SELECT "abnormal_remark 字段已存在" AS message'
);
PREPARE stmt_abnormal_remark FROM @sql_abnormal_remark;
EXECUTE stmt_abnormal_remark;
DEALLOCATE PREPARE stmt_abnormal_remark;

SET @exist_actual_quantity := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'wms_return_detail'
      AND COLUMN_NAME = 'actual_quantity'
);
SET @sql_actual_quantity := IF(
    @exist_actual_quantity = 0,
    'ALTER TABLE `wms_return_detail` ADD COLUMN `actual_quantity` INT DEFAULT NULL COMMENT "实际归还数量(数量不符时记录)" AFTER `abnormal_remark`',
    'SELECT "actual_quantity 字段已存在" AS message'
);
PREPARE stmt_actual_quantity FROM @sql_actual_quantity;
EXECUTE stmt_actual_quantity;
DEALLOCATE PREPARE stmt_actual_quantity;

SET @has_return_status_detail := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'wms_return_detail'
      AND COLUMN_NAME = 'return_status'
);
SET @sql_backfill_condition_status := IF(
    @has_return_status_detail > 0,
    'UPDATE `wms_return_detail` SET `condition_status` = `return_status` WHERE `condition_status` IS NULL OR `condition_status` = 1',
    'SELECT "return_status 旧字段不存在，无需回填 condition_status" AS message'
);
PREPARE stmt_backfill_condition_status FROM @sql_backfill_condition_status;
EXECUTE stmt_backfill_condition_status;
DEALLOCATE PREPARE stmt_backfill_condition_status;

SET @has_abnormal_desc := (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'wms_return_detail'
      AND COLUMN_NAME = 'abnormal_desc'
);
SET @sql_backfill_abnormal_remark := IF(
    @has_abnormal_desc > 0,
    'UPDATE `wms_return_detail` SET `abnormal_remark` = `abnormal_desc` WHERE (`abnormal_remark` IS NULL OR `abnormal_remark` = "") AND `abnormal_desc` IS NOT NULL',
    'SELECT "abnormal_desc 旧字段不存在，无需回填 abnormal_remark" AS message'
);
PREPARE stmt_backfill_abnormal_remark FROM @sql_backfill_abnormal_remark;
EXECUTE stmt_backfill_abnormal_remark;
DEALLOCATE PREPARE stmt_backfill_abnormal_remark;
