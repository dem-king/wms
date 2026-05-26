package com.wms.common.storage;

/**
 * 文件存储常量类
 * 定义存储桶名等常量，全模块统一使用
 */
public final class StorageConstants {
    private StorageConstants() {}

    /** 桶名：头像 */
    public static final String BUCKET_AVATARS = "avatars";

    /** 桶名：物品图片 */
    public static final String BUCKET_ITEMS = "items";

    /** 预签名URL默认过期时间（秒） */
    public static final int PRESIGN_EXPIRE_SECONDS = 300;
}
