/** 物品管理 */

/** 主类目视图对象 */
import type { EntityId } from './common'

export type { EntityId }

export interface WmsCategoryVo {
  /** 类目ID */
  id: EntityId
  /** 类目名称 */
  categoryName: string
  /** 类目编码 */
  categoryCode: string
  /** 排序号 */
  sortOrder: number
  /** 标识颜色 */
  categoryColor?: string
  /** 图标 */
  icon?: string
  /** 是否消耗品(0-否 1-是) */
  isConsumable?: number
  /** 创建时间 */
  createTime: string
  /** 细分类目列表 */
  subCategories?: WmsSubCategoryVo[]
}

/** 主类目新增/编辑DTO */
export interface WmsCategoryDto {
  /** 类目名称 */
  categoryName: string
  /** 类目编码 */
  categoryCode: string
  /** 排序号 */
  sortOrder: number
  /** 标识颜色 */
  categoryColor?: string
  /** 图标 */
  icon?: string
  /** 是否消耗品(0-否 1-是) */
  isConsumable?: number
}

/** 细分类目视图对象 */
export interface WmsSubCategoryVo {
  /** 细分类目ID */
  id: EntityId
  /** 主类目ID */
  categoryId: EntityId
  /** 细分类目名称 */
  subCategoryName: string
  /** 细分类目编码 */
  subCategoryCode: string
  /** 排序号 */
  sortOrder: number
  /** 创建时间 */
  createTime: string
}

/** 细分类目新增/编辑DTO */
export interface WmsSubCategoryDto {
  /** 细分类目名称 */
  subCategoryName: string
  /** 细分类目编码 */
  subCategoryCode: string
  /** 排序号 */
  sortOrder: number
}

/** 标签视图对象 */
export interface WmsTagVo {
  /** 标签ID */
  id: EntityId
  /** 标签名称 */
  tagName: string
  /** 标签颜色 */
  tagColor?: string
  /** 描述 */
  tagDesc?: string
  /** 关联范围(0-全局 1-主类目 2-细分类目 3-具体物品) */
  scopeType?: number
  /** 关联范围对象ID */
  scopeId?: number
  /** 创建时间 */
  createTime: string
}

/** 标签新增/编辑DTO */
export interface WmsTagDto {
  /** 标签名称 */
  tagName: string
  /** 标签颜色 */
  tagColor?: string
  /** 描述 */
  tagDesc?: string
  /** 关联范围(0-全局 1-主类目 2-细分类目 3-具体物品) */
  scopeType?: number
  /** 关联范围对象ID */
  scopeId?: number
}

/** 物品视图对象 */
export interface WmsItemVo {
  /** 物品ID */
  id: EntityId
  /** 物品编码 */
  itemCode: string
  /** 物品名称 */
  itemName: string
  /** 规格型号 */
  specModel: string
  /** 计量单位 */
  unit: string
  /** 主类目ID */
  categoryId: EntityId
  /** 主类目名称 */
  categoryName: string
  /** 细分类目ID */
  subCategoryId: EntityId
  /** 细分类目名称 */
  subCategoryName: string
  /** 标签ID列表 */
  tagIds: EntityId[]
  /** 标签名称列表 */
  tagNames: string[]
  /** 标签列表 */
  tags?: WmsTagVo[]
  /** 供应商ID */
  binIds?: EntityId[]
  locations?: ItemLocationVo[]
  supplierId: EntityId
  /** 供应商名称 */
  supplierName: string
  /** 安全库存(下限) */
  stockLowerLimit: number
  /** 最大库存(上限) */
  stockUpperLimit: number
  /** 补货阈值 */
  replenishThreshold: number
  /** 实时库存 */
  currentStock: number
  /** 库存数量 */
  stockQty?: number
  /** 物品图片列表 */
  images: ItemImageVo[]
  /** 拼音首字母 */
  pinyinInitial: string
  /** 状态(0-禁用 1-启用) */
  status: number
  /** 创建时间 */
  createTime: string
}

/** 物品图片视图对象 */
export interface ItemImageVo {
  /** 图片ID */
  id: EntityId
  /** 物品ID */
  itemId: EntityId
  /** 图片URL */
  imageUrl: string
  /** 存储桶 */
  bucket?: string
  /** 对象存储路径 */
  objectName?: string
  /** 图片名称 */
  imageName: string
  /** 排序号 */
  sortOrder: number
}

/** 物品图片关联DTO */
export interface ItemImageDto {
  /** 图片URL */
  imageUrl: string
  /** 对象存储路径 */
  objectName: string
  /** 图片名称 */
  imageName?: string
  /** 排序号 */
  sortOrder?: number
}

/** 物品新增/编辑DTO */
export interface WmsItemDto {
  /** 物品编码 */
  itemCode: string
  /** 物品名称 */
  itemName: string
  /** 规格型号 */
  specModel: string
  /** 计量单位 */
  unit: string
  /** 主类目ID */
  categoryId: EntityId
  /** 细分类目ID */
  subCategoryId: EntityId
  /** 标签ID列表 */
  tagIds: EntityId[]
  /** 供应商ID */
  binIds?: EntityId[]
  supplierId: EntityId
  /** 安全库存(下限) */
  stockLowerLimit: number
  /** 最大库存(上限) */
  stockUpperLimit: number
  /** 补货阈值 */
  replenishThreshold: number
  /** 状态(0-禁用 1-启用) */
  status: number
}

/** 物品提交DTO */
export interface WmsItemSubmitDto {
  /** 物品名称 */
  itemName: string
  /** 型号 */
  model: string
  /** 规格 */
  spec: string
  /** 计量单位 */
  unit: string
  /** 主类目ID */
  categoryId: EntityId
  /** 细分类目ID */
  subCategoryId: EntityId
  /** 标签ID列表 */
  tagIds: EntityId[]
  /** 默认库位ID列表 */
  binIds?: EntityId[]
  /** 供应商ID */
  supplierId: EntityId
  /** 安全库存(下限) */
  stockLowerLimit: number
  /** 最大库存(上限) */
  stockUpperLimit: number
  /** 补货阈值 */
  replenishThreshold: number
  /** 状态(0-禁用 1-启用) */
  status: number
}

/** 库存视图对象 */
export interface ItemLocationVo {
  /** 库房ID */
  warehouseId: EntityId
  /** 库房名称 */
  warehouseName?: string
  /** 区域ID */
  areaId: EntityId
  /** 区域名称 */
  areaName?: string
  /** 存放柜ID */
  cabinetId: EntityId
  /** 存放柜名称 */
  cabinetName?: string
  /** 库位ID */
  binId: EntityId
  /** 库位编码 */
  binCode?: string
  /** 完整库位路径 */
  locationText?: string
}

export interface WmsStockVo {
  /** 库存ID */
  id: EntityId
  /** 物品ID */
  itemId: EntityId
  /** 物品编码 */
  itemCode: string
  /** 物品名称 */
  itemName: string
  /** 库房ID */
  warehouseId: EntityId
  /** 库房名称 */
  warehouseName: string
  /** 区域ID */
  areaId: EntityId
  /** 存放柜ID */
  cabinetId: EntityId
  /** 库位ID */
  binId: EntityId
  /** 库位编码 */
  binCode: string
  /** 数量 */
  quantity: number
  /** 锁定数量(审批中) */
  lockedQuantity: number
  /** 库存金额 */
  amount: number
  /** 最后入库时间 */
  lastInboundTime: string
  /** 最后出库时间 */
  lastOutboundTime: string
  /** 安全库存(下限) */
  stockLowerLimit: number
  /** 最大库存(上限) */
  stockUpperLimit: number
  /** 是否预警(quantity < stockLowerLimit) */
  alert: boolean
}
