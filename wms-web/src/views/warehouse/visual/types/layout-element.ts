/**
 * 库房布局元素类型定义
 * 对应后端 LayoutElementVo/Dto 等数据结构
 */

import type { EntityId } from '@/types/warehouse'

// ==================== 枚举/常量类型 ====================

/** 元素类型枚举 */
export type ElementType = 'wall' | 'aisle' | 'reserved' | 'device' | 'text' | 'dimension'

/** 形状类型枚举 */
export type ShapeType = 'line' | 'rect' | 'polygon' | 'circle' | 'text'

/** 视图模式 */
export type ViewMode = '2d' | '2.5d'

// ==================== 数据结构 ====================

/** 点坐标 */
export interface Point {
  /** X坐标 */
  x: number
  /** Y坐标 */
  y: number
}

/** 线段/多边形点位数据 */
export interface PointData {
  /** 点位列表（线段2个端点，多边形至少3个顶点） */
  points: Point[]
}

/** 尺寸标注点位数据 */
export interface DimensionPointData {
  /** 起点 */
  start: Point
  /** 终点 */
  end: Point
}

/** 样式数据 */
export interface StyleData {
  /** 填充颜色 */
  fillColor?: string
  /** 描边颜色 */
  strokeColor?: string
  /** 描边宽度 */
  strokeWidth?: number
  /** 字号 */
  fontSize?: number
  /** 字色 */
  fontColor?: string
  /** 虚线模式（如[10,5]） */
  dashPattern?: number[] | null
}

// ==================== VO/DTO ====================

/** 布局元素视图对象 */
export interface LayoutElementVo {
  /** 主键 */
  id: EntityId
  /** 所属库房ID */
  warehouseId: EntityId
  /** 关联区域ID */
  areaId?: EntityId | null
  /** 元素编码 */
  elementCode: string
  /** 元素名称 */
  elementName: string
  /** 元素类型(wall/aisle/reserved/device/text/dimension) */
  elementType: ElementType
  /** 形状类型(line/rect/polygon/circle/text) */
  shapeType: ShapeType
  /** X坐标 */
  positionX: number
  /** Y坐标 */
  positionY: number
  /** 宽度 */
  layoutWidth: number
  /** 高度 */
  layoutHeight: number
  /** 旋转角度 */
  rotation: number
  /** 点位数据JSON */
  pointData: string
  /** 样式数据JSON */
  styleData: string
  /** 展示文本 */
  labelText: string
  /** 排序号 */
  sortOrder: number
  /** 状态(1-启用 0-禁用) */
  status: number
  /** 备注 */
  remark?: string
  /** 创建时间 */
  createTime: string
}

/** 布局元素创建/更新DTO */
export interface LayoutElementDto {
  /** 所属库房ID */
  warehouseId: EntityId
  /** 关联区域ID */
  areaId?: EntityId | null
  /** 元素名称 */
  elementName: string
  /** 元素类型 */
  elementType: ElementType
  /** 形状类型 */
  shapeType: ShapeType
  /** X坐标 */
  positionX: number
  /** Y坐标 */
  positionY: number
  /** 宽度 */
  layoutWidth: number
  /** 高度 */
  layoutHeight: number
  /** 旋转角度 */
  rotation: number
  /** 点位数据JSON */
  pointData: string
  /** 样式数据JSON */
  styleData: string
  /** 展示文本 */
  labelText: string
  /** 排序号 */
  sortOrder: number
  /** 备注 */
  remark?: string
}

/** 布局元素更新项DTO（含id） */
export interface LayoutElementUpdateItemDto extends LayoutElementDto {
  /** 元素ID */
  id: EntityId
}

/** 批量保存DTO */
export interface LayoutElementBatchSaveDto {
  /** 所属库房ID */
  warehouseId: EntityId
  /** 新增元素列表 */
  created: LayoutElementDto[]
  /** 更新元素列表 */
  updated: LayoutElementUpdateItemDto[]
  /** 删除元素ID列表 */
  deletedIds: EntityId[]
}

/** 失败项VO */
export interface LayoutElementFailedItemVo {
  /** 元素名称 */
  elementName: string
  /** 失败原因 */
  reason: string
}

/** 批量保存结果VO */
export interface LayoutElementBatchSaveVo {
  /** 新增成功数量 */
  createdCount: number
  /** 更新成功数量 */
  updatedCount: number
  /** 删除成功数量 */
  deletedCount: number
  /** 失败项列表 */
  failedItems: LayoutElementFailedItemVo[]
}

/** 底图上传结果VO */
export interface WarehouseBackgroundVo {
  /** 库房ID */
  warehouseId: EntityId
  /** 新版本号 */
  layoutBackgroundVersion: string
  /** 底图访问URL */
  backgroundUrl: string
}

/** 区域布局坐标项DTO */
export interface AreaLayoutItemDto {
  /** 区域ID */
  id: EntityId
  /** 画布X坐标 */
  coordX?: number | null
  /** 画布Y坐标 */
  coordY?: number | null
}

/** 区域布局坐标批量DTO */
export interface AreaLayoutBatchDto {
  /** 区域坐标项列表 */
  items: AreaLayoutItemDto[]
}

// ==================== 渲染上下文 ====================

/** 渲染上下文 */
export interface RenderContext {
  /** 视图模式 */
  viewMode: ViewMode
  /** 是否选中 */
  isSelected: boolean
  /** 是否高亮 */
  isHighlighted: boolean
  /** 是否编辑模式 */
  isEditMode: boolean
  /** 当前缩放比例 */
  scale: number
}

// ==================== 常量 ====================

/** 元素类型常量 */
export const ELEMENT_TYPES: Record<ElementType, string> = {
  wall: '墙体',
  aisle: '通道',
  reserved: '预留区',
  device: '设备',
  text: '文字标注',
  dimension: '尺寸标注',
} as const

/** 形状类型常量 */
export const SHAPE_TYPES: Record<ShapeType, string> = {
  line: '线段',
  rect: '矩形',
  polygon: '多边形',
  circle: '圆形',
  text: '文字',
} as const

/** 默认样式映射 */
export const DEFAULT_ELEMENT_STYLES: Record<ElementType, StyleData> = {
  wall: { fillColor: '#4a4a4a', strokeColor: '#333333', strokeWidth: 2 },
  aisle: { fillColor: '#f0f9ff', strokeColor: '#91d5ff', strokeWidth: 1, dashPattern: [10, 5] },
  reserved: { fillColor: '#fff7e6', strokeColor: '#fa8c16', strokeWidth: 2 },
  device: { fillColor: '#e6f7ff', strokeColor: '#1890ff', strokeWidth: 1 },
  text: { fontSize: 14, fontColor: '#333333' },
  dimension: { strokeColor: '#333333', strokeWidth: 1, fontSize: 12, fontColor: '#333333' },
} as const

/** 尺寸标注端点标记线长度(px) */
export const DIMENSION_END_MARK_LENGTH = 8

/** 尺寸标注文本最小字号(px) */
export const DIMENSION_MIN_FONT_SIZE = 10

/** 布局元素批量加载每批数量 */
export const ELEMENT_BATCH_SIZE = 100

/** 布局元素最大数量 */
export const ELEMENT_MAX_COUNT = 500

// ==================== 工具函数 ====================

/**
 * 解析点位数据JSON
 * @param pointDataStr 点位数据JSON字符串
 * @returns 解析后的点位数据，解析失败返回null
 */
export function parsePointData(pointDataStr: string): PointData | DimensionPointData | null {
  if (!pointDataStr) {
    return null
  }
  try {
    return JSON.parse(pointDataStr)
  } catch {
    console.warn('[layout-element] 点位数据JSON解析失败:', pointDataStr)
    return null
  }
}

/**
 * 解析样式数据JSON
 * @param styleDataStr 样式数据JSON字符串
 * @returns 解析后的样式数据，解析失败返回空对象
 */
export function parseStyleData(styleDataStr: string): StyleData {
  if (!styleDataStr) {
    return {}
  }
  try {
    return JSON.parse(styleDataStr)
  } catch {
    console.warn('[layout-element] 样式数据JSON解析失败:', styleDataStr)
    return {}
  }
}

/**
 * 合并默认样式与自定义样式
 * @param elementType 元素类型
 * @param customStyle 自定义样式
 * @returns 合并后的样式
 */
export function mergeStyle(elementType: ElementType, customStyle: StyleData): StyleData {
  return { ...DEFAULT_ELEMENT_STYLES[elementType], ...customStyle }
}

/**
 * 根据起止点和比例尺自动计算物理距离文本
 * @param startX 起点X
 * @param startY 起点Y
 * @param endX 终点X
 * @param endY 终点Y
 * @param layoutScale 比例尺（1px对应的物理长度m），null或0时显示px
 * @returns 标注文本
 */
export function calculateDimensionText(
  startX: number,
  startY: number,
  endX: number,
  endY: number,
  layoutScale: number | null,
): string {
  const pixelDistance = Math.sqrt((endX - startX) ** 2 + (endY - startY) ** 2)
  if (layoutScale && layoutScale > 0) {
    // 比例尺：1px = layoutScale m
    const physicalDistance = pixelDistance * layoutScale
    return `${physicalDistance.toFixed(1)}m`
  }
  // 比例尺未配置时显示像素距离
  return `${Math.round(pixelDistance)}px`
}