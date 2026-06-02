-- ============================================================
-- 通知中心增量脚本
-- 适用范围：在已有库上新增站内信消息表，以及消息中心菜单/权限。
-- 注意：如果已执行 database/V20260601__approval_flow_completion.sql，
--       则无需重复执行 wms_approval_order.config_id 相关变更。
-- ============================================================

CREATE TABLE IF NOT EXISTS `sys_message` (
    `id`              BIGINT       NOT NULL COMMENT '主键(雪花ID)',
    `receiver_id`     BIGINT       NOT NULL COMMENT '接收人ID',
    `title`           VARCHAR(128) NOT NULL COMMENT '消息标题',
    `content`         VARCHAR(512) NOT NULL COMMENT '消息内容',
    `message_type`    VARCHAR(64)  NOT NULL COMMENT '消息类型',
    `message_level`   VARCHAR(32)  NOT NULL DEFAULT 'INFO' COMMENT '消息级别(INFO-普通 WARNING-警告)',
    `business_key`    VARCHAR(128) NOT NULL COMMENT '业务去重键',
    `target_url`      VARCHAR(256) DEFAULT '' COMMENT '目标跳转地址',
    `read_status`     TINYINT      NOT NULL DEFAULT 0 COMMENT '读取状态(0-未读 1-已读)',
    `read_time`       DATETIME     DEFAULT NULL COMMENT '读取时间',
    `del_flag`        TINYINT      DEFAULT 0 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT '' COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT '' COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_receiver_type_business` (`receiver_id`, `message_type`, `business_key`),
    KEY `idx_receiver_read_time` (`receiver_id`, `read_status`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内信消息表';

-- 消息中心菜单：放在“系统管理”下。
INSERT IGNORE INTO `sys_menu`
(`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`,
 `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`)
VALUES
(108, '消息中心', 'system:message', 100, 2, 'message', 'system/message', NULL, 'Bell',
 0, 0, 1, 1, 8, NULL, 0),
(1081, '消息查询', 'system:message:list', 108, 3, NULL, NULL, NULL, NULL,
 0, 0, 1, 1, 1, 'system:message:list', 0),
(1082, '消息已读', 'system:message:read', 108, 3, NULL, NULL, NULL, NULL,
 0, 0, 1, 1, 2, 'system:message:read', 0);

INSERT IGNORE INTO `sys_permission`
(`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
VALUES
(20098, '消息查询', 'system:message:list', 2, 0, 1081, 1, 0),
(20099, '消息已读', 'system:message:read', 2, 0, 1082, 1, 0);

-- 管理员角色授权消息中心菜单和权限。
INSERT IGNORE INTO `sys_role_menu`
(`id`, `role_id`, `menu_id`, `del_flag`)
VALUES
(9108, 1, 108, 0),
(91081, 1, 1081, 0),
(91082, 1, 1082, 0);

INSERT IGNORE INTO `sys_role_permission`
(`id`, `role_id`, `perm_id`, `del_flag`)
VALUES
(920098, 1, 20098, 0),
(920099, 1, 20099, 0);
