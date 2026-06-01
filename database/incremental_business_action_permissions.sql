-- Incremental permissions for business/menu action authorization.
-- Apply this to an already initialized database, then re-login users to refresh frontend permissions.

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20001, '库房查询', 'warehouse:warehouse:list', 2, 0, 201, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'warehouse:warehouse:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20002, '库房新增', 'warehouse:warehouse:add', 2, 0, 201, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'warehouse:warehouse:add' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20003, '库房编辑', 'warehouse:warehouse:edit', 2, 0, 201, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'warehouse:warehouse:edit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20004, '库房删除', 'warehouse:warehouse:delete', 2, 0, 201, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'warehouse:warehouse:delete' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20005, '区域查询', 'warehouse:area:list', 2, 0, 202, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'warehouse:area:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20006, '区域新增', 'warehouse:area:add', 2, 0, 202, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'warehouse:area:add' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20007, '区域编辑', 'warehouse:area:edit', 2, 0, 202, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'warehouse:area:edit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20008, '区域删除', 'warehouse:area:delete', 2, 0, 202, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'warehouse:area:delete' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20009, '存放柜查询', 'warehouse:cabinet:list', 2, 0, 203, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'warehouse:cabinet:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20010, '存放柜新增', 'warehouse:cabinet:add', 2, 0, 203, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'warehouse:cabinet:add' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20011, '存放柜编辑', 'warehouse:cabinet:edit', 2, 0, 203, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'warehouse:cabinet:edit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20012, '存放柜删除', 'warehouse:cabinet:delete', 2, 0, 203, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'warehouse:cabinet:delete' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20013, '库位查询', 'warehouse:bin:list', 2, 0, 204, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'warehouse:bin:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20014, '库位新增', 'warehouse:bin:add', 2, 0, 204, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'warehouse:bin:add' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20015, '库位编辑', 'warehouse:bin:edit', 2, 0, 204, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'warehouse:bin:edit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20016, '库位删除', 'warehouse:bin:delete', 2, 0, 204, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'warehouse:bin:delete' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20017, '库位批量生成', 'warehouse:bin:batch', 2, 0, 204, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'warehouse:bin:batch' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20018, '库房可视化编辑', 'warehouse:visual:edit', 2, 0, 205, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'warehouse:visual:edit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20019, '物品查询', 'item:item:list', 2, 0, 301, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:item:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20020, '物品新增', 'item:item:add', 2, 0, 301, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:item:add' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20021, '物品编辑', 'item:item:edit', 2, 0, 301, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:item:edit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20022, '物品删除', 'item:item:delete', 2, 0, 301, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:item:delete' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20023, '类目查询', 'item:category:list', 2, 0, 302, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:category:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20024, '类目新增', 'item:category:add', 2, 0, 302, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:category:add' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20025, '类目编辑', 'item:category:edit', 2, 0, 302, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:category:edit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20026, '类目删除', 'item:category:delete', 2, 0, 302, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:category:delete' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20027, '标签查询', 'item:tag:list', 2, 0, 303, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:tag:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20028, '标签新增', 'item:tag:add', 2, 0, 303, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:tag:add' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20029, '标签编辑', 'item:tag:edit', 2, 0, 303, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:tag:edit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20030, '标签删除', 'item:tag:delete', 2, 0, 303, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:tag:delete' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20031, '电子标签查询', 'item:label:list', 2, 0, 304, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:label:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20032, '电子标签生成', 'item:label:generate', 2, 0, 304, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:label:generate' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20033, '电子标签绑定', 'item:label:bind', 2, 0, 304, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:label:bind' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20034, '电子标签状态修改', 'item:label:status', 2, 0, 304, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:label:status' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20035, '电子标签打印', 'item:label:print', 2, 0, 304, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:label:print' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20036, '库存查询', 'item:stock:list', 2, 0, 305, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:stock:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20037, '库存阈值设置', 'item:stock:edit', 2, 0, 305, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:stock:edit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20038, '机器备件查询', 'item:machine-spare:list', 2, 0, 306, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:machine-spare:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20039, '机器备件新增', 'item:machine-spare:add', 2, 0, 306, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:machine-spare:add' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20040, '机器备件编辑', 'item:machine-spare:edit', 2, 0, 306, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:machine-spare:edit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20041, '机器备件删除', 'item:machine-spare:delete', 2, 0, 306, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'item:machine-spare:delete' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20042, '入库单查询', 'business:inbound:list', 2, 0, 401, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:inbound:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20043, '入库单新增', 'business:inbound:add', 2, 0, 401, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:inbound:add' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20044, '入库单编辑', 'business:inbound:edit', 2, 0, 401, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:inbound:edit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20045, '入库单提交', 'business:inbound:submit', 2, 0, 401, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:inbound:submit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20046, '入库单删除', 'business:inbound:delete', 2, 0, 401, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:inbound:delete' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20047, '出库单查询', 'business:outbound:list', 2, 0, 402, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:outbound:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20048, '出库单新增', 'business:outbound:add', 2, 0, 402, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:outbound:add' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20049, '出库单编辑', 'business:outbound:edit', 2, 0, 402, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:outbound:edit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20050, '出库单提交', 'business:outbound:submit', 2, 0, 402, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:outbound:submit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20051, '出库单删除', 'business:outbound:delete', 2, 0, 402, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:outbound:delete' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20052, '归还单查询', 'business:return:list', 2, 0, 403, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:return:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20053, '归还单新增', 'business:return:add', 2, 0, 403, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:return:add' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20054, '归还单编辑', 'business:return:edit', 2, 0, 403, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:return:edit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20055, '归还单提交', 'business:return:submit', 2, 0, 403, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:return:submit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20056, '归还单删除', 'business:return:delete', 2, 0, 403, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:return:delete' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20057, '报废单查询', 'business:scrap:list', 2, 0, 404, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:scrap:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20058, '报废单新增', 'business:scrap:add', 2, 0, 404, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:scrap:add' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20059, '报废单编辑', 'business:scrap:edit', 2, 0, 404, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:scrap:edit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20060, '报废单提交', 'business:scrap:submit', 2, 0, 404, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:scrap:submit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20061, '报废单删除', 'business:scrap:delete', 2, 0, 404, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:scrap:delete' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20062, '调拨单查询', 'business:transfer:list', 2, 0, 405, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:transfer:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20063, '调拨单新增', 'business:transfer:add', 2, 0, 405, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:transfer:add' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20064, '调拨单编辑', 'business:transfer:edit', 2, 0, 405, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:transfer:edit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20065, '调拨单提交', 'business:transfer:submit', 2, 0, 405, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:transfer:submit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20066, '调拨单删除', 'business:transfer:delete', 2, 0, 405, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:transfer:delete' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20067, '待审批查询', 'approval:pending:list', 2, 0, 501, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'approval:pending:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20068, '审批通过', 'approval:pending:approve', 2, 0, 501, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'approval:pending:approve' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20069, '审批驳回', 'approval:pending:reject', 2, 0, 501, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'approval:pending:reject' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20070, '审批撤回', 'approval:pending:revoke', 2, 0, 501, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'approval:pending:revoke' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20071, '审批配置查询', 'approval:config:list', 2, 0, 502, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'approval:config:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20072, '审批配置新增', 'approval:config:add', 2, 0, 502, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'approval:config:add' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20073, '审批配置编辑', 'approval:config:edit', 2, 0, 502, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'approval:config:edit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20074, '审批配置删除', 'approval:config:delete', 2, 0, 502, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'approval:config:delete' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20075, '审批记录查询', 'approval:history:list', 2, 0, 503, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'approval:history:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20076, '库存报表查询', 'report:stock:list', 2, 0, 601, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'report:stock:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20077, '库存报表导出', 'report:stock:export', 2, 0, 601, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'report:stock:export' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20078, '入库报表查询', 'report:inbound:list', 2, 0, 602, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'report:inbound:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20079, '入库报表导出', 'report:inbound:export', 2, 0, 602, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'report:inbound:export' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20080, '出库报表查询', 'report:outbound:list', 2, 0, 603, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'report:outbound:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20081, '出库报表导出', 'report:outbound:export', 2, 0, 603, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'report:outbound:export' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20082, '借还报表查询', 'report:borrow-return:list', 2, 0, 604, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'report:borrow-return:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20083, '借还报表导出', 'report:borrow-return:export', 2, 0, 604, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'report:borrow-return:export' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20084, '报废报表查询', 'report:scrap:list', 2, 0, 605, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'report:scrap:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20085, '报废报表导出', 'report:scrap:export', 2, 0, 605, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'report:scrap:export' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20086, '调拨报表查询', 'report:transfer:list', 2, 0, 606, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'report:transfer:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20087, '调拨报表导出', 'report:transfer:export', 2, 0, 606, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'report:transfer:export' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20088, '预警报表查询', 'report:alert:list', 2, 0, 607, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'report:alert:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20089, '预警报表导出', 'report:alert:export', 2, 0, 607, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'report:alert:export' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20090, '费用核算查询', 'report:cost:list', 2, 0, 608, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'report:cost:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20091, '费用核算导出', 'report:cost:export', 2, 0, 608, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'report:cost:export' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20092, '费用核算配置', 'report:cost:edit', 2, 0, 608, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'report:cost:edit' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20093, '库存预警查询', 'monitor:stock-alert:list', 2, 0, 701, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'monitor:stock-alert:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20094, '库存预警处理', 'monitor:stock-alert:resolve', 2, 0, 701, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'monitor:stock-alert:resolve' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20095, '逾期归还查询', 'monitor:overdue-return:list', 2, 0, 701, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'monitor:overdue-return:list' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20096, '入库扫码', 'business:inbound:scan', 2, 0, 401, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:inbound:scan' AND `del_flag` = 0);

INSERT INTO `sys_permission` (`id`, `perm_name`, `perm_code`, `perm_type`, `parent_id`, `menu_id`, `status`, `del_flag`)
SELECT 20097, '出库扫码', 'business:outbound:scan', 2, 0, 402, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE `perm_code` = 'business:outbound:scan' AND `del_flag` = 0);
-- Grant the new permissions to the built-in administrator role (role_id = 1).
INSERT INTO `sys_role_permission` (`id`, `role_id`, `perm_id`, `del_flag`)
SELECT 300000 + p.`id`, 1, p.`id`, 0 FROM `sys_permission` p
WHERE p.`del_flag` = 0
  AND p.`perm_code` IN (
    'warehouse:warehouse:list',
    'warehouse:warehouse:add',
    'warehouse:warehouse:edit',
    'warehouse:warehouse:delete',
    'warehouse:area:list',
    'warehouse:area:add',
    'warehouse:area:edit',
    'warehouse:area:delete',
    'warehouse:cabinet:list',
    'warehouse:cabinet:add',
    'warehouse:cabinet:edit',
    'warehouse:cabinet:delete',
    'warehouse:bin:list',
    'warehouse:bin:add',
    'warehouse:bin:edit',
    'warehouse:bin:delete',
    'warehouse:bin:batch',
    'warehouse:visual:edit',
    'item:item:list',
    'item:item:add',
    'item:item:edit',
    'item:item:delete',
    'item:category:list',
    'item:category:add',
    'item:category:edit',
    'item:category:delete',
    'item:tag:list',
    'item:tag:add',
    'item:tag:edit',
    'item:tag:delete',
    'item:label:list',
    'item:label:generate',
    'item:label:bind',
    'item:label:status',
    'item:label:print',
    'item:stock:list',
    'item:stock:edit',
    'item:machine-spare:list',
    'item:machine-spare:add',
    'item:machine-spare:edit',
    'item:machine-spare:delete',
    'business:inbound:list',
    'business:inbound:add',
    'business:inbound:edit',
    'business:inbound:submit',
    'business:inbound:scan',
    'business:inbound:delete',
    'business:outbound:list',
    'business:outbound:add',
    'business:outbound:edit',
    'business:outbound:submit',
    'business:outbound:scan',
    'business:outbound:delete',
    'business:return:list',
    'business:return:add',
    'business:return:edit',
    'business:return:submit',
    'business:return:delete',
    'business:scrap:list',
    'business:scrap:add',
    'business:scrap:edit',
    'business:scrap:submit',
    'business:scrap:delete',
    'business:transfer:list',
    'business:transfer:add',
    'business:transfer:edit',
    'business:transfer:submit',
    'business:transfer:delete',
    'approval:pending:list',
    'approval:pending:approve',
    'approval:pending:reject',
    'approval:pending:revoke',
    'approval:config:list',
    'approval:config:add',
    'approval:config:edit',
    'approval:config:delete',
    'approval:history:list',
    'report:stock:list',
    'report:stock:export',
    'report:inbound:list',
    'report:inbound:export',
    'report:outbound:list',
    'report:outbound:export',
    'report:borrow-return:list',
    'report:borrow-return:export',
    'report:scrap:list',
    'report:scrap:export',
    'report:transfer:list',
    'report:transfer:export',
    'report:alert:list',
    'report:alert:export',
    'report:cost:list',
    'report:cost:export',
    'report:cost:edit',
    'monitor:stock-alert:list',
    'monitor:stock-alert:resolve',
    'monitor:overdue-return:list'
  )
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` rp
    WHERE rp.`role_id` = 1 AND rp.`perm_id` = p.`id` AND rp.`del_flag` = 0
  );
