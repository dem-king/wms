-- ============================================================
-- 系统配置表初始化数据
-- 日期: 2026-05-28
-- 说明: 初始化备品备件库房管理平台常用系统配置参数
-- ============================================================

SET NAMES utf8mb4;

-- 清空现有配置数据（谨慎操作）
-- DELETE FROM sys_config WHERE del_flag = 0;

-- ============================================================
-- 一、安全认证配置 (security)
-- ============================================================
INSERT INTO sys_config (id, config_key, config_value, config_name, config_group, config_desc, del_flag, create_time, create_by, update_time, update_by) VALUES
(1812345678901234561, 'wms.security.default-password', 'Wms@2024', '默认密码', 'security', '新用户默认密码/重置密码时的默认值', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234562, 'wms.security.password-expire-days', '90', '密码有效期(天)', 'security', '用户密码过期天数，过期后需强制修改密码', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234563, 'wms.security.login-fail-threshold', '5', '登录失败次数限制', 'security', '连续登录失败次数达到此值后锁定账户', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234564, 'wms.security.lock-duration-seconds', '1800', '账户锁定时间(秒)', 'security', '账户锁定后的自动解锁时间', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234565, 'wms.security.captcha-enabled', 'true', '是否启用验证码', 'security', '登录时是否显示图形验证码', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234566, 'wms.security.captcha-expire-seconds', '300', '验证码有效期(秒)', 'security', '图形验证码的有效时间', 0, NOW(), 'system', NOW(), 'system');

-- ============================================================
-- 二、会话与Token配置 (auth)
-- ============================================================
INSERT INTO sys_config (id, config_key, config_value, config_name, config_group, config_desc, del_flag, create_time, create_by, update_time, update_by) VALUES
(1812345678901234567, 'wms.auth.access-token-expire', '7200', '访问令牌有效期(秒)', 'auth', 'JWT访问令牌过期时间，默认2小时', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234568, 'wms.auth.refresh-token-expire', '604800', '刷新令牌有效期(秒)', 'auth', 'JWT刷新令牌过期时间，默认7天', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234569, 'wms.auth.token-renew-threshold', '1800', '令牌续期阈值(秒)', 'auth', '访问令牌剩余有效期小于此值时自动续期', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234570, 'wms.auth.session-timeout-minutes', '30', '会话超时时间(分钟)', 'auth', '用户无操作后自动登出时间', 0, NOW(), 'system', NOW(), 'system');

-- ============================================================
-- 三、分页与查询配置 (system)
-- ============================================================
INSERT INTO sys_config (id, config_key, config_value, config_name, config_group, config_desc, del_flag, create_time, create_by, update_time, update_by) VALUES
(1812345678901234571, 'wms.system.default-page-size', '20', '默认分页大小', 'system', '列表查询默认每页显示条数', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234572, 'wms.system.max-page-size', '1000', '最大分页大小', 'system', '列表查询允许的最大每页条数', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234573, 'wms.system.export-max-rows', '10000', '导出最大行数', 'system', 'Excel/CSV导出时允许的最大数据条数', 0, NOW(), 'system', NOW(), 'system');

-- ============================================================
-- 四、文件上传配置 (storage)
-- ============================================================
INSERT INTO sys_config (id, config_key, config_value, config_name, config_group, config_desc, del_flag, create_time, create_by, update_time, update_by) VALUES
(1812345678901234574, 'wms.storage.max-file-size-mb', '10', '单文件最大大小(MB)', 'storage', '允许上传的单个文件最大容量', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234575, 'wms.storage.avatar-max-size-mb', '2', '头像最大大小(MB)', 'storage', '用户头像上传大小限制', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234576, 'wms.storage.allowed-image-types', 'jpg,jpeg,png,gif,webp', '允许的图片类型', 'storage', '上传图片时允许的文件扩展名，逗号分隔', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234577, 'wms.storage.allowed-document-types', 'pdf,doc,docx,xls,xlsx,txt', '允许的文档类型', 'storage', '上传文档时允许的文件扩展名，逗号分隔', 0, NOW(), 'system', NOW(), 'system');

-- ============================================================
-- 五、业务规则配置 (business)
-- ============================================================
INSERT INTO sys_config (id, config_key, config_value, config_name, config_group, config_desc, del_flag, create_time, create_by, update_time, update_by) VALUES
(1812345678901234578, 'wms.business.order-no-prefix-inbound', 'RK', '入库单编号前缀', 'business', '入库单编号前缀，如RK202605280001', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234579, 'wms.business.order-no-prefix-outbound', 'CK', '出库单编号前缀', 'business', '出库单编号前缀，如CK202605280001', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234580, 'wms.business.order-no-prefix-return', 'GH', '归还单编号前缀', 'business', '归还单编号前缀，如GH202605280001', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234581, 'wms.business.order-no-prefix-scrap', 'BF', '报废单编号前缀', 'business', '报废单编号前缀，如BF202605280001', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234582, 'wms.business.order-no-prefix-transfer', 'DB', '调拨单编号前缀', 'business', '调拨单编号前缀，如DB202605280001', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234583, 'wms.business.low-stock-threshold', '10', '库存预警阈值', 'business', '库存数量低于此值时触发预警', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234584, 'wms.business.overdue-warning-days', '7', '归还预警天数', 'business', '物品借用到期前多少天开始预警', 0, NOW(), 'system', NOW(), 'system');

-- ============================================================
-- 六、标签打印配置 (label)
-- ============================================================
INSERT INTO sys_config (id, config_key, config_value, config_name, config_group, config_desc, del_flag, create_time, create_by, update_time, update_by) VALUES
(1812345678901234585, 'wms.label.print-qr-code-size', '200', '二维码打印尺寸(像素)', 'label', '标签上二维码的打印尺寸', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234586, 'wms.label.print-barcode-height', '80', '条形码打印高度(像素)', 'label', '标签上条形码的打印高度', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234587, 'wms.label.idle-warning-days', '30', '标签闲置预警天数', 'label', '标签超过此天数未使用触发闲置预警', 0, NOW(), 'system', NOW(), 'system');

-- ============================================================
-- 七、报表统计配置 (report)
-- ============================================================
INSERT INTO sys_config (id, config_key, config_value, config_name, config_group, config_desc, del_flag, create_time, create_by, update_time, update_by) VALUES
(1812345678901234588, 'wms.report.daily-aggregation-hour', '2', '日报统计执行时间(点)', 'report', '每日报表统计任务的执行时间（凌晨2点）', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234589, 'wms.report.max-chart-data-points', '100', '图表最大数据点', 'report', '图表展示时最多显示的数据点数量', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234590, 'wms.report.default-date-range-days', '30', '默认查询日期范围(天)', 'report', '报表默认查询最近多少天的数据', 0, NOW(), 'system', NOW(), 'system');

-- ============================================================
-- 八、系统信息配置 (info)
-- ============================================================
INSERT INTO sys_config (id, config_key, config_value, config_name, config_group, config_desc, del_flag, create_time, create_by, update_time, update_by) VALUES
(1812345678901234591, 'wms.info.system-name', '备品备件库房管理系统', '系统名称', 'info', '系统显示名称', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234592, 'wms.info.copyright', '© 2026 备品备件库房管理系统 版权所有', '版权信息', 'info', '页面底部版权信息', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234593, 'wms.info.help-phone', '400-123-4567', '客服电话', 'info', '系统帮助页面显示的客服电话', 0, NOW(), 'system', NOW(), 'system'),
(1812345678901234594, 'wms.info.help-email', 'support@wms.com', '客服邮箱', 'info', '系统帮助页面显示的客服邮箱', 0, NOW(), 'system', NOW(), 'system');
