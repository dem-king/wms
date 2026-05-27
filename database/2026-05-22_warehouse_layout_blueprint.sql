-- ============================================================
-- 库房平面底图 Task1 增量脚本
-- 目标：补齐布局底图数据库基线与辅助元素表
-- 要求：本脚本使用 ADD COLUMN IF NOT EXISTS，需在 MySQL 8.0.29+ 执行
--       若环境低于该版本，请先升级数据库，或将列存在性判断改为手工分步执行
-- ============================================================

SET NAMES utf8mb4;

ALTER TABLE `wms_warehouse`
    ADD COLUMN IF NOT EXISTS `layout_width` INT DEFAULT NULL COMMENT '布局画布宽度' AFTER `height`,
    ADD COLUMN IF NOT EXISTS `layout_height` INT DEFAULT NULL COMMENT '布局画布高度' AFTER `layout_width`,
    ADD COLUMN IF NOT EXISTS `layout_scale` DECIMAL(10,2) DEFAULT NULL COMMENT '布局比例尺' AFTER `layout_height`,
    ADD COLUMN IF NOT EXISTS `layout_background_version` VARCHAR(32) DEFAULT NULL COMMENT '底图版本号' AFTER `layout_scale`;

ALTER TABLE `wms_area`
    ADD COLUMN IF NOT EXISTS `shape_type` VARCHAR(16) DEFAULT NULL COMMENT '区域形状(rect/polygon)' AFTER `coord_y`,
    ADD COLUMN IF NOT EXISTS `polygon_points` TEXT DEFAULT NULL COMMENT '多边形点位JSON' AFTER `shape_type`,
    ADD COLUMN IF NOT EXISTS `label_x` INT DEFAULT NULL COMMENT '标题X坐标' AFTER `polygon_points`,
    ADD COLUMN IF NOT EXISTS `label_y` INT DEFAULT NULL COMMENT '标题Y坐标' AFTER `label_x`;

ALTER TABLE `wms_cabinet`
    ADD COLUMN IF NOT EXISTS `layout_width` INT DEFAULT NULL COMMENT '渲染宽度' AFTER `position_y`,
    ADD COLUMN IF NOT EXISTS `layout_height` INT DEFAULT NULL COMMENT '渲染高度' AFTER `layout_width`,
    ADD COLUMN IF NOT EXISTS `rotation` INT DEFAULT 0 COMMENT '旋转角度' AFTER `layout_height`;

CREATE TABLE `wms_layout_element` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `warehouse_id`    BIGINT       NOT NULL                COMMENT '所属库房ID',
    `area_id`         BIGINT       DEFAULT NULL            COMMENT '关联区域ID',
    `element_code`    VARCHAR(64)  DEFAULT NULL            COMMENT '元素编码',
    `element_name`    VARCHAR(100) NOT NULL                COMMENT '元素名称',
    `element_type`    VARCHAR(32)  NOT NULL                COMMENT '元素类型(wall/aisle/reserved/device/text/dimension)',
    `shape_type`      VARCHAR(16)  NOT NULL                COMMENT '形状类型(line/rect/polygon/circle/text)',
    `position_x`      INT          DEFAULT NULL            COMMENT 'X坐标',
    `position_y`      INT          DEFAULT NULL            COMMENT 'Y坐标',
    `layout_width`    INT          DEFAULT NULL            COMMENT '宽度',
    `layout_height`   INT          DEFAULT NULL            COMMENT '高度',
    `rotation`        INT          DEFAULT 0               COMMENT '旋转角度',
    `point_data`      TEXT         DEFAULT NULL            COMMENT '点位数据JSON',
    `style_data`      TEXT         DEFAULT NULL            COMMENT '样式数据JSON',
    `label_text`      VARCHAR(255) DEFAULT NULL            COMMENT '展示文本',
    `sort_order`      INT          DEFAULT 0               COMMENT '排序号',
    `status`          TINYINT      DEFAULT 1               COMMENT '状态(1-启用 0-禁用)',
    `remark`          VARCHAR(500) DEFAULT NULL            COMMENT '备注',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_warehouse_id` (`warehouse_id`),
    KEY `idx_area_id` (`area_id`),
    KEY `idx_type_sort` (`element_type`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库房布局元素表';
