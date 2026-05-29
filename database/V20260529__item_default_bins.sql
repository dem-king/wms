CREATE TABLE IF NOT EXISTS `wms_item_bin` (
    `id`          BIGINT       NOT NULL COMMENT '主键(雪花ID)',
    `item_id`     BIGINT       NOT NULL COMMENT '物品ID',
    `bin_id`      BIGINT       NOT NULL COMMENT '库位ID',
    `sort_order`  INT          DEFAULT 0 COMMENT '排序号',
    `del_flag`    TINYINT      DEFAULT 0 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`   VARCHAR(64)  DEFAULT '' COMMENT '创建人',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`   VARCHAR(64)  DEFAULT '' COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_item_bin` (`item_id`, `bin_id`),
    KEY `idx_item_id` (`item_id`),
    KEY `idx_bin_id` (`bin_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物品默认库位关联表';
