/** 库房管理 */

/** 实体ID */
export type EntityId = string

/** 库房视图对象 */
export interface WmsWarehouseVo {
  /** 库房ID */
  id: EntityId
  /** 库房名称 */
  warehouseName: string
  /** 库房编码 */
  warehouseCode: string
  /** 地址 */
  address: string
  /** 负责人 */
  manager: string
  /** 联系电话 */
  phone?: string
  /** 面积(平方米) */
  area: number
  /** 状态(0-禁用 1-启用) */
  status: number
  /** 备注 */
  remark?: string
  /** 创建时间 */
  createTime: string
}

/** 库房新增/编辑DTO */
export interface WmsWarehouseDto {
  /** 库房名称 */
  warehouseName: string
  /** 库房编码 */
  warehouseCode?: string
  /** 地址 */
  address: string
  /** 负责人 */
  manager: string
  /** 联系电话 */
  phone?: string
  /** 面积(平方米) */
  area: number
  /** 状态(0-禁用 1-启用) */
  status: number
  /** 备注 */
  remark?: string
}

/** 区域视图对象 */
export interface WmsAreaVo {
  /** 区域ID */
  id: EntityId
  /** 库房ID */
  warehouseId: EntityId
  /** 区域名称 */
  areaName: string
  /** 区域编码 */
  areaCode: string
  /** 排序号 */
  sortOrder: number
  /** 状态(0-禁用 1-启用) */
  status: number
  /** X坐标(画布自由定位) */
  coordX?: number | null
  /** Y坐标(画布自由定位) */
  coordY?: number | null
  /** 区域形状类型(rect/polygon) */
  shapeType?: 'rect' | 'polygon' | null
  /** 多边形顶点坐标JSON */
  polygonPoints?: string | null
  /** 标题相对X坐标 */
  labelX?: number | null
  /** 标题相对Y坐标 */
  labelY?: number | null
  /** 创建时间 */
  createTime: string
}

/** 区域新增/编辑DTO */
export interface WmsAreaDto {
  /** 库房ID */
  warehouseId: EntityId
  /** 区域名称 */
  areaName: string
  /** 区域编码 */
  areaCode: string
  /** 排序号 */
  sortOrder: number
  /** 状态(0-禁用 1-启用) */
  status: number
}

/** 存放柜视图对象 */
export interface WmsCabinetVo {
  /** 存放柜ID */
  id: EntityId
  /** 区域ID */
  areaId: EntityId
  /** 所属库房ID */
  warehouseId?: EntityId
  /** 区域名称 */
  areaName?: string
  /** 存放柜名称 */
  cabinetName: string
  /** 存放柜编码 */
  cabinetCode: string
  /** X坐标(可视化位置) */
  positionX?: number
  /** Y坐标(可视化位置) */
  positionY?: number
  /** 存放柜类型 */
  cabinetType?: number
  /** 行数 */
  rows: number
  /** 列数 */
  cols: number
  /** 排序号 */
  sortOrder: number
  /** 状态(0-禁用 1-启用) */
  status: number
  /** 备注 */
  remark?: string
  /** 物品数量 */
  itemCount?: number
  /** 创建时间 */
  createTime: string
}

/** 存放柜新增/编辑DTO */
export interface WmsCabinetDto {
  /** 区域ID */
  areaId: EntityId
  /** 存放柜名称 */
  cabinetName: string
  /** 存放柜编码 */
  cabinetCode: string
  /** 行数 */
  rows: number
  /** 列数 */
  cols: number
  /** 排序号 */
  sortOrder: number
  /** 状态(0-禁用 1-启用) */
  status: number
}

/** 存放柜布局项DTO */
export interface WmsCabinetLayoutItemDto {
  /** 存放柜ID */
  id: EntityId
  /** X坐标 */
  positionX: number
  /** Y坐标 */
  positionY: number
  /** 排序号 */
  sortOrder: number
}

/** 存放柜布局批量保存DTO */
export interface WmsCabinetLayoutBatchSaveDto {
  /** 区域ID */
  areaId: EntityId
  /** 布局项列表 */
  cabinets: WmsCabinetLayoutItemDto[]
}

/** 存放柜布局保存结果VO */
export interface WmsCabinetLayoutSaveVo {
  /** 区域ID */
  areaId: EntityId
  /** 已保存的存放柜列表 */
  cabinets: WmsCabinetVo[]
}

/** 库位视图对象 */
export interface WmsBinVo {
  /** 库位ID */
  id: EntityId
  /** 存放柜ID */
  cabinetId: EntityId
  /** 库位编码 */
  binCode: string
  /** 行号 */
  row: number
  /** 列号 */
  col: number
  /** 状态(0-禁用 1-启用) */
  status: number
  /** 创建时间 */
  createTime: string
}

/** 库位新增/编辑DTO */
export interface WmsBinDto {
  /** 存放柜ID */
  cabinetId: EntityId
  /** 库位编码 */
  binCode: string
  /** 行号 */
  row: number
  /** 列号 */
  col: number
  /** 状态(0-禁用 1-启用) */
  status: number
}

/** 批量生成库位DTO */
export interface WmsBinBatchDto {
  /** 存放柜ID */
  cabinetId: EntityId
  /** 起始行 */
  startRow: number
  /** 结束行 */
  endRow: number
  /** 起始列 */
  startCol: number
  /** 结束列 */
  endCol: number
}
