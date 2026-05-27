-- WMS数据导入SQL
-- 生成时间: 自动生成
-- 注意：执行前请确保数据库已备份

-- ==================== 主类目 ====================

INSERT INTO wms_category (id, category_name, category_code, category_color, icon, sort_order, is_consumable, del_flag, create_time, create_by)
VALUES (1000000000000000001, '普天备件', 'PT', NULL, NULL, 1, 0, 0, NOW(), 'system');

INSERT INTO wms_category (id, category_name, category_code, category_color, icon, sort_order, is_consumable, del_flag, create_time, create_by)
VALUES (1000000000000000002, '其他备件', 'QT', NULL, NULL, 3, 0, 0, NOW(), 'system');

INSERT INTO wms_category (id, category_name, category_code, category_color, icon, sort_order, is_consumable, del_flag, create_time, create_by)
VALUES (1000000000000000003, '科盛备件', 'KS', NULL, NULL, 2, 0, 0, NOW(), 'system');

-- ==================== 细分类目 ====================

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000001, 1000000000000000001, '普天备件', '普天备件', 0, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000002, 1000000000000000001, '普天气缸', '普天气缸', 2, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000003, 1000000000000000001, '普天多楔带', '普天多楔带', 4, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000004, 1000000000000000001, '普天同步带', '普天同步带', 5, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000005, 1000000000000000001, '普天圆带备件', '普天圆带备件', 6, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000006, 1000000000000000001, '普天传输皮带', '普天传输皮带', 7, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000007, 1000000000000000001, '普天气动配件', '普天气动配件', 8, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000008, 1000000000000000001, '普天滚筒/辊筒', '普天滚筒辊筒', 9, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000009, 1000000000000000001, '普天轴承备件', '普天轴承备件', 10, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000010, 1000000000000000001, '普天轴/杆', '普天轴杆', 11, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000011, 1000000000000000001, '普天减速器', '普天减速器', 12, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000012, 1000000000000000001, '普天司服电机', '普天司服电机', 13, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000013, 1000000000000000001, '普天减速电机', '普天减速电机', 14, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000014, 1000000000000000001, '普天电机同步轮/带轮/链轮/限位轮', '普天电机同步轮带轮链', 15, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000015, 1000000000000000001, '普天光电开关', '普天光电开关', 16, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000016, 1000000000000000001, '普天接近开关', '普天接近开关', 17, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000017, 1000000000000000001, '普天电磁阀', '普天电磁阀', 18, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000018, 1000000000000000001, '普天机械系配件', '普天机械系配件', 19, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000019, 1000000000000000001, '普天易损备件', '普天易损备件', 20, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000020, 1000000000000000001, '普天链条备件', '普天链条备件', 21, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000021, 1000000000000000002, '驱动器/变频器', '驱动器变频器', 22, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000022, 1000000000000000002, '电器配件', '电器配件', 23, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000023, 1000000000000000002, '继电器', '继电器', 24, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000024, 1000000000000000003, '科盛备件', '科盛备件', 25, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000025, 1000000000000000003, '科盛气缸', '科盛气缸', 26, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000026, 1000000000000000003, '科盛电机/伺服电机', '科盛电机伺服电机', 27, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000027, 1000000000000000003, '科盛滚筒/辊筒', '科盛滚筒辊筒', 28, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000028, 1000000000000000003, '科盛光电开关/接近开关', '科盛光电开关接近开关', 29, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000029, 1000000000000000003, '科盛机械配件', '科盛机械配件', 30, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000030, 1000000000000000003, '科盛气动备件', '科盛气动备件', 31, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000031, 1000000000000000003, '科盛同步轮/带轮/链轮', '科盛同步轮带轮链轮', 32, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000032, 1000000000000000003, '科盛皮带/同步带/多契带', '科盛皮带同步带多契带', 33, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000033, 1000000000000000003, '科盛电磁阀', '科盛电磁阀', 34, 0, NOW(), 'system');

INSERT INTO wms_sub_category (id, category_id, sub_category_name, sub_category_code, sort_order, del_flag, create_time, create_by)
VALUES (2000000000000000034, 1000000000000000002, '常备备件、', '常备备件', 35, 0, NOW(), 'system');

-- ==================== 物品档案 ====================

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000001, '591', '塑包机电磁阀', 'SBJDCF', '', 'VUVS-LT25-M52-MD-G14-F8', '件',
        '', 1000000000000000003, 2000000000000000033, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '大品规塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000002, '580', '塑包机电机', 'SBJDJ', '', 'CM-10-400-200-TJ', '件',
        '', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '塑包机出口输送', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000003, '948', '减速电机DRN63M4 转速1375r/min功率0.18kw', 'JSDJDRN63M4ZS1375RMINGL018KW', '', 'RF37-DRN63M4/BE03HF  速比134.82 转速1375/10r/min 扭矩169Nm功率0.18kw', '件',
        '', 1000000000000000001, 2000000000000000011, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '副库', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000004, '871', '堆垛机行走轮', 'DDJXZL', '', 'DR10-7020005A', '件',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '堆垛机行走轮', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000005, '577', '塑包机伺服电机', 'SBJSFDJ', '', 'JSMA-PLC80ABKB', '件',
        '', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '二级堆垛电机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000006, '220', '过滤减压阀(带配件)', 'GLJYFDPJ', '', 'AW60-06DG-B', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000007, '582', '塑包机电机', 'SBJDJ', '', 'CH12#-1-1/10-L420S2949-1', '件',
        '', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '塑包机拨杆', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000008, '530', '气缸', 'QG', '', 'MAC25*42SCAG', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 1, NULL, NULL,
        '传输挡烟', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000009, '562', '电机', 'DJ', '', 'DRS80S4（R27-DRS80S4）', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000010, '558', '塑包机电机', 'SBJDJ', '', '100YS200GY22（100GF5RS）', '件',
        '', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '拨烟皮带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000011, '64', '电机', 'DJ', '', 'Y2-7126', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000012, '593', '塑包机真空阀', 'SBJZKF', '', '58D-36-121BA', '件',
        '', 1000000000000000003, 2000000000000000033, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '小品规塑包机机械手', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000013, '578', '塑包机电机', 'SBJDJ', '', 'CM10-4-200-TJ（SV-A10）', '件',
        '', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000014, '552', '电机', 'DJ', '', 'DR63M4', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 1, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000015, '579', '塑包机电机', 'SBJDJ', '', 'CM11-4-400-TJ', '件',
        '', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '塑包机输送电机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000016, '551', '电机', 'DJ', '', 'DR63S4(R07-DR63S4)', '台',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 1, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000017, '588', '电机', 'DJ', '', 'RIK120GU-C-T（5GU-15KB-S2）', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 12, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000018, '523', '气缸', 'QG', '', 'MAC25*115SCA', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000019, '242', '低噪音钢丝同步带', 'DZYGSTBD', '', '16500-14M-90', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000020, '576', '塑包机伺服电机', 'SBJSFDJ', '', 'JSMA-PLC80ABA', '件',
        '', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '小品规翻板', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000021, '525', '气缸', 'QG', '', 'MAC25*42SCAG(CD737B)', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '划箱机前', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000022, '574', '塑包机伺服电机', 'SBJSFDJ', '', 'JSMA-PUC04ABK', '件',
        '', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '拨烟伺服', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000023, '550', '电机', 'DJ', '', 'DR63L4', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 1, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000024, '733', '轴承', 'ZC', '', '6801Z-NSK', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000025, '585', '电机', 'DJ', '', '5RK90GU-C-T（5GU-7.5KB-S2）', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 5, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000026, '534', '塑包机气缸', 'SBJQG', '', 'DSBC25*200-PPV-A', '件',
        '', 1000000000000000003, 2000000000000000025, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '大品规塑包机挡板', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000027, '555', '电机', 'DJ', '', 'DRS80S4(R17-DRS80S4)', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 1, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000028, '833', '304不锈钢沉头内六角螺丝', '304BXGCTNLJLS', '', 'M8*35', '件',
        '', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 0, 100, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000029, '570', '塑包机伺服电机', 'SBJSFDJ', '', 'ECMA-CA0604SS', '件',
        '', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '推烟伺服', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000030, '586', '电机', 'DJ', '', '5RK90GU-C-FT-9W', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000031, '903', '驱动轮（补货小车）', 'QDLBHXC', '', 'TYFJD02-02-00-00-03', '个',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '补货小车丝杆电机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000032, '567', '电机', 'DJ', '', 'DRE80M4(SA37/T-DRE80M4BE1)', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000033, '560', '塑包机电机', 'SBJDJ', '', '90YB90GY22（90GF5H）', '件',
        '', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '机械手推烟电机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000034, '546', '气缸', 'QG', '', 'DSBC32*100-PPSA-N', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '大品规划箱机压烟', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000035, '505', '气缸', 'QG', '', 'MAC25*100SCA（CD752B）', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 3, NULL, NULL,
        '小品上下耳挡烟气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000036, '571', '伺服电机', 'SFDJ', '', 'ECMA-C20604RS', '件',
        '', 1000000000000000001, 2000000000000000012, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '细支划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000037, '515', '旋转气缸', 'XZQG', '', 'MCQB50R', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000038, '575', '塑包机伺服电机', 'SBJSFDJ', '', 'JSMA-PLC80ABK', '件',
        '', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '大品规翻版', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000039, '568', '电机', 'DJ', '', 'DRE80M4(SA37/T-DRE80M4BE1HF)', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000040, '503', '气缸', 'QG', '', 'MAC32*100SCAG', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 3, NULL, NULL,
        '侧耳挡烟气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000041, '248', '导轨SBR25', 'DGSBR25', '', '营口19TS701200B-00-00-00-01', '件',
        '', 1000000000000000001, 2000000000000000010, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000042, '526', '气缸', 'QG', '', 'MIC25*80SCA（CQ346B）', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '划箱机前', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000043, '556', '电机', 'DJ', '', 'DRE80M4', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000044, '564', '电机', 'DJ', '', 'DRS71M4（SA37/T-DRS71M4）', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000045, '539', '塑包机气缸', 'SBJQG', '', 'TD25*200', '件',
        '', 1000000000000000003, 2000000000000000025, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '小品规接烟', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000046, '529', '气缸', 'QG', '', 'CD85N25-40C-B', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 1, NULL, NULL,
        '滑道细支挡烟', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000047, '122', '滑座及滑轨', 'HJHG', '', 'HLH30AL', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000048, '121', '轴支座', 'ZZ', '', 'SK30', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000049, '565', '电机', 'DJ', '', 'DRS71M4（SA37/T-DRS71M4BE1）', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000050, '87', '轨道', 'GD', '', 'WSQ-16', '件',
        '', 1000000000000000001, 2000000000000000010, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000051, '572', '电机', 'DJ', '', 'PHSE18-A（0.2KW）', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '标烟划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000052, '955', '同步皮带', 'TBPD', '', '520-8M-20', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000053, '583', '塑包机电机', 'SBJDJ', '', 'CM12-4-750-TJ', '件',
        '', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '塑包机输送电机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000054, '563', '电机', 'DJ', '', 'DRS80S4（R37-DRS80S4）', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000055, '41', '推头', 'TT', '', 'TYFJA01-06-01-00-00-01', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000056, '166', '轨道', 'GD', '', 'WS-10', '件',
        '', 1000000000000000001, 2000000000000000010, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000057, '566', '电机', 'DJ', '', 'DRS80S4(SA37/T-DRS80S4)', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000058, '221', '压力开关(配座子)', 'YLKGPZ', '', 'SDE5-D10-O-Q6E-P-K', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000059, '521', '气缸', 'QG', '', 'MAC40*110SCAC(CQ308B)', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000060, '559', '塑包机电机', 'SBJDJ', '', '100YB200GY22(100GF5H)', '件',
        '', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '推烟电机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000061, '509', '气缸', 'QG', '', 'MAC32*100SCAQ（CQ265）', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '侧耳挡烟气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000062, '548', '气缸', 'QG', '', 'ADN32*125-A-PPS-A', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '细支划箱机侧压', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000063, '516', '气缸', 'QG', '', 'TCL20*100SG', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000064, '661', '塑包机磁偶合式无杆气缸', 'SBJCOHSWGQG', '', 'RMS20*450', '件',
        '', 1000000000000000003, 2000000000000000025, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000065, '413', '交流接触器', 'JLJCQ', '', 'LC1D11500M5C', '件',
        '', 1000000000000000002, 2000000000000000022, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-001', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000066, '908', '变频器丹佛斯380-480v', 'BPQDFS380480V', '', 'PC-051P1K5T4E20H3BXCXXXSXXX380V', '件',
        '普天', 1000000000000000002, 2000000000000000021, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-002; 变频器', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000067, '721', '塑包机变频器', 'SBJBPQ', '', 'FC-051PK37S2(0.37KW/220V/丹佛斯0', '件',
        '', 1000000000000000003, 2000000000000000033, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '货位:A1-1-002; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000068, '722', '塑包机变频器', 'SBJBPQ', '', 'FC-051PK75S2(0.75KW/220V/丹佛斯）', '件',
        '', 1000000000000000003, 2000000000000000033, NULL, 1, 0, 1,
        0.0, 0, 0, 3, NULL, NULL,
        '货位:A1-1-002; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000069, '187', '司服电机', 'SFDJ', '', 'ECMA-C11010RS', '件',
        '', 1000000000000000001, 2000000000000000012, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-003', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000070, '806', '交流伺服驱动器400w/220-230v', 'JLSFQDQ400W220230V', '', 'ASD-A2-0421-L', '件',
        '', 1000000000000000002, 2000000000000000021, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-1-004; 普天', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000071, '419', '交流司服驱动器', 'JLSFQDQ', '', 'ASD-A2-0421-U', '件',
        '', 1000000000000000002, 2000000000000000021, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-004', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000072, '965', '交流伺服驱动器1kw/220-230v', 'JLSFQDQ1KW220230V', '', 'ASD-A2-1021-L', '件',
        '', 1000000000000000002, 2000000000000000021, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-004', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000073, '964', '交流伺服驱动器750w/220-230v', 'JLSFQDQ750W220230V', '', 'ASD-A2-0721-L', '件',
        '', 1000000000000000002, 2000000000000000021, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-004', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000074, '926', '塑包机磁性开关03.09.04.5019', 'SBJCXKG0309045019', '', 'DMSG-020', 'PCS',
        '', 1000000000000000003, 2000000000000000028, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-005; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000075, '174', '光电开关', 'GDKG', '', 'E3FA-DP14-PT', 'PCS',
        '', 1000000000000000001, 2000000000000000015, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-006', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000076, '163', '接近开关', 'JJKG', '', 'DMSJ-P020/IP64', '件',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-008', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000077, '615', '限位开关', 'XWKG', '', 'DMSH-P020/PNP/IP64', '个',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-1-009', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000078, '173', '接近开关', 'JJKG', '', 'DMSG-P020/PNP/IP64/IP65', '件',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-010', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000079, '47', '磁性开关', 'CXKG', '', 'D-M9PL', '个',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-011', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000080, '616', '磁性限位开关', 'CXXWKG', '', 'D-M9B', '个',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-1-011', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000081, '148', '接近开关', 'JJKG', '', 'DMSE-020/俩线式2-wire/IP64', '个',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-012', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000082, '161', '接近开关', 'JJKG', '', 'DMSE-P020/PNP/IP65', '个',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-012', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000083, '834', '接近开关', 'JJKG', '', 'DMSG-P020/PNP/IP64', '个',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-013', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000084, '147', '接近开关', 'JJKG', '', 'IS-12-H1-03', '件',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-014', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000085, '175', '光电开关', 'GDKG', '', 'ZR-350N', '件',
        '', 1000000000000000001, 2000000000000000015, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-015', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000086, '986', '接近开关', 'JJKG', '', 'SMT-8M-A-PS-24-E-2,5-OE', '件',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 20, NULL, NULL,
        '货位:A1-1-016', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000087, '146', '接近开关', 'JJKG', '', 'AMI/AN-4A', '件',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-017', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000088, '145', '接近开关', 'JJKG', '', '150376-SIEN-6，5B-NS-K-L', '件',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-018', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000089, '144', '接近开关', 'JJKG', '', 'SME-8M-DS-24V-K-2,5-OE', '件',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-019', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000090, '107', '光电开关', 'GDKG', '', 'S100-PR-2-D00-PK', '件',
        '', 1000000000000000001, 2000000000000000015, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-020', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000091, '110', '塑包机热电偶', 'SBJRDO', '', 'WRKX-210', '件',
        '', 1000000000000000003, 2000000000000000028, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-021; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000092, '115', '接近开关', 'JJKG', '', 'SMT-8M-A-NS-24V-E-Z', '件',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-022', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000093, '118', '接近开关', 'JJKG', '', 'NBB4-12GM50-E2', '件',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-023; 库房堆垛机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000094, '998', '接近开关', 'JJKG', '', 'NNBB4-12GM30-E2', '件',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-023', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000095, '759', '接近传感器', 'JJCGQ', '', 'E2A-M18KS08-WP-B1', '件',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-1-025', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000096, '124', '接近开关', 'JJKG', '', 'NBB2-8GM30-E2', '件',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-026', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000097, '130', '接近开关', 'JJKG', '', 'E2B-M18KS08-WZ-B1', '件',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-027', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000098, '132', '接近开关', 'JJKG', '', 'E2A-M18KS08-WP-C1', '件',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-028', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000099, '129', '接近开关', 'JJKG', '', 'NBB4-12GM50-5M', '件',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-029', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000100, '131', '接近开关', 'JJKG', '', 'DSIB2P020', '件',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-030', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000101, '140', '对射开关', 'DSKG', '', 'E3Z-D82-PT', '件',
        '', 1000000000000000001, 2000000000000000015, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-033', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000102, '105', '对射开关', 'DSKG', '', 'E3Z-D61', '件',
        '', 1000000000000000001, 2000000000000000015, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-035', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000103, '141', '塑包机对射开关', 'SBJDSKG', '', 'EX-13EB', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-036; 包装机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000104, '142', '漫反射开关', 'MFSKG', '', 'E3Z-R81', '件',
        '', 1000000000000000001, 2000000000000000015, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-037', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000105, '139', '对射开关', 'DSKG', '', 'E3Z-T81-PT', '件',
        '', 1000000000000000001, 2000000000000000015, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-038', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000106, '823', '漫反射开个', 'MFSKG', '', 'E3Z-D81', '件',
        '', 1000000000000000001, 2000000000000000015, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-039', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000107, '143', '对射开关', 'DSKG', '', 'E3Z-G81', '件',
        '', 1000000000000000001, 2000000000000000015, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-040', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000108, '153', '吸盘(绿色大号)', 'XPLSDH', '', 'SPB1-60-ED-65-SC050', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-041; 侧耳', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000109, '116', '吸盘(绿色小号)', 'XPLSXH', '', 'SPB1-40-ED-65-G1/4-AG', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-042', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000110, '216', '黑色皮头(导杆锥形堵头）', 'HSPTDGZXDT', '', 'JKX01A-01-00-00-20', '件',
        '', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-043', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000111, '1032', '电池（伺服驱动器数据储存）', 'DCSFQDQSJCC', '', 'ER14505', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-044; 伺服驱动器数据储备电池', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000112, '656', '吸盘接头', 'XPJT', '', 'SC-050-G1/4-AG', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 20, NULL, NULL,
        '货位:A1-1-045', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000113, '79', '吸盘(黑色)', 'XPHS', '', 'VASB-55-1/4-NBR', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-046', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000114, '80', '吸盘座板', 'XPB', '', 'JKX01A-02-01-00-09', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-047', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000115, '942', '气弹簧安装件（划箱机）', 'QDHAZJHXJ', '', 'FHJ51-6D', '件',
        '', 1000000000000000001, 2000000000000000018, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-048; 划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000116, '664', '气弹簧安装件（划箱机）', 'QDHAZJHXJ', '', 'FHJ51-6A', '件',
        '', 1000000000000000001, 2000000000000000018, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-1-048', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000117, '204', '塑包机高温胶布（棕色）', 'SBJGWJBZS', '', '', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-050; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000118, '922', '浮动接头', 'FDJT', '', 'F-M10*125F亚德客', '个',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-051', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000119, '312', '急停按钮', 'JTAN', '', 'XALJ01C', '件',
        '', 1000000000000000002, 2000000000000000022, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-053', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000120, '38', '急停开关(红）', 'JTKGH', '', 'XA2ES542', '件',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-053', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000121, '178', '塑包机急停开关', 'SBJJTKG', '', 'LAY7', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-054; 贴标机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000122, '1003', '复位按钮（绿）', 'FWANL', '', 'XA2EA31', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-055', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000123, '138', '按钮开关(绿）', 'ANKGL', '', 'XA2EA35', '件',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-055', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000124, '168', '按钮', 'AN', '', 'XA2EA45', '件',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-055', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000125, '203', '复位开关按钮', 'FWKGAN', '', 'XB7-EA.1C', '件',
        '', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-055', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000126, '918', '熔断器', 'RDQ', '', 'RT18-32X', '个',
        '普天', 1000000000000000002, 2000000000000000022, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-056', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000127, '618', '划箱机双刃刀片', 'HXJSRDP', '', 'LKX201607A-10-70-10-07/ HSDD60.00.001/700MS-10-80-10-02', '片',
        '普天', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 0, 200, NULL, NULL,
        '货位:A1-1-057; 划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000128, '619', '划箱机锯齿刀片', 'HXJJCDP', '', 'LZYCJ-01A-10-70-04A', '件',
        '', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-1-057; 划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000129, '622', '划箱机深沟球轴承', 'HXJSGQZC', '', 'GB/T  276-1994     637/6', '个',
        '普天', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-1-058; 划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000130, '620', '划箱机限位板/限位块', 'HXJXWBXWK', '', 'LZYCJ-01A.10.60.07/  HSJX.01A.10.60.07', '件',
        '普天', 1000000000000000001, 2000000000000000018, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-1-058; 划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000131, '623', '划箱机压缩弹簧', 'HXJYSDH', '', 'LZYCJ-01A.10.60.04A/  HSJX.01A.10.60.04A', '件',
        '普天', 1000000000000000001, 2000000000000000018, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-1-058; 划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000132, '201', '划箱机拉伸弹簧', 'HXJLSTH', '', 'HSDD60.00.03', '个',
        '普天', 1000000000000000001, 2000000000000000018, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-059; 划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000133, '898', '划箱机拉伸弹簧', 'HXJLSTH', '', 'HSDD60.00.02', '个',
        '普天', 1000000000000000001, 2000000000000000018, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-059; 划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000134, '769', '联轴器', 'LZQ', '', '20*30-', '件',
        '', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-1-061; 多穿小车锁钩电机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000135, '350', '电机（锁钩）', 'DJSG', '', 'PG25370246000-76.8K', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-061', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000136, '109', '底座', 'D', '', 'F-SI63FA', '件',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-062', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000137, '199', '白色皮头', 'BSPT', '', '', '件',
        '', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-064', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000138, '329', '安装码（滑到气缸）', 'AZMHDQG', '', 'BM5-025', 'PCS',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-065; 固定气缸限位开关', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000139, '549', '气缸(划箱机）', 'QGHXJ', '', 'ADN25*25-A-PPS-A', '件',
        '亚德客', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '货位:A1-1-066; 细支划箱机齿刀', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000140, '395', '塑包机高温胶布（电子胶布）03.06.20.5001', 'SBJGWJBDZJB0306205001', '', 'ASF-110FR（0.08*13*10米）', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-067; 塑包机切刀', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000141, '876', '皮带（叉体回环）1169*25', 'PDCTHH116925', '', 'TYFJC05-00-00-00-03', '条',
        '普天', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-068; 大小品规细支叉体皮带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000142, '655', '张紧板（多穿小车）', 'ZJBDCXC', '', 'ZNB03-01-00-00-07', '个',
        '普天', 1000000000000000001, 2000000000000000018, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-1-069; 多穿小车pu皮带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000143, '866', '张紧顶板（多穿小车）', 'ZJDBDCXC', '', 'BJZNB01-02-01-00-20', '件',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-069; 多穿小车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000144, '937', '张紧板（多穿张紧轮）', 'ZJBDCZJL', '', 'ZNB01-04-11-00-01', '件',
        '', 1000000000000000002, 0, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-069; 多穿小车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000145, '895', '划箱机弹性刀架A01', 'HXJTXDJA01', '', 'HSDJ60.00.05', '件',
        '普天', 1000000000000000001, 2000000000000000018, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-071; 划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000146, '385', '划箱机弹性刀架(长）', 'HXJDXDJC', '', 'HSDJ60.00.04', '件',
        '普天', 1000000000000000001, 2000000000000000018, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-072; 划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000147, '112', '堆垛机清扫刷', 'DDJQSS', '', '', '个',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-073; 堆垛机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000148, '1019', '反射板(光电检测开关）', 'FSBGDJCKG', '', 'E39-R1', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-076', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000149, '897', '划箱机同步带', 'HXJTBD', '', 'HTD-1108-5M-25', '米',
        '普天', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-077; 划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000150, '992', '底座（多穿小车）', 'DDCXC', '', '', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-078; 多穿小车张紧轮底座', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000151, '266', '调速阀（开箱机气缸）', 'DSFKXJQG', '', 'ASP330F-01-08S', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-079; 开箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000152, '387', '小车滑触电刀', 'XCHCDD', '', 'K40-325M', '件',
        '', 1000000000000000001, 2000000000000000018, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-081', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000153, '261', '快速排气阀—开箱机', 'KSPQFKXJ', '', 'SEU-1/8', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-083; 开箱机-细支', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000154, '848', '铆钉（链板机）', 'MDLBJ', '', '', '个',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 10, 600, NULL, NULL,
        '货位:A1-1-084; 链板机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000155, '1034', '打码器（水冷）接头', 'DMQSLJT', '', '8-8', '件',
        '', 1000000000000000002, 2000000000000000034, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-085; 激光打码器', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000156, '117', '接头(拖链）', 'JTTL', '', '', '个',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 0, NULL, NULL,
        '货位:A1-1-086', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000157, '916', '开关支架', 'KGZJ', '', 'BZLJKGJ2011-00-02', '个',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-087', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000158, '917', '开关支架', 'KGZJ', '', 'BZLJKGJ2011-00-03', '个',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-087', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000159, '658', 'PU钢丝同步带（多穿小车）', 'PUGSTBDDCXC', '', 'PU1810-T5-15', '米',
        '普天', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 7, NULL, NULL,
        '货位:A1-1-090; 多穿小车伸叉', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000160, '410', '开关电源（12V）', 'KGDY12V', '', 'RS-15-12', '件',
        '', 1000000000000000002, 2000000000000000022, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-091', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000161, '407', '英特诺电动滚筒控制器', 'YTNDDGTKZQ', '', '1001415', '件',
        '', 1000000000000000002, 2000000000000000022, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-091', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000162, '939', '行程开关', 'XCKG', '', 'XCJ-127', '件',
        '', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-092; 库房拆垛机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000163, '966', '伺服驱动器插头', 'SFQDQCT', '', 'SM-20J', '件',
        '', 1000000000000000002, 2000000000000000021, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-093', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000164, '967', '伺服驱动器插头', 'SFQDQCT', '', 'MY11-50T', '件',
        '', 1000000000000000002, 2000000000000000021, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-093', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000165, '968', '伺服驱动器和电机连接插头', 'SFQDQHDJLJCT', '', '', '件',
        '', 1000000000000000002, 2000000000000000021, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-093', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000166, '327', 'PU钢丝同步带(多穿小车）', 'PUGSTBDDCXC', '', 'PU1810-T5-12', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-094; 多穿小车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000167, '30', '标准插座', 'BZC', '', 'MSSD-E', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-095', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000168, '962', '插头插座', 'CTC', '', 'MSSD-F', '件',
        '普天', 1000000000000000001, 2000000000000000017, NULL, 1, 0, 1,
        0.0, 0, 0, 90, NULL, NULL,
        '货位:A1-1-095', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000169, '961', '电磁阀线圈', 'DCFXQ', '', 'MSFG-24/42-50/60', '件',
        '亚德客', 1000000000000000001, 2000000000000000017, NULL, 1, 0, 1,
        0.0, 0, 0, 20, NULL, NULL,
        '货位:A1-1-096', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000170, '921', '门把手', 'MBS', '', 'LS522-1', '个',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-098', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000171, '957', '真空发生器（细支旋转）', 'ZKFSQXZXZ', '', 'VAD-3/8', '件',
        '普天', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-099; 大品规补货线细支旋转', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000172, '323', '检测座（多穿小车）', 'JCDCXC', '', 'ZNB03-01-00-00-04', 'PCS',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-101; 多穿小车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000173, '328', '轴承座（多穿小车）', 'ZCDCXC', '', 'ZNB03-01-00-00-05', 'PCS',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-101; 多穿小车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000174, '983', '电磁阀三位五通', 'DCFSWWT', '', 'SY5420-5DZ-01 SMC', '件',
        '', 1000000000000000001, 2000000000000000017, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-102; 开箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000175, '940', '继电器', 'JDQ', '', 'MY2N-GS DC24 BY OMZ/Z', '件',
        '', 1000000000000000002, 2000000000000000023, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-103', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000176, '959', '电磁线圈', 'DCXQ', '', 'VACS-C-C1-1', '件',
        '普天', 1000000000000000001, 2000000000000000017, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-104', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000177, '958', '电磁线圈', 'DCXQ', '', 'VACF-B-B2-1', '件',
        '普天', 1000000000000000001, 2000000000000000017, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-1-104', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000178, '335', '电机', 'DJ', '', '5IK120GU-C-T', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-105', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000179, '489', '电机', 'DJ', '', '5RK60GU-C-T', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 0, NULL, NULL,
        '货位:A1-1-106; 8好4旧', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000180, '346', '电机', 'DJ', '', '5IK120GU-S3B24-6T', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-107', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000181, '229', '电机', 'DJ', '', '5IK120GU-S3B-6T24（5GU-7。5KB-S2）', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-107; 小车拨烟电机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000182, '244', '电机', 'DJ', '', '5RK60GU-CFT/5GU7.5KB', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-108', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000183, '352', '电机', 'DJ', '', '5IK60GU-C-T/5GU-12.5KB-S', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-108', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000184, '339', '电机', 'DJ', '', '5IK40GN-CFT-40W', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-108', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000185, '336', '电机', 'DJ', '', '5IK40GN-C-40W', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-1-108', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000186, '750', '减速器', 'JSQ', '', 'ZPLF060-L1-5-K-P2', '件',
        '', 1000000000000000001, 2000000000000000011, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-2-001', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000187, '760', '伺服电机', 'SFDJ', '', 'MSK043C-0600-NN-M1-UG1-NNNN', 'PCS',
        '', 1000000000000000001, 2000000000000000012, NULL, 1, 0, 1,
        0.0, 0, 0, 12, NULL, NULL,
        '货位:A1-2-002', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000188, '183', '工业平板电脑（大品规划箱机）', 'GYPBDNDPGHXJ', '', 'PPC-5810', '件',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-2-004; 划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000189, '185', '风扇（二维码机柜）', 'FSEWMJG', '', 'SF9025AT-P/N2092HBL', '个',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-2-005; 二维码机柜', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000190, '197', '辊筒L103（划箱机）', 'GTL103HXJ', '', '700MS-10-20-10-01', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-2-008; 划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000191, '120', '下挡板（卧式机执行仓）', 'XDBWSJZXC', '', 'TYFJG03A-01-00-00-03', '件',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-2-010; （卧式机执行仓）', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000192, '196', '滚筒L255（划箱机）', 'GTL255HXJ', '', '700MS-10-10-10-01', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-2-011; 划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000193, '24', '不锈钢传动辊筒（库房备速链）', 'BXGCDGTKFBSL', '', '哈烟18JYFJE02A-00-00-00-05/2031.BG2.5015-S11B-58.5C-125L-T', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-021; 塑钢多楔带传动式输送辊筒', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000194, '902', '划箱机转换接头', 'HXJZHJT', '', '75转50', 'PCS',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-2-027; 激光划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000195, '944', '电磁阀', 'DCF', '', 'VUVS-L20-M52-MD-G18-F7-1C1', '件',
        '', 1000000000000000001, 2000000000000000017, NULL, 1, 0, 1,
        0.0, 0, 0, 20, NULL, NULL,
        '货位:A1-2-041/042/043', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000196, '63', '电磁阀', 'DCF', '', 'VUVS-L20-M52-MD-G18-F7-1C1', '个',
        '普天', 1000000000000000001, 2000000000000000017, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-041/042/043', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000197, '604', '电磁阀', 'DCF', '', 'VUVS-L20-P53C-MD-G18-F7-1C1', '件',
        '', 1000000000000000001, 2000000000000000017, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-2-045', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000198, '15', '电磁阀', 'DCF', '', 'VUVS-L20-P53E-MD-G18-F7-1C1', '件',
        '', 1000000000000000001, 2000000000000000017, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-046', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000199, '960', '电磁阀', 'DCF', '', 'MFH-5-1/4-B', '件',
        '普天', 1000000000000000001, 2000000000000000017, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-2-047', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000200, '605', '电磁阀', 'DCF', '', 'MFH-5-3/8-B', '件',
        '', 1000000000000000001, 2000000000000000017, NULL, 1, 0, 1,
        0.0, 0, 0, 12, NULL, NULL,
        '货位:A1-2-048', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000201, '606', '电磁阀', 'DCF', '', 'VUVS-LT30-M52-MD-G38-F8-1C1', '件',
        '', 1000000000000000001, 2000000000000000017, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-2-049', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000202, '607', '电磁阀（大品规核数气缸）', 'DCFDPGHSQG', '', 'MEBH-5/2-1/8-B', '个',
        '普天', 1000000000000000001, 2000000000000000017, NULL, 1, 0, 1,
        0.0, 0, 0, 12, NULL, NULL,
        '货位:A1-2-050; 大品规核数', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000203, '608', '电磁阀', 'DCF', '', 'CPE14-M1BH-5L-1/8', '件',
        '', 1000000000000000001, 2000000000000000017, NULL, 1, 0, 1,
        0.0, 0, 0, 12, NULL, NULL,
        '货位:A1-2-051', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000204, '603', '电磁阀', 'DCF', '', 'VUVS-L20-M32C-MD-G18-F7-1C1', '件',
        '', 1000000000000000001, 2000000000000000017, NULL, 1, 0, 1,
        0.0, 0, 0, 6, NULL, NULL,
        '货位:A1-2-052', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000205, '49', 'U型夹', 'UXJ', '', 'GERM-10F', '件',
        '', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-057', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000206, '485', 'UY', 'UY', '', '型SC气缸Y型接头', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 0, NULL, NULL,
        '货位:A1-2-059', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000207, '949', '连接柄', 'LJB', '', 'TFA-02-00-00-02', '个',
        '普天', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-2-060; 停放器', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000208, '58', '合页(铰链)', 'HYJL', '', 'CL211', '件',
        '', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 2, 30, NULL, NULL,
        '货位:A1-2-061', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000209, '662', '弹性负载销', 'TXFZX', '', 'CEFM-10', '件',
        '', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 0, 20, NULL, NULL,
        '货位:A1-2-062', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000210, '48', '弹性负载销', 'TXFZX', '', 'GEFM-10', '件',
        '', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-062', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000211, '501', '链条卡簧链扣', 'LTKHLK', '', '', '件',
        '', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 0, 0, NULL, NULL,
        '货位:A1-2-063', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000212, '502', 'U型卡头，绳卡', 'UXKTSK', '', '', '件',
        '', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 0, 0, NULL, NULL,
        '货位:A1-2-064', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000213, '33', '机柜转舌锁/挡烟门锁（执行/烟仓）', 'JGZSSDYMSZXYC', '', '714/MS714-4-1/S1B4512', '个',
        '', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 2, 30, NULL, NULL,
        '货位:A1-2-065', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000214, '1002', '生久柜锁', 'SJGS', '', 'MS722', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-2-065', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000215, '98', '合页卧式机', 'HYWSJ', '', 'CL253-1', '个',
        '普天', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 3, 30, NULL, NULL,
        '货位:A1-2-066; 卧式机柜门', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000216, '93', '销轴12x45', 'XZ12X45', '', 'JKX01A-02-00-00-06', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-067', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000217, '499', '销轴', 'XZ', '', '', '件',
        '', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-2-067', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000218, '71', '销轴10x45', 'XZ10X45', '', 'JKX01A-02-00-00-05', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-067', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000219, '864', '滑块(小）', 'HKX', '', 'WJ200VW-01-10', '件',
        '普天', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 3, 20, NULL, NULL,
        '货位:A1-2-068; 卧式机出推气缸滑块', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000220, '596', '扭臂', 'NB', '', 'BZLJJLJ2011-00-12', '件',
        '', 1000000000000000002, 0, NULL, 1, 0, 1,
        0.0, 0, 2, 30, NULL, NULL,
        '货位:A1-2-071; 分拣线', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000221, '617', '方型滑块(大）', 'FXHKD', '', 'WJ200QM-01-16-LLY', '件',
        '', 1000000000000000001, 2000000000000000018, NULL, 1, 0, 1,
        0.0, 0, 0, 12, NULL, NULL,
        '货位:A1-2-072', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000222, '804', '铰链', 'JL', '', 'CL201-1', '件',
        '', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 0, 100, NULL, NULL,
        '货位:A1-2-073', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000223, '811', '快插接头PC8-01', 'KCJTPC801', '', 'PC8-01', '个',
        '普天', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-074; I型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000224, '170', '快插接头PC8-02', 'KCJTPC802', '', 'PC8-02', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-074; 直接', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000225, '945', '直通接头', 'ZTJT', '', 'PC6', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-2-075', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000226, '946', '直通接头', 'ZTJT', '', 'PC10', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-2-075', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000227, '181', '快插接头PL8-04', 'KCJTPL804', '', 'PL8-04', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-076; L型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000228, '331', '三通变径APEG8-6', 'STBJAPEG86', '', 'APEG8-6', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-077; T型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000229, '1000', '手阀', 'SF', '', 'PHV12A', '件',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-2-078', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000230, '1001', '手阀', 'SF', '', 'PHV10A', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-2-078', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000231, '200', '三通APE-12', 'STAPE12', '', 'APE-2', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-079; T型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000232, '219', '快插接头PL12-02', 'KCJTPL1202', '', 'PL12-02', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-080; L型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000233, '251', '节流阀SA-10', 'JLFSA10', '', 'SA-10', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-081', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000234, '482', '节流阀（亚）', 'JLFY', '', 'PSA08', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 50, NULL, NULL,
        '货位:A1-2-081', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000235, '483', '节流阀（亚）', 'JLFY', '', 'PSA10', 'PCS',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 50, NULL, NULL,
        '货位:A1-2-081', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000236, '360', '三通APE-8', 'STAPE8', '', 'APE-8', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-082; Y型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000237, '388', '三通变径APEG10-8', 'STBJAPEG108', '', 'APEG10-8', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-082; Y型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000238, '807', '三通TPE-10', 'STTPE10', '', 'APE-10', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 2, 100, NULL, NULL,
        '货位:A1-2-083; 三通T型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000239, '332', '调速阀ASL12-01', 'DSFASL1201', '', 'ASL12-01', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-084; L型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000240, '150', '塑料泡沫消声器', 'SLPMXSQ', '', 'PAL-04', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-085', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000241, '114', '塑料泡沫消声器', 'SLPMXSQ', '', 'PAL-03', '件',
        '', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-086', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000242, '164', '塑料泡沫消声器', 'SLPMXSQ', '', 'PAL-02', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-087', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000243, '29', '塑料泡沫消声器', 'SLPMXSQ', '', 'PAL-01', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-088', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000244, '311', '快插接头PL8-03', 'KCJTPL803', '', 'PL8-03', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-089; L型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000245, '217', '快插接头PL8-02', 'KCJTPL80', '', 'PL8-02', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-090', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000246, '292', '快插接头PL8-01', 'KCJTPL801', '', 'PL8-01', '个',
        '普拉多', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-091; L型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000247, '236', '快插接头PL6-01', 'KCJTPL601', '', 'PL6-01', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-092; L型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000248, '232', '快插接头PL6-5M', 'KCJTPL65M', '', 'PL6-5M', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-092; 贴标机L型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000249, '382', '快插接头PL10-04', 'KCJTPL1004', '', 'PL10-04', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-093; L型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000250, '340', '快插接头PL10-03', 'KCJTPL1003', '', 'PL10-03', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-094; L型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000251, '330', '快插接头PL10-02', 'KCJTPL1002', '', 'PL10-02', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-095; L型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000252, '288', '快插接头PL10-01', 'KCJTPL1001', '', 'PL10-01', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-096; L型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000253, '446', '调速阀ASL10-01', 'DSFASL1001', '', 'ASL10-01', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-097; L型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000254, '295', '调速阀ASL10-02', 'DSFASL1002', '', 'ASL10-02', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-098; L型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000255, '303', '调速阀ASL8-03', 'DSFASL803', '', 'PSL8-03-A', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-099; L型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000256, '305', '调速节流阀ASL8-01', 'DSJLFASL801', '', 'ASL8-01', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-100; L型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000257, '306', '三通变径APEG10-8', 'STBJAPEG108', '', 'APEG10-8', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-101; Y型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000258, '308', '三通APE-6', 'STAPE6', '', 'APE-6', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-102; T型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000259, '875', '二通接头', 'ETJT', '', 'PV-10', '个',
        '普天', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-103; 直角接头', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000260, '852', '直通PU-8', 'ZTPU8', '', 'PU-8', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 100, NULL, NULL,
        '货位:A1-2-103; I型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000261, '851', '直通PU-6', 'ZTPU6', '', 'PU-6', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 100, NULL, NULL,
        '货位:A1-2-103; I型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000262, '853', '直通PU-10', 'ZTPU10', '', 'PU-10', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 100, NULL, NULL,
        '货位:A1-2-103; I型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000263, '874', '二通接头', 'ETJT', '', 'PV-8', '个',
        '普天', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-103; 直角接头', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000264, '810', '直通变径PG12-10', 'ZTBJPG1210', '', 'PG12-10', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-104; I型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000265, '850', '直通变径PG8-6', 'ZTBJPG86', '', 'PG8-6', '个',
        '亚德客', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 100, NULL, NULL,
        '货位:A1-2-104; I型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000266, '809', '直通变径PG10-8', 'ZTBJPG108', '', 'PG10-8', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-104; I型', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000267, '343', '电机DRS71S4功率037kw转速1380r/min', 'DJDRS71S4GL037KWZS1380RMIN', '', 'R07-DRS71S4', '件',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-105; 小品核数皮带电机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000268, '176', '电机', 'DJ', '', 'R17-DR63L4', '件',
        'ST', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-105; 补货小车缓存皮带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000269, '764', '电机DRN71MS4转速1405r/min功率0.25kw', 'DJDRN71MS4ZS1405RMINGL025KW', '', 'R17-DRN71MS4速比10.15转速1405-138r/min扭矩17nm功率0.25kw', 'ST',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-2-105', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000270, '344', '电机', 'DJ', '', 'E22', 'ST',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-105', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000271, '6', '电机SK80LP/4TF(SK172.1-80LP/4TF)', 'DJSK80LP4TFSK172180LP4TF', '', 'SK 172.1-80LP/4', '个',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-106', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000272, '26', '电机SK80LP/4TF(SK92072.1ABDH-80LP/4TF)', 'DJSK80LP4TFSK920721ABDH80LP4TF', '', 'SK92072.1AZDBH-80LP/4', '个',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-2-106', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000273, '947', '减速电机DRN71M4   功率0.37kw 转速1415r/min', 'JSDJDRN71M4GL037KWZS1415RMIN', '', 'R17-DRN71M4 速比3.83 转速1415/369r/min  扭矩9Nm 功率0.37kw', '件',
        '', 1000000000000000001, 2000000000000000011, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-2-107; 2台在副库', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000274, '751', '减速器', 'JSQ', '', 'ZPT140-L2-15-K-P2', '件',
        '', 1000000000000000001, 2000000000000000011, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-2-107', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000275, '904', '电机DRN71M4转速14515r/min功率0.37kw', 'DJDRN71M4ZS14515RMINGL037KW', '', 'R07-DRN71M4速比3.68转速1415/385r/min扭矩9nm功率0.37kw', '件',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-2-108', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000276, '859', '打码驱动滚筒（大品规）T35CM/53MM/Z38.5)', 'DMQDGTDPGT35CM53MMZ385', '', '哈烟18ZXDBPD-03-00-00-00', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-01; 大品规打码滚筒', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000277, '45', '510打码端头滚筒T37.5-37.4mm/54-56mm/Z40.5cm', '510DMDTGTT375374MM5456MMZ405CM', '', '哈烟18YXDBPDA-03-00-00-00', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-01; 俩边有弧度', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000278, '746', 'Φ40滚筒总成（小品规）T37.5cm/40mm/Z40.5cm', '40GTZCXPGT375CM40MMZ405CM', '', 'PDL01A-01-00-01-00', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-01; 小品规', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000279, '380', '气缸', 'QG', '', 'DNC-50*50PPVA-RPD', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-02; 2件是旧件', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000280, '689', '从动滚筒T55.5cm/70mm/Z61cm', 'CDGTT555CM70MMZ61CM', '', 'T55.5cm*70mm*Z61cm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-03; 轴俩边有螺丝孔', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000281, '28', '端头驱动滚筒T55.5cm/70mm/65.5cm', 'DTQDGTT555CM70MM655CM', '', 'PDL02B-04-03-00', 'PCS',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-03; 轴俩侧有螺丝孔', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000282, '7', '同步带', 'TBD', '', '90HTD14M-16500', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-03', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000283, '291', '端头滚筒总成T65.5cm/70mm/Z71cm', 'DTGTZCT655CM70MMZ71CM', '', 'PDL02B-01-00-01-00', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-03; 端头滚筒', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000284, '282', '端头滚筒总成T55.5cm/70mm/Z61cm', 'DTGTZCT555CM70MMZ61CM', '', 'PDL02A-01-00-01-00', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-03; 端头滚筒', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000285, '659', '双面齿钢丝同步带（多穿小车）', 'SMCGSTBDDCXC', '', 'B45T10-1600DE', '米',
        '普天', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-03; 多穿小车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000286, '100', '开口PU同步带', 'KKPUTBD', '', '64HTD/4M-5000', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-03', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000287, '294', '张紧滚筒总成T65.5cm/70mm/75cm', 'ZJGTZCT655CM70MM75CM', '', 'PDL02B-01-00-03-00', 'PCS',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-03; 轴俩侧有螺丝孔', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000288, '293', '改向滚筒总成T65.5cm/70mm/Z69.5cm', 'GXGTZCT655CM70MMZ695CM', '', 'PDL02B-01-00-02-00', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-03; 改向滚筒', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000289, '694', '从动滚筒T55.5cm/70mm/Z61cm', 'CDGTT555CM70MMZ61CM', '', 'T55.5cm*70mm*Z61cm', 'PCS',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-03', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000290, '283', '张紧滚筒总成T65.5cm/70mm/71cm', 'ZJGTZCT655CM70MM71CM', '', 'PDL02A-01-00-03-00', 'PCS',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-03; 轴俩侧有螺丝孔', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000291, '22', '端头滚筒T55.5cm/70mm/61cm', 'DTGTT555CM70MM61CM', '', 'PDL02A-07-00-04-00', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-03; 轴俩侧有螺丝孔', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000292, '934', '同步带', 'TBD', '', '45HTD8M-5704/HTD5704-8M-45', '件',
        '普天', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-03', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000293, '102', '转弯皮带(30)1504mnm*355mm', 'ZWPD301504MNM355MM', '', '榆林19WD01-00-00-00-01', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-04; 大品规', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000294, '872', '转弯皮带2198mm*360mm', 'ZWPD2198MM360MM', '', '2198mm*360mm', '件',
        '普天', 1000000000000000002, 0, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-04; 转弯皮带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000295, '727', '转弯皮带(45)2198mm*355mm', 'ZWPD452198MM355MM', '', '重庆16ZWPDA-01-00-00-04', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-04', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000296, '774', '790滚筒T67cm/70cm/Z71cm', '790GTT67CM70CMZ71CM', '', '790', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-04; 库房三层入库皮带一侧有齿轮', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000297, '597', '转弯皮带2198mm*395mm', 'ZWPD2198MM395MM', '', '2.198mm*395mm', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 6, NULL, NULL,
        '货位:A1-G-04; 分拣线', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000298, '103', '转弯输送皮带2224mm*415mm', 'ZWSSPD2224MM415MM', '', '2224*415/SY16082525', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-04', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000299, '691', '6齿同步滚筒T40cm/55mm/Z43cm', '6CTBGTT40CM55MMZ43CM', '', 'T40cm*55mm*Z43cm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-04; 6齿', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000300, '775', '滚筒T57cm/(69-71)mm/61cm', 'GTT57CM6971MM61CM', '', 'T57cm/(69-71)mm/61cm', 'PCS',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-04; 一端有齿轮', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000301, '777', '转弯锥度滚筒T43cm/76-99cm/Z75.5cm', 'ZWZDGTT43CM7699CMZ755CM', '', 'T43cm/76-99cm/Z75.5cm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-04; 转弯滚筒', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000302, '290', '驱动滚筒总成T68.3cm/70mm/Z71cm', 'QDGTZCT683CM70MMZ71CM', '', 'PDL02B-10-02-00-00', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-04; 一边多楔带/一边齿轮', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000303, '670', '2齿同步滚筒T 40cm/55mm/Z43cm', '2CTBGTT40CM55MMZ43CM', '', 'T 40cm*55*mmZ 42.5/43cm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-04', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000304, '669', '2齿同步滚筒T36cm/55/mmT 39cm', '2CTBGTT36CM55MMT39CM', '', 'T36cm*55mm*Z 38.5/39cm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-04', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000305, '795', '不锈钢无动力滚筒（蓝）总长67.5cm/50mm', 'BXGWDLGTLZC675CM50MM', '', '总长67.5cm/直径50mm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 20, NULL, NULL,
        '货位:A1-G-05; 不锈钢无动力', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000306, '793', '不锈钢无动力滚筒总长57.5cm/50mm', 'BXGWDLGTZC575CM50MM', '', '总长57.5cm/直径50mm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 12, NULL, NULL,
        '货位:A1-G-05; 不锈钢无动力', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000307, '753', '塑包机多沟带（橡胶*4mm红胶）', 'SBJDGDXJ4MMHJ', '', 'PJ(E-2.34)56(W)*内周长1240', 'PCS',
        '', 1000000000000000003, 2000000000000000032, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-05; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000308, '790', '不锈钢多楔带滚筒总长58cm/50mm', 'BXGDXDGTZC58CM50MM', '', '筒总长58cm/直径50mm', 'PCS',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 20, NULL, NULL,
        '货位:A1-G-05; 不锈钢', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000309, '794', '不锈钢无动力滚筒总长58cm/50mm', 'BXGWDLGTZC58CM50MM', '', '下托辊PDL02A-01-00-04-00', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-05; 下托辊不锈钢/无动力', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000310, '788', '不锈钢多楔带滚筒总长68.3cm/50mm', 'BXGDXDGTZC683CM50MM', '', '总长68.3cm/直径50mm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 0, NULL, NULL,
        '货位:A1-G-05; 不锈钢', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000311, '789', '不锈钢多楔带滚筒总长68cm/50mm', 'BXGDXDGTZC68CM50MM', '', '总长68cm/直径50mm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 20, NULL, NULL,
        '货位:A1-G-05; 不锈钢', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000312, '459', '滚筒790(不锈钢）', 'GT790BXG', '', 'LJGT01-00-02', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 20, NULL, NULL,
        '货位:A1-G-05', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000313, '752', '塑包机多沟带（橡胶*4mm红胶)', 'SBJDGDXJ4MMHJ', '', 'PJ(E-2.34)56(W)内周长L820', 'PCS',
        '', 1000000000000000003, 2000000000000000032, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-05; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000314, '792', '不锈钢滚筒总长（黑）67.5cm/50mm', 'BXGGTZCH675CM50MM', '', '总长67.5cm/直径50mm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 20, NULL, NULL,
        '货位:A1-G-05; 不锈钢无动力滚筒', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000315, '741', '不锈钢多契带从动滚筒', 'BXGDQDCDGT', '', '京烟17ZZA01-03-00-00-01', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-05; 俩侧有螺丝孔', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000316, '104', '传送皮带', 'CSPD', '', 'E8/20/V45/CTSTR/13LACK600*340', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-05', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000317, '787', '不锈钢多楔带滚筒总长63.5cm/50mm', 'BXGDXDGTZC635CM50MM', '', '总长63.5cm/直径50mm', 'PCS',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 60, NULL, NULL,
        '货位:A1-G-05; 不锈钢', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000318, '1015', '510托辊总成(硅胶）', '510TGZCGJ', '', 'PDL01C-01-00-01-00', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-06; 硅胶', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000319, '231', '470托辊（硅胶）', '470TGGJ', '', 'PDL01F-01-07-0-00', '件',
        '普天', 1000000000000000002, 0, NULL, 1, 0, 1,
        0.0, 0, 0, 0, NULL, NULL,
        '货位:A1-G-06; 硅胶', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000320, '35', '510紧张滚筒T37cm/54mm/50.5cm', '510JZGTT37CM54MM505CM', '', '西安18ZXDBPD-04-00-00-00', 'PCS',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-06; 轴俩侧有螺丝孔', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000321, '783', '带传动主动辊', 'DCDZDG', '', 'FK11K-2100-23', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-06; 黑色滚筒', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000322, '780', '470张紧滚筒T33.5cm/54mm/46.5cm', '470ZJGTT335CM54MM465CM', '', 'T33.5cm/54mm/46.5cm-@2', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-06; 轴俩侧有螺丝孔', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000323, '743', '不锈钢双槽滚筒总长40cm/60mm', 'BXGSCGTZC40CM60MM', '', '榆林19BSLB-04-02-00-02', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 22, NULL, NULL,
        '货位:A1-G-06; 俩侧螺丝孔固定/圆带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000324, '8', '510打码端头滚筒(粗）', '510DMDTGTC', '', '哈烟18YXDBPDA-03-00-00-00/T39CM/54-55MM/Z42.5CM', 'ST',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-06', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000325, '665', '划箱机多楔带PVC塑料棍筒T44cm/54mm/47.5cm', 'HXJDXDPVCSLGTT44CM54MM475CM', '', 'LKX201607A-10.30.11A/  HSJG03.10.3011A', '个',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 20, NULL, NULL,
        '货位:A1-G-06; 激光划箱机硅胶多楔带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000326, '672', '端头张紧滚筒T37.5cm//54mm/Z40.5cm', 'DTZJGTT375CM54MMZ405CM', '', 'T37.5cm//54mm/Z40.5cm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-06', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000327, '782', '带传动主动辊029-20外', 'DCDZDG02920W', '', 'FK11K-2100-23', 'PCS',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-06; 黑色滚筒', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000328, '784', '不锈钢双槽滚筒总成38.5cm/60mm', 'BXGSCGTZC385CM60MM', '', '总成38.5cm/60mm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-06; 不锈钢/圆带/双槽', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000329, '781', '470改向滚筒T33.5cm/54mm/Z36.5cm', '470GXGTT335CM54MMZ365CM', '', 'T33.5cm/54mm/Z36.5cm-@3', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-06; 改向滚筒', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000330, '778', '470驱动滚筒03-02/T39cm/54mm/Z43cm', '470QDGT0302T39CM54MMZ43CM', '', 'T39cm/53-54mm/*Z43-42.5cm-@1', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-06; 一侧多楔带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000331, '744', '不锈钢双槽滚筒总长39.5cm/60mm', 'BXGSCGTZC395CM60MM', '', 'JKX01A-01-17-00-00', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 22, NULL, NULL,
        '货位:A1-G-06; 双槽圆带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000332, '779', '510打码端头驱动滚筒T39cm/53-54mm/*Z43-42.5cm', '510DMDTQDGTT39CM5354MMZ43425CM', '', 'T39cm/53-54mm/*Z43-42.5cm-@1', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-06; 一侧多楔带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000333, '785', '不锈钢滚筒总长37.5cm/38mm', 'BXGGTZC375CM38MM', '', '总长37.5cm/38mm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-06; 不锈钢，小短', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000334, '1033', '驱动滚筒（小品规）', 'QDGTXPG', '', 'T39CM/53-54MM/Z42.5CM', 'ST',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-06; 多楔带驱动滚筒', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000335, '683', '锥度转弯从动滚筒T39cm/80-100/Z57cm', 'ZDZWCDGTT39CM80100Z57CM', '', 'T39cm/80-100/Z57cm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-07; 转弯滚筒', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000336, '763', '驱动总成', 'QDZC', '', '百色17BSLSJ-03-00-00-00', 'PCS',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-07', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000337, '681', '112驱动滚筒T41.7cm/110mm/Z48.5cm（多楔带）', '112QDGTT417CM110MMZ485CMDXD', '', 'PDL01C-01-02-00-00', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-07; 一侧多契带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000338, '39', '打击机构二（立式机）', 'DJJGELSJ', '', 'TYFJC01A-01-03-00-00-00', '件',
        '普天', 1000000000000000001, 2000000000000000018, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-07; 立式机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000339, '36', '（多楔带）80驱动滚筒T41.5/41.7cm/80mm/48.5cm', 'DXD80QDGTT415417CM80MM485CM', '', 'T41.5/41.7cm/80mm/48.5cm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-07; 一侧多楔带驱动', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000340, '97', '打击机构一(立式机）', 'DJJGYLSJ', '', 'TYFJC01A-01-02-00-00-00', '件',
        '', 1000000000000000001, 2000000000000000018, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-07; 立式机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000341, '739', '驱动轴总成', 'QDZZC', '', '京烟17JYFJE02A-12-00-00-00', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A1-G-07', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000342, '969', '皮带', 'PD', '', 'E8/20/V4 S/GSTR BLACK  5900*340', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-08', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000343, '69', '挡烟板（立式机或者卧式机出口挡烟条）', 'DYBLSJHZWSJCKDYT', '', 'E4//2 U0/V4 BA   APPLE GREEN', 'PCS',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-09; （立式机或者卧式机出口挡烟条）', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000344, '432', '皮带（XW)', 'PDXW', '', 'E8/2 U0/V15  LG-SE  BLACK 1700*340', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-09; 小品规外侧打码皮带后', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000345, '835', '钢丝', 'GS', '', '细支烟/库房摆臂', '米',
        '', 1000000000000000002, 0, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-1(下层）; 钢丝', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000346, '972', '皮带（大内137）', 'PDDN137', '', 'E 8/2 0/V4 S/GSTR BLACK 2310*300', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-10', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000347, '976', '皮带（小内128）', 'PDXN128', '', 'E 8/2 0/V4 S/GSTR BLACK 2100*340', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-10', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000348, '978', '皮带（小外219摆臂）', 'PDXW219BB', '', 'E 8/2 U0/V15 LG BLACK 2750*340', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-10', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000349, '188', '皮带（库房人工拆垛）', 'PDKFRGCD', '', '皮带E8/2 0/V4 S/GSTR BLACK/1610*510', '件',
        '普天', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-10; 库房人工拆垛', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000350, '979', '皮带（小外225)', 'PDXW225', '', 'E 8/2 0/V4 S/GSTR BLACK 865*340', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-10', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000351, '970', '皮带(大内130）', 'PDDN130', '', 'E12/2 U0/V/UO SE BLACK 1220*300/1210*300', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-10', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000352, '982', '皮带（小外222摆臂）', 'PDXW222BB', '', 'E 8/2 U0/V15 LG BIACK 2200*340', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-10', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000353, '258', '皮带（库房人工拆垛）', 'PDKFRGCD', '', '50/13BDV  1600*520（防滑）', '件',
        '普天', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-10; 库房人工拆垛', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000354, '981', '皮带（小内125）', 'PDXN125', '', 'E 8/2 0/V4 S/GSTR BLACK 2930*340', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-10', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000355, '980', '皮带（小内130）', 'PDXN130', '', 'E 8/2 0/V4 S/GSTR BLACK 5850*340', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-10', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000356, '977', '皮带（小外221）', 'PDXW221', '', 'E 8/2 U0/V15 LG BLACK 2950*145', '件',
        '', 1000000000000000002, 0, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-10', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000357, '975', '皮带(大外204）', 'PDDW204', '', 'E 8/2 0/V4 S/GSTR BLACK 5980*300', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-10', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000358, '974', '皮带（大外231打码）', 'PDDW231DM', '', 'E 8/2 0/V4 S/GSTR BLACK 2700*115', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-10', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000359, '973', '皮带（大外213摆臂）', 'PDDW213BB', '', 'E 8/2 U0/V15 LG BLACK 2100*300', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-10', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000360, '971', '皮带（大内134）', 'PDDN134', '', 'E 8/2 0/V4 S/GSTR BLACK 1550*300', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-10', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000361, '1017', '皮带（DW204)', 'PDDW204', '', 'E8/20/V4 S/GSTRBLACK    5960*300', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-10; 204', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000362, '657', '划箱机输送皮带（大品规细支）2230*190*D10', 'HXJSSPDDPGXZ2230190D10', '', 'PDC201705A-10-40-02B', '件',
        '普天', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 6, NULL, NULL,
        '货位:A1-G-13; 大品规细支划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000363, '726', '划箱机输送皮带（小品规）2065*370', 'HXJSSPDXPG2065370', '', 'LZYCJ-01A-10-20-01/2065*370*D10/700MS-10-70-05', '件',
        '普天', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-13; 小品规划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000364, '896', '划箱机输送皮带（小品规）2070*370', 'HXJSSPDXPG2070370', '', 'HSJG02.40.14', '卷',
        '普天', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-13; 小品规划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000365, '863', '塑包机PVC黑色小钻石纹平皮带', 'SBJPVCHSXZSWPPD', '', '厚2*宽480*内周长580', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000032, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-13; 塑包机切口皮带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000366, '68', '皮带/DW234/1380MM*300MM', 'PDDW2341380MM300MM', '', 'E8/20/V4 S/GSTR BLACK/1380MM*300MM', 'PCS',
        '普天', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A1-G-13; 大品规外侧234皮带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000367, '802', '塑包机海绵橡胶+20mm皮带（长）03.03.15.5305', 'SBJHMXJ20MMPDC0303155305', '', 'PJ(E=2.34)56(W)*1240(内周长L)', 'PCS',
        '', 1000000000000000003, 2000000000000000032, NULL, 1, 0, 1,
        0.0, 0, 0, 20, NULL, NULL,
        '货位:A1-G-14; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000368, '861', '塑包机EVA海绵板', 'SBJEVAHMB', '', '10*150*360', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A1-G-14', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000369, '803', '塑包机海绵橡胶+20mm皮带（短）03.03.15.5304', 'SBJHMXJ20MMPDD0303155304', '', 'PJ(E=2.34)56(W)*820(内周长L)', '件',
        '', 1000000000000000003, 2000000000000000032, NULL, 1, 0, 1,
        0.0, 0, 0, 20, NULL, NULL,
        '货位:A1-G-14; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000370, '136', '电机', 'DJ', '', '5IK60GU-CFT/5GU12.5KB', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-001', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000371, '356', '电机', 'DJ', '', '5IK150GU-CFT', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-002', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000372, '587', '电机', 'DJ', '', '5IK90GU-CFT(5GU-20KB)', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 12, NULL, NULL,
        '货位:A2-1-003', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000373, '351', '电机', 'DJ', '', '5IK120GU-YMBT（5GU/7.5KB）', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-003', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000374, '357', '减速器(多穿小车）', 'JSQDCXC', '', 'ZPT070-L1-20-S2-P2', '件',
        '普天', 1000000000000000001, 2000000000000000011, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-004; 多穿小车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000375, '111', '气缸', 'QG', '', 'MAC-40*110-S-CA', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-005; 小品开箱机气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000376, '186', '气缸', 'QG', '', 'MAC-25*42-S-CA-G', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-006; 大品旋转细支烟气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000377, '123', '主动气缸(补货小车）', 'ZDQGBHXC', '', 'MAC-50*90-S-CA', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-008; 缓存皮带挡板气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000378, '160', '滑移气缸', 'HYQG', '', 'MAC25*75-S-CA', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-009', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000379, '362', '气缸', 'QG', '', 'MAC32*49SCAG-CD751B', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-010', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000380, '361', '气缸', 'QG', '', 'XS185B-ACQ32*110SB', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-011', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000381, '108', '气缸', 'QG', '', 'MAC-25*115-S-CA', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-012; 开箱机气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000382, '74', '不锈钢迷你气缸', 'BXGMNQG', '', 'MAC40*30-S-CA', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-013; 上耳夹烟', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000383, '363', '气缸', 'QG', '', 'DSNU-40*68-PPV-A', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-014', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000384, '365', '塑包机托料气缸', 'SBJTLQG', '', 'DSBC32*200-PPVA-N3', '件',
        '', 1000000000000000003, 2000000000000000025, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-015; 塑包机托料气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000385, '31', '气缸', 'QG', '', 'MAC-32*53-S-CA-G1/8(定制)', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-016', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000386, '543', '气缸', 'QG', '', 'DSBC50*100-PPVA-N3', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-017; 合流', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000387, '522', '旋转气缸', 'XZQG', '', 'MSQB50R（MAX-PRESS‘0’6MPA）', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-018; 开箱旋转气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000388, '364', '旋转气缸', 'XZQG', '', 'HRQ50A-28-G-W', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-018', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000389, '119', '气缸', 'QG', '', 'MAC-25*100-S-CA', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-019', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000390, '76', '不锈钢迷你气缸', 'BXGMNQG', '', 'MAC32*125-S-CA', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-020; 下耳夹烟', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000391, '84', '双轴气缸', 'SZQG', '', 'TR-32*25-S-CQ213A', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-021', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000392, '535', '塑包机气缸', 'SBJQG', '', 'DSNU25*80-PPV-A', '件',
        '', 1000000000000000003, 2000000000000000025, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '货位:A2-1-022; 压料气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000393, '367', '塑包机三轴气缸', 'SBJSZQG', '', 'DFM-25*50-P-A-GF', '件',
        '', 1000000000000000003, 2000000000000000025, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-023; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000394, '179', '三轴气缸', 'SZQG', '', 'TCL25*50-S', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-024; 缓存皮带夹烟气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000395, '368', '鱼眼杆端关节轴承', 'YYGDGJZC', '', 'F-M36*200U', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-025', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000396, '366', '三轴气缸（抽板小型）', 'SZQGCBXX', '', 'JMGP16-Z1W233-20', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-026; 卧式机抽板', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000397, '32', '气缸', 'QG', '', 'MAC-32*49-S-CA-G1/8(定制)', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-027', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000398, '536', '塑包机气缸', 'SBJQG', '', 'DSNU25*250-PPV-A', '件',
        '', 1000000000000000003, 2000000000000000025, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-027; 二级压料气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000399, '538', '塑包机气缸', 'SBJQG', '', 'TCL25*80S', '件',
        '', 1000000000000000003, 2000000000000000025, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-028; 塑包机夹烟', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000400, '371', '气缸', 'QG', '', 'DSNU-25*50-PPV-A', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-029', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000401, '127', '三轴气缸', 'SZQG', '', 'TCL25*100-S', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-030', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000402, '157', '固定翻转气缸', 'GDFZQG', '', 'DSNU-40*62-PPV-A', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-031', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000403, '218', '超薄气缸', 'CBQG', '', 'ACQ80*25-G', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-032; 大品规转角传输升降气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000404, '544', '气缸', 'QG', '', 'DSBC50*160-PPVA-N3', '件',
        '普天', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-033; 分流/方', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000405, '333', '塑包机气缸（接烟气缸）', 'SBJQGJYQG', '', 'TN25*200S', '个',
        '科盛', 1000000000000000003, 2000000000000000025, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-034; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000406, '514', '气缸', 'QG', '', 'TCM20*110SG(CQ204A)', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-034', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000407, '373', '气缸', 'QG', '', 'MAC40*50SCA-CD1808B', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-035', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000408, '611', '气缸', 'QG', '', 'MAC32*53-S-CA-G-CD736B', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 12, NULL, NULL,
        '货位:A2-1-036', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000409, '839', '三轴气缸（核数）', 'SZQGHS', '', 'TCL12*25S-CQ304B', 'PCS',
        '普天', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-037; 大品规核数气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000410, '610', '气缸', 'QG', '', 'TCLJ12*25-5S-CD2053C(可调节行程）', 'PCS',
        '普天', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 12, NULL, NULL,
        '货位:A2-1-037', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000411, '54', '导杆气缸', 'DGQG', '', 'MGPM25-55Z-Z1S00073', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-038', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000412, '601', '塑包机气缸（圆型气缸）', 'SBJQGYXQG', '', 'DSNU-25*200-PPV-A', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-039; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000413, '609', '气缸双轴', 'QGSZ', '', 'TR-32*25SG-CQ213A', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 12, NULL, NULL,
        '货位:A2-1-040', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000414, '824', '轴承', 'ZC', '', '61801-2Z/VA9813', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-041', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000415, '458', '轴承', 'ZC', '', '6802Z', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-041', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000416, '1024', '轴承', 'ZC', '', '61904Z', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-041', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000417, '1031', '轴承', 'ZC', '', 'NATR30-HRHB', 'PCS',
        '普天', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-042', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000418, '25', '轴承（链条转折机构轴承座轴承）', 'ZCLTZZJGZCZC', '', 'GB/T296-94', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-043; 链条转折机构', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000419, '473', '轴承（堆垛机)', 'ZCDDJ', '', 'NUTR50110-FYB', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-044; 堆垛机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000420, '497', '轴承', 'ZC', '', '62201-2RS', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-045', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000421, '1023', '轴承', 'ZC', '', '62202-RZ', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-045', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000422, '820', '轴承', 'ZC', '', '3201-BB-2RSR-TVH', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-046', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000423, '462', '轴承', 'ZC', '', '3203-RS', '件',
        '普天', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-046', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000424, '455', '轴承', 'ZC', '', '3202-RS', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-046', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000425, '819', '轴承', 'ZC', '', '3203-A-2RS1TN9/MT33', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-046', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000426, '734', '轴承', 'ZC', '', '3203-2RS', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-046', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000427, '1004', '轴承', 'ZC', '', '6906-Z', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-047', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000428, '488', '塑包机轴承', 'SBJZC', '', '6901Z', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-047; 大品规塑包机进烟', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000429, '470', '轴承', 'ZC', '', '6902Z-NSK', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-047', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000430, '469', '轴承', 'ZC', '', '1205-ATN', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-048', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000431, '830', '轴承 1204(（圆柱孔调心球轴承）', 'ZC1204YZKDXQZC', '', 'HRB     1204ATN', 'PCS',
        '普天', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-048; 多穿小车张紧轮', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000432, '870', '轴承', 'ZC', '', '1205-JAPAN', '件',
        '普天', 1000000000000000001, 2000000000000000010, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-048', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000433, '816', '轴承', 'ZC', '', '6206-2Z/C3', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-049', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000434, '732', '轴承', 'ZC', '', '6205-RZ-HRB', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-049', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000435, '821', '轴承', 'ZC', '', '6204-2Z', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-049', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000436, '817', '轴承', 'ZC', '', '6205-2Z/C3', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-049', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000437, '464', '轴承', 'ZC', '', '6204-2RZ', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-049', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000438, '1026', '轴承', 'ZC', '', '6206-2Z', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-049', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000439, '255', '轴承', 'ZC', '', '6206-RZ', '件',
        '普天', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-049', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000440, '479', '轴承', 'ZC', '', '6204-ZZ', 'PCS',
        '普天', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-050', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000441, '467', '轴承', 'ZC', '', '6202-2RS/C3', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-050', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000442, '463', '轴承', 'ZC', '', '6203-2RS1', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-050', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000443, '460', '轴承', 'ZC', '', '6202-Z-NSK', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-050', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000444, '735', '轴承', 'ZC', '', '6202-RZ', '件',
        '', 1000000000000000002, 0, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-050', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000445, '1029', '轴承', 'ZC', '', '6203-RS', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-050', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000446, '1028', '轴承', 'ZC', '', '6204-Z', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-050', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000447, '1027', '轴承', 'ZC', '', '6204-2RS1', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-050', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000448, '477', '轴承', 'ZC', '', '6202-RS', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-050', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000449, '1022', '轴承', 'ZC', '', '6201-C-2HRS', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-051', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000450, '478', '轴承', 'ZC', '', '6201-RS', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-051', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000451, '729', '轴承', 'ZC', '', '6201DU-NSK', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-051', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000452, '481', '轴承', 'ZC', '', '6201LU', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-051', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000453, '492', '轴承', 'ZC', '', '6201-RZ', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-051', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000454, '475', '轴承', 'ZC', '', '6201-RS-CDY', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-051', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000455, '471', '轴承', 'ZC', '', '6201-2RS-CDY', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-051', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000456, '465', '轴承', 'ZC', '', '6201-2Z', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-051', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000457, '1021', '轴承', 'ZC', '', '6201-2RZ', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-051', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000458, '466', '轴承', 'ZC', '', '6002-2Z-SKF', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-052', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000459, '663', '轴承', 'ZC', '', '6001RZ-HRB', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 20, NULL, NULL,
        '货位:A2-1-052', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000460, '476', '轴承', 'ZC', '', '6002-2RS1-SKF', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-052', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000461, '457', '轴承', 'ZC', '', '6001Z-HRB', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-052', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000462, '1025', '轴承', 'ZC', '', '6002-RZ', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-052', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000463, '474', '轴承（堆垛机）', 'ZCDDJ', '', 'NUTR30X-FYB', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-054; 堆垛机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000464, '1030', '轴承', 'ZC', '', '33207-NFA7', 'PCS',
        '普天', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-055', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000465, '736', '轴承', 'ZC', '', '6305-RZ-HRB', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-056', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000466, '818', '轴承', 'ZC', '', '6303-2Z/C3', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-056', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000467, '730', '轴承', 'ZC', '', '6306-Z', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-056', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000468, '493', '轴承', 'ZC', '', '6303-Z', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-056', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000469, '456', '轴承', 'ZC', '', '6304-2RS1/C3', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-056', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000470, '815', '轴承', 'ZC', '', '6305-2Z', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-056', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000471, '814', '轴承', 'ZC', '', '6306-2Z', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-056', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000472, '1020', '轴承', 'ZC', '', '6304-2Z', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-056', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000473, '257', '轴承', 'ZC', '', '6305-2RZ', '件',
        '普天', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-056', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000474, '256', '轴承', 'ZC', '', '6306-2RZ', '件',
        '普天', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-056', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000475, '731', '轴承', 'ZC', '', '6305Z-HRB', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-056', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000476, '491', '塑包机司服分料电机减速器', 'SBJSFFLDJJSQ', '', 'JSMA-PSC04A/PB62-4', '件',
        '', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-057; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000477, '214', '塑包机切刀电热丝03.04.26.5501', 'SBJQDDRS0304265501', '', '0.2*20', '米',
        '科盛', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-058; 塑包机切刀', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000478, '950', '塑包机悬挂带座轴承02.05.07.5103', 'SBJXGDZC0205075103', '', 'UCFB203(WL)', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-059; 贴标机皮带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000479, '930', '塑包机封膜导套座组件（反）FK.101.9300L', 'SBJFMDTZJFFK1019300L', '', 'FK101-9300L', 'PCS',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-060; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000480, '929', '塑包机封膜导套座组件(正）FK.101.9300R', 'SBJFMDTZJZFK1019300R', '', 'FK101-9300R', '套',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-060; 塑包机切刀', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000481, '417', '机柜散热风扇', 'JGSRFS', '', 'MQ12025HSL', '件',
        '', 1000000000000000002, 2000000000000000022, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-061; 塑包机切刀散热风扇', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000482, '1012', '塑包机风扇（切刀）', 'SBJFSQD', '', 'SF12025AT     P/N2122HSL', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-061; 塑包机风扇', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000483, '537', '塑包机气缸03.02.03.5101', 'SBJQG0302035101', '', 'DSBC32*25-PPVA-N3', '件',
        '', 1000000000000000003, 2000000000000000025, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-062; 塑包机切刀气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000484, '999', '塑包机Y接头/双耳环/03.04.20.5062', 'SBJYJTSEH0304205062', '', 'SG - M10*1.25', '件',
        '', 1000000000000000003, 2000000000000000030, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-063; 塑包机切刀', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000485, '925', '塑包机气接头浮动吸盘03.02.10.5061（小）', 'SBJQJTFDXP0302105061X', '', 'NPQE-L-R14-Q8-P10', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000030, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-064; 塑包机浮动吸盘气嘴', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000486, '1010', '塑包机带肩挡圈固定型销', 'SBJDJDQGDXX', '', 'MTT04-M-D10-L20', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-065; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000487, '322', '塑包机推烟滑套组件05.01.98.5359', 'SBJTYHTZJ0501985359', '', '', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-066; 包装机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000488, '304', '塑包机深沟球轴承02.05.01.5152', 'SBJSGQZC0205015152', '', '6202-2Z', 'ST',
        '科盛', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-067; 包装机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000489, '301', '塑包机深沟球轴承02.05.01.5154', 'SBJSGQZC0205015154', '', '6204-2Z', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-067; 包装机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000490, '280', '塑包机调心球轴承02.05.01.5015', 'SBJDXQZC0205015015', '', '1205', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-067', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000491, '264', '塑包机深沟球轴承', 'SBJSGQZC', '', '6903-2Z(ZZCM)', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-067; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000492, '66', '塑包机（切膜气缸固定板切刀）', 'SBJQMQGGDBQD', '', 'PK101-9000-06', '件',
        '科盛', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-068; 塑包机切刀底座', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000493, '182', '轴承（堆垛机）', 'ZCDDJ', '', '6010-RZ', '个',
        '普天', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-069; 堆垛机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000494, '254', '轴承', 'ZC', '', '6307-RZ', '件',
        '普天', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-070', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000495, '253', '轴承', 'ZC', '', '6307-Z', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-070', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000496, '284', '塑包机直线轴承02.05.10.5301', 'SBJZXZC0205105301', '', 'LM20LUU', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-071; 包装机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000497, '274', '塑包机贴标机轴承内接式回卷器（底纸回收轴）轴承/贴标机', 'SBJTBJZCNJSHJQDZHSZZCTBJ', '', '5901090.001 Ball Bearing DIN625-F608-ZZ(CAB A4+/4.3打印机通用)', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-072; 贴标机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000498, '271', '塑包机贴标机内接式回卷器（含底纸回收轴）', 'SBJTBJNJSHJQHDZHSZ', '', 'CAB  SQUTX4.3打印机用/5977818.001  Internal', '件',
        '科盛', 1000000000000000003, 2000000000000000029, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-072; 贴标机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000499, '696', '塑包机贴标机打印头', 'SBJTBJDYT', '', 'CAB SQUIX4.3/200P打印机用/5977382.001', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-073; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000500, '697', '塑包机电磁阀', 'SBJDCF', '', 'SV-6102-DC24-K-L', '件',
        '', 1000000000000000003, 2000000000000000033, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-074; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000501, '162', '单向节流阀', 'DXJLF', '', 'ASC200-08', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-075', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000502, '710', '塑包机光电开关03.09.02.5208', 'SBJGDKG0309025208', '', 'GTE6-N1211(方形-SICK)', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000028, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-076; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000503, '711', '塑包机光电开关', 'SBJGDKG', '', 'FAI4/BN-OA(圆形-墨迪）', '件',
        '', 1000000000000000003, 2000000000000000028, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-077; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000504, '725', '塑包机（固态继电器）', 'SBJGTJDQ', '', 'SSR-40DA(40A/直流控交流-阿女）', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-078', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000505, '713', '塑包机固态继电器', 'SBJGTJDQ', '', 'SSR-40VA(40A/调压固态-ANV)', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-078; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000506, '715', '塑包机浮动吸盘组件FK.401.3210', 'SBJFDXPZJFK4013210', '', 'FK401-3210', '套',
        '科盛', 1000000000000000003, 2000000000000000029, NULL, 1, 0, 1,
        0.0, 0, 0, 20, NULL, NULL,
        '货位:A2-1-079; 塑包机贴标机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000507, '716', '塑包机打印机同步带长', 'SBJDYJTBDC', '', '310MKL', '件',
        '', 1000000000000000003, 2000000000000000032, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-080; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000508, '717', '塑包机打印机同步带短', 'SBJDYJTBDD', '', 'B145-6MXL', '件',
        '', 1000000000000000003, 2000000000000000032, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-080; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000509, '754', '塑包机打印机下胶辊', 'SBJDYJXJG', '', 'FK401-2200-01', 'PCS',
        '', 1000000000000000003, 2000000000000000027, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-081; 贴标机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000510, '718', '塑包机打印机上胶辊03.05.09.5016', 'SBJDYJSJG0305095016', '', 'Drive RollerDR4 5954180.001', '件',
        '', 1000000000000000003, 2000000000000000027, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-081; 塑包机贴标机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000511, '719', '塑包机标签纸传感器', 'SBJBQZCGQ', '', '', '件',
        '', 1000000000000000003, 2000000000000000029, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-082; 塑包机贴标机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000512, '723', '塑包机电感式接近开关', 'SBJDGSJJKG', '', 'AM1/AN-4A', '件',
        '', 1000000000000000003, 2000000000000000028, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-083; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000513, '724', '塑包机对射开关03.09.02.5102', 'SBJDSKG0309025102', '', 'Z3T-2500N', '件',
        '', 1000000000000000003, 2000000000000000028, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-084; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000514, '707', '塑包机机器人弹簧', 'SBJJQRDH', '', '原装', '个',
        '科盛', 1000000000000000003, 2000000000000000029, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-085; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000515, '414', '塑包机真空发生器', 'SBJZKFSQ', '', 'ABT-X10/03.02.04.5011', '件',
        '', 1000000000000000003, 2000000000000000030, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-086; 贴标机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000516, '621', '塑包机真空发生器（贴标机）', 'SBJZKFSQTBJ', '', '派亚博真空发生器0122896', '个',
        '科盛', 1000000000000000003, 2000000000000000030, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-086; 贴标机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000517, '757', '塑包机CAB打印头压杆', 'SBJCABDYTYG', '', 'CAB SQUIX4.3/200P打印机', 'PCS',
        '', 1000000000000000003, 2000000000000000029, NULL, 1, 0, 1,
        0.0, 0, 0, 3, NULL, NULL,
        '货位:A2-1-087; 贴标机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000518, '756', '塑包机贴标机齿轮/内接式回卷器（含底纸回收纸轴）', 'SBJTBJCLNJSHJQHDZHSZZ', '', '5954115.001/CAB  SQUIX4.3', 'PCS',
        '', 1000000000000000003, 2000000000000000031, NULL, 1, 0, 1,
        0.0, 0, 0, 3, NULL, NULL,
        '货位:A2-1-088; 贴标机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000519, '755', '塑包机贴标机调节器/底纸回收扭力调节器含齿轮', 'SBJTBJDJQDZHSNLDJQHCL', '', '5954113.001/CAB  A4+和SQUIX4.3', 'PCS',
        '', 1000000000000000003, 2000000000000000031, NULL, 1, 0, 1,
        0.0, 0, 0, 3, NULL, NULL,
        '货位:A2-1-088; 贴标机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000520, '770', '塑包机（轴承）', 'SBJZC', '', '6206Z-NSK', 'PCS',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-089; 塑包机真空泵', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000521, '987', '塑包机轴承', 'SBJZC', '', '6206-2Z/VA201', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-089; 塑包机真空泵', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000522, '827', '塑包机机械臂螺丝', 'SBJJXBLS', '', '', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 12, NULL, NULL,
        '货位:A2-1-090; 塑包机机械臂', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000523, '858', '塑包机液压缓冲器（阻尼器）03.04.22.5005', 'SBJYYHCQZNQ0304225005', '', 'ACA1416-1', 'PCS',
        'AIRTAC', 1000000000000000003, 2000000000000000029, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-091; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000524, '914', '塑包机电磁阀03.02.04.5536', 'SBJDCF0302045536', '', '两位五通VUVS-L25-M52-MD-G14-F8-1C1', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000033, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-092; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000525, '877', '封口机输送皮带', 'FKJSSPD', '', '', '条',
        '普天', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 60, NULL, NULL,
        '货位:A2-1-093; 封口机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000526, '285', '塑包机同步带03.03.10.5212', 'SBJTBD0303105212', '', 'HTD 5M-25开口（橡胶+玻璃纤维绳）', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000032, NULL, 1, 0, 1,
        0.0, 0, 0, 0, NULL, NULL,
        '货位:A2-1-093; 包装机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000527, '826', '塑包机合页（门)', 'SBJHYM', '', 'H337（50*70/塑料）', 'PCS',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-094; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000528, '386', '塑包机气缸浮动接头（封切）', 'SBJQGFDJTFQ', '', 'FD-1016/M16*1.5', '件',
        '', 1000000000000000003, 2000000000000000030, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-095; 塑包机封切气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000529, '915', '塑包机电磁阀03.02.04.5529', 'SBJDCF0302045529', '', '三位五通VUVS-L25-P53C-MD-G14-F8-1C1', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000033, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-096; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000530, '812', '塑包机张紧装置SE-18型', 'SBJZJZZSE18X', '', '06011003', 'PCS',
        '', 1000000000000000003, 2000000000000000029, NULL, 1, 0, 1,
        0.0, 0, 0, 3, NULL, NULL,
        '货位:A2-1-097; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000531, '843', '塑包机真空泵油封', 'SBJZKBYF', '', '35*47*7', '个',
        '科盛', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-098; 塑包机真空泵', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000532, '906', '塑包机光电开关座FK401.3200-06', 'SBJGDKGFK401320006', '', 'FK401-3200-06', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-099; 塑包机贴标机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000533, '813', '塑包机PVC黑色小钻石纹平皮带（卡拨烟皮带）', 'SBJPVCHSXZSWPPDKBYPD', '', '厚2*宽55*内周长1290', '条',
        '', 1000000000000000003, 2000000000000000032, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-101; 塑包机卡拨烟皮带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000534, '427', '塑包机机器人弹簧', 'SBJJQRDH', '', '原装03.05.05.5005', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-102; 机械手', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000535, '846', '塑包机吸盘（机机械手)03.02.09.5001', 'SBJXPJJXS0302095001', '', 'DP-S20', '个',
        '科盛', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-102; 塑包机机械手', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000536, '913', '塑包机镜面反射开关03.09.02.5001', 'SBJJMFSKG0309025001', '', 'Z2R-400N(OPTEX)', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000028, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-103; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000537, '928', '塑包机悬挂带座轴承02.05.07.5104', 'SBJXGDZC0205075104', '', 'UCFB204', 'PCS',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-104; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000538, '767', '电机DRN63MS4转速1380r/min功率0.12kw', 'DJDRN63MS4ZS1380RMINGL012KW', '', 'R07-DRN63MS4速比3.21转速1380-430r/min扭矩2nm功率0.12kw', 'ST',
        'ST', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-1-105', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000539, '342', '电机DRN71M4转速1415r/min功率0.37kw', 'DJDRN71M4ZS1415RMINGL037KW', '', 'R07-DRN71M4速比3.21转速1415-441r/min扭矩8nm功率0.37kw', 'ST',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-106', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000540, '125', '电机SK71LP/4TF(SK172.1-71LP/4TF)', 'DJSK71LP4TFSK172171LP4TF', '', 'SK172.1-71LP/4TF', '个',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-107', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000541, '5', '电机SK71LA/4(SK072.1-71LA/4)', 'DJSK71LA4SK072171LA4', '', 'SK 072.1-71LA/4', '个',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-107', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000542, '341', '电机SK71LP/4TF(SK 072.1-71LP/4-TF)', 'DJSK71LP4TFSK072171LP4TF', '', 'SK 072.1-71LP/4-TF', '个',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-1-107', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000543, '880', '电机DRN71M4转速1415r/min功率0.37kw', 'DJDRN71M4ZS1415RMINGL037KW', '', 'R07-DRN71M4速比3.95转速1415-358r/min扭矩9nm功率0.37kw', '个',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-108', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000544, '879', '电机DRN63MS4转速1380r/min功率0.12kw', 'DJDRN63MS4ZS1380RMINGL012KW', '', 'R07-DRN63MS4速比7.48转速1380-184r/min扭矩6nm功率0.12kw', '个',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-108', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000545, '881', '电机DRN71M4转速14515r/min功率0.37kw', 'DJDRN71M4ZS14515RMINGL037KW', '', 'R07-DRN71M4速比4.92转速1415-288r/min扭矩12nm功率0.37kw', '个',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-108', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000546, '882', '电机DRN71M4转速1415r/min功率0.37kw', 'DJDRN71M4ZS1415RMINGL037KW', '', 'R07-DRN71M4速比4.57转速1415-310r/min扭矩11nm功率0.37kw', '个',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-1-108', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000547, '81', '三轴气缸', 'SZQG', '', 'TCM-63*120-S', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-001; 开箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000548, '180', '塑包机司服电机', 'SBJSFDJ', '', 'JSMA-PUC04ABA', '件',
        '', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-002; 塑包机卡位', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000549, '911', '塑包机伺服电机03.01.01.5101', 'SBJSFDJ0301015101', '', 'MSK043C-0600-NN-M1-UG1NNNN', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 0, NULL, NULL,
        '货位:A2-2-002; 塑包机机械手', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000550, '91', '司服电机', 'SFDJ', '', 'ECMA-C10604RS', '件',
        '', 1000000000000000001, 2000000000000000012, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-003', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000551, '354', '伺服电机', 'SFDJ', '', 'JSMA-PLC08ABA', '件',
        '', 1000000000000000001, 2000000000000000012, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-004', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000552, '837', '从动滚筒（大品规核数）', 'CDGTDPGHS', '', '长治17HSJGA-02-00-00-05', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-006; 大品规核数机构', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000553, '700', '塑包机叠层传动轴', 'SBJDCCDZ', '', 'FK101-4000-06', '件',
        '', 1000000000000000003, 2000000000000000029, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '货位:A2-2-007; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000554, '907', '塑包机张紧辊筒05.01.98.5413', 'SBJZJGT0501985413', '', 'FK11K-2200-05/06轴承6903', '套',
        '科盛', 1000000000000000003, 2000000000000000027, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-009; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000555, '867', '堆垛机轴承护盖', 'DDJZCHG', '', '', '片',
        '普天', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-010; 堆垛机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000556, '748', '塑包机链条电机链轮', 'SBJLTDJLL', '', 'FK11K-2100-34(4分16齿Φ16键5）', '件',
        '', 1000000000000000003, 2000000000000000031, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-2-011; 塑包机卡拨', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000557, '747', '塑包机卡拨烟链条（088*126）', 'SBJKBYLT088126', '', 'FK11K-2100-35', '件',
        '', 1000000000000000003, 2000000000000000029, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-2-011; 塑包机卡拨', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000558, '487', '塑包机挡烟主动轴（标准型）', 'SBJDYZDZBZX', '', '082-20-FK11K-2100-51', '件',
        '', 1000000000000000003, 2000000000000000029, NULL, 1, 0, 1,
        0.0, 0, 0, 3, NULL, NULL,
        '货位:A2-2-011; 科盛塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000559, '602', '塑包机前过渡辊筒组件', 'SBJQGDGTZJ', '', 'FK101-1-2A00', '套',
        '科盛', 1000000000000000003, 2000000000000000027, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-012; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000560, '886', '塑包机前过渡辊筒组件', 'SBJQGDGTZJ', '', 'FK101-2B00', '个',
        '科盛', 1000000000000000003, 2000000000000000027, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-012; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000561, '1009', '塑包机前端被动辊筒（小）', 'SBJQDBDGTX', '', 'FK19014.02.02-05/02-06轴承6901', '件',
        '', 1000000000000000003, 2000000000000000027, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-014; 塑包机小品规进烟皮带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000562, '345', '塑包机链轮VIII（出口电机）', 'SBJLLVIIICKDJ', '', '4分11齿φ15键5', '件',
        '科盛', 1000000000000000003, 2000000000000000031, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-015; 包装机出口输送皮带机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000563, '337', '塑包机链轮VI', 'SBJLLVI', '', 'ZBSF/T-45(4分16齿φ18键5）', '件',
        '科盛', 1000000000000000003, 2000000000000000031, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-015; 包装机电机轮', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000564, '334', '塑包机链轮IV', 'SBJLLIV', '', 'ZBSF/T-43(4分16齿φ22键7）', '件',
        '科盛', 1000000000000000003, 2000000000000000031, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-015; 包装机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000565, '528', '气缸', 'QG', '', 'CD85N25-50C-B', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-017; 滑道挡烟气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000566, '215', '传动链条', 'CDLT', '', '06B-46*160', '件',
        '', 1000000000000000001, 2000000000000000020, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-021', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000567, '40', '链条组件', 'LTZJ', '', 'TYFJA01-06-01-00-00-00', '件',
        '', 1000000000000000001, 2000000000000000018, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-025', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000568, '228', '行走轮总成', 'XZLZC', '', 'TYFJD02-03-04-00-00', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-027', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000569, '379', '气缸', 'QG', '', 'SE50*200S', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-032', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000570, '542', '气缸', 'QG', '', 'DSBC50*190-PPVA-N3', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '货位:A2-2-033; 合流/方', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000571, '83', '标准气缸', 'BZQG', '', 'SAU-63*160-S', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-034; 小品上下耳气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000572, '370', '气缸', 'QG', '', 'MAL32*75', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-037', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000573, '195', '电机带轮', 'DJDL', '', 'BZLJDL2011-00-05', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-038', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000574, '206', '电机带轮', 'DJDL', '', 'BZLJDL2011-00-09', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-039', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000575, '189', '电机带轮', 'DJDL', '', 'BZLJDL2011-00-11', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-041', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000576, '878', '端头带总成（轴/轴承）', 'DTDZCZZC', '', 'ZNB01-04-07-00-02', '个',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-042; 多穿小车/无键槽', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000577, '391', '半滑块轴承', 'BHKZC', '', 'LM25UUOP', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-043', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000578, '988', '滑块直线轴承（半）', 'HKZXZCB', '', 'TBR30UU', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-043', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000579, '398', '轴承座（小）', 'ZCX', '', 'KFL000', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-044; 大品规', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000580, '60', '驱动带轮', 'QDDL', '', 'TYFJG03A-08-00-00-03', '个',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-045; 有键槽', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000581, '37', '驱动带轮', 'QDDL', '', 'TYFJG03A-08-03-00-01', '个',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-045; 有键槽', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000582, '424', '同步轮', 'TBL', '', 'TYFJG3A-08-00-00-03', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-045', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000583, '230', '驱动带轮（补货小车）', 'QDDLBHXC', '', 'TYFJD02-02-00-00-01', '个',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-045; 补货小车拨烟电机/有键槽', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000584, '235', '驱动带轮', 'QDDL', '', 'TYFJD02-02-09-00-05', '件',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-045; 有键槽', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000585, '177', '多楔带轮', 'DXDL', '', 'JKX01A-05-00-00-17', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-046; 铁质黑色', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000586, '223', '电机多契带轮', 'DJDQDL', '', 'TYFJD03-02-00-00-05', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-046', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000587, '67', '带轮', 'DL', '', 'BZLJDL2011-00-01', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-047; 铝合金多褶', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000588, '137', '电机带轮', 'DJDL', '', 'JKX01A-01-00-00-16', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-048', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000589, '210', '单槽滚轮', 'DCGL', '', 'BZLJDL2011-00-13', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-049', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000590, '209', '转辙滚轮', 'ZZGL', '', 'BZLJDL2011-00-12', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-049', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000591, '423', '同步轮', 'TBL', '', 'BZLJDL2011-00-02', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-050', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000592, '422', '同步轮（多穿小车伸叉）', 'TBLDCXCSC', '', 'ZNB03-01-01-00-01', '个',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-051; 多穿小车/无键槽', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000593, '324', '滚轮组件（多穿小车伸叉）', 'GLZJDCXCSC', '', 'ZNB03-01-01-00-01', '套',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-051; 多穿小车/轴承/轴', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000594, '873', '伸叉驱动带轮（多穿小车伸叉）', 'SCQDDLDCXCSC', '', 'BJZNB01-02-01-00-03', '个',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-052; 多穿小车伸叉驱动', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000595, '420', '驱动同步轮', 'QDTBL', '', 'TYSFJ-03-00-10', '个',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-053; 有键槽', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000596, '238', '定位同步带轮', 'DWTBDL', '', 'TYFJD02-03-00-00-07', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-054; 补货小车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000597, '172', '导向轮总成', 'DXLZC', '', 'JKX01A-06-07-00-00', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-057', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000598, '171', '行走限位轮', 'XZXWL', '', 'JKX01A-06-02-00-00', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-057', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000599, '227', '限位轮总成', 'XWLZC', '', 'TYFJD02-03-01-00-00', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-057', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000600, '245', '导向滚轮', 'DXGL', '', 'RKO55.25', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-057', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000601, '486', '塑包机推烟导向滑套', 'SBJTYDXHT', '', '029-20', '件',
        '', 1000000000000000003, 2000000000000000029, NULL, 1, 0, 1,
        0.0, 0, 0, 0, NULL, NULL,
        '货位:A2-2-059; 科盛塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000602, '394', '直线轴承', 'ZXZC', '', 'LM20UU*80MM', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-060; 无固定座', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000603, '993', '直线轴承', 'ZXZC', '', 'LM25UU*60MM', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-060', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000604, '994', '直线轴承', 'ZXZC', '', 'LM20UU*41MM', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 0, NULL, NULL,
        '货位:A2-2-060', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000605, '226', '链轮', 'LL', '', 'TYFJD02-02-09-00-04', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-065', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000606, '225', '链轮二', 'LLE', '', 'TYFJD02-02-09-01-03', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-065', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000607, '212', '驱动链轮', 'QDLL', '', 'BZLJLL2011-00-02', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-065', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000608, '211', '从动链轮', 'CDLL', '', 'BZLJLL2011-00-03', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-065', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000609, '224', '链轮一', 'LLY', '', 'TYFJD02-02-09-01-01', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-065', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000610, '149', '旋摆气缸座', 'XBQG', '', 'JKX01A-02-01-03-00', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-067', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000611, '397', '轴承座', 'ZC', '', 'UC205-P205', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-069', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000612, '249', '带座轴承', 'DZC', '', 'SBLF202G', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-069', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000613, '396', '轴承座', 'ZC', '', 'UC205-FB205', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-070', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000614, '88', '关节M10', 'GJM10', '', 'JKX01A-02-00-00-03', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-071', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000615, '73', '关节M12', 'GJM12', '', 'JKX01A-02-00-00-02', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-071', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000616, '594', '张紧带轮（多穿小车）', 'ZJDLDCXC', '', 'BJZNB01-02-01-03-02', '个',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 5, NULL, NULL,
        '货位:A2-2-074; 多穿小车无键槽', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000617, '86', '辅助轮（补货小车)', 'FZLBHXC', '', '90（带轴）', '件',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-075; 补货小车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000618, '1', '直线轴承', 'ZXZC', '', 'LM16uu', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-077; 方座', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000619, '647', '伸缩驱动带轮', 'SSQDDL', '', 'BJZNB01-02-01-02-01', '个',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-078; 无键槽', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000620, '595', '端头带轮（多穿小车）', 'DTDLDCXC', '', 'BJZNB01-02-01-04-02', '件',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 5, NULL, NULL,
        '货位:A2-2-079; 多穿小车/无键槽', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000621, '648', '行走从动带轮（多穿小车）', 'XZCDDLDCXC', '', 'ZNB01-04-00-00-12', '件',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-080; 多穿小车 行走驱动轮', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000622, '649', '电机带轮(多穿小车）', 'DJDLDCXC', '', 'ZNB01-04-00-00-07', '个',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-2-081; 多穿小车/有键槽', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000623, '650', '伸叉从动带轮（多穿小车伸叉）', 'SCCDDLDCXCSC', '', 'BJZNB01-02-01-00-17', '个',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-2-082; 多穿小车/有键槽', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000624, '651', '丝杆驱动带轮(多穿小车）', 'SGQDDLDCXC', '', 'BJZNB01-02-01-00-14', '个',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-2-083; 多穿小车有键槽', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000625, '652', '丝杆从动带轮(多穿小车）', 'SGCDDLDCXC', '', 'BJZNB01-02-01-00-15', '个',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 1, NULL, NULL,
        '货位:A2-2-084; 多穿小车有键槽', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000626, '653', '改向带轮', 'GXDL', '', 'ZNB01-02-02-00-02', '个',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-2-085; 无键槽', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000627, '829', '张紧轮  （多穿小车）', 'ZJLDCXC', '', 'ZNB01-04-11-00-02', '个',
        '普天', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-086; 多穿小车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000628, '421', '端头带轮', 'DTDL', '', 'ZNB01-04-07-00-02', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-087; 多穿小车端头带轮', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000629, '654', '驱动链轮', 'QDLL', '', 'TPZZ01A-00-00-00-07', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 1, NULL, NULL,
        '货位:A2-2-089', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000630, '706', '塑包机锥齿轮', 'SBJZCL', '', '', '件',
        '', 1000000000000000003, 2000000000000000031, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-2-090; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000631, '708', '塑包机叠层主动链轮', 'SBJDCZDLL', '', 'FK101-4000-09(4分15齿22键6）', '件',
        '', 1000000000000000003, 2000000000000000031, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-2-091; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000632, '709', '塑包机叠层从动链轮', 'SBJDCCDLL', '', 'FK101-4000-10(4分15齿37键10-46)', '件',
        '', 1000000000000000003, 2000000000000000031, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-2-092; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000633, '849', '尼龙链轮（链板机）', 'NLLLLBJ', '', 'ADL01913303A-2401', '个',
        '普天', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 60, NULL, NULL,
        '货位:A2-2-093; 链板机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000634, '43', '从动链轮', 'CDLL', '', 'BZLJLL2011-00-05', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-094', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000635, '42', '驱动链轮', 'QDLL', '', 'BZLJLL2011-00-04', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-094', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000636, '738', '托轮总成', 'TLZC', '', 'PDL01E-05-03-00-00', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-2-097', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000637, '832', '鱼眼杆端轴承', 'YYGDZC', '', 'F-M6X100U', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-2-101', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000638, '831', '鱼眼杆端轴承', 'YYGDZC', '', 'F-M8X125U', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-2-101', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000639, '996', '鱼眼杆端轴承', 'YYGDZC', '', 'F-M5X080U', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-101', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000640, '320', '滚轮组件', 'GLZJ', '', 'CSCO2-01-04-02-00-00', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-102', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000641, '883', '减速电机DRN80MK4功率0.55kw转速1435r/min', 'JSDJDRN80MK4GL055KWZS1435RMIN', '', 'R17-DRN80MK4速比4.51转速1435-318r/min扭矩17nm功率0.55kw', '个',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-105', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000642, '766', '减速电机DRN80MK4转速1435r/min功率0.55kw', 'JSDJDRN80MK4ZS1435RMINGL055KW', '', 'R17-DRN80MK4速比5.09转速1435-282r/min扭矩19nm功率0.55kw', 'ST',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-2-105', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000643, '991', '电机DRN63MS4转速1380r/min功率0.12kw', 'DJDRN63MS4ZS1380RMINGL012KW', '', 'R07-DRN63MS4速比7.85扭矩6Nm转速1380/176r/min功率0.12kw', '件',
        '', 1000000000000000001, 2000000000000000011, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-105', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000644, '884', '电机DRN71M4转速1415r/min功率0.37kw', 'DJDRN71M4ZS1415RMINGL037KW', '', 'R17-DRN71M4速比4.51转速1415-314r/min扭矩11nm功率0.37kw', '个',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-106', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000645, '338', '电机DRS90M4(转速837-1395r/min/功率1.5kw）', 'DJDRS90M4ZS8371395RMINGL15KW', '', 'DRS90M4（速比5.06/转速1395-276r/min/扭矩52nm/功率1.5kw', '个',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-106', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000646, '768', '减速电机DRN80M4功率0.75kw转速1440r/min', 'JSDJDRN80M4GL075KWZS1440RMIN', '', 'R27-DRN80M4速比5.00转速1440-288r/min扭矩25nm功率0.75kw', 'ST',
        'ST', 1000000000000000001, 2000000000000000011, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-2-106', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000647, '885', '电机DRN71M4转速1415r/min功率0.37kw', 'DJDRN71M4ZS1415RMINGL037KW', '', 'R17-DRN71M4速比10.15转速1415-139r/min扭矩25nm功率0.37kw', '个',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-106', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000648, '765', '减速电机DRN80M4功率0.75kw转速1440r/min', 'JSDJDRN80M4GL075KWZS1440RMIN', '', 'R17-DRN80M4速比5.09转速1440-288r/min扭矩25nm功率0.75kw', 'ST',
        '', 1000000000000000001, 2000000000000000011, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:A2-2-106', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000649, '933', '行走轮(多穿小车）', 'XZLDCXC', '', 'CSC02-01-02-00-00-01', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-107; 多穿一号车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000650, '316', '行走轮', 'XZL', '', 'CSCO2-01-02-00-00-01', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-107', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000651, '246', '驱动同步带轮', 'QDTBDL', '', '商丘15TS70-03-00-00-01', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-107; 库房提升机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000652, '239', '推烟总成', 'TYZC', '', 'TYFJD03-02-08-00-00', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-107; 补货小车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000653, '247', '从动同步带轮', 'CDTBDL', '', '商丘15TS70-11-00-00-01', '件',
        '', 1000000000000000001, 2000000000000000014, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-107; 库房提升机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000654, '910', '塑包机电机03.01.01.5.002', 'SBJDJ0301015002', '', 'MS712-4/长轴/内孔/4.5/380v/立式/0.37kw', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:A2-2-108; 热风电机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000655, '234', '组合链条', 'ZHLT', '', 'TYFJD03-02-08-00-01', '件',
        '', 1000000000000000001, 2000000000000000020, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-108', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000656, '202', '链条', 'LT', '', 'GB/T1243-1997', '件',
        '', 1000000000000000001, 2000000000000000020, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-108', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000657, '241', '驱动链条', 'QDLT', '', 'GB1243.1-83', '件',
        '', 1000000000000000001, 2000000000000000020, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:A2-2-108', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000658, '259', '通讯模块（从站）-电力猫', 'TXMKCZDLM', '', 'ZB-G03/220/1200M', '件',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B1-1-01; 多穿', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000659, '359', '减速器', 'JSQ', '', '5GU-12.5KB-S2', '件',
        '', 1000000000000000001, 2000000000000000011, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B1-1-03', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000660, '1014', '减速器', 'JSQ', '', '5GU-10KB-S2', '件',
        '', 1000000000000000001, 2000000000000000011, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B1-1-04', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000661, '358', '减速器', 'JSQ', '', '5GU-12.5KB', '件',
        '', 1000000000000000001, 2000000000000000011, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B1-1-04', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000662, '353', '减速器', 'JSQ', '', '5GU-7.5KB-S2', '件',
        '', 1000000000000000001, 2000000000000000011, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B1-1-04', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000663, '347', '电机（热风电机）', 'DJRFDJ', '', 'JW6334', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B1-1-06', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000664, '666', '电机', 'DJ', '', '71L/6-203511939-400-0.25KW-920r/min', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B1-1-07', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000665, '889', '拨烟轮（叉体回环）', 'BYLCTHH', '', 'TYFJC05-02-00-00-03', '件',
        '普天', 1000000000000000001, 2000000000000000010, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B1-1-08; 大小品规细支旋转叉体', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000666, '348', '电机', 'DJ', '', '71L/6-202888089-100-0.29KW-1105r/min', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B1-1-09', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000667, '584', '减速器', 'JSQ', '', '5GU-15KB-S2', '件',
        '', 1000000000000000001, 2000000000000000011, NULL, 1, 0, 1,
        0.0, 0, 0, 9, NULL, NULL,
        '货位:B1-1-4', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000668, '984', '电力通讯主站', 'DLTXZZ', '', 'ZB-G02/380-1200', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B1-2-01; 通讯', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000669, '416', '控制变压器', 'KZBYQ', '', 'SP-7BSM-12000', '件',
        '', 1000000000000000002, 2000000000000000022, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B1-2-03', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000670, '90', '塑包机减速电机', 'SBJJSDJ', '', '90YB120GY22(90CKF5RCF414)', '件',
        '', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B1-2-05; 小品规塑包机分料', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000671, '355', '塑包机电机', 'SBJDJ', '', '90YS40GV22(90GK10HK)', '件',
        '', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '货位:B1-2-06; 送膜电机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000672, '581', '塑包机电机', 'SBJDJ', '', 'MS712-4/长轴/内孔14.5/380V/0.37KW', '件',
        '', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B1-2-07; 塑包机加热链条输送', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000673, '401', '变压器', 'BYQ', '', 'BK-150VA', '个',
        '普天', 1000000000000000002, 2000000000000000022, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B1-3-04', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000674, '349', '电机', 'DJ', '', '1LA7073-4AB10', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B1-3-08', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000675, '923', '集电臂（堆垛机）', 'JDBDDJ', '', 'DH5748K2-5P600V60A', '支',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B1-3-09; 堆垛机碳刷组件', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000676, '589', '电机', 'DJ', '', '5IK150GU-C-T（5GU-15KB-S2）', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 12, NULL, NULL,
        '货位:B1-4-02', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000677, '989', '电机(卧式机打击电机现有）', 'DJWSJDJDJXY', '', 'YXM71M2-6-0.25KW', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B1-4-06; 卧式机/立式机打击机构', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000678, '573', '电机（原厂）', 'DJYC', '', 'YXM71M2-6-0.25KW转速860r/min', '件',
        '', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '货位:B1-4-06; 打击电机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000679, '273', '三相异步电动机（打击机构）', 'SXYBDDJDJJG', '', 'TYPE  YXM71M2-6', '台',
        '普天', 1000000000000000001, 2000000000000000013, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B1-4-06; 打击机构', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000680, '927', '塑包机风扇03.10.25.5003', 'SBJFS0310255003', '', '20060', 'PCS',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 20, NULL, NULL,
        '货位:B1-4-09; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000681, '901', '划箱机烟雾管道', 'HXJYWGD', '', '50MM', '米',
        '普天', 1000000000000000002, 0, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B1-G-01; 激光划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000682, '95', '塑包机机械手波纹管带钢丝PU管', 'SBJJXSBWGDGSPUG', '', '内孔25*0.6', '米',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B1-G-01; 塑包机机械手', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000683, '822', '塑包机（波纹管带钢丝PU管）', 'SBJBWGDGSPUG', '', '内孔40*1.5', '米',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B1-G-01; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000684, '92', '塑包机硅胶条（切刀）', 'SBJGJTQD', '', '', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B1-G-01; 塑包机封切', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000685, '96', '气管', 'QG', '', '#6/8/10', '件',
        '', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B1-G-02', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000686, '165', '气缸座横撑总成', 'QGHCZC', '', 'JTY11-01-12-00-00', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B1-G-05', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000687, '762', '塑包机主动辊T51.60.Z65/FK401.2200-01', 'SBJZDGT5160Z65FK401220001', '', 'FK401-2200-01', 'PCS',
        '', 1000000000000000003, 2000000000000000027, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-0-01', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000688, '82', '端头驱动滚筒T67cm/71mm/Z71cm', 'DTQDGTT67CM71MMZ71CM', '', 'PDL02B-04-01-00-00', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-01; 一侧多楔带驱动', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000689, '888', '轴（叉体回环拨烟轮）', 'ZCTHHBYL', '', 'TYFJC05-02-00-00-03', '件',
        '普天', 1000000000000000001, 2000000000000000010, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-0-01; 大小品规旋转叉体', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000690, '740', '从动滚筒总成(补货小车）T76cm/40mm/Z82cm', 'CDGTZCBHXCT76CM40MMZ82CM', '', 'TYFJD03-02-06-00-00-@2', '根',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-0-01; 大品规补货小车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000691, '286', '690链式端头滚筒总成T56.5cm/69-71mm/Z61cm', '690LSDTGTZCT565CM6971MMZ61CM', '', 'PDL02A-06-01-00-00', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-01; 一侧有齿轮', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000692, '704', '塑包机钱输送主动尼龙辊组件', 'SBJQSSZDNLGZJ', '', 'FK101-3000-08/09', '件',
        '', 1000000000000000003, 2000000000000000027, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-0-01; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000693, '705', '塑包机后输送主动尼龙辊组件', 'SBJHSSZDNLGZJ', '', 'FK101-3000-09/10', '件',
        '', 1000000000000000003, 2000000000000000027, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-0-01; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000694, '682', '470*112驱动滚筒T37.6cm/110mm/Z45.5cm', '470112QDGTT376CM110MMZ455CM', '', 'PDL01F-06-01-00-00', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-0-01; 一侧多楔带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000695, '23', '轴（转折机构链条轴）', 'ZZZJGLTZ', '', '试18LTZZ-01-05-00-02', '件',
        '普天', 1000000000000000001, 2000000000000000010, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-01; 转折机构', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000696, '801', '驱动滚筒T38cm/94mm/Z56cm', 'QDGTT38CM94MMZ56CM', '', 'T38cm/94mm/Z56cm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-0-01; 无轴承', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000697, '799', '驱动滚筒（缓存皮带）', 'QDGTHCPD', '', '京烟HCPTJ-04-00-00-00-@1', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-0-01; 补货线缓存皮带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000698, '952', '固定驱动轴(补货线标烟侧耳开箱）', 'GDQDZBHXBYCEKX', '', 'JTY12-01-01-00-01', '根',
        '', 1000000000000000001, 2000000000000000010, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-0-01; 开箱侧耳', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000699, '797', '驱动滚筒总成T76.4cm/40mm/Z82cm(补货小车）', 'QDGTZCT764CM40MMZ82CMBHXC', '', 'TYFJD03-02-07-00-00-@1', '根',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-0-01; 补货小车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000700, '687', '驱动滚筒', 'QDGT', '', 'JKX01A-05-07-00-00@1', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-0-01', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000701, '891', '驱动辊筒总成（叉体回环）', 'QDGTZCCTHH', '', 'TYFJC05-01-00-00-00', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-0-01; 细支叉体回环', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000702, '702', '塑包机叠层从动轴', 'SBJDCCDZ', '', 'FK101-4000-08', '件',
        '', 1000000000000000003, 2000000000000000029, NULL, 1, 0, 1,
        0.0, 0, 0, 3, NULL, NULL,
        '货位:B2-0-01; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000703, '836', '驱动滚筒（大品规回环）', 'QDGTDPGHH', '', '长治17HSJGA-02-00-00-04', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-0-01; 大品规核数机构', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000704, '18', '塑包机光轴（50mm）', 'SBJGZ50MM', '', '50*15', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-01; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000705, '1008', '690传动轴', '690CDZ', '', 'ZZA-01-01-03-01', '件',
        '', 1000000000000000001, 2000000000000000010, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-0-01; 690', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000706, '800', '张紧滚筒T71cm/40mm/Z77cm', 'ZJGTT71CM40MMZ77CM', '', 'T71cm/40mm/Z77cm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-0-01; 轴俩侧有螺丝孔，一侧有凹槽', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000707, '701', '塑包机叠层主动轴', 'SBJDCZDZ', '', 'FK101-4000-07', '件',
        '', 1000000000000000003, 2000000000000000029, NULL, 1, 0, 1,
        0.0, 0, 0, 5, NULL, NULL,
        '货位:B2-0-01; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000708, '272', '从动滚筒总成T59cm/40mm/Z65.5cm', 'CDGTZCT59CM40MMZ655CM', '', '京烟HCPTJ-01-00-00-00-@2', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-01; 轴俩侧有螺丝孔/一侧有凹槽', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000709, '892', '张紧托辊总成（叉体回环）', 'ZJTGZCCTHH', '', 'TYFJC05-04-00-00-00', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-0-01; 细支叉体回环', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000710, '78', '驱动滚筒T70cm/109mm/Z78cm', 'QDGTT70CM109MMZ78CM', '', 'PDL02B-01-02-00-00', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-01; 一侧多楔带驱动', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000711, '686', '从动滚筒', 'CDGT', '', 'JKX01A-05-06-00-00@2', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-0-01; 轴俩侧有螺丝孔/一侧有凹槽', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000712, '1013', '塑包机输出主动轴（出口皮带）', 'SBJSCZDZCKPD', '', 'FK101-R000-04', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-0-01; 塑包机出口', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000713, '953', '滑动驱动轴', 'HDQDZ', '', 'JTY12-01-02-00-01', '根',
        '', 1000000000000000001, 2000000000000000010, NULL, 1, 0, 1,
        0.0, 0, 0, 0, NULL, NULL,
        '货位:B2-0-01', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000714, '372', '气缸', 'QG', '', 'SAU50*780S', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-02; 大品规标烟推烟器', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000715, '374', '塑包机气缸', 'SBJQG', '', 'DSBC32*570-PPVA-N3', '件',
        '', 1000000000000000003, 2000000000000000025, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-02; 塑包机推烟器', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000716, '59', '导轨长：560mm，宽50mm', 'DGC560MMK50MM', '', 'TYFJG03A-03-00-00-03', '个',
        '普天', 1000000000000000001, 2000000000000000010, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-02; 铝合金', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000717, '62', '塑包机气缸（封切气缸）', 'SBJQGFQQG', '', 'DSBC-50*370-PPVA-N3/03.02.03.5155', '件',
        '科盛', 1000000000000000003, 2000000000000000025, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-0-02; 塑包机封切气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000718, '985', '塑包机气缸（封切）', 'SBJQGFQ', '', 'DSBC-50*370-PPVA-N3', '件',
        '', 1000000000000000003, 2000000000000000025, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-0-02; 塑包机封切气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000719, '169', '导杆', 'DG', '', 'JKX01A-06-00-00-02', '件',
        '', 1000000000000000001, 2000000000000000010, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-02', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000720, '27', '气缸（库房夹烟旋转）', 'QGKFJYXZ', '', 'SE40*260SG', '件',
        '普天', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-0-02; 气缸（库房夹烟旋转）', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000721, '614', '液压缸', 'YYG', '', 'YQL410-160-18-8-180N', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '货位:B2-0-02', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000722, '749', '冷却风管', 'LQFG', '', 'FK101-9000-18', '件',
        '', 1000000000000000001, 2000000000000000018, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '货位:B2-0-02', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000723, '773', '塑包机无杆气缸', 'SBJWGQG', '', 'RMS20*450', 'PCS',
        '', 1000000000000000003, 2000000000000000025, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-0-02', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000724, '698', '塑包机气缸（贴标机）03.02.03.5002', 'SBJQGTBJ0302035002', '', 'DSNU-20*450-PPV-A', '件',
        '', 1000000000000000003, 2000000000000000025, NULL, 1, 0, 1,
        0.0, 0, 0, 12, NULL, NULL,
        '货位:B2-0-02; 贴标机标签下压气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000725, '508', '气缸（吸烟箱）', 'QGXYX', '', 'SE32*500SG-CD861A', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 3, NULL, NULL,
        '货位:B2-0-02; 吸烟箱气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000726, '17', '气缸', 'QG', '', 'DSBC50*145-PPVA-N3', '件',
        '普天', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-02; 合流升降臂/方', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000727, '377', '气缸', 'QG', '', 'SE63*400S', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-02', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000728, '375', '塑包机气缸', 'SBJQG', '', 'DSNU-20*200-PPV-A', '件',
        '', 1000000000000000003, 2000000000000000025, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-02; 塑包机挡板', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000729, '250', '气缸', 'QG', '', 'SAU-50*640-S', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-02', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000730, '613', '液压缸（氮气弹簧）划箱机', 'YYGDQDHHXJ', '', 'FHJ41-18120-400', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 4, NULL, NULL,
        '货位:B2-0-02; 划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000731, '21', '气缸（库房夹烟旋转）', 'QGKFJYXZ', '', 'SE32*350SG', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-0-02; 气缸（库房夹烟旋转）', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000732, '541', '气缸', 'QG', '', 'DSBC50*270-PPVA-N3', '件',
        '普天', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '货位:B2-0-02; 分流/方', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000733, '44', '小型汽缸（出推滑道）', 'XXQGCTHD', '', 'CD85N25-370C-B-X2018', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-02; 出推气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000734, '376', '划箱机液压弹簧', 'HXJYYDH', '', 'FGS18120-300', '个',
        '普天', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-02; 划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000735, '524', '气缸', 'QG', '', 'DSBC-63*380-PPVA-N3', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '货位:B2-0-02; 大品规升降气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000736, '378', '塑包机气缸03.02.03.5154', 'SBJQG0302035154', '', 'DSBC50*320-PPVA-N3', '件',
        '科盛', 1000000000000000003, 2000000000000000025, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-02; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000737, '660', '塑包机封切底座', 'SBJFQD', '', 'PK101-9000-05', 'PCS',
        '', 1000000000000000003, 2000000000000000029, NULL, 1, 0, 1,
        0.0, 0, 0, 6, NULL, NULL,
        '货位:B2-0-03; 塑包机（旧的）', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000738, '296', '端头滚筒总成T116.5cm/71mm/Z121.5cm', 'DTGTZCT1165CM71MMZ1215CM', '', 'PDL03A-04-00-01-00-@1', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-03; 长', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000739, '298', '张紧滚筒总成T116.5cm/71mm/Z126cm', 'ZJGTZCT1165CM71MMZ126CM', '', 'PDL03A-04-00-02-00-@2', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-03; 轴俩侧有螺丝孔', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000740, '887', '塑包机被动滚筒', 'SBJBDGT', '', 'FK5GD-7000(FK5GD-0000-24/25)', '套',
        '科盛', 1000000000000000003, 2000000000000000027, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-0-03; 塑包机输送皮带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000741, '703', '塑包机机械人手臂杆', 'SBJJXRSBG', '', '1100（总长870）', '件',
        '', 1000000000000000003, 2000000000000000029, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-0-03; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000742, '745', '不锈钢无动力下托辊(蓝）总长118.5cm/50mm', 'BXGWDLXTGLZC1185CM50MM', '', 'PDL03A-03-00-04-00', 'PCS',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-0-03; 不锈钢无动力', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000743, '194', '导轨（库房拆垛旋转抱夹机构）一套', 'DGKFCDXZBJJGYT', '', 'HLH25*800', '件',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-0-03; 库房拆垛抱夹', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000744, '912', '塑包机无动力辊（650mm）03.03.16.5001', 'SBJWDLG650MM0303165001', '', '25*650（FK401-2000-05)', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000027, NULL, 1, 0, 1,
        0.0, 0, 0, 20, NULL, NULL,
        '货位:B2-0-03; 塑包机出口', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000745, '547', '塑包机无杆气缸', 'SBJWGQG', '', 'CY3820-450', '件',
        '', 1000000000000000003, 2000000000000000025, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '货位:B2-0-03; 小品塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000746, '600', '塑包机固定导膜辊', 'SBJGDDMG', '', '25*740（FK101M-7000-20）', '件',
        '', 1000000000000000003, 2000000000000000027, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-03; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000747, '20', '光轴(820mm）', 'GZ820MM', '', '820*20', '件',
        '', 1000000000000000001, 2000000000000000010, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-03', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000748, '207', '不锈钢无动力上托辊T总长118.5cm/50', 'BXGWDLSTGTZC1185CM50', '', 'LJGT-00-06', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-0-03; 不锈钢无动力', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000749, '205', '滑块（库房拆垛旋转抱夹机构）一套', 'HKKFCDXZBJJGYT', '', 'HLH25D', '件',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-0-03; 库房拆垛抱夹', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000750, '1016', '塑包机张紧滚筒', 'SBJZJGT', '', 'FK5GD-3000', '件',
        '', 1000000000000000003, 2000000000000000027, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-0-03; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000751, '714', '塑包机封后辊筒（614mm）', 'SBJFHGT614MM', '', '25*614（KS28.AO1T.04-30)', '件',
        '', 1000000000000000003, 2000000000000000027, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-0-03; 不锈钢', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000752, '761', '塑包机张紧被动辊轴组件', 'SBJZJBDGZZJ', '', 'KSY.0.14-03/14-05', 'PCS',
        '', 1000000000000000003, 2000000000000000027, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-0-03', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000753, '599', '塑包机固定导膜辊03.03.16.5003', 'SBJGDDMG0303165003', '', '25*850（KS28.A01T.01-10)', '件',
        '', 1000000000000000003, 2000000000000000027, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-03; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000754, '198', '塑包机浮动导膜辊FK101.7000.15', 'SBJFDDMGFK101700015', '', 'FK101-7000-15-07-2-14', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000027, NULL, 1, 0, 1,
        20.0, 0, 0, 30, NULL, NULL,
        '货位:B2-0-3; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000755, '461', '塑包机被动辊筒装配（小品输送皮带）', 'SBJBDGTZPXPSSPD', '', 'FK5W2-1100', '套',
        '', 1000000000000000003, 2000000000000000027, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-0-3; 塑包机小品规输送皮带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000756, '480', '塑包机被动送膜辊（包装机送膜辊筒）', 'SBJBDSMGBZJSMGT', '', 'Φ50*740（KSY.M.07-4)', '件',
        '', 1000000000000000003, 2000000000000000027, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-0-3; 包装机送膜辊筒', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000757, '315', '输出模块', 'SCM', '', '6ES7 132-6BH00-0BA0', '件',
        '', 1000000000000000002, 2000000000000000022, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-1-02', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000758, '399', '塑包机数据总插头', 'SBJSJZCT', '', '6ES7972-OBA12-OXAO', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-1-02; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000759, '314', '输入模块', 'SRMK', '', '6ES7 131-6BH00-0BA0', '件',
        '', 1000000000000000002, 2000000000000000022, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-1-02', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000760, '712', '塑包机光纤线03.09.03.5001', 'SBJGXX0309035001', '', 'FD-30（M3-松下）', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-1-03; 塑包机机械手', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000761, '403', '塑包机光纤线', 'SBJGXX', '', 'FD-31', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 1, 30, NULL, NULL,
        '货位:B2-1-03; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000762, '14', '电磁阀', 'DCF', '', 'MLH-5-1/4-B', '件',
        '', 1000000000000000001, 2000000000000000017, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-1-04', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000763, '383', '行程开关', 'XCKG', '', 'WLCA2-8-N', '个',
        '普天', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-1-04', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000764, '404', '电机断路器', 'DJDLQ', '', 'GV2-ME07C/1.6A-2.5A(8C1811)', '件',
        '普天', 1000000000000000002, 2000000000000000022, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-1-05', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000765, '384', '行程开关', 'XCKG', '', 'WLCA12-2N', '个',
        '普天', 1000000000000000001, 2000000000000000016, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-1-06; 有俩个QC 借用', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000766, '699', '塑包机光电开关', 'SBJGDKG', '', 'HT5.1/2(方形-劳易测）', '件',
        '', 1000000000000000003, 2000000000000000028, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-1-07; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000767, '406', '塑包机劳易测光纤放大器03.09.03.5101', 'SBJLYCGXFDQ0309035101', '', 'LV461.1/P2', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-1-07; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000768, '307', '单点继电器', 'DDJDQ', '', 'CR-PSS', '件',
        '', 1000000000000000002, 2000000000000000023, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-1-09', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000769, '472', '轴承', 'ZC', '', 'LR5308-2Z（02K15）', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-1-11; A2-1-53(2)/B2-1-11(4)', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000770, '319', 'TR轴承', 'TRZC', '', 'UCP207', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-1-12', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000771, '318', 'TR轴承', 'TRZC', '', 'UCFL207', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-1-12', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000772, '390', '直线轴承', 'ZXZC', '', 'LMF16LUU', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-1-13', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000773, '222', '自动排水器', 'ZDPSQ', '', 'AD402-04', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-1-14', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000774, '19', '打码从动滚筒(粗）', 'DMCDGTC', '', '哈烟18ZXDBPD-04-00-00-00', 'PCS',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-1-15', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000775, '276', '从动滚筒总成', 'CDGTZC', '', 'JKX01A-01-19-00-00', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-1-15', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000776, '275', '驱动滚筒总成', 'QDGTZC', '', 'JKX01A-01-11-00-00', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-1-16', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000777, '990', '驱动滚筒', 'QDGT', '', '京烟17JTY01-02-01-01', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-1-17; 无轴无轴承', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000778, '56', '不锈钢无动力滚筒', 'BXGWDLGT', '', '23.5cm*60mm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-1-17; 不锈钢', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000779, '270', '驱动滚筒', 'QDGT', '', '京烟17JTY15-01-02-01-00', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-1-17; 细标细支补货线', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000780, '860', '打码从动滚筒（细）/大品规/T33.5CM/39MM/Z36CM', 'DMCDGTXDPGT335CM39MMZ36CM', '', '哈烟18ZXDBPD-04-00-00-00', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 3, NULL, NULL,
        '货位:B2-1-18; 大品规打码滚筒', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000781, '402', '塑包机智能温控仪', 'SBJZNWKY', '', 'XMTD-8000', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-2-01; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000782, '855', '按钮盒', 'ANH', '', 'BX1-Y22C(黄）', '个',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-2-02', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000783, '919', '真空发生器18D', 'ZKFSQ18D', '', 'ZH18DS-03-03-03', '个',
        '普天', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-2-03', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000784, '415', '四点继电器模组', 'SDJDQMZ', '', 'G6D-F4B', '件',
        '', 1000000000000000002, 2000000000000000023, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-2-04', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000785, '405', '交流接触器', 'JLJCQ', '', 'LC1N0601M5N', '件',
        '', 1000000000000000002, 2000000000000000022, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-2-05', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000786, '309', '交流接触器', 'JLJCQ', '', 'LC1N0601M5N', '件',
        '', 1000000000000000002, 2000000000000000022, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-2-05', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000787, '924', '交流接触器', 'JLJCQ', '', 'LC1D09M7C', '个',
        '普天', 1000000000000000002, 2000000000000000022, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-2-06', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000788, '854', '交流接触器', 'JLJCQ', '', 'LC1D18M7C', '个',
        '施耐德', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-2-07', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000789, '646', '塑包机吸盘固定板', 'SBJXPGDB', '', 'FK009-1000-KH', '件',
        '', 1000000000000000003, 2000000000000000029, NULL, 1, 0, 1,
        0.0, 0, 0, 3, NULL, NULL,
        '货位:B2-2-08; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000790, '400', '塑包机三相固态', 'SBJSXGT', '', 'ESR-80DA-H', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-2-09; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000791, '498', '直线滑块轴承座', 'ZXHKZC', '', 'SMA20GUU', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 3, NULL, NULL,
        '货位:B2-2-10', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000792, '392', '滑块轴承', 'HKZC', '', 'SC20UU', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-2-10', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000793, '468', '轴承', 'ZC', '', '6904Z-NSK', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-2-11', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000794, '737', 'TR轴承', 'TRZC', '', 'UCPH204', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-2-12', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000795, '389', '直线轴承', 'ZXZC', '', 'LMF25LUU', '件',
        '', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-2-13', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000796, '645', '划箱机右侧吸烟罩', 'HXJYCXYZ', '', 'LKX201607A-10.02E/  HSG03.10.02E', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 3, NULL, NULL,
        '货位:B2-2-14; 激光划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000797, '644', '划箱机左侧吸烟罩挡板', 'HXJZCXYZDB', '', 'LKX201607A-10.90.03/  HSJG03.10.90.03', '件',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-2-14; 激光划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000798, '262', '载货台顶轮组件（堆垛机）', 'ZHTDLZJDDJ', '', '', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-2-17; 堆垛机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000799, '85', '施耐德变频器', 'SNDBPQ', '', 'ATV312H075M2', '件',
        '施耐德', 1000000000000000002, 2000000000000000021, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-3-01', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000800, '317', '聚氨脂缓冲器', 'JAZHCQ', '', 'JHQ-A-4', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-3-02; 库房', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000801, '771', '真空发生器20D', 'ZKFSQ20D', '', 'ZH20DS-03-04-04', '个',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-3-03', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000802, '310', '热继电器', 'RJDQ', '', 'LRN08N', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-3-04', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000803, '409', '塑包机U型磁开关', 'SBJUXCKG', '', 'SU15-NP', '件',
        '', 1000000000000000003, 2000000000000000028, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-3-05; 塑包机翻版', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000804, '758', '塑包机光电开关03.09.02.5407', 'SBJGDKG0309025407', '', 'SU18-NP', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000028, NULL, 1, 0, 1,
        0.0, 0, 0, 7, NULL, NULL,
        '货位:B2-3-05; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000805, '408', '三档旋转开关', 'SDXZKG', '', 'XB2-BS542', '件',
        '', 1000000000000000002, 2000000000000000022, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-3-06', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000806, '313', '开关电源', 'KGDY', '', 'LRS-200-24', '件',
        '', 1000000000000000002, 2000000000000000022, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-3-07', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000807, '941', '开关电源', 'KGDY', '', 'LRS-350-24', '件',
        '', 1000000000000000002, 2000000000000000022, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-3-07', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000808, '805', '开关电源', 'KGDY', '', 'LRS-100-24', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 12, NULL, NULL,
        '货位:B2-3-08', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000809, '3', '开关电源', 'KGDY', '', 'RSP-100-24', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-3-08', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000810, '411', '塑包机劳易测检测开关', 'SBJLYCJCKG', '', 'RT318M/N-100.11', '件',
        '', 1000000000000000003, 2000000000000000028, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-3-09; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000811, '720', '塑包机伺服驱动器', 'SBJSFQDQ', '', 'JSDAP15A', '件',
        '', 1000000000000000003, 2000000000000000033, NULL, 1, 0, 1,
        0.0, 0, 0, 4, NULL, NULL,
        '货位:B2-3-10; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000812, '909', '塑包机伺服驱动器（翻版）', 'SBJSFQDQFB', '', 'JSDAP20A', 'PCS',
        '科盛', 1000000000000000003, 2000000000000000033, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-3-10; 塑包机翻板', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000813, '128', 'G200系列调压阀', 'G200XLDYF', '', 'GR200-06-C-1-G', '件',
        '', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-3-11', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000814, '936', '扩展模块（03.07.02.5354）', 'KZMK0307025354', '', 'S20-0000-8/1(8入/8出博世）', '件',
        '', 1000000000000000002, 0, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-3-12; 塑包机机械手', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000815, '935', '塑包机XM21运动控制器（03.07.01.5201）', 'SBJXM21YDKZQ0307015201', '', 'XM2100.01.013131301-NN-102NNNN(博世）', '件',
        '', 1000000000000000003, 2000000000000000029, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-3-12; 塑包机机械手', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000816, '847', '气缸（升举）', 'QGSJ', '', 'ACQS -80*60', '个',
        '亚德客', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 6, NULL, NULL,
        '货位:B2-3-13; 升举气缸', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000817, '643', '划箱机左侧吸烟罩', 'HXJZCXYZ', '', 'LKX201607A-10.90.02D/  HSG03.10.90.02D', '件',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 3, NULL, NULL,
        '货位:B2-3-14; 激光划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000818, '381', '塑包机链条', 'SBJLT', '', '08B-1R*120L', '件',
        '科盛', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-3-16; 包装机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000819, '951', '变频驱动器', 'BPQDQ', '', 'SK545E-751-340-A', '件',
        '诺德', 1000000000000000002, 2000000000000000021, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-4-01; 库房电控柜', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000820, '844', '普拉多激光打码主板', 'PLDJGDMZB', '', '', 'PCS',
        '普拉多', 1000000000000000002, 0, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-4-01; 激光打码', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000821, '857', '塑包机电磁阀大', 'SBJDCFD', '', '58D-36-121BA', '件',
        '科盛', 1000000000000000003, 2000000000000000030, NULL, 1, 0, 1,
        0.0, 0, 0, 3, NULL, NULL,
        '货位:B2-4-03; 塑包机吸盘电磁阀', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000822, '856', '开关电源', 'KGDY', '', 'EDR-150-24', '台',
        '台湾明纬', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-4-04; 链板机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000823, '943', '阻挡气缸', 'ZDQG', '', 'TWH-S-63*30 K G', '件',
        '', 1000000000000000001, 2000000000000000002, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-4-05; 库房码垛机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000824, '956', '吸盘(细支旋转）', 'XPXZXZ', '', 'ESS-80-BU', '件',
        '普天', 1000000000000000001, 2000000000000000007, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-4-06; 大品规补货线细支旋转，激光划箱机前面', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000825, '938', '编码器（多穿）', 'BMQDC', '', 'TSI40N-27AK2T6TN-01000', '件',
        '', 1000000000000000002, 2000000000000000021, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-4-07; 多穿小车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000826, '52', '倒车蜂鸣器', 'DCFMQ', '', 'DJB-24V', '件',
        '', 1000000000000000002, 2000000000000000022, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-4-09', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000827, '412', '塑包机精密温控仪', 'SBJJMWKY', '', 'XMTD-B8431N', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-4-10', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000828, '393', '塑包机直线轴承02.05.10.5212', 'SBJZXZC0205105212', '', 'LMF20LUU(THK)', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-4-11; 塑包机推烟直线轴承', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000829, '53', '光电传感器', 'GDCGQ', '', 'CDD-11N', '件',
        '', 1000000000000000001, 2000000000000000015, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-4-12', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000830, '931', '塑包机减速器03.03.01.5206', 'SBJJSQ0303015206', '', 'SD90-25-19T-PS-PS', 'PCS',
        '', 1000000000000000003, 2000000000000000026, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-4-14; 塑包机机械手', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000831, '868', '堆垛机轴承', 'DDJZC', '', '型号GB/T288-1994 (22218EMD1)', '件',
        '普天', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-4-16; 堆垛机从动轮', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000832, '869', '堆垛机涨紧套', 'DDJZJT', '', 'JB/T7934-1999(Z4-90*140)', '件',
        '普天', 1000000000000000001, 2000000000000000009, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-4-17; 堆垛机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000833, '841', '刀片', 'DP', '', '', '片',
        '美工', 1000000000000000002, 0, NULL, 1, 0, 1,
        0.0, 0, 0, 100, NULL, NULL,
        '货位:B2-G-02', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000834, '995', '螺纹锁固剂', 'LWSGJ', '', '222/50ml', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 20, NULL, NULL,
        '货位:B2-G-02', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000835, '840', '电工胶布', 'DGJB', '', '3M1500*', '卷',
        '3M', 1000000000000000002, 0, NULL, 1, 0, 1,
        0.0, 0, 0, 100, NULL, NULL,
        '货位:B2-G-02', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000836, '900', '划箱机激光清洁套装', 'HXJJGQJTZ', '', 'HSJG10.00.02', '片',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-G-02; 划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000837, '1006', '挡烟门（标烟）', 'DYMBY', '', 'TYFJG03A-01-03-00-01', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-G-03; 大品规卧式机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000838, '75', '挡烟门二（标烟左边侧)', 'DYMEBYZBC', '', 'TYFJG03A-01-04-00-01', '件',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-G-03; 大品规', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000839, '1005', '挡烟门（细支）', 'DYMXZ', '', 'TYFJG05-01-03-00-01', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-G-04; 小品规卧式机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000840, '61', '挡烟门三（细支左边侧）', 'DYMSXZZBC', '', 'TYFJG05-01-10-00-01', '件',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-G-04; 小品规', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000841, '57', '挡烟门二（细支右边侧）', 'DYMEXZYBC', '', 'TYFJG05-01-09-00-01', '件',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:B2-G-04; 小品规', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000842, '798', '划箱机过滤棉', 'HXJGLM', '', 'HSJG500-004/43cm/41cm', '片',
        '普天', 1000000000000000001, 2000000000000000019, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-G-05; 划箱机过滤棉', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000843, '890', '划箱机预过滤器', 'HXJYGLQ', '', 'HSJG500-001/LZ5XL-YA', '件',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 50, NULL, NULL,
        '货位:B2-G-05; 激光划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000844, '893', '划箱机主过滤器', 'HXJZGLQ', '', 'HSJG500-002/LZ5XL-ZA', '件',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-G-05; 激光划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000845, '894', '划箱机气体过滤器', 'HXJQTGLQ', '', 'HSJG500-003/LZ5XL-QA', '件',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:B2-G-05; 激光划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000846, '728', '传输皮带P25-34/0D', 'CSPDP25340D', '', '1700*715', '件',
        '', 1000000000000000001, 2000000000000000006, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '货位:B2-G-07', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000847, '684', '驱动滚筒T34cm/80mm/Z58.5cm', 'QDGTT34CM80MMZ585CM', '', 'T34cm*79-80mm*Z58.5-59cm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-G-08; 无轴承/皮带主动驱动', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000848, '692', '驱动滚筒T38cm/80mm/Z62.5cm', 'QDGTT38CM80MMZ625CM', '', 'T38cm*79-80mm*Z62.5-63cm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-G-08; 无轴承/皮带主动滚筒', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000849, '688', '（多楔带）驱动滚筒T56.9cm/70mm/Z60.5cm', 'DXDQDGTT569CM70MMZ605CM', '', 'T56.9cm/70mm/Z60.5cm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-G-08; 一侧多楔带驱动', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000850, '786', '从动滚筒T55.5cm/70mm/Z59cm', 'CDGTT555CM70MMZ59CM', '', 'T55.5cm/70-71mm/Z58.5-59.5cm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-G-08', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000851, '685', '多楔带驱动滚筒T60cm/11-11.5mm/67.5cm', 'DXDQDGTT60CM11115MM675CM', '', 'PDL02A-01-02-00-00', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-G-08; 一侧多楔带驱动', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000852, '693', '驱动滚筒T38cm/60mm/*Z53.5cm', 'QDGTT38CM60MMZ535CM', '', 'T38cxm*59-60mm*Z53.5cm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-G-08; 无轴承/皮带驱动滚筒', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000853, '695', '张紧滚筒T55.5cm/*71mm/Z65.5cm', 'ZJGTT555CM71MMZ655CM', '', 'T55.5cm/*71mm/Z65.5cm', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:B2-G-08; 轴俩侧有螺丝孔', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000854, '184', '同步皮带（划箱机）', 'TBPDHXJ', '', 'HTD400-5M', '条',
        '普天', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:F; 激光划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000855, '438', '多楔带', 'DXD', '', 'PJ290', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000856, '208', '多楔带', 'DXD', '', 'PL1160', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000857, '639', '同步带', 'TBD', '', 'HTD760-5M', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 111, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000858, '13', '多楔带', 'DXD', '', '9PK810', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000859, '624', '多楔带', 'DXD', '', 'PJ680', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 111, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000860, '440', '多楔带', 'DXD', '', 'PJ610', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000861, '637', '多楔带', 'DXD', '', 'PK1065', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 111, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000862, '629', '多楔带', 'DXD', '', 'PJ288', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 111, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000863, '444', '多楔带', 'DXD', '', 'PJ560-16mm', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000864, '630', '多楔带', 'DXD', '', 'PJ584', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 111, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000865, '627', '多楔带', 'DXD', '', 'PJ546', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 111, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000866, '626', '多楔带', 'DXD', '', 'PJ572', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 111, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000867, '437', '多楔带', 'DXD', '', 'PJ336', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000868, '321', '同步带', 'TBD', '', 'HTD456-8M-23', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙; 多穿小车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000869, '425', '同步带', 'TBD', '', 'HTD425-5M', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000870, '445', '多楔带', 'DXD', '', 'PJ550', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000871, '628', '多楔带', 'DXD', '', 'PJ314', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 111, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000872, '634', '多楔带', 'DXD', '', 'PK967', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 111, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000873, '2', '同步带', 'TBD', '', 'HTD685-5M-20', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000874, '436', '多楔带', 'DXD', '', 'PJ286', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000875, '429', '同步带', 'TBD', '', 'HTD540-5M', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000876, '51', '同步带', 'TBD', '', 'HTD900-5M-20', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000877, '636', '多楔带', 'DXD', '', 'PK1014', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 111, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000878, '633', '多楔带', 'DXD', '', 'PJ359', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 1, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000879, '213', '圆带', 'YD', '', 'PUφ6/330MM', '件',
        '', 1000000000000000001, 2000000000000000005, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000880, '431', '同步带', 'TBD', '', 'HTD560-5M', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000881, '4', '多楔带', 'DXD', '', '9PK811', '条',
        '普天', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000882, '9', '多楔带', 'DXD', '', '9PK961', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000883, '426', '同步带', 'TBD', '', 'HTD456-8M', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙; 多穿小车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000884, '433', '同步带', 'TBD', '', 'HTD585-5M', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000885, '428', '同步带', 'TBD', '', 'HTD530-5M-20', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000886, '10', '多楔带', 'DXD', '', '9PK1056', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000887, '252', '圆带', 'YD', '', 'PUφ6    L=340mm', '条',
        '普天', 1000000000000000001, 2000000000000000005, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000888, '65', '圆带', 'YD', '', 'PUφ8    L=363MM', '条',
        '普天', 1000000000000000001, 2000000000000000005, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000889, '439', '多楔带', 'DXD', '', 'PJ381-7mm', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000890, '638', '多楔带', 'DXD', '', 'PK1048', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 22, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000891, '11', '多楔带', 'DXD', '', '9PK1050', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000892, '631', '多楔带', 'DXD', '', 'PJ480', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 111, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000893, '16', '多楔带', 'DXD', '', '7PJ495', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000894, '34', '同步带', 'TBD', '', 'HTD970-5M-20', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000895, '641', '同步带', 'TBD', '', 'HTD980-5M', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 111, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000896, '635', '多楔带', 'DXD', '', 'PK1100', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 111, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000897, '430', '同步带', 'TBD', '', 'HTD520-5M', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000898, '442', '多楔带', 'DXD', '', 'PJ520', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000899, '434', '同步带', 'TBD', '', 'HTD755-5M', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000900, '632', '多楔带', 'DXD', '', 'PJ508', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 111, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000901, '243', '圆带', 'YD', '', 'PUφ6  L=310mm', '条',
        '普天', 1000000000000000001, 2000000000000000005, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000902, '12', '多楔带', 'DXD', '', '9PK950', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000903, '899', '划箱机同步带', 'HXJTBD', '', 'HTD-5M-15-400', '条',
        '普天', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:北墙; 激光划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000904, '441', '多楔带', 'DXD', '', 'PJ650', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000905, '448', '多楔带', 'DXD', '', 'PJ486', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000906, '447', '多楔带', 'DXD', '', 'PJ379', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000907, '50', '同步带', 'TBD', '', 'HTD620-5M-20', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000908, '642', '多楔带', 'DXD', '', 'PJ483', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 111, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000909, '126', '多楔带', 'DXD', '', '7PJ690', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000910, '237', '同步带二', 'TBDE', '', '530-5M-20', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000911, '625', '多楔带', 'DXD', '', 'PJ750', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 111, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000912, '449', '多楔带', 'DXD', '', '280-5M-20', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000913, '451', '同步带', 'TBD', '', '5M-635/8809', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000914, '453', '划箱机多楔带', 'HXJDXD', '', '135J', '个',
        '普天', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙; 激光划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000915, '133', '圆带', 'YD', '', '', '件',
        '', 1000000000000000001, 2000000000000000005, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000916, '838', '导条皮带（大品规核数）', 'DTPDDPGHS', '', '试TYFJC03A-00-00-00-02', '件',
        '普天', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:北墙; 大品规核数机构', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000917, '89', '同步带', 'TBD', '', 'PJ470', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000918, '190', '弹性多楔带', 'TXDXD', '', 'PJ302', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000919, '494', '划箱机多锲带', 'HXJDD', '', '190J-3F1240', '个',
        '普天', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:北墙; 激光划箱机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000920, '135', '多楔带', 'DXD', '', 'PJ711', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000921, '191', '弹性多楔带', 'TXDXD', '', 'PJ346', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000922, '134', '圆带', 'YD', '', '', '件',
        '', 1000000000000000001, 2000000000000000005, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000923, '496', '光电开关', 'GDKG', '', 'Z2T-D-2022H', '件',
        '', 1000000000000000001, 2000000000000000015, NULL, 1, 0, 1,
        0.0, 0, 0, 20, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000924, '443', '多楔带', 'DXD', '', 'PJ560-11mm', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000925, '193', '弹性多楔带', 'TXDXD', '', 'PJ416', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000926, '192', '弹性多楔带', 'TXDXD', '', 'PJ376', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙; 库房', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000927, '70', '多楔带', 'DXD', '', '711PJ7', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000928, '454', '塑包机双面齿同步皮带HTD1032-8M', 'SBJSMCTBPDHTD10328M', '', 'HTD.DA8M-1032-25(橡胶+玻璃纤维绳）', '件',
        '', 1000000000000000003, 2000000000000000032, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000929, '450', '多楔带', 'DXD', '', '288-5M', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000930, '452', '多楔带', 'DXD', '', '290J', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000931, '435', '同步带', 'TBD', '', 'HTD640-5M', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000932, '233', '多契带', 'DQD', '', 'PJ381-28mm', '件',
        '', 1000000000000000001, 2000000000000000003, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:北墙', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000933, '862', '手动黄油枪', 'SDHYQ', '', 'Y2-090-A01', '件',
        '艾瑞泽', 1000000000000000002, 0, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '货位:抽屉; 黄油枪', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000934, '155', '驱动滚筒（锥度）', 'QDGTZD', '', '总长77cm/筒49cm', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:副库; 锥度', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000935, '299', '驱动滚筒总成（粗）', 'QDGTZCC', '', 'PDL03A-04-02-00-00', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:副库; 粗', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000936, '772', '滚筒', 'GT', '', '长65.5cm.70mm', '个',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:副库', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000937, '152', '滚筒', 'GT', '', '长67cm/69mm', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:副库', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000938, '905', '同步带（补货小车）长', 'TBDBHXCC', '', 'HTD14M-38100-64', '米',
        '普天', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:副库; 补货小车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000939, '158', '滚筒（粗）', 'GTC', '', '', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:副库; 粗', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000940, '159', '驱动滚筒（粗）', 'QDGTC', '', '', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:副库', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000941, '267', '前端张紧滚筒（79.5cm/70mm）', 'QDZJGT795CM70MM', '', '01-00-03', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:副库; 俩侧有螺丝孔', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000942, '281', '链式驱动滚筒（长58cm/70mm）', 'LSQDGTC58CM70MM', '', '06-02', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:副库; 一边多楔带，一边齿轮', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000943, '154', '端头张紧滚筒', 'DTZJGT', '', '长65.5cm/71mm', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:副库; 轴俩侧有螺丝孔', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000944, '277', '链式驱动滚筒（长68cm/70mm）', 'LSQDGTC68CM70MM', '', '10-02', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:副库; 一边多楔带，一边齿轮', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000945, '279', '驱动动滚筒（粗）', 'QDDGTC', '', 'PDL03A-04-02-00-00', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:副库; 粗滚筒', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000946, '278', '链式端头滚筒（长81cm.70mm）', 'LSDTGTC81CM70MM', '', '01-01', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:副库; 一侧齿轮', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000947, '151', '滚筒', 'GT', '', '长65.5cm/69mm', '个',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:副库; 151', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000948, '287', '端头张紧滚筒总成（长116.5cm/71mm）', 'DTZJGTZCC1165CM71MM', '', '04-02-02-00', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:副库; 俩边轴有螺丝孔', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000949, '167', '滚筒（粗）', 'GTC', '', '', '件',
        '', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:副库; （粗）', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000950, '289', '790链式端头滚筒总成（长66.5cm/71mm）', '790LSDTGTZCC665CM71MM', '', 'PDL02B-10-01-00-00', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:副库; 一边齿轮', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000951, '612', '驱动滚筒', 'QDGT', '', '长38cm/95mm', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 2, NULL, NULL,
        '货位:副库', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000952, '742', '双链传动辊筒（长104.5cm）', 'SLCDGTC1045CM', '', 'TPG01A-03-00-00-00', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 11, NULL, NULL,
        '货位:副库; 高架库', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000953, '302', '470驱动滚筒（长37.5cm/80mm）', '470QDGTC375CM80MM', '', '01-03-00-00', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:副库; 一边多楔带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000954, '156', '驱动滚筒（锥度）', 'QDGTZD', '', '总长71cm/筒39cm', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:副库; 锥度', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000955, '260', '端头滚筒', 'DTGT', '', '长116.5cm/71mm', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:副库', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000956, '297', '改向滚筒总成（长116.5cm/71mm）', 'GXGTZCC1165CM71MM', '', 'PDL03A-03-00-06-00', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:副库', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000957, '268', '驱动滚筒总成（长70cm/112mm）', 'QDGTZCC70CM112MM', '', '01-02', '件',
        '普天', 1000000000000000001, 2000000000000000008, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:副库', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000958, '1011', '补货小车顶板', 'BHXCDB', '', 'TYFJD03-01-01-00-02', '件',
        '', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:角落; 补货小车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000959, '932', '塑包机90度转弯机皮带03.05.12.9014', 'SBJ90DZWJPD0305129014', '', 'R500*W600', 'PCS',
        '', 1000000000000000003, 2000000000000000032, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:角落; 塑包机转弯皮带', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000960, '1018', '塑包机和谐号输送网', 'SBJHXHSSW', '', '03.03.09.7004/周长5486.4', '件',
        '', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:角落; 塑包机', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000961, '94', '塑包机（真空泵）', 'SBJZKB', '', '', '件',
        '科盛', 1000000000000000003, 2000000000000000024, NULL, 1, 0, 1,
        0.0, 0, 0, 3, NULL, NULL,
        '货位:平台', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000962, '325', '同步齿条', 'TBCT', '', 'ZNB01-02-00-00-01', '件',
        '', 1000000000000000001, 2000000000000000004, NULL, 1, 0, 1,
        0.0, 0, 0, 30, NULL, NULL,
        '货位:平台; 多穿小车', 0, NOW(), 'system');

INSERT INTO wms_item (id, item_code, item_name, pinyin, model, spec, unit, brand, category_id, sub_category_id,
                      supplier_id, status, is_consumable, is_returnable, purchase_price, stock_qty,
                      stock_lower_limit, stock_upper_limit, replenish_threshold, idle_days, remark, del_flag, create_time, create_by)
VALUES (3000000000000000963, '865', '伸叉叉体(多穿小车）', 'SCCTDCXC', '', 'ZNB03-01-00-00-01', '件',
        '普天', 1000000000000000001, 2000000000000000001, NULL, 1, 0, 1,
        0.0, 0, 0, 10, NULL, NULL,
        '货位:平台; 多穿小车叉体', 0, NOW(), 'system');