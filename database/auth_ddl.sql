-- 认证登录日志表
CREATE TABLE IF NOT EXISTS `auth_login_log` (
    `id` BIGINT NOT NULL COMMENT '主键(雪花ID)',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
    `login_result` INT NOT NULL COMMENT '登录结果(0成功 1失败 2锁定)',
    `login_ip` VARCHAR(50) DEFAULT NULL COMMENT '登录IP',
    `user_agent` VARCHAR(200) DEFAULT NULL COMMENT '用户代理',
    `fail_reason` VARCHAR(100) DEFAULT NULL COMMENT '失败原因',
    `login_time` DATETIME NOT NULL COMMENT '登录时间',
    `del_flag` INT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='认证登录日志表';

-- 认证操作日志表
CREATE TABLE IF NOT EXISTS `auth_oper_log` (
    `id` BIGINT NOT NULL COMMENT '主键(雪花ID)',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
    `oper_type` VARCHAR(30) NOT NULL COMMENT '操作类型',
    `oper_result` INT NOT NULL COMMENT '操作结果(1成功 0失败)',
    `client_ip` VARCHAR(50) DEFAULT NULL COMMENT '客户端IP',
    `request_id` VARCHAR(50) DEFAULT NULL COMMENT '请求ID',
    `oper_time` DATETIME NOT NULL COMMENT '操作时间',
    `del_flag` INT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建人',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_oper_time` (`oper_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='认证操作日志表';
