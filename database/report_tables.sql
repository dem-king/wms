-- ============================================================
-- 统计报表预聚合表
-- ============================================================

-- 入库日聚合表
CREATE TABLE IF NOT EXISTS `report_inbound_daily` (
    `id`              BIGINT       NOT NULL                  COMMENT '主键(雪花ID)',
    `stat_date`       DATE         NOT NULL                  COMMENT '统计日期',
    `warehouse_id`    BIGINT       NOT NULL                  COMMENT '库房ID',
    `category_id`     BIGINT       NOT NULL                  COMMENT '物品分类ID',
    `total_quantity`  INT          NOT NULL DEFAULT 0        COMMENT '入库总数量',
    `total_amount`    DECIMAL(12,2) NOT NULL DEFAULT 0.00   COMMENT '入库总金额',
    `order_count`     INT          NOT NULL DEFAULT 0        COMMENT '入库单据数',
    `del_flag`        TINYINT      DEFAULT 0                 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''                COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''                COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_stat_date` (`stat_date`),
    KEY `idx_warehouse_category` (`warehouse_id`, `category_id`),
    UNIQUE KEY `uk_date_warehouse_category` (`stat_date`, `warehouse_id`, `category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='入库日聚合表';

-- 出库日聚合表
CREATE TABLE IF NOT EXISTS `report_outbound_daily` (
    `id`              BIGINT       NOT NULL                  COMMENT '主键(雪花ID)',
    `stat_date`       DATE         NOT NULL                  COMMENT '统计日期',
    `warehouse_id`    BIGINT       NOT NULL                  COMMENT '库房ID',
    `category_id`     BIGINT       NOT NULL                  COMMENT '物品分类ID',
    `total_quantity`  INT          NOT NULL DEFAULT 0        COMMENT '出库总数量',
    `total_amount`    DECIMAL(12,2) NOT NULL DEFAULT 0.00   COMMENT '出库总金额',
    `order_count`     INT          NOT NULL DEFAULT 0        COMMENT '出库单据数',
    `del_flag`        TINYINT      DEFAULT 0                 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''                COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''                COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_stat_date` (`stat_date`),
    KEY `idx_warehouse_category` (`warehouse_id`, `category_id`),
    UNIQUE KEY `uk_date_warehouse_category` (`stat_date`, `warehouse_id`, `category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='出库日聚合表';

-- 库存日快照表
CREATE TABLE IF NOT EXISTS `report_stock_daily` (
    `id`              BIGINT       NOT NULL                  COMMENT '主键(雪花ID)',
    `stat_date`       DATE         NOT NULL                  COMMENT '快照日期',
    `warehouse_id`    BIGINT       NOT NULL                  COMMENT '库房ID',
    `category_id`     BIGINT       NOT NULL                  COMMENT '物品分类ID',
    `total_quantity`  INT          NOT NULL DEFAULT 0        COMMENT '库存总数量',
    `total_amount`    DECIMAL(12,2) NOT NULL DEFAULT 0.00   COMMENT '库存总金额',
    `del_flag`        TINYINT      DEFAULT 0                 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''                COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''                COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_stat_date` (`stat_date`),
    KEY `idx_warehouse_category` (`warehouse_id`, `category_id`),
    UNIQUE KEY `uk_date_warehouse_category` (`stat_date`, `warehouse_id`, `category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='库存日快照表';

-- 归还日聚合表
CREATE TABLE IF NOT EXISTS `report_return_daily` (
    `id`              BIGINT       NOT NULL                  COMMENT '主键(雪花ID)',
    `stat_date`       DATE         NOT NULL                  COMMENT '统计日期',
    `warehouse_id`    BIGINT       NOT NULL                  COMMENT '库房ID',
    `category_id`     BIGINT       NOT NULL                  COMMENT '物品分类ID',
    `total_quantity`  INT          NOT NULL DEFAULT 0        COMMENT '归还总数量',
    `normal_quantity` INT          NOT NULL DEFAULT 0        COMMENT '正常归还数量',
    `damaged_quantity` INT         NOT NULL DEFAULT 0        COMMENT '损坏归还数量',
    `order_count`     INT          NOT NULL DEFAULT 0        COMMENT '归还单据数',
    `del_flag`        TINYINT      DEFAULT 0                 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''                COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''                COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_stat_date` (`stat_date`),
    KEY `idx_warehouse_category` (`warehouse_id`, `category_id`),
    UNIQUE KEY `uk_date_warehouse_category` (`stat_date`, `warehouse_id`, `category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='归还日聚合表';

-- 报废日聚合表
CREATE TABLE IF NOT EXISTS `report_scrap_daily` (
    `id`              BIGINT       NOT NULL                  COMMENT '主键(雪花ID)',
    `stat_date`       DATE         NOT NULL                  COMMENT '统计日期',
    `warehouse_id`    BIGINT       NOT NULL                  COMMENT '库房ID',
    `category_id`     BIGINT       NOT NULL                  COMMENT '物品分类ID',
    `total_quantity`  INT          NOT NULL DEFAULT 0        COMMENT '报废总数量',
    `total_amount`    DECIMAL(12,2) NOT NULL DEFAULT 0.00   COMMENT '报废总金额',
    `order_count`     INT          NOT NULL DEFAULT 0        COMMENT '报废单据数',
    `del_flag`        TINYINT      DEFAULT 0                 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''                COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''                COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_stat_date` (`stat_date`),
    KEY `idx_warehouse_category` (`warehouse_id`, `category_id`),
    UNIQUE KEY `uk_date_warehouse_category` (`stat_date`, `warehouse_id`, `category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='报废日聚合表';

-- 调拨日聚合表
CREATE TABLE IF NOT EXISTS `report_transfer_daily` (
    `id`                BIGINT       NOT NULL                  COMMENT '主键(雪花ID)',
    `stat_date`         DATE         NOT NULL                  COMMENT '统计日期',
    `from_warehouse_id` BIGINT       NOT NULL                  COMMENT '调出库房ID',
    `to_warehouse_id`   BIGINT       NOT NULL                  COMMENT '调入库房ID',
    `category_id`       BIGINT       NOT NULL                  COMMENT '物品分类ID',
    `total_quantity`    INT          NOT NULL DEFAULT 0        COMMENT '调拨总数量',
    `total_amount`      DECIMAL(12,2) NOT NULL DEFAULT 0.00   COMMENT '调拨总金额',
    `order_count`       INT          NOT NULL DEFAULT 0        COMMENT '调拨单据数',
    `del_flag`          TINYINT      DEFAULT 0                 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`         VARCHAR(64)  DEFAULT ''                COMMENT '创建人',
    `update_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`         VARCHAR(64)  DEFAULT ''                COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_stat_date` (`stat_date`),
    KEY `idx_from_to_category` (`from_warehouse_id`, `to_warehouse_id`, `category_id`),
    UNIQUE KEY `uk_date_from_to_category` (`stat_date`, `from_warehouse_id`, `to_warehouse_id`, `category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='调拨日聚合表';

-- 预警日聚合表
CREATE TABLE IF NOT EXISTS `report_alert_daily` (
    `id`                   BIGINT       NOT NULL                  COMMENT '主键(雪花ID)',
    `stat_date`            DATE         NOT NULL                  COMMENT '统计日期',
    `warehouse_id`         BIGINT       NOT NULL                  COMMENT '库房ID',
    `alert_type`           VARCHAR(20)  NOT NULL                  COMMENT '预警类型(STOCK_LOW/STOCK_HIGH)',
    `trigger_count`        INT          NOT NULL DEFAULT 0        COMMENT '预警触发次数',
    `affected_item_count`  INT          NOT NULL DEFAULT 0        COMMENT '涉及物品种类数',
    `resolved_count`       INT          NOT NULL DEFAULT 0        COMMENT '已处理次数',
    `del_flag`             TINYINT      DEFAULT 0                 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`          DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`            VARCHAR(64)  DEFAULT ''                COMMENT '创建人',
    `update_time`          DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`            VARCHAR(64)  DEFAULT ''                COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_stat_date` (`stat_date`),
    KEY `idx_warehouse_type` (`warehouse_id`, `alert_type`),
    UNIQUE KEY `uk_date_warehouse_type` (`stat_date`, `warehouse_id`, `alert_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='预警日聚合表';

-- 库存预警表(P4-38扩展版本，供预警日聚合任务和预警扫描任务使用)
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
