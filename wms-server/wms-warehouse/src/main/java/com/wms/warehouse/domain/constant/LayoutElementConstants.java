package com.wms.warehouse.domain.constant;

/**
 * 库房平面底图元素常量类
 * 统一定义辅助布局元素类型和形状类型，避免业务代码出现魔法字符串。
 */
public final class LayoutElementConstants {

    private LayoutElementConstants() {
    }

    /** 元素类型：墙体/边界 */
    public static final String ELEMENT_TYPE_WALL = "wall";

    /** 元素类型：通道 */
    public static final String ELEMENT_TYPE_AISLE = "aisle";

    /** 元素类型：预留区 */
    public static final String ELEMENT_TYPE_RESERVED = "reserved";

    /** 元素类型：设备/点位 */
    public static final String ELEMENT_TYPE_DEVICE = "device";

    /** 元素类型：文字标注 */
    public static final String ELEMENT_TYPE_TEXT = "text";

    /** 元素类型：尺寸标注 */
    public static final String ELEMENT_TYPE_DIMENSION = "dimension";

    /** 形状类型：线段 */
    public static final String SHAPE_TYPE_LINE = "line";

    /** 形状类型：矩形 */
    public static final String SHAPE_TYPE_RECT = "rect";

    /** 形状类型：多边形 */
    public static final String SHAPE_TYPE_POLYGON = "polygon";

    /** 形状类型：圆形 */
    public static final String SHAPE_TYPE_CIRCLE = "circle";

    /** 形状类型：文字 */
    public static final String SHAPE_TYPE_TEXT = "text";

    /** 元素编码前缀 */
    public static final String ELEMENT_CODE_PREFIX = "LE";

    /** 底图文件存储桶名 */
    public static final String BACKGROUND_BUCKET = "warehouse-layout";

    /** 底图文件路径模板：warehouse-layout/{warehouseId}/background/{version}.{ext} */
    public static final String BACKGROUND_PATH_TEMPLATE = "%d/background/%s.%s";

    /** 底图文件最大大小：10MB */
    public static final long BACKGROUND_MAX_SIZE = 10 * 1024 * 1024L;

    /** 底图允许的文件扩展名 */
    public static final String[] BACKGROUND_ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "svg"};

    /** 底图允许的Content-Type */
    public static final String[] BACKGROUND_ALLOWED_CONTENT_TYPES = {
            "image/jpeg", "image/png", "image/svg+xml"
    };
}
