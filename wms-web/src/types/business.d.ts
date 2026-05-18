/** 业务单据管理 */

/** 单据状态枚举 */
export type OrderStatus = 'DRAFT' | 'PENDING_REVIEW' | 'APPROVED' | 'COMPLETED' | 'REJECTED'

/** 入库单视图对象 */
export interface InboundOrderVo {
  /** 入库单ID */
  id: number
  /** 入库单号 */
  orderNo: string
  /** 库房ID */
  warehouseId: number
  /** 库房名称 */
  warehouseName: string
  /** 供应商ID */
  supplierId: number
  /** 供应商名称 */
  supplierName: string
  /** 入库类型 */
  inboundType: string
  /** 单据状态 */
  status: OrderStatus
  /** 总金额 */
  totalAmount: number
  /** 备注 */
  remark: string
  /** 明细列表 */
  details: InboundDetailVo[]
  /** 创建时间 */
  createTime: string
}

/** 入库明细视图对象 */
export interface InboundDetailVo {
  /** 明细ID */
  id: number
  /** 入库单ID */
  orderId: number
  /** 物品ID */
  itemId: number
  /** 物品编码 */
  itemCode: string
  /** 物品名称 */
  itemName: string
  /** 规格型号 */
  specModel: string
  /** 单位 */
  unit: string
  /** 数量 */
  quantity: number
  /** 单价 */
  unitPrice: number
  /** 金额 */
  amount: number
}

/** 入库单新增/编辑DTO */
export interface InboundOrderDto {
  /** 库房ID */
  warehouseId: number
  /** 供应商ID */
  supplierId: number
  /** 入库类型 */
  inboundType: string
  /** 备注 */
  remark: string
  /** 明细列表 */
  details: InboundDetailDto[]
}

/** 入库明细DTO */
export interface InboundDetailDto {
  /** 物品ID */
  itemId: number
  /** 数量 */
  quantity: number
  /** 单价 */
  unitPrice: number
}

/** 出库单视图对象 */
export interface OutboundOrderVo {
  /** 出库单ID */
  id: number
  /** 出库单号 */
  orderNo: string
  /** 库房ID */
  warehouseId: number
  /** 库房名称 */
  warehouseName: string
  /** 出库类型 */
  outboundType: string
  /** 领用人 */
  recipient: string
  /** 用途 */
  purpose: string
  /** 归还日期 */
  returnDate: string
  /** 单据状态 */
  status: OrderStatus
  /** 总金额 */
  totalAmount: number
  /** 备注 */
  remark: string
  /** 明细列表 */
  details: OutboundDetailVo[]
  /** 创建时间 */
  createTime: string
}

/** 出库明细视图对象 */
export interface OutboundDetailVo {
  /** 明细ID */
  id: number
  /** 出库单ID */
  orderId: number
  /** 物品ID */
  itemId: number
  /** 物品编码 */
  itemCode: string
  /** 物品名称 */
  itemName: string
  /** 规格型号 */
  specModel: string
  /** 单位 */
  unit: string
  /** 数量 */
  quantity: number
  /** 单价 */
  unitPrice: number
  /** 金额 */
  amount: number
}

/** 出库单新增/编辑DTO */
export interface OutboundOrderDto {
  /** 库房ID */
  warehouseId: number
  /** 出库类型 */
  outboundType: string
  /** 领用人 */
  recipient: string
  /** 用途 */
  purpose: string
  /** 归还日期 */
  returnDate: string
  /** 备注 */
  remark: string
  /** 明细列表 */
  details: OutboundDetailDto[]
}

/** 出库明细DTO */
export interface OutboundDetailDto {
  /** 物品ID */
  itemId: number
  /** 数量 */
  quantity: number
  /** 单价 */
  unitPrice: number
}

/** 归还单视图对象 */
export interface ReturnOrderVo {
  /** 归还单ID */
  id: number
  /** 归还单号 */
  orderNo: string
  /** 关联出库单ID */
  outboundOrderId: number
  /** 关联出库单号 */
  outboundOrderNo: string
  /** 领用人 */
  recipient: string
  /** 单据状态 */
  status: OrderStatus
  /** 备注 */
  remark: string
  /** 明细列表 */
  details: ReturnDetailVo[]
  /** 创建时间 */
  createTime: string
}

/** 归还明细视图对象 */
export interface ReturnDetailVo {
  /** 明细ID */
  id: number
  /** 归还单ID */
  orderId: number
  /** 物品ID */
  itemId: number
  /** 物品编码 */
  itemCode: string
  /** 物品名称 */
  itemName: string
  /** 归还数量 */
  quantity: number
}

/** 归还单新增DTO */
export interface ReturnOrderDto {
  /** 关联出库单ID */
  outboundOrderId: number
  /** 备注 */
  remark: string
  /** 明细列表 */
  details: ReturnDetailDto[]
}

/** 归还明细DTO */
export interface ReturnDetailDto {
  /** 物品ID */
  itemId: number
  /** 归还数量 */
  quantity: number
}

/** 报废单视图对象 */
export interface ScrapOrderVo {
  /** 报废单ID */
  id: number
  /** 报废单号 */
  orderNo: string
  /** 库房ID */
  warehouseId: number
  /** 库房名称 */
  warehouseName: string
  /** 报废原因 */
  scrapReason: string
  /** 单据状态 */
  status: OrderStatus
  /** 备注 */
  remark: string
  /** 明细列表 */
  details: ScrapDetailVo[]
  /** 创建时间 */
  createTime: string
}

/** 报废明细视图对象 */
export interface ScrapDetailVo {
  /** 明细ID */
  id: number
  /** 报废单ID */
  orderId: number
  /** 物品ID */
  itemId: number
  /** 物品编码 */
  itemCode: string
  /** 物品名称 */
  itemName: string
  /** 报废数量 */
  quantity: number
}

/** 报废单新增DTO */
export interface ScrapOrderDto {
  /** 库房ID */
  warehouseId: number
  /** 报废原因 */
  scrapReason: string
  /** 备注 */
  remark: string
  /** 明细列表 */
  details: ScrapDetailDto[]
}

/** 报废明细DTO */
export interface ScrapDetailDto {
  /** 物品ID */
  itemId: number
  /** 报废数量 */
  quantity: number
}

/** 调拨单视图对象 */
export interface TransferOrderVo {
  /** 调拨单ID */
  id: number
  /** 调拨单号 */
  orderNo: string
  /** 调出库房ID */
  fromWarehouseId: number
  /** 调出库房名称 */
  fromWarehouseName: string
  /** 调入库房ID */
  toWarehouseId: number
  /** 调入库房名称 */
  toWarehouseName: string
  /** 单据状态 */
  status: OrderStatus
  /** 备注 */
  remark: string
  /** 明细列表 */
  details: TransferDetailVo[]
  /** 创建时间 */
  createTime: string
}

/** 调拨明细视图对象 */
export interface TransferDetailVo {
  /** 明细ID */
  id: number
  /** 调拨单ID */
  orderId: number
  /** 物品ID */
  itemId: number
  /** 物品编码 */
  itemCode: string
  /** 物品名称 */
  itemName: string
  /** 调拨数量 */
  quantity: number
}

/** 调拨单新增DTO */
export interface TransferOrderDto {
  /** 调出库房ID */
  fromWarehouseId: number
  /** 调入库房ID */
  toWarehouseId: number
  /** 备注 */
  remark: string
  /** 明细列表 */
  details: TransferDetailDto[]
}

/** 调拨明细DTO */
export interface TransferDetailDto {
  /** 物品ID */
  itemId: number
  /** 调拨数量 */
  quantity: number
}
