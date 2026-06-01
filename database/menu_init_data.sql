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
(1034, '菜单查询', 'system:menu:list', 103, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 4, 'system:menu:list', 0),
(1041, '权限新增', 'system:perm:add', 104, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 1, 'system:perm:add', 0),
(1042, '权限编辑', 'system:perm:edit', 104, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 2, 'system:perm:edit', 0),
(1043, '权限删除', 'system:perm:delete', 104, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 3, 'system:perm:delete', 0),
(1044, '权限查询', 'system:perm:list', 104, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 4, 'system:perm:list', 0),
(1051, '部门新增', 'system:dept:add', 105, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 1, 'system:dept:add', 0),
(1052, '部门编辑', 'system:dept:edit', 105, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 2, 'system:dept:edit', 0),
(1053, '部门删除', 'system:dept:delete', 105, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 3, 'system:dept:delete', 0),
(1054, '部门查询', 'system:dept:list', 105, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 4, 'system:dept:list', 0),
(1061, '配置编辑', 'system:config:edit', 106, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 1, 'system:config:edit', 0),
(1062, '配置查询', 'system:config:list', 106, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 2, 'system:config:list', 0),
(1071, '供应商新增', 'system:supplier:add', 107, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 1, 'system:supplier:add', 0),
(1072, '供应商编辑', 'system:supplier:edit', 107, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 2, 'system:supplier:edit', 0),
(1073, '供应商删除', 'system:supplier:delete', 107, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 3, 'system:supplier:delete', 0),
(1074, '供应商查询', 'system:supplier:list', 107, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 4, 'system:supplier:list', 0);

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
(802, '登录日志', 'log:login', 800, 2, 'login', 'log/login', NULL, 'User', 0, 0, 1, 1, 2, NULL, 0),
(8011, '操作日志查询', 'system:oper-log:list', 801, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 1, 'system:oper-log:list', 0),
(8021, '登录日志查询', 'system:login-log:list', 802, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 1, 'system:login-log:list', 0);


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
(13, '菜单查询', 'system:menu:list', 1, 0, 1034, 1, 0),
(14, '权限新增', 'system:perm:add', 2, 0, 1041, 1, 0),
(15, '权限编辑', 'system:perm:edit', 2, 0, 1042, 1, 0),
(16, '权限删除', 'system:perm:delete', 2, 0, 1043, 1, 0),
(17, '权限查询', 'system:perm:list', 2, 0, 1044, 1, 0),
(18, '部门新增', 'system:dept:add', 2, 0, 1051, 1, 0),
(19, '部门编辑', 'system:dept:edit', 2, 0, 1052, 1, 0),
(20, '部门删除', 'system:dept:delete', 2, 0, 1053, 1, 0),
(21, '部门查询', 'system:dept:list', 2, 0, 1054, 1, 0),
(22, '配置编辑', 'system:config:edit', 2, 0, 1061, 1, 0),
(23, '配置查询', 'system:config:list', 2, 0, 1062, 1, 0),
(24, '供应商新增', 'system:supplier:add', 2, 0, 1071, 1, 0),
(25, '供应商编辑', 'system:supplier:edit', 2, 0, 1072, 1, 0),
(26, '供应商删除', 'system:supplier:delete', 2, 0, 1073, 1, 0),
(27, '供应商查询', 'system:supplier:list', 2, 0, 1074, 1, 0),
(28, '操作日志查询', 'system:oper-log:list', 2, 0, 8011, 1, 0),
(29, '登录日志查询', 'system:login-log:list', 2, 0, 8021, 1, 0);


-- ==================== 四、角色-权限关联（管理员角色ID=1，拥有全部权限） ====================

-- Business and menu action permissions
INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`) VALUES
(20001, '库房查询', 'warehouse:warehouse:list', 2, 0, 201, 1, 0),
(20002, '库房新增', 'warehouse:warehouse:add', 2, 0, 201, 1, 0),
(20003, '库房编辑', 'warehouse:warehouse:edit', 2, 0, 201, 1, 0),
(20004, '库房删除', 'warehouse:warehouse:delete', 2, 0, 201, 1, 0),
(20005, '区域查询', 'warehouse:area:list', 2, 0, 202, 1, 0),
(20006, '区域新增', 'warehouse:area:add', 2, 0, 202, 1, 0),
(20007, '区域编辑', 'warehouse:area:edit', 2, 0, 202, 1, 0),
(20008, '区域删除', 'warehouse:area:delete', 2, 0, 202, 1, 0),
(20009, '存放柜查询', 'warehouse:cabinet:list', 2, 0, 203, 1, 0),
(20010, '存放柜新增', 'warehouse:cabinet:add', 2, 0, 203, 1, 0),
(20011, '存放柜编辑', 'warehouse:cabinet:edit', 2, 0, 203, 1, 0),
(20012, '存放柜删除', 'warehouse:cabinet:delete', 2, 0, 203, 1, 0),
(20013, '库位查询', 'warehouse:bin:list', 2, 0, 204, 1, 0),
(20014, '库位新增', 'warehouse:bin:add', 2, 0, 204, 1, 0),
(20015, '库位编辑', 'warehouse:bin:edit', 2, 0, 204, 1, 0),
(20016, '库位删除', 'warehouse:bin:delete', 2, 0, 204, 1, 0),
(20017, '库位批量生成', 'warehouse:bin:batch', 2, 0, 204, 1, 0),
(20018, '库房可视化编辑', 'warehouse:visual:edit', 2, 0, 205, 1, 0),
(20019, '物品查询', 'item:item:list', 2, 0, 301, 1, 0),
(20020, '物品新增', 'item:item:add', 2, 0, 301, 1, 0),
(20021, '物品编辑', 'item:item:edit', 2, 0, 301, 1, 0),
(20022, '物品删除', 'item:item:delete', 2, 0, 301, 1, 0),
(20023, '类目查询', 'item:category:list', 2, 0, 302, 1, 0),
(20024, '类目新增', 'item:category:add', 2, 0, 302, 1, 0),
(20025, '类目编辑', 'item:category:edit', 2, 0, 302, 1, 0),
(20026, '类目删除', 'item:category:delete', 2, 0, 302, 1, 0),
(20027, '标签查询', 'item:tag:list', 2, 0, 303, 1, 0),
(20028, '标签新增', 'item:tag:add', 2, 0, 303, 1, 0),
(20029, '标签编辑', 'item:tag:edit', 2, 0, 303, 1, 0),
(20030, '标签删除', 'item:tag:delete', 2, 0, 303, 1, 0),
(20031, '电子标签查询', 'item:label:list', 2, 0, 304, 1, 0),
(20032, '电子标签生成', 'item:label:generate', 2, 0, 304, 1, 0),
(20033, '电子标签绑定', 'item:label:bind', 2, 0, 304, 1, 0),
(20034, '电子标签状态修改', 'item:label:status', 2, 0, 304, 1, 0),
(20035, '电子标签打印', 'item:label:print', 2, 0, 304, 1, 0),
(20036, '库存查询', 'item:stock:list', 2, 0, 305, 1, 0),
(20037, '库存阈值设置', 'item:stock:edit', 2, 0, 305, 1, 0),
(20038, '机器备件查询', 'item:machine-spare:list', 2, 0, 306, 1, 0),
(20039, '机器备件新增', 'item:machine-spare:add', 2, 0, 306, 1, 0),
(20040, '机器备件编辑', 'item:machine-spare:edit', 2, 0, 306, 1, 0),
(20041, '机器备件删除', 'item:machine-spare:delete', 2, 0, 306, 1, 0),
(20042, '入库单查询', 'business:inbound:list', 2, 0, 401, 1, 0),
(20043, '入库单新增', 'business:inbound:add', 2, 0, 401, 1, 0),
(20044, '入库单编辑', 'business:inbound:edit', 2, 0, 401, 1, 0),
(20045, '入库单提交', 'business:inbound:submit', 2, 0, 401, 1, 0),
(20046, '入库单删除', 'business:inbound:delete', 2, 0, 401, 1, 0),
(20047, '出库单查询', 'business:outbound:list', 2, 0, 402, 1, 0),
(20048, '出库单新增', 'business:outbound:add', 2, 0, 402, 1, 0),
(20049, '出库单编辑', 'business:outbound:edit', 2, 0, 402, 1, 0),
(20050, '出库单提交', 'business:outbound:submit', 2, 0, 402, 1, 0),
(20051, '出库单删除', 'business:outbound:delete', 2, 0, 402, 1, 0),
(20052, '归还单查询', 'business:return:list', 2, 0, 403, 1, 0),
(20053, '归还单新增', 'business:return:add', 2, 0, 403, 1, 0),
(20054, '归还单编辑', 'business:return:edit', 2, 0, 403, 1, 0),
(20055, '归还单提交', 'business:return:submit', 2, 0, 403, 1, 0),
(20056, '归还单删除', 'business:return:delete', 2, 0, 403, 1, 0),
(20057, '报废单查询', 'business:scrap:list', 2, 0, 404, 1, 0),
(20058, '报废单新增', 'business:scrap:add', 2, 0, 404, 1, 0),
(20059, '报废单编辑', 'business:scrap:edit', 2, 0, 404, 1, 0),
(20060, '报废单提交', 'business:scrap:submit', 2, 0, 404, 1, 0),
(20061, '报废单删除', 'business:scrap:delete', 2, 0, 404, 1, 0),
(20062, '调拨单查询', 'business:transfer:list', 2, 0, 405, 1, 0),
(20063, '调拨单新增', 'business:transfer:add', 2, 0, 405, 1, 0),
(20064, '调拨单编辑', 'business:transfer:edit', 2, 0, 405, 1, 0),
(20065, '调拨单提交', 'business:transfer:submit', 2, 0, 405, 1, 0),
(20066, '调拨单删除', 'business:transfer:delete', 2, 0, 405, 1, 0),
(20067, '待审批查询', 'approval:pending:list', 2, 0, 501, 1, 0),
(20068, '审批通过', 'approval:pending:approve', 2, 0, 501, 1, 0),
(20069, '审批驳回', 'approval:pending:reject', 2, 0, 501, 1, 0),
(20070, '审批撤回', 'approval:pending:revoke', 2, 0, 501, 1, 0),
(20071, '审批配置查询', 'approval:config:list', 2, 0, 502, 1, 0),
(20072, '审批配置新增', 'approval:config:add', 2, 0, 502, 1, 0),
(20073, '审批配置编辑', 'approval:config:edit', 2, 0, 502, 1, 0),
(20074, '审批配置删除', 'approval:config:delete', 2, 0, 502, 1, 0),
(20075, '审批记录查询', 'approval:history:list', 2, 0, 503, 1, 0),
(20076, '库存报表查询', 'report:stock:list', 2, 0, 601, 1, 0),
(20077, '库存报表导出', 'report:stock:export', 2, 0, 601, 1, 0),
(20078, '入库报表查询', 'report:inbound:list', 2, 0, 602, 1, 0),
(20079, '入库报表导出', 'report:inbound:export', 2, 0, 602, 1, 0),
(20080, '出库报表查询', 'report:outbound:list', 2, 0, 603, 1, 0),
(20081, '出库报表导出', 'report:outbound:export', 2, 0, 603, 1, 0),
(20082, '借还报表查询', 'report:borrow-return:list', 2, 0, 604, 1, 0),
(20083, '借还报表导出', 'report:borrow-return:export', 2, 0, 604, 1, 0),
(20084, '报废报表查询', 'report:scrap:list', 2, 0, 605, 1, 0),
(20085, '报废报表导出', 'report:scrap:export', 2, 0, 605, 1, 0),
(20086, '调拨报表查询', 'report:transfer:list', 2, 0, 606, 1, 0),
(20087, '调拨报表导出', 'report:transfer:export', 2, 0, 606, 1, 0),
(20088, '预警报表查询', 'report:alert:list', 2, 0, 607, 1, 0),
(20089, '预警报表导出', 'report:alert:export', 2, 0, 607, 1, 0),
(20090, '费用核算查询', 'report:cost:list', 2, 0, 608, 1, 0),
(20091, '费用核算导出', 'report:cost:export', 2, 0, 608, 1, 0),
(20092, '费用核算配置', 'report:cost:edit', 2, 0, 608, 1, 0),
(20093, '库存预警查询', 'monitor:stock-alert:list', 2, 0, 701, 1, 0),
(20094, '库存预警处理', 'monitor:stock-alert:resolve', 2, 0, 701, 1, 0),
(20095, '逾期归还查询', 'monitor:overdue-return:list', 2, 0, 701, 1, 0),
(20096, '入库扫码', 'business:inbound:scan', 2, 0, 401, 1, 0),
(20097, '出库扫码', 'business:outbound:scan', 2, 0, 402, 1, 0);
INSERT INTO `sys_role_permission` (`role_id`, `perm_id`, `del_flag`)
SELECT 1, `id`, 0 FROM `sys_permission` WHERE `del_flag` = 0;


-- ==================== 五、默认管理员用户角色关联 ====================

-- 假设管理员用户ID=1，角色ID=1
INSERT INTO `sys_user_role` (`user_id`, `role_id`, `del_flag`) VALUES (1, 1, 0);
