CREATE TABLE wms_machine_spare (
    `id`              BIGINT       NOT NULL COMMENT '主键(雪花ID)',
    `machine_name`    VARCHAR(128) NOT NULL COMMENT '机器名称',
    `machine_code`    VARCHAR(64)  NOT NULL COMMENT '机器编号',
    `spare_item_id`   BIGINT       NOT NULL COMMENT '备件物品ID',
    `quantity`        INT          DEFAULT 1 COMMENT '数量',
    `remark`          VARCHAR(512) DEFAULT '' COMMENT '备注',
    `del_flag`        TINYINT      DEFAULT 0 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT '' COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT '' COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_machine_code` (`machine_code`),
    KEY `idx_spare_item_id` (`spare_item_id`)
) COMMENT='机器-备件关联表';
