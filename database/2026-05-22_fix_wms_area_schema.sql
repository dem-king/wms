-- 修复 wms_area 表结构与后端实体不一致的问题
-- 执行场景：已有数据库缺少 area_type / status 字段，导致区域查询报错

ALTER TABLE `wms_area`
    ADD COLUMN `area_type` INT DEFAULT 1 COMMENT '区域类型(1-存储区 2-暂存区 3-操作区 4-退货区)' AFTER `area_code`,
    ADD COLUMN `status` TINYINT DEFAULT 1 COMMENT '状态(1-启用 0-禁用)' AFTER `sort_order`;
