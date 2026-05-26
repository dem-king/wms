-- 统一认证日志表到系统标准日志表

CREATE TABLE IF NOT EXISTS `sys_oper_log` (
    `id`              BIGINT       NOT NULL COMMENT '主键(雪花ID)',
    `module`          VARCHAR(64)  DEFAULT '' COMMENT '操作模块',
    `type`            VARCHAR(32)  DEFAULT '' COMMENT '操作类型',
    `desc`            VARCHAR(256) DEFAULT '' COMMENT '操作描述',
    `operator_id`     BIGINT       DEFAULT NULL COMMENT '操作人ID',
    `operator_name`   VARCHAR(64)  DEFAULT '' COMMENT '操作人姓名',
    `request_url`     VARCHAR(256) DEFAULT '' COMMENT '请求URL',
    `request_method`  VARCHAR(16)  DEFAULT '' COMMENT '请求方法',
    `request_params`  TEXT         DEFAULT NULL COMMENT '请求参数(脱敏后)',
    `response_result` TEXT         DEFAULT NULL COMMENT '响应结果',
    `oper_ip`         VARCHAR(64)  DEFAULT '' COMMENT '操作IP',
    `status`          VARCHAR(16)  DEFAULT 'SUCCESS' COMMENT '操作状态(SUCCESS/FAIL)',
    `error_msg`       VARCHAR(512) DEFAULT '' COMMENT '异常信息',
    `cost_time`       BIGINT       DEFAULT 0 COMMENT '耗时(毫秒)',
    `oper_time`       DATETIME     DEFAULT NULL COMMENT '操作时间',
    `del_flag`        TINYINT      DEFAULT 0 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT '' COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT '' COMMENT '更新人',
    PRIMARY KEY (`id`),
    INDEX `idx_oper_time` (`oper_time`),
    INDEX `idx_operator_id` (`operator_id`),
    INDEX `idx_module` (`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志表';

CREATE TABLE IF NOT EXISTS `sys_login_log` (
    `id`              BIGINT       NOT NULL COMMENT '主键(雪花ID)',
    `username`        VARCHAR(64)  DEFAULT '' COMMENT '用户名',
    `user_id`         BIGINT       DEFAULT NULL COMMENT '用户ID',
    `login_ip`        VARCHAR(64)  DEFAULT '' COMMENT '登录IP',
    `login_location`  VARCHAR(256) DEFAULT '' COMMENT '登录地点',
    `browser`         VARCHAR(128) DEFAULT '' COMMENT '浏览器',
    `os`              VARCHAR(128) DEFAULT '' COMMENT '操作系统',
    `status`          VARCHAR(16)  DEFAULT 'SUCCESS' COMMENT '登录状态(SUCCESS/FAIL/LOGOUT)',
    `fail_reason`     VARCHAR(256) DEFAULT '' COMMENT '失败原因',
    `login_time`      DATETIME     DEFAULT NULL COMMENT '登录时间',
    `del_flag`        TINYINT      DEFAULT 0 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT '' COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT '' COMMENT '更新人',
    PRIMARY KEY (`id`),
    INDEX `idx_login_time` (`login_time`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='登录日志表';

SET @auth_login_log_exists := (
    SELECT COUNT(1)
    FROM information_schema.tables
    WHERE table_schema = DATABASE()
      AND table_name = 'auth_login_log'
);

SET @login_migration_sql := IF(
    @auth_login_log_exists > 0,
    'INSERT INTO `sys_login_log` (`id`, `username`, `user_id`, `login_ip`, `browser`, `status`, `fail_reason`, `login_time`, `del_flag`, `create_time`, `create_by`, `update_time`, `update_by`)
     SELECT
         a.`id`,
         COALESCE(a.`username`, ''''),
         a.`user_id`,
         COALESCE(a.`login_ip`, ''''),
         COALESCE(a.`user_agent`, ''''),
         CASE a.`login_result`
             WHEN 0 THEN ''SUCCESS''
             WHEN 1 THEN ''FAIL''
             WHEN 2 THEN ''FAIL''
             ELSE ''FAIL''
         END AS `status`,
         COALESCE(a.`fail_reason`, ''''),
         a.`login_time`,
         COALESCE(a.`del_flag`, 0),
         COALESCE(a.`create_time`, a.`login_time`, CURRENT_TIMESTAMP),
         COALESCE(a.`create_by`, ''''),
         COALESCE(a.`update_time`, a.`create_time`, a.`login_time`, CURRENT_TIMESTAMP),
         COALESCE(a.`update_by`, '''')
     FROM `auth_login_log` a
     LEFT JOIN `sys_login_log` s ON s.`id` = a.`id`
     WHERE s.`id` IS NULL',
    'SELECT 1'
);

PREPARE stmt_login_migration FROM @login_migration_sql;
EXECUTE stmt_login_migration;
DEALLOCATE PREPARE stmt_login_migration;

SET @auth_oper_log_exists := (
    SELECT COUNT(1)
    FROM information_schema.tables
    WHERE table_schema = DATABASE()
      AND table_name = 'auth_oper_log'
);

SET @oper_migration_sql := IF(
    @auth_oper_log_exists > 0,
    'INSERT INTO `sys_oper_log` (`id`, `module`, `type`, `desc`, `operator_id`, `operator_name`, `oper_ip`, `status`, `error_msg`, `oper_time`, `del_flag`, `create_time`, `create_by`, `update_time`, `update_by`)
     SELECT
         a.`id`,
         ''auth'' AS `module`,
         COALESCE(a.`oper_type`, ''''),
         COALESCE(a.`request_id`, ''''),
         a.`user_id`,
         COALESCE(a.`username`, ''''),
         COALESCE(a.`client_ip`, ''''),
         CASE a.`oper_result`
             WHEN 1 THEN ''SUCCESS''
             ELSE ''FAIL''
         END AS `status`,
         '''' AS `error_msg`,
         a.`oper_time`,
         COALESCE(a.`del_flag`, 0),
         COALESCE(a.`create_time`, a.`oper_time`, CURRENT_TIMESTAMP),
         COALESCE(a.`create_by`, ''''),
         COALESCE(a.`update_time`, a.`create_time`, a.`oper_time`, CURRENT_TIMESTAMP),
         COALESCE(a.`update_by`, '''')
     FROM `auth_oper_log` a
     LEFT JOIN `sys_oper_log` s ON s.`id` = a.`id`
     WHERE s.`id` IS NULL',
    'SELECT 1'
);

PREPARE stmt_oper_migration FROM @oper_migration_sql;
EXECUTE stmt_oper_migration;
DEALLOCATE PREPARE stmt_oper_migration;

-- 历史 auth_* 表删除请在确认迁移完成后手动执行，默认不自动删除。
-- DROP TABLE IF EXISTS `auth_login_log`;
-- DROP TABLE IF EXISTS `auth_oper_log`;
