-- 为物品图片补充统一存储元数据
ALTER TABLE `wms_item_image`
    ADD COLUMN `bucket` VARCHAR(50) DEFAULT 'items' COMMENT '存储桶' AFTER `image_url`,
    ADD COLUMN `object_name` VARCHAR(500) DEFAULT NULL COMMENT '对象存储路径' AFTER `bucket`;
