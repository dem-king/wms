-- ============================================
-- 菜单及权限初始化数据
-- 包含：角色、菜单树、权限、关联关系、管理员用户
-- ============================================

-- ==================== 零、角色和用户基础数据 ====================

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
(305, '库存管理', 'item:stock', 300, 2, 'stock', 'stock', NULL, 'Goods', 0, 0, 1, 1, 5, NULL, 0),
(306, '机器备件', 'item:machine-spare', 300, 2, 'machine-spare', 'item/machineSpare/index', NULL, 'Connection', 0, 0, 1, 1, 6, NULL, 0);

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


-- ==================== 二、角色-菜单关联（管理员角色ID=1，拥有全部菜单） ====================

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`, `del_flag`)
SELECT 1, `id`, 0 FROM `sys_menu` WHERE `del_flag` = 0;


-- ==================== 三、权限数据（与菜单permCode对应） ====================

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


-- ==================== 四、角色-权限关联（管理员角色ID=1，拥有全部权限） ====================

INSERT INTO `sys_role_permission` (`role_id`, `perm_id`, `del_flag`)
SELECT 1, `id`, 0 FROM `sys_permission` WHERE `del_flag` = 0;


-- ==================== 五、默认管理员用户角色关联 ====================

-- 假设管理员用户ID=1，角色ID=1
INSERT INTO `sys_user_role` (`user_id`, `role_id`, `del_flag`) VALUES (1, 1, 0);
