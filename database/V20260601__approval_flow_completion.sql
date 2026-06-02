-- 审批流程完善：审批单绑定发起时使用的审批配置
ALTER TABLE `wms_approval_order`
    ADD COLUMN `config_id` BIGINT DEFAULT NULL COMMENT '审批配置ID' AFTER `biz_type`;

CREATE INDEX `idx_approval_order_config_step`
    ON `wms_approval_order` (`config_id`, `current_step`);
