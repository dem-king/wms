-- P3业务闭环完善迁移脚本
-- 1. 归还明细表添加实际归还数量字段
ALTER TABLE `wms_return_detail`
    ADD COLUMN `actual_quantity` INT DEFAULT NULL COMMENT '实际归还数量(数量不符时记录)' AFTER `abnormal_desc`;

-- 2. 审批配置表添加超时处理方式字段(如尚未存在)
-- 注意：timeout_hours字段在原DDL中已存在，此处仅补充timeout_action
ALTER TABLE `wms_approval_config`
    ADD COLUMN `timeout_action` TINYINT DEFAULT 1 COMMENT '超时处理方式(1-自动提醒 2-自动取消)' AFTER `timeout_hours`;
