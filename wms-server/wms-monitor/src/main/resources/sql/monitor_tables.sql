-- ============================================================
-- 监控模块表
-- ============================================================

-- 库存预警表(完整版本，在P4-34基础版本上扩展字段)
-- 注意：如果P4-34已创建基础版本，需执行ALTER TABLE添加新字段
CREATE TABLE IF NOT EXISTS `monitor_stock_alert` (
    `id`                BIGINT       NOT NULL                  COMMENT '主键(雪花ID)',
    `alert_type`        VARCHAR(20)  NOT NULL                  COMMENT '预警类型(STOCK_LOW/STOCK_HIGH)',
    `item_id`           BIGINT       NOT NULL                  COMMENT '物品ID',
    `item_name`         VARCHAR(200) DEFAULT ''                COMMENT '物品名称',
    `item_code`         VARCHAR(64)  DEFAULT ''                COMMENT '物品编码',
    `warehouse_id`      BIGINT       NOT NULL                  COMMENT '库房ID',
    `warehouse_name`    VARCHAR(200) DEFAULT ''                COMMENT '库房名称',
    `current_quantity`  INT          DEFAULT 0                 COMMENT '当前库存量',
    `threshold_value`   INT          DEFAULT 0                 COMMENT '触发阈值',
    `status`            VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '处理状态(PENDING/RESOLVED)',
    `trigger_time`      DATETIME     DEFAULT NULL              COMMENT '触发时间',
    `is_resolved`       TINYINT      NOT NULL DEFAULT 0        COMMENT '是否已处理(0-未处理 1-已处理,兼容P4-34)',
    `del_flag`          TINYINT      DEFAULT 0                 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`         VARCHAR(64)  DEFAULT ''                COMMENT '创建人',
    `update_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`         VARCHAR(64)  DEFAULT ''                COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_warehouse_type` (`warehouse_id`, `alert_type`),
    KEY `idx_item_warehouse_type` (`item_id`, `warehouse_id`, `alert_type`),
    KEY `idx_status` (`status`),
    KEY `idx_trigger_time` (`trigger_time`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='库存预警表';

-- 逾期归还表
CREATE TABLE IF NOT EXISTS `monitor_overdue_return` (
    `id`                   BIGINT       NOT NULL                  COMMENT '主键(雪花ID)',
    `order_id`             BIGINT       NOT NULL                  COMMENT '借出单ID',
    `order_no`             VARCHAR(64)  DEFAULT ''                COMMENT '借出单号',
    `item_id`              BIGINT       NOT NULL                  COMMENT '物品ID',
    `item_name`            VARCHAR(200) DEFAULT ''                COMMENT '物品名称',
    `item_code`            VARCHAR(64)  DEFAULT ''                COMMENT '物品编码',
    `borrow_quantity`      INT          DEFAULT 0                 COMMENT '借出数量',
    `borrower_name`        VARCHAR(100) DEFAULT ''                COMMENT '借用人姓名',
    `borrow_time`          DATETIME     DEFAULT NULL              COMMENT '借出时间',
    `expected_return_date` DATE         DEFAULT NULL              COMMENT '预计归还日期',
    `overdue_days`         INT          DEFAULT 0                 COMMENT '逾期天数',
    `alert_level`          VARCHAR(20)  NOT NULL DEFAULT 'NORMAL' COMMENT '提醒级别(NORMAL/IMPORTANT/URGENT)',
    `status`               VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '处理状态(PENDING/RESOLVED)',
    `del_flag`             TINYINT      DEFAULT 0                 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`          DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`            VARCHAR(64)  DEFAULT ''                COMMENT '创建人',
    `update_time`          DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`            VARCHAR(64)  DEFAULT ''                COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_item_id` (`item_id`),
    KEY `idx_alert_level` (`alert_level`),
    KEY `idx_status` (`status`),
    KEY `idx_overdue_days` (`overdue_days`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='逾期归还表';

-- ============================================================
-- 如果P4-34基础版本的monitor_stock_alert表已存在，执行以下ALTER语句添加新字段:
-- ALTER TABLE `monitor_stock_alert` ADD COLUMN `item_name` VARCHAR(200) DEFAULT '' COMMENT '物品名称' AFTER `item_id`;
-- ALTER TABLE `monitor_stock_alert` ADD COLUMN `item_code` VARCHAR(64) DEFAULT '' COMMENT '物品编码' AFTER `item_name`;
-- ALTER TABLE `monitor_stock_alert` ADD COLUMN `warehouse_name` VARCHAR(200) DEFAULT '' COMMENT '库房名称' AFTER `warehouse_id`;
-- ALTER TABLE `monitor_stock_alert` ADD COLUMN `current_quantity` INT DEFAULT 0 COMMENT '当前库存量' AFTER `warehouse_name`;
-- ALTER TABLE `monitor_stock_alert` ADD COLUMN `threshold_value` INT DEFAULT 0 COMMENT '触发阈值' AFTER `current_quantity`;
-- ALTER TABLE `monitor_stock_alert` ADD COLUMN `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '处理状态(PENDING/RESOLVED)' AFTER `threshold_value`;
-- ALTER TABLE `monitor_stock_alert` ADD COLUMN `trigger_time` DATETIME DEFAULT NULL COMMENT '触发时间' AFTER `status`;
-- ALTER TABLE `monitor_stock_alert` ADD INDEX `idx_item_warehouse_type` (`item_id`, `warehouse_id`, `alert_type`);
-- ALTER TABLE `monitor_stock_alert` ADD INDEX `idx_status` (`status`);
-- ALTER TABLE `monitor_stock_alert` ADD INDEX `idx_trigger_time` (`trigger_time`);
-- ============================================================
