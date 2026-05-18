/** 库房管理 */

/** 库房视图对象 */
export interface WmsWarehouseVo {
  /** 库房ID */
  id: number
  /** 库房名称 */
  warehouseName: string
  /** 库房编码 */
  warehouseCode: string
  /** 地址 */
  address: string
  /** 负责人 */
  manager: string
  /** 面积(平方米) */
  area: number
  /** 状态(0-禁用 1-启用) */
  status: number
  /** 创建时间 */
  createTime: string
}

/** 库房新增/编辑DTO */
export interface WmsWarehouseDto {
  /** 库房名称 */
  warehouseName: string
  /** 库房编码 */
  warehouseCode: string
  /** 地址 */
  address: string
  /** 负责人 */
  manager: string
  /** 面积(平方米) */
  area: number
  /** 状态(0-禁用 1-启用) */
  status: number
}

/** 区域视图对象 */
export interface WmsAreaVo {
  /** 区域ID */
  id: number
  /** 库房ID */
  warehouseId: number
  /** 区域名称 */
  areaName: string
  /** 区域编码 */
  areaCode: string
  /** 排序号 */
  sortOrder: number
  /** 状态(0-禁用 1-启用) */
  status: number
  /** 创建时间 */
  createTime: string
}

/** 区域新增/编辑DTO */
export interface WmsAreaDto {
  /** 库房ID */
  warehouseId: number
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
  id: number
  /** 区域ID */
  areaId: number
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
  /** 创建时间 */
  createTime: string
}

/** 存放柜新增/编辑DTO */
export interface WmsCabinetDto {
  /** 区域ID */
  areaId: number
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

/** 库位视图对象 */
export interface WmsBinVo {
  /** 库位ID */
  id: number
  /** 存放柜ID */
  cabinetId: number
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
  cabinetId: number
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
  cabinetId: number
  /** 起始行 */
  startRow: number
  /** 结束行 */
  endRow: number
  /** 起始列 */
  startCol: number
  /** 结束列 */
  endCol: number
}
