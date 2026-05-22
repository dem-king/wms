-- ============================================================
-- 备品备件库房管理平台 - 完整初始化脚本
-- 数据库: MySQL 8.0+
-- 字符集: utf8mb4
-- 包含: 所有表结构 + 菜单初始化数据
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- 第一部分: 认证模块表 (auth_ddl.sql)
-- ============================================================

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

-- ============================================================
-- 第二部分: WMS业务表 (wms_ddl.sql)
-- ============================================================

-- 一、系统基础模块

-- 1. 部门表
DROP TABLE IF EXISTS `sys_department`;
CREATE TABLE `sys_department` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `dept_name`       VARCHAR(100) NOT NULL                COMMENT '部门名称',
    `dept_code`       VARCHAR(50)  NOT NULL                COMMENT '部门编码',
    `parent_id`       BIGINT       DEFAULT 0               COMMENT '上级部门ID(0为顶级)',
    `sort_order`      INT          DEFAULT 0               COMMENT '排序号',
    `leader`          VARCHAR(50)  DEFAULT NULL            COMMENT '负责人',
    `phone`           VARCHAR(20)  DEFAULT NULL            COMMENT '联系电话',
    `status`          TINYINT      DEFAULT 1               COMMENT '状态(0-禁用 1-启用)',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dept_code` (`dept_code`, `del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 2. 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `username`        VARCHAR(64)  NOT NULL                COMMENT '用户名',
    `password`        VARCHAR(128) NOT NULL                COMMENT '密码(加密存储)',
    `real_name`       VARCHAR(50)  NOT NULL                COMMENT '真实姓名',
    `dept_id`         BIGINT       DEFAULT NULL            COMMENT '所属部门ID',
    `phone`           VARCHAR(20)  DEFAULT NULL            COMMENT '联系电话',
    `email`           VARCHAR(100) DEFAULT NULL            COMMENT '邮箱',
    `avatar`          VARCHAR(255) DEFAULT NULL            COMMENT '头像URL',
    `status`          TINYINT      DEFAULT 1               COMMENT '状态(0-禁用 1-启用)',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`, `del_flag`),
    KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 3. 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `role_name`       VARCHAR(50)  NOT NULL                COMMENT '角色名称',
    `role_code`       VARCHAR(50)  NOT NULL                COMMENT '角色编码',
    `role_desc`       VARCHAR(200) DEFAULT NULL            COMMENT '角色描述',
    `data_scope`      TINYINT      DEFAULT 1               COMMENT '数据范围(1-全部 2-本部门 3-自定义)',
    `status`          TINYINT      DEFAULT 1               COMMENT '状态(0-禁用 1-启用)',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`, `del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 4. 用户-角色关联表
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `id`              BIGINT   NOT NULL COMMENT '主键',
    `user_id`         BIGINT   NOT NULL                COMMENT '用户ID',
    `role_id`         BIGINT   NOT NULL                COMMENT '角色ID',
    `del_flag`        TINYINT  DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64) DEFAULT ''           COMMENT '创建人',
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64) DEFAULT ''           COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`, `del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

-- 5. 菜单表
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `menu_name`       VARCHAR(50)  NOT NULL                COMMENT '菜单名称',
    `menu_code`       VARCHAR(100) NOT NULL                COMMENT '菜单编码',
    `parent_id`       BIGINT       DEFAULT 0               COMMENT '上级菜单ID(0为顶级)',
    `menu_type`       TINYINT      NOT NULL                COMMENT '菜单类型(1-目录 2-菜单 3-按钮/操作)',
    `path`            VARCHAR(255) DEFAULT NULL            COMMENT '路由路径',
    `component`       VARCHAR(255) DEFAULT NULL            COMMENT '前端组件路径',
    `redirect`        VARCHAR(255) DEFAULT NULL            COMMENT '重定向路径',
    `icon`            VARCHAR(100) DEFAULT NULL            COMMENT '菜单图标',
    `is_external`     TINYINT      DEFAULT 0               COMMENT '是否外链(0-否 1-是)',
    `is_cache`        TINYINT      DEFAULT 0               COMMENT '是否缓存(0-否 1-是)',
    `visible`         TINYINT      DEFAULT 1               COMMENT '是否可见(0-隐藏 1-显示)',
    `status`          TINYINT      DEFAULT 1               COMMENT '状态(0-禁用 1-启用)',
    `sort_order`      INT          DEFAULT 0               COMMENT '排序号',
    `perm_code`       VARCHAR(100) DEFAULT NULL            COMMENT '关联权限编码(按钮级菜单必填)',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_menu_code` (`menu_code`, `del_flag`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 6. 角色-菜单关联表
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
    `id`              BIGINT   NOT NULL COMMENT '主键',
    `role_id`         BIGINT   NOT NULL                COMMENT '角色ID',
    `menu_id`         BIGINT   NOT NULL                COMMENT '菜单ID',
    `del_flag`        TINYINT  DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64) DEFAULT ''           COMMENT '创建人',
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64) DEFAULT ''           COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`, `del_flag`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-菜单关联表';

-- 7. 权限表
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `perm_name`       VARCHAR(50)  NOT NULL                COMMENT '权限名称',
    `perm_code`       VARCHAR(100) NOT NULL                COMMENT '权限编码',
    `perm_type`       TINYINT      NOT NULL                COMMENT '类型(1-功能权限 2-数据权限)',
    `parent_id`       BIGINT       DEFAULT 0               COMMENT '上级权限ID',
    `menu_id`         BIGINT       DEFAULT NULL            COMMENT '关联菜单ID(功能权限所属菜单)',
    `status`          TINYINT      DEFAULT 1               COMMENT '状态(0-禁用 1-启用)',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_perm_code` (`perm_code`, `del_flag`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 8. 角色-权限关联表
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
    `id`              BIGINT   NOT NULL COMMENT '主键',
    `role_id`         BIGINT   NOT NULL                COMMENT '角色ID',
    `perm_id`         BIGINT   NOT NULL                COMMENT '权限ID',
    `del_flag`        TINYINT  DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64) DEFAULT ''           COMMENT '创建人',
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64) DEFAULT ''           COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_perm` (`role_id`, `perm_id`, `del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限关联表';

-- 9. 供应商表
DROP TABLE IF EXISTS `sys_supplier`;
CREATE TABLE `sys_supplier` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `supplier_name`   VARCHAR(100) NOT NULL                COMMENT '供应商名称',
    `supplier_code`   VARCHAR(50)  NOT NULL                COMMENT '供应商编码',
    `contact_person`  VARCHAR(50)  DEFAULT NULL            COMMENT '联系人',
    `contact_phone`   VARCHAR(20)  DEFAULT NULL            COMMENT '联系电话',
    `address`         VARCHAR(255) DEFAULT NULL            COMMENT '地址',
    `annual_amount`   DECIMAL(14,2) DEFAULT 0.00          COMMENT '年度采购金额',
    `remark`          VARCHAR(500) DEFAULT NULL            COMMENT '备注',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_supplier_code` (`supplier_code`, `del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- 10. 系统配置表
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `config_key`      VARCHAR(100) NOT NULL                COMMENT '配置键',
    `config_value`    VARCHAR(500) NOT NULL                COMMENT '配置值',
    `config_name`     VARCHAR(100) NOT NULL                COMMENT '配置名称',
    `config_group`    VARCHAR(50)  DEFAULT 'default'       COMMENT '配置分组',
    `config_desc`     VARCHAR(255) DEFAULT NULL            COMMENT '配置说明',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`, `del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 二、库房结构模块

-- 11. 库房表
DROP TABLE IF EXISTS `wms_warehouse`;
CREATE TABLE `wms_warehouse` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `warehouse_name`  VARCHAR(100) NOT NULL                COMMENT '库房名称',
    `warehouse_code`  VARCHAR(50)  NOT NULL                COMMENT '库房编码',
    `address`         VARCHAR(255) DEFAULT NULL            COMMENT '地址',
    `manager`         VARCHAR(100) DEFAULT NULL            COMMENT '负责人',
    `phone`           VARCHAR(20)  DEFAULT NULL            COMMENT '联系电话',
    `area`            DECIMAL(10,2) DEFAULT NULL           COMMENT '面积(平方米)',
    `status`          TINYINT      DEFAULT 1               COMMENT '状态(1-启用 0-禁用)',
    `manager_id`      BIGINT       DEFAULT NULL            COMMENT '管理员用户ID',
    `length`          DECIMAL(10,2) DEFAULT NULL           COMMENT '长度(米)',
    `width`           DECIMAL(10,2) DEFAULT NULL           COMMENT '宽度(米)',
    `height`          DECIMAL(10,2) DEFAULT NULL           COMMENT '高度(米)',
    `remark`          VARCHAR(500) DEFAULT NULL            COMMENT '备注',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_warehouse_code` (`warehouse_code`, `del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库房表';

-- 12. 存放区域表
DROP TABLE IF EXISTS `wms_area`;
CREATE TABLE `wms_area` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `warehouse_id`    BIGINT       NOT NULL                COMMENT '所属库房ID',
    `area_name`       VARCHAR(100) NOT NULL                COMMENT '区域名称',
    `area_code`       VARCHAR(50)  NOT NULL                COMMENT '区域编码',
    `area_type`       INT          DEFAULT 1               COMMENT '区域类型(1-存储区 2-暂存区 3-操作区 4-退货区)',
    `category_id`     BIGINT       DEFAULT NULL            COMMENT '关联主类目ID(按类目分区)',
    `area_color`      VARCHAR(20)  DEFAULT NULL            COMMENT '区域标识颜色',
    `coord_x`         DECIMAL(10,2) DEFAULT NULL           COMMENT 'X坐标(2D图形用)',
    `coord_y`         DECIMAL(10,2) DEFAULT NULL           COMMENT 'Y坐标(2D图形用)',
    `sort_order`      INT          DEFAULT 0               COMMENT '排序号',
    `status`          TINYINT      DEFAULT 1               COMMENT '状态(1-启用 0-禁用)',
    `remark`          VARCHAR(500) DEFAULT NULL            COMMENT '备注',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_area_code` (`warehouse_id`, `area_code`, `del_flag`),
    KEY `idx_warehouse_id` (`warehouse_id`),
    KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存放区域表';

-- 13. 存放柜表
DROP TABLE IF EXISTS `wms_cabinet`;
CREATE TABLE `wms_cabinet` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `area_id`         BIGINT       NOT NULL                COMMENT '所属区域ID',
    `cabinet_name`    VARCHAR(100) NOT NULL                COMMENT '存放柜名称',
    `cabinet_code`    VARCHAR(50)  NOT NULL                COMMENT '存放柜编码',
    `cabinet_type`    VARCHAR(30)  DEFAULT 'normal'        COMMENT '柜类型(normal-普通 locked-加锁 cold-冷藏)',
    `rows`            INT          DEFAULT 1               COMMENT '行数',
    `cols`            INT          DEFAULT 1               COMMENT '列数',
    `coord_x`         DECIMAL(10,2) DEFAULT NULL           COMMENT 'X坐标',
    `coord_y`         DECIMAL(10,2) DEFAULT NULL           COMMENT 'Y坐标',
    `width`           DECIMAL(10,2) DEFAULT NULL           COMMENT '宽度(图形用)',
    `height`          DECIMAL(10,2) DEFAULT NULL           COMMENT '高度(图形用)',
    `sort_order`      INT          DEFAULT 0               COMMENT '排序号',
    `remark`          VARCHAR(500) DEFAULT NULL            COMMENT '备注',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_cabinet_code` (`area_id`, `cabinet_code`, `del_flag`),
    KEY `idx_area_id` (`area_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存放柜表';

-- 14. 库位表
DROP TABLE IF EXISTS `wms_bin`;
CREATE TABLE `wms_bin` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `cabinet_id`      BIGINT       NOT NULL                COMMENT '所属存放柜ID',
    `bin_code`        VARCHAR(50)  NOT NULL                COMMENT '库位编码',
    `row_num`         INT          DEFAULT 1               COMMENT '行号',
    `col_num`         INT          DEFAULT 1               COMMENT '列号',
    `capacity`        INT          DEFAULT 0               COMMENT '容量(0为不限)',
    `used_capacity`   INT          DEFAULT 0               COMMENT '已用容量',
    `bin_status`      TINYINT      DEFAULT 1               COMMENT '状态(0-禁用 1-正常 2-满)',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_bin_code` (`cabinet_id`, `bin_code`, `del_flag`),
    KEY `idx_cabinet_id` (`cabinet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库位表';

-- 三、类目与标签模块

-- 15. 主类目表
DROP TABLE IF EXISTS `wms_category`;
CREATE TABLE `wms_category` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `category_name`   VARCHAR(50)  NOT NULL                COMMENT '类目名称',
    `category_code`   VARCHAR(50)  NOT NULL                COMMENT '类目编码',
    `category_color`  VARCHAR(20)  DEFAULT NULL            COMMENT '标识颜色',
    `icon`            VARCHAR(100) DEFAULT NULL            COMMENT '图标',
    `sort_order`      INT          DEFAULT 0               COMMENT '排序号',
    `is_consumable`   TINYINT      DEFAULT 0               COMMENT '是否消耗品(0-否 1-是)',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_code` (`category_code`, `del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='主类目表';

-- 16. 细分类目表
DROP TABLE IF EXISTS `wms_sub_category`;
CREATE TABLE `wms_sub_category` (
    `id`                  BIGINT       NOT NULL COMMENT '主键',
    `category_id`         BIGINT       NOT NULL                COMMENT '所属主类目ID',
    `sub_category_name`   VARCHAR(50)  NOT NULL                COMMENT '细分类目名称',
    `sub_category_code`   VARCHAR(50)  NOT NULL                COMMENT '细分类目编码',
    `sort_order`          INT          DEFAULT 0               COMMENT '排序号',
    `del_flag`            TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`         DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`           VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`         DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`           VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sub_category_code` (`category_id`, `sub_category_code`, `del_flag`),
    KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='细分类目表';

-- 17. 自定义标签表
DROP TABLE IF EXISTS `wms_tag`;
CREATE TABLE `wms_tag` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `tag_name`        VARCHAR(50)  NOT NULL                COMMENT '标签名称',
    `tag_color`       VARCHAR(20)  DEFAULT NULL            COMMENT '标签颜色',
    `tag_desc`        VARCHAR(200) DEFAULT NULL            COMMENT '标签描述',
    `scope_type`      TINYINT      DEFAULT 0               COMMENT '关联范围(0-全局 1-主类目 2-细分类目 3-具体物品)',
    `scope_id`        BIGINT       DEFAULT NULL            COMMENT '关联范围对象ID',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义标签表';

-- 18. 物品-标签关联表
DROP TABLE IF EXISTS `wms_item_tag`;
CREATE TABLE `wms_item_tag` (
    `id`              BIGINT   NOT NULL COMMENT '主键',
    `item_id`         BIGINT   NOT NULL                COMMENT '物品ID',
    `tag_id`          BIGINT   NOT NULL                COMMENT '标签ID',
    `del_flag`        TINYINT  DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64) DEFAULT ''           COMMENT '创建人',
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64) DEFAULT ''           COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_item_tag` (`item_id`, `tag_id`, `del_flag`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物品-标签关联表';

-- 四、物品与库存模块

-- 19. 物品档案表
DROP TABLE IF EXISTS `wms_item`;
CREATE TABLE `wms_item` (
    `id`                  BIGINT       NOT NULL COMMENT '主键',
    `item_code`           VARCHAR(50)  NOT NULL                COMMENT '物品编号',
    `item_name`           VARCHAR(100) NOT NULL                COMMENT '物品名称',
    `pinyin`              VARCHAR(200) DEFAULT NULL            COMMENT '拼音/首字母(用于检索)',
    `model`               VARCHAR(100) DEFAULT NULL            COMMENT '型号',
    `spec`                VARCHAR(100) DEFAULT NULL            COMMENT '规格',
    `unit`                VARCHAR(20)  DEFAULT NULL            COMMENT '计量单位',
    `brand`               VARCHAR(50)  DEFAULT NULL            COMMENT '品牌',
    `category_id`         BIGINT       NOT NULL                COMMENT '主类目ID',
    `sub_category_id`     BIGINT       DEFAULT NULL            COMMENT '细分类目ID',
    `supplier_id`         BIGINT       DEFAULT NULL            COMMENT '默认供应商ID',
    `status`              TINYINT      DEFAULT 1               COMMENT '状态(1-在库 2-使用中 3-已归还 4-损坏 5-丢失 6-报废 7-闲置)',
    `is_consumable`       TINYINT      DEFAULT 0               COMMENT '是否消耗品(0-否 1-是)',
    `is_returnable`       TINYINT      DEFAULT 1               COMMENT '是否可归还(0-否 1-是)',
    `purchase_price`      DECIMAL(14,2) DEFAULT 0.00          COMMENT '采购单价',
    `stock_qty`           INT          DEFAULT 0               COMMENT '库存数量',
    `stock_lower_limit`   INT          DEFAULT 0               COMMENT '库存下限',
    `stock_upper_limit`   INT          DEFAULT 0               COMMENT '库存上限',
    `replenish_threshold` INT          DEFAULT 0               COMMENT '补货阈值(消耗品专用)',
    `idle_days`           INT          DEFAULT NULL            COMMENT '闲置判定天数',
    `remark`              VARCHAR(500) DEFAULT NULL            COMMENT '备注',
    `del_flag`            TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`         DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`           VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`         DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`           VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_item_code` (`item_code`, `del_flag`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_sub_category_id` (`sub_category_id`),
    KEY `idx_supplier_id` (`supplier_id`),
    KEY `idx_item_name` (`item_name`),
    KEY `idx_pinyin` (`pinyin`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物品档案表';

-- 20. 物品图片表
DROP TABLE IF EXISTS `wms_item_image`;
CREATE TABLE `wms_item_image` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `item_id`         BIGINT       NOT NULL                COMMENT '物品ID',
    `image_url`       VARCHAR(500) NOT NULL                COMMENT '图片URL',
    `image_name`      VARCHAR(100) DEFAULT NULL            COMMENT '图片名称',
    `sort_order`      INT          DEFAULT 0               COMMENT '排序号',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物品图片表';

-- 21. 电子标签表
DROP TABLE IF EXISTS `wms_electronic_label`;
CREATE TABLE `wms_electronic_label` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `label_no`        VARCHAR(50)  NOT NULL                COMMENT '标签编号',
    `label_type`      TINYINT      DEFAULT 1               COMMENT '标签类型(1-二维码 2-条形码 3-RFID)',
    `item_id`         BIGINT       NOT NULL                COMMENT '绑定物品ID',
    `batch_no`        VARCHAR(50)  DEFAULT NULL            COMMENT '批次号(批次对应时使用)',
    `rfid_code`       VARCHAR(100) DEFAULT NULL            COMMENT 'RFID编码',
    `qr_content`      VARCHAR(500) DEFAULT NULL            COMMENT '二维码内容',
    `barcode_content` VARCHAR(200) DEFAULT NULL            COMMENT '条形码内容',
    `label_status`    TINYINT      DEFAULT 1               COMMENT '标签状态(1-在库 2-正在使用 3-已归还 4-报废 5-闲置)',
    `bind_type`       TINYINT      DEFAULT 1               COMMENT '绑定类型(1-单品对应 2-批次对应)',
    `current_user_id` BIGINT       DEFAULT NULL            COMMENT '当前领用人ID',
    `current_dept_id` BIGINT       DEFAULT NULL            COMMENT '当前领用部门ID',
    `borrow_time`     DATETIME     DEFAULT NULL            COMMENT '借出时间',
    `expected_return` DATETIME     DEFAULT NULL            COMMENT '预计归还时间',
    `print_status`    TINYINT      DEFAULT 0               COMMENT '打印状态(0-未打印 1-已打印)',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_label_no` (`label_no`, `del_flag`),
    KEY `idx_item_id` (`item_id`),
    KEY `idx_rfid_code` (`rfid_code`),
    KEY `idx_label_status` (`label_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电子标签表';

-- 22. 库存表
DROP TABLE IF EXISTS `wms_stock`;
CREATE TABLE `wms_stock` (
    `id`              BIGINT        NOT NULL COMMENT '主键',
    `item_id`         BIGINT        NOT NULL                COMMENT '物品ID',
    `bin_id`          BIGINT        DEFAULT NULL            COMMENT '库位ID',
    `warehouse_id`    BIGINT        NOT NULL                COMMENT '库房ID',
    `area_id`         BIGINT        DEFAULT NULL            COMMENT '区域ID',
    `cabinet_id`      BIGINT        DEFAULT NULL            COMMENT '存放柜ID',
    `quantity`        INT           DEFAULT 0               COMMENT '库存数量',
    `locked_quantity` INT           DEFAULT 0               COMMENT '锁定数量(审批中)',
    `amount`          DECIMAL(14,2) DEFAULT 0.00           COMMENT '库存金额',
    `last_inbound_time` DATETIME    DEFAULT NULL            COMMENT '最后入库时间',
    `last_outbound_time` DATETIME   DEFAULT NULL            COMMENT '最后出库时间',
    `del_flag`        TINYINT       DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)   DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)   DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_item_bin` (`item_id`, `bin_id`, `del_flag`),
    KEY `idx_item_id` (`item_id`),
    KEY `idx_bin_id` (`bin_id`),
    KEY `idx_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 23. 机器-备件关联表
DROP TABLE IF EXISTS `wms_machine_spare`;
CREATE TABLE `wms_machine_spare` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `machine_id`      BIGINT       NOT NULL                COMMENT '机器/设备物品ID',
    `spare_id`        BIGINT       NOT NULL                COMMENT '备件物品ID',
    `quantity`        INT          DEFAULT 1               COMMENT '所需数量',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_machine_spare` (`machine_id`, `spare_id`, `del_flag`),
    KEY `idx_spare_id` (`spare_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机器-备件关联表';

-- 五、业务单据模块

-- 24. 入库单主表
DROP TABLE IF EXISTS `wms_inbound_order`;
CREATE TABLE `wms_inbound_order` (
    `id`                BIGINT        NOT NULL COMMENT '主键',
    `order_no`          VARCHAR(50)   NOT NULL                COMMENT '入库单号',
    `order_type`        TINYINT       DEFAULT 1               COMMENT '入库类型(1-采购入库 2-归还入库 3-调拨入库 4-盘盈入库)',
    `supplier_id`       BIGINT        DEFAULT NULL            COMMENT '供应商ID',
    `total_amount`      DECIMAL(14,2) DEFAULT 0.00           COMMENT '总金额',
    `total_quantity`    INT           DEFAULT 0               COMMENT '总数量',
    `order_status`      TINYINT       DEFAULT 0               COMMENT '单据状态(0-草稿 1-待审批 2-审批中 3-已通过 4-已驳回 5-已完成)',
    `inbound_time`      DATETIME      DEFAULT NULL            COMMENT '入库时间',
    `operator_id`       BIGINT        DEFAULT NULL            COMMENT '操作人ID',
    `approval_required` TINYINT       DEFAULT 0               COMMENT '是否需要审批(0-否 1-是)',
    `warehouse_id`      BIGINT        DEFAULT NULL            COMMENT '所属库房ID',
    `remark`            VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    `del_flag`          TINYINT       DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`       DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`         VARCHAR(64)   DEFAULT ''              COMMENT '创建人',
    `update_time`       DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`         VARCHAR(64)   DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`, `del_flag`),
    KEY `idx_supplier_id` (`supplier_id`),
    KEY `idx_order_status` (`order_status`),
    KEY `idx_inbound_time` (`inbound_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单主表';

-- 25. 入库明细表
DROP TABLE IF EXISTS `wms_inbound_detail`;
CREATE TABLE `wms_inbound_detail` (
    `id`              BIGINT        NOT NULL COMMENT '主键',
    `order_id`        BIGINT        NOT NULL                COMMENT '入库单ID',
    `item_id`         BIGINT        NOT NULL                COMMENT '物品ID',
    `label_id`        BIGINT        DEFAULT NULL            COMMENT '电子标签ID(扫码入库时关联)',
    `quantity`        INT           NOT NULL                COMMENT '入库数量',
    `unit_price`      DECIMAL(14,2) DEFAULT 0.00           COMMENT '单价',
    `amount`          DECIMAL(14,2) DEFAULT 0.00           COMMENT '金额',
    `bin_id`          BIGINT        DEFAULT NULL            COMMENT '入库库位ID',
    `del_flag`        TINYINT       DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)   DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)   DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库明细表';

-- 26. 出库/领用单主表
DROP TABLE IF EXISTS `wms_outbound_order`;
CREATE TABLE `wms_outbound_order` (
    `id`                BIGINT        NOT NULL COMMENT '主键',
    `order_no`          VARCHAR(50)   NOT NULL                COMMENT '出库单号',
    `order_type`        TINYINT       DEFAULT 1               COMMENT '出库类型(1-领用出库 2-调拨出库 3-盘亏出库)',
    `applicant_id`      BIGINT        NOT NULL                COMMENT '领用申请人ID',
    `dept_id`           BIGINT        DEFAULT NULL            COMMENT '领用部门ID',
    `purpose`           VARCHAR(200)  DEFAULT NULL            COMMENT '领用用途',
    `total_quantity`    INT           DEFAULT 0               COMMENT '总数量',
    `order_status`      TINYINT       DEFAULT 0               COMMENT '单据状态(0-草稿 1-待审批 2-审批中 3-已通过 4-已驳回 5-已完成)',
    `outbound_time`     DATETIME      DEFAULT NULL            COMMENT '出库时间',
    `expected_return`   DATETIME      DEFAULT NULL            COMMENT '预计归还时间',
    `operator_id`       BIGINT        DEFAULT NULL            COMMENT '操作人ID',
    `approval_required` TINYINT       DEFAULT 0               COMMENT '是否需要审批(0-否 1-是)',
    `warehouse_id`      BIGINT        DEFAULT NULL            COMMENT '所属库房ID',
    `remark`            VARCHAR(500)  DEFAULT NULL            COMMENT '备注',
    `del_flag`          TINYINT       DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`       DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`         VARCHAR(64)   DEFAULT ''              COMMENT '创建人',
    `update_time`       DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`         VARCHAR(64)   DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`, `del_flag`),
    KEY `idx_applicant_id` (`applicant_id`),
    KEY `idx_order_status` (`order_status`),
    KEY `idx_outbound_time` (`outbound_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库/领用单主表';

-- 27. 出库明细表
DROP TABLE IF EXISTS `wms_outbound_detail`;
CREATE TABLE `wms_outbound_detail` (
    `id`              BIGINT        NOT NULL COMMENT '主键',
    `order_id`        BIGINT        NOT NULL                COMMENT '出库单ID',
    `item_id`         BIGINT        NOT NULL                COMMENT '物品ID',
    `label_id`        BIGINT        DEFAULT NULL            COMMENT '电子标签ID(扫码出库时关联)',
    `quantity`        INT           NOT NULL                COMMENT '出库数量',
    `bin_id`          BIGINT        DEFAULT NULL            COMMENT '出库库位ID',
    `is_returnable`   TINYINT       DEFAULT 1               COMMENT '是否需归还(0-否 1-是)',
    `expected_return` DATETIME      DEFAULT NULL            COMMENT '预计归还时间',
    `del_flag`        TINYINT       DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)   DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)   DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库明细表';

-- 28. 归还单主表
DROP TABLE IF EXISTS `wms_return_order`;
CREATE TABLE `wms_return_order` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `order_no`        VARCHAR(50)  NOT NULL                COMMENT '归还单号',
    `outbound_id`     BIGINT       NOT NULL                COMMENT '关联出库单ID',
    `returner_id`     BIGINT       NOT NULL                COMMENT '归还人ID',
    `handler_id`      BIGINT       DEFAULT NULL            COMMENT '经办人ID',
    `return_time`     DATETIME     DEFAULT NULL            COMMENT '归还时间',
    `return_status`   TINYINT      DEFAULT 1               COMMENT '归还状态(1-正常 2-损坏 3-丢失 4-数量不符)',
    `is_overdue`      TINYINT      DEFAULT 0               COMMENT '是否逾期(0-否 1-是)',
    `remark`          VARCHAR(500) DEFAULT NULL            COMMENT '备注',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`, `del_flag`),
    KEY `idx_outbound_id` (`outbound_id`),
    KEY `idx_returner_id` (`returner_id`),
    KEY `idx_return_time` (`return_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='归还单主表';

-- 29. 归还明细表
DROP TABLE IF EXISTS `wms_return_detail`;
CREATE TABLE `wms_return_detail` (
    `id`              BIGINT   NOT NULL COMMENT '主键',
    `order_id`        BIGINT   NOT NULL                COMMENT '归还单ID',
    `item_id`         BIGINT   NOT NULL                COMMENT '物品ID',
    `label_id`        BIGINT   DEFAULT NULL            COMMENT '电子标签ID',
    `quantity`        INT      NOT NULL                COMMENT '归还数量',
    `return_status`   TINYINT  DEFAULT 1               COMMENT '归还状态(1-正常 2-损坏 3-丢失 4-数量不符)',
    `abnormal_desc`   VARCHAR(500) DEFAULT NULL        COMMENT '异常描述',
    `bin_id`          BIGINT   DEFAULT NULL            COMMENT '归还库位ID',
    `del_flag`        TINYINT  DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64) DEFAULT ''           COMMENT '创建人',
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64) DEFAULT ''           COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='归还明细表';

-- 30. 报废单主表
DROP TABLE IF EXISTS `wms_scrap_order`;
CREATE TABLE `wms_scrap_order` (
    `id`                BIGINT       NOT NULL COMMENT '主键',
    `order_no`          VARCHAR(50)  NOT NULL                COMMENT '报废单号',
    `applicant_id`      BIGINT       NOT NULL                COMMENT '申请人ID',
    `scrap_reason`      VARCHAR(500) DEFAULT NULL            COMMENT '报废原因',
    `order_status`      TINYINT      DEFAULT 0               COMMENT '单据状态(0-草稿 1-待审批 2-审批中 3-已通过 4-已驳回 5-已完成)',
    `scrap_time`        DATETIME     DEFAULT NULL            COMMENT '报废时间',
    `approval_required` TINYINT      DEFAULT 1               COMMENT '是否需要审批(0-否 1-是)',
    `remark`            VARCHAR(500) DEFAULT NULL            COMMENT '备注',
    `warehouse_id`      BIGINT       DEFAULT NULL            COMMENT '所属库房ID',
    `del_flag`          TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`         VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`         VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`, `del_flag`),
    KEY `idx_applicant_id` (`applicant_id`),
    KEY `idx_order_status` (`order_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报废单主表';

-- 31. 报废明细表
DROP TABLE IF EXISTS `wms_scrap_detail`;
CREATE TABLE `wms_scrap_detail` (
    `id`              BIGINT   NOT NULL COMMENT '主键',
    `order_id`        BIGINT   NOT NULL                COMMENT '报废单ID',
    `item_id`         BIGINT   NOT NULL                COMMENT '物品ID',
    `label_id`        BIGINT   DEFAULT NULL            COMMENT '电子标签ID',
    `quantity`        INT      NOT NULL                COMMENT '报废数量',
    `del_flag`        TINYINT  DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64) DEFAULT ''           COMMENT '创建人',
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64) DEFAULT ''           COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报废明细表';

-- 32. 调拨单主表
DROP TABLE IF EXISTS `wms_transfer_order`;
CREATE TABLE `wms_transfer_order` (
    `id`                BIGINT       NOT NULL COMMENT '主键',
    `order_no`          VARCHAR(50)  NOT NULL                COMMENT '调拨单号',
    `applicant_id`      BIGINT       NOT NULL                COMMENT '申请人ID',
    `from_warehouse_id` BIGINT       DEFAULT NULL            COMMENT '调出库房ID',
    `from_area_id`      BIGINT       DEFAULT NULL            COMMENT '调出区域ID',
    `from_cabinet_id`   BIGINT       DEFAULT NULL            COMMENT '调出存放柜ID',
    `to_warehouse_id`   BIGINT       DEFAULT NULL            COMMENT '调入库房ID',
    `to_area_id`        BIGINT       DEFAULT NULL            COMMENT '调入区域ID',
    `to_cabinet_id`     BIGINT       DEFAULT NULL            COMMENT '调入存放柜ID',
    `order_status`      TINYINT      DEFAULT 0               COMMENT '单据状态(0-草稿 1-待审批 2-审批中 3-已通过 4-已驳回 5-已完成)',
    `transfer_time`     DATETIME     DEFAULT NULL            COMMENT '调拨时间',
    `approval_required` TINYINT      DEFAULT 1               COMMENT '是否需要审批(0-否 1-是)',
    `remark`            VARCHAR(500) DEFAULT NULL            COMMENT '备注',
    `del_flag`          TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`         VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`         VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`, `del_flag`),
    KEY `idx_applicant_id` (`applicant_id`),
    KEY `idx_order_status` (`order_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调拨单主表';

-- 33. 调拨明细表
DROP TABLE IF EXISTS `wms_transfer_detail`;
CREATE TABLE `wms_transfer_detail` (
    `id`              BIGINT   NOT NULL COMMENT '主键',
    `order_id`        BIGINT   NOT NULL                COMMENT '调拨单ID',
    `item_id`         BIGINT   NOT NULL                COMMENT '物品ID',
    `label_id`        BIGINT   DEFAULT NULL            COMMENT '电子标签ID',
    `quantity`        INT      NOT NULL                COMMENT '调拨数量',
    `from_bin_id`     BIGINT   DEFAULT NULL            COMMENT '调出库位ID',
    `to_bin_id`       BIGINT   DEFAULT NULL            COMMENT '调入库位ID',
    `del_flag`        TINYINT  DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64) DEFAULT ''           COMMENT '创建人',
    `update_time`     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64) DEFAULT ''           COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调拨明细表';

-- 六、审批与日志模块

-- 34. 审批流程配置表
DROP TABLE IF EXISTS `wms_approval_config`;
CREATE TABLE `wms_approval_config` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `biz_type`        TINYINT      NOT NULL                COMMENT '业务类型(1-入库 2-出库/领用 3-报废 4-调拨)',
    `config_name`     VARCHAR(100) NOT NULL                COMMENT '配置名称',
    `approval_level`  TINYINT      DEFAULT 1               COMMENT '审批级别(0-免审 1-一级 2-二级 3-多级)',
    `timeout_hours`   INT          DEFAULT NULL            COMMENT '审批超时时限(小时)',
    `is_enabled`      TINYINT      DEFAULT 1               COMMENT '是否启用(0-否 1-是)',
    `condition_type`  TINYINT      DEFAULT 0               COMMENT '触发条件类型(0-无条件 1-按金额 2-按数量 3-按类别)',
    `condition_value` VARCHAR(200) DEFAULT NULL            COMMENT '触发条件值(JSON)',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_biz_type` (`biz_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批流程配置表';

-- 35. 审批记录表
DROP TABLE IF EXISTS `wms_approval_record`;
CREATE TABLE `wms_approval_record` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `biz_type`        TINYINT      NOT NULL                COMMENT '业务类型(1-入库 2-出库/领用 3-报废 4-调拨)',
    `biz_id`          BIGINT       NOT NULL                COMMENT '业务单据ID',
    `biz_no`          VARCHAR(50)  DEFAULT NULL            COMMENT '业务单据编号',
    `config_id`       BIGINT       DEFAULT NULL            COMMENT '审批配置ID',
    `step_no`         INT          DEFAULT 1               COMMENT '审批步骤(第几级审批)',
    `approver_id`     BIGINT       NOT NULL                COMMENT '审批人ID',
    `approval_result` TINYINT      DEFAULT NULL            COMMENT '审批结果(1-通过 2-驳回 3-转审)',
    `approval_opinion` VARCHAR(500) DEFAULT NULL           COMMENT '审批意见',
    `approval_time`   DATETIME     DEFAULT NULL            COMMENT '审批时间',
    `is_timeout`      TINYINT      DEFAULT 0               COMMENT '是否超时(0-否 1-是)',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_biz` (`biz_type`, `biz_id`),
    KEY `idx_approver_id` (`approver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批记录表';

-- 36. 操作日志表
DROP TABLE IF EXISTS `wms_operation_log`;
CREATE TABLE `wms_operation_log` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `user_id`         BIGINT       DEFAULT NULL            COMMENT '操作用户ID',
    `username`        VARCHAR(64)  DEFAULT NULL            COMMENT '操作用户名',
    `module`          VARCHAR(50)  NOT NULL                COMMENT '操作模块',
    `oper_type`       VARCHAR(20)  NOT NULL                COMMENT '操作类型(ADD/MODIFY/DELETE/IMPORT/EXPORT/APPROVE等)',
    `biz_type`        TINYINT      DEFAULT NULL            COMMENT '业务类型',
    `biz_id`          BIGINT       DEFAULT NULL            COMMENT '业务ID',
    `biz_no`          VARCHAR(50)  DEFAULT NULL            COMMENT '业务单据编号',
    `oper_desc`       VARCHAR(500) DEFAULT NULL            COMMENT '操作描述',
    `oper_content`    TEXT         DEFAULT NULL            COMMENT '操作内容(修改前后JSON)',
    `ip_address`      VARCHAR(50)  DEFAULT NULL            COMMENT 'IP地址',
    `cost_time`       BIGINT       DEFAULT NULL            COMMENT '耗时(毫秒)',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_module` (`module`),
    KEY `idx_biz` (`biz_type`, `biz_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 37. 登录日志表
DROP TABLE IF EXISTS `wms_login_log`;
CREATE TABLE `wms_login_log` (
    `id`              BIGINT       NOT NULL COMMENT '主键',
    `username`        VARCHAR(64)  DEFAULT NULL            COMMENT '登录用户名',
    `user_id`         BIGINT       DEFAULT NULL            COMMENT '用户ID',
    `ip_address`      VARCHAR(50)  DEFAULT NULL            COMMENT 'IP地址',
    `login_location`  VARCHAR(100) DEFAULT NULL            COMMENT '登录地点',
    `browser`         VARCHAR(50)  DEFAULT NULL            COMMENT '浏览器',
    `os`              VARCHAR(50)  DEFAULT NULL            COMMENT '操作系统',
    `login_result`    TINYINT      DEFAULT 1               COMMENT '登录结果(1-成功 0-失败)',
    `fail_reason`     VARCHAR(200) DEFAULT NULL            COMMENT '失败原因',
    `del_flag`        TINYINT      DEFAULT 0               COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT ''              COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT ''              COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 第三部分: 菜单初始化数据 (menu_init_data.sql)
-- ============================================================

-- 管理员角色
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `role_desc`, `data_scope`, `status`, `del_flag`) VALUES
(1, '系统管理员', 'admin', '拥有系统全部权限', 1, 1, 0);

-- 默认管理员用户(密码: admin123 的BCrypt哈希)
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `dept_id`, `phone`, `email`, `avatar`, `status`, `del_flag`) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiQ/0Hal3O0O5Oqi', '系统管理员', NULL, NULL, NULL, NULL, 1, 0);

-- 系统管理目录
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(100, '系统管理', 'system', 0, 1, 'system', NULL, '/system/user', 'Setting', 0, 0, 1, 1, 1, NULL, 0);

-- 系统管理子菜单
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(101, '用户管理', 'system:user', 100, 2, 'user', 'system/user', NULL, 'User', 0, 0, 1, 1, 1, NULL, 0),
(102, '角色管理', 'system:role', 100, 2, 'role', 'system/role', NULL, 'UserFilled', 0, 0, 1, 1, 2, NULL, 0),
(103, '菜单管理', 'system:menu', 100, 2, 'menu', 'system/menu', NULL, 'Menu', 0, 0, 1, 1, 3, NULL, 0),
(104, '权限管理', 'system:permission', 100, 2, 'permission', 'system/permission', NULL, 'Lock', 0, 0, 1, 1, 4, NULL, 0),
(105, '部门管理', 'system:dept', 100, 2, 'dept', 'system/dept', NULL, 'OfficeBuilding', 0, 0, 1, 1, 5, NULL, 0),
(106, '系统配置', 'system:config', 100, 2, 'config', 'system/config', NULL, 'Operation', 0, 0, 1, 1, 6, NULL, 0),
(107, '供应商管理', 'system:supplier', 100, 2, 'supplier', 'system/supplier', NULL, 'Van', 0, 0, 1, 1, 7, NULL, 0);

-- 用户管理按钮权限
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(1011, '用户新增', 'system:user:add', 101, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 1, 'system:user:add', 0),
(1012, '用户编辑', 'system:user:edit', 101, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 2, 'system:user:edit', 0),
(1013, '用户删除', 'system:user:delete', 101, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 3, 'system:user:delete', 0),
(1014, '用户查询', 'system:user:list', 101, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 4, 'system:user:list', 0),
(1015, '重置密码', 'system:user:resetPwd', 101, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 5, 'system:user:resetPwd', 0);

-- 角色管理按钮权限
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(1021, '角色新增', 'system:role:add', 102, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 1, 'system:role:add', 0),
(1022, '角色编辑', 'system:role:edit', 102, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 2, 'system:role:edit', 0),
(1023, '角色删除', 'system:role:delete', 102, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 3, 'system:role:delete', 0),
(1024, '角色查询', 'system:role:list', 102, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 4, 'system:role:list', 0);

-- 菜单管理按钮权限
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(1031, '菜单新增', 'system:menu:add', 103, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 1, 'system:menu:add', 0),
(1032, '菜单编辑', 'system:menu:edit', 103, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 2, 'system:menu:edit', 0),
(1033, '菜单删除', 'system:menu:delete', 103, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 3, 'system:menu:delete', 0),
(1034, '菜单查询', 'system:menu:list', 103, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 4, 'system:menu:list', 0);

-- 库房结构目录
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(200, '库房管理', 'warehouse', 0, 1, 'warehouse', NULL, '/warehouse/warehouse', 'House', 0, 0, 1, 1, 2, NULL, 0);

INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(201, '库房管理', 'warehouse:warehouse', 200, 2, 'warehouse', 'warehouse/warehouse', NULL, 'OfficeBuilding', 0, 0, 1, 1, 1, NULL, 0),
(202, '区域管理', 'warehouse:area', 200, 2, 'area', 'warehouse/area', NULL, 'Grid', 0, 0, 1, 1, 2, NULL, 0),
(203, '存放柜管理', 'warehouse:cabinet', 200, 2, 'cabinet', 'warehouse/cabinet', NULL, 'Files', 0, 0, 1, 1, 3, NULL, 0),
(204, '库位管理', 'warehouse:bin', 200, 2, 'bin', 'warehouse/bin', NULL, 'Collection', 0, 0, 1, 1, 4, NULL, 0),
(205, '库房可视化', 'warehouse:visual', 200, 2, 'visual', 'warehouse/visual', NULL, 'Picture', 0, 0, 1, 1, 5, NULL, 0);

-- 物品管理目录
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(300, '物品管理', 'item', 0, 1, 'item', NULL, '/item/list', 'Box', 0, 0, 1, 1, 3, NULL, 0);

INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(301, '物品列表', 'item:list', 300, 2, 'list', 'item/list', NULL, 'List', 0, 0, 1, 1, 1, NULL, 0),
(302, '类目管理', 'item:category', 300, 2, 'category', 'item/category', NULL, 'Menu', 0, 0, 1, 1, 2, NULL, 0),
(303, '标签管理', 'item:tag', 300, 2, 'tag', 'item/tag', NULL, 'PriceTag', 0, 0, 1, 1, 3, NULL, 0),
(304, '电子标签', 'item:label', 300, 2, 'label', 'label', NULL, 'Ticket', 0, 0, 1, 1, 4, NULL, 0),
(305, '库存管理', 'item:stock', 300, 2, 'stock', 'stock', NULL, 'Goods', 0, 0, 1, 1, 5, NULL, 0);

-- 业务管理目录
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(400, '业务管理', 'business', 0, 1, 'business', NULL, '/business/inbound', 'Document', 0, 0, 1, 1, 4, NULL, 0);

INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(401, '入库管理', 'business:inbound', 400, 2, 'inbound', 'business/inbound', NULL, 'Bottom', 0, 0, 1, 1, 1, NULL, 0),
(402, '出库/领用', 'business:outbound', 400, 2, 'outbound', 'business/outbound', NULL, 'Top', 0, 0, 1, 1, 2, NULL, 0),
(403, '归还管理', 'business:return', 400, 2, 'return', 'business/return', NULL, 'RefreshLeft', 0, 0, 1, 1, 3, NULL, 0),
(404, '报废管理', 'business:scrap', 400, 2, 'scrap', 'business/scrap', NULL, 'Delete', 0, 0, 1, 1, 4, NULL, 0),
(405, '调拨管理', 'business:transfer', 400, 2, 'transfer', 'business/transfer', NULL, 'Sort', 0, 0, 1, 1, 5, NULL, 0);

-- 审批管理目录
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(500, '审批管理', 'approval', 0, 1, 'approval', NULL, '/approval/pending', 'Stamp', 0, 0, 1, 1, 5, NULL, 0);

INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(501, '待审批', 'approval:pending', 500, 2, 'pending', 'approval/pending', NULL, 'Bell', 0, 0, 1, 1, 1, NULL, 0),
(502, '审批配置', 'approval:config', 500, 2, 'config', 'approval/config', NULL, 'Operation', 0, 0, 1, 1, 2, NULL, 0),
(503, '审批记录', 'approval:history', 500, 2, 'history', 'approval/history', NULL, 'Document', 0, 0, 1, 1, 3, NULL, 0);

-- 统计报表目录
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(600, '统计报表', 'report', 0, 1, 'report', NULL, '/report/stock', 'DataAnalysis', 0, 0, 1, 1, 6, NULL, 0);

INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(601, '库存报表', 'report:stock', 600, 2, 'stock', 'report/stock', NULL, 'PieChart', 0, 0, 1, 1, 1, NULL, 0),
(602, '入库报表', 'report:inbound', 600, 2, 'inbound', 'report/inbound', NULL, 'TrendCharts', 0, 0, 1, 1, 2, NULL, 0),
(603, '出库报表', 'report:outbound', 600, 2, 'outbound', 'report/outbound', NULL, 'DataLine', 0, 0, 1, 1, 3, NULL, 0),
(604, '借还报表', 'report:borrow-return', 600, 2, 'borrow-return', 'report/borrow-return', NULL, 'Connection', 0, 0, 1, 1, 4, NULL, 0),
(605, '报废报表', 'report:scrap', 600, 2, 'scrap', 'report/scrap', NULL, 'Histogram', 0, 0, 1, 1, 5, NULL, 0),
(606, '调拨报表', 'report:transfer', 600, 2, 'transfer', 'report/transfer', NULL, 'Guide', 0, 0, 1, 1, 6, NULL, 0),
(607, '预警报表', 'report:alert', 600, 2, 'alert', 'report/alert', NULL, 'Warning', 0, 0, 1, 1, 7, NULL, 0),
(608, '费用核算', 'report:cost', 600, 2, 'cost', 'report/cost', NULL, 'Money', 0, 0, 1, 1, 8, NULL, 0);

-- 预警监控目录
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(700, '预警监控', 'monitor', 0, 1, 'monitor', NULL, NULL, 'AlarmClock', 0, 0, 1, 1, 7, NULL, 0);

INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(701, '预警中心', 'monitor:alert', 700, 2, 'alert', 'monitor', NULL, 'Warning', 0, 0, 1, 1, 1, NULL, 0);

-- 日志管理目录
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(800, '日志管理', 'log', 0, 1, 'log', NULL, NULL, 'Notebook', 0, 0, 1, 1, 8, NULL, 0);

INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(801, '操作日志', 'log:operation', 800, 2, 'operation', 'log/operation', NULL, 'Document', 0, 0, 1, 1, 1, NULL, 0),
(802, '登录日志', 'log:login', 800, 2, 'login', 'log/login', NULL, 'User', 0, 0, 1, 1, 2, NULL, 0);

-- 角色-菜单关联（管理员角色ID=1，拥有全部菜单）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `del_flag`)
SELECT 1, `id`, 0 FROM `sys_menu` WHERE `del_flag` = 0;

-- 权限数据（与菜单permCode对应）
INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`) VALUES
(1, '用户新增', 'system:user:add', 1, 0, 1011, 1, 0),
(2, '用户编辑', 'system:user:edit', 1, 0, 1012, 1, 0),
(3, '用户删除', 'system:user:delete', 1, 0, 1013, 1, 0),
(4, '用户查询', 'system:user:list', 1, 0, 1014, 1, 0),
(5, '重置密码', 'system:user:resetPwd', 1, 0, 1015, 1, 0),
(6, '角色新增', 'system:role:add', 1, 0, 1021, 1, 0),
(7, '角色编辑', 'system:role:edit', 1, 0, 1022, 1, 0),
(8, '角色删除', 'system:role:delete', 1, 0, 1023, 1, 0),
(9, '角色查询', 'system:role:list', 1, 0, 1024, 1, 0),
(10, '菜单新增', 'system:menu:add', 1, 0, 1031, 1, 0),
(11, '菜单编辑', 'system:menu:edit', 1, 0, 1032, 1, 0),
(12, '菜单删除', 'system:menu:delete', 1, 0, 1033, 1, 0),
(13, '菜单查询', 'system:menu:list', 1, 0, 1034, 1, 0);

-- 角色-权限关联（管理员角色ID=1，拥有全部权限）
INSERT INTO `sys_role_permission` (`role_id`, `perm_id`, `del_flag`)
SELECT 1, `id`, 0 FROM `sys_permission` WHERE `del_flag` = 0;

-- 默认管理员用户角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`, `del_flag`) VALUES (1, 1, 0);
