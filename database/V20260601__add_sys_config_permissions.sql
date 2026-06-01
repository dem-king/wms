-- 新增系统配置的新增/删除按钮权限
INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_code`, `parent_id`, `menu_type`, `path`, `component`, `redirect`, `icon`, `is_external`, `is_cache`, `visible`, `status`, `sort_order`, `perm_code`, `del_flag`) VALUES
(1063, '配置新增', 'system:config:add', 106, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 3, 'system:config:add', 0),
(1064, '配置删除', 'system:config:delete', 106, 3, NULL, NULL, NULL, NULL, 0, 0, 1, 1, 4, 'system:config:delete', 0);

-- 管理员角色绑定新增权限
INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`, `del_flag`) VALUES
(1063, 1, 1063, 0),
(1064, 1, 1064, 0);