-- 审批配置表：定义每种业务类型的审批流程
CREATE TABLE wms_approval_config (
    `id`              BIGINT       NOT NULL COMMENT '主键(雪花ID)',
    `biz_type`        TINYINT      NOT NULL COMMENT '业务类型(1-入库 2-出库 3-报废 4-调拨 5-归还)',
    `enabled`         TINYINT      DEFAULT 1 COMMENT '是否启用(0-否 1-是)',
    `auto_approve`    TINYINT      DEFAULT 0 COMMENT '是否免审(0-否 1-是)',
    `config_name`     VARCHAR(128) NOT NULL COMMENT '配置名称',
    `remark`          VARCHAR(512) DEFAULT '' COMMENT '备注',
    `timeout_hours`   INT          DEFAULT 48 COMMENT '审批超时阈值(小时)',
    `timeout_action`  TINYINT      DEFAULT 1 COMMENT '超时处理方式(1-自动提醒 2-自动取消)',
    `del_flag`        TINYINT      DEFAULT 0 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT '' COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT '' COMMENT '更新人',
    PRIMARY KEY (`id`)
) COMMENT='审批配置表';

-- 审批节点配置表
CREATE TABLE wms_approval_node (
    `id`              BIGINT       NOT NULL COMMENT '主键(雪花ID)',
    `config_id`       BIGINT       NOT NULL COMMENT '审批配置ID',
    `step_order`      INT          NOT NULL COMMENT '节点顺序(从1开始)',
    `node_name`       VARCHAR(128) NOT NULL COMMENT '节点名称',
    `approver_type`   TINYINT      NOT NULL COMMENT '审批人类型(1-指定角色 2-指定用户 3-库房管理员)',
    `approver_id`     BIGINT       DEFAULT NULL COMMENT '审批人/角色ID',
    `del_flag`        TINYINT      DEFAULT 0 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT '' COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT '' COMMENT '更新人',
    PRIMARY KEY (`id`)
) COMMENT='审批节点配置表';

-- 审批单表
CREATE TABLE wms_approval_order (
    `id`              BIGINT       NOT NULL COMMENT '主键(雪花ID)',
    `biz_id`          BIGINT       NOT NULL COMMENT '业务单据ID',
    `biz_type`        TINYINT      NOT NULL COMMENT '业务类型(1-入库 2-出库 3-报废 4-调拨 5-归还)',
    `status`          TINYINT      NOT NULL DEFAULT 0 COMMENT '审批状态(0-待审批 1-审批中 2-已通过 3-已驳回 4-已撤回)',
    `applicant_id`    BIGINT       DEFAULT NULL COMMENT '申请人ID',
    `current_step`    INT          DEFAULT 1 COMMENT '当前审批节点(从1开始)',
    `total_steps`     INT          DEFAULT 0 COMMENT '总审批节点数',
    `remark`          VARCHAR(512) DEFAULT '' COMMENT '备注',
    `del_flag`        TINYINT      DEFAULT 0 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT '' COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT '' COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_biz` (`biz_id`, `biz_type`)
) COMMENT='审批单表';

-- 审批记录表
CREATE TABLE wms_approval_record (
    `id`              BIGINT       NOT NULL COMMENT '主键(雪花ID)',
    `approval_id`     BIGINT       NOT NULL COMMENT '审批单ID',
    `step_order`      INT          NOT NULL COMMENT '节点顺序',
    `approver_id`     BIGINT       DEFAULT NULL COMMENT '审批人ID',
    `approver_name`   VARCHAR(64)  DEFAULT '' COMMENT '审批人姓名',
    `result`          TINYINT      DEFAULT NULL COMMENT '审批结果(1-通过 2-驳回)',
    `opinion`         VARCHAR(512) DEFAULT '' COMMENT '审批意见',
    `approve_time`    DATETIME     DEFAULT NULL COMMENT '审批时间',
    `del_flag`        TINYINT      DEFAULT 0 COMMENT '逻辑删除(0-正常 1-已删除)',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by`       VARCHAR(64)  DEFAULT '' COMMENT '创建人',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `update_by`       VARCHAR(64)  DEFAULT '' COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_approval_id` (`approval_id`)
) COMMENT='审批记录表';
