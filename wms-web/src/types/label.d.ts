/** 电子标签管理 */

/** 电子标签视图对象 */
export interface ElectronicLabelVo {
  /** 标签ID */
  id: number
  /** 标签编号 */
  labelNo: string
  /** 标签类型(1-二维码 2-条形码 3-RFID) */
  labelType: number
  /** 物品ID */
  itemId: number
  /** 物品名称 */
  itemName: string
  /** 物品编码 */
  itemCode: string
  /** 批次号 */
  batchNo: string
  /** RFID编码 */
  rfidCode: string
  /** 二维码内容 */
  qrContent: string
  /** 条形码内容 */
  barcodeContent: string
  /** 标签状态(1-在库 2-正在使用 3-已归还 4-报废 5-闲置) */
  labelStatus: number
  /** 绑定类型(1-单品对应 2-批次对应) */
  bindType: number
  /** 打印状态(0-未打印 1-已打印) */
  printStatus: number
  /** 借用时间 */
  borrowTime: string
  /** 预计归还时间 */
  expectedReturn: string
  /** 创建时间 */
  createTime: string
}

/** 标签生成DTO */
export interface LabelGenerateDto {
  /** 物品ID */
  itemId: number
  /** 生成数量 */
  count: number
  /** 标签类型(1-二维码 2-条形码 3-RFID) */
  labelType: number
  /** 绑定类型(1-单品对应 2-批次对应) */
  bindType: number
}

/** 标签绑定DTO */
export interface LabelBindDto {
  /** 物品ID */
  itemId: number
  /** 绑定类型(1-单品对应 2-批次对应) */
  bindType: number
}

/** 标签状态更新DTO */
export interface LabelStatusDto {
  /** 标签状态(1-在库 2-正在使用 3-已归还 4-报废 5-闲置) */
  labelStatus: number
}

/** 批量打印请求DTO */
export interface LabelBatchPrintDto {
  /** 标签ID列表 */
  labelIds: number[]
}

/** 扫码查询结果 */
export interface LabelScanResultVo {
  /** 标签ID */
  id: number
  /** 标签编号 */
  labelNo: string
  /** 标签类型 */
  labelType: number
  /** 物品ID */
  itemId: number
  /** 物品名称 */
  itemName: string
  /** 物品编码 */
  itemCode: string
  /** 标签状态 */
  labelStatus: number
  /** 绑定类型 */
  bindType: number
}
