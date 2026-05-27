/** 业务单据管理 */

/** 单据状态枚举 */
export type OrderStatus = 'DRAFT' | 'PENDING_REVIEW' | 'APPROVED' | 'COMPLETED' | 'REJECTED'

/** 入库类型枚举 */
export type InboundType = 'PURCHASE' | 'RETURN' | 'TRANSFER'

/** 出库类型枚举 */
export type OutboundType = 'BORROW' | 'TRANSFER' | 'SCRAP'

/** 扫码标签元数据 */
export interface ScannedLabelMeta {
  /** 标签ID */
  labelId: number
  /** 标签编号 */
  labelNo: string
}

/** 单据扫码结果中的明细建议 */
export interface OrderScanResultDetail {
  /** 物品ID */
  itemId: number
  /** 数量 */
  quantity: number
}

/** 单据扫码结果 */
export interface OrderScanResult {
  /** 标签ID */
  labelId: number
  /** 标签编号 */
  labelNo: string
  /** 标签状态 */
  labelStatus: number
  /** 物品ID */
  itemId: number
  /** 物品名称 */
  itemName: string
  /** 物品编码 */
  itemCode: string
  /** 建议回填的明细 */
  detail: OrderScanResultDetail
}

/** 单据扫码请求 */
export interface OrderScanRequest {
  /** 扫码内容 */
  code: string
  /** 当前单据中已扫描的标签ID */
  currentLabelIds?: number[]
}

/** 入库扫码请求 */
export type InboundScanRequest = OrderScanRequest

/** 出库扫码请求 */
export type OutboundScanRequest = OrderScanRequest

/** 表单中的扫码明细行 */
export interface OrderScanDetailRow {
  /** 物品ID */
  itemId: number
  /** 数量 */
  quantity: number
  /** 单价 */
  unitPrice: number
  /** 库位ID */
  binId?: number
  /** 物品名称 */
  itemName?: string
  /** 物品编码 */
  itemCode?: string
  /** 规格型号 */
  specModel?: string
  /** 单位 */
  unit?: string
  /** 金额 */
  amount?: number
  /** 该行已扫描标签 */
  scannedLabels?: ScannedLabelMeta[]
}

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
  inboundType: InboundType
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
  /** 入库库位ID */
  binId?: number
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
  outboundType: OutboundType
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
  /** 库位ID */
  binId?: number
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
  /** 归还人 */
  receiver: string
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
  /** 物品状态(0-正常 1-损坏) */
  conditionStatus: number
  /** 异常说明 */
  abnormalRemark: string
}

/** 归还单新增DTO */
export interface ReturnOrderDto {
  /** 关联出库单ID */
  outboundOrderId: number
  /** 归还人 */
  receiver: string
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
  /** 物品状态(0-正常 1-损坏) */
  conditionStatus: number
  /** 异常说明 */
  abnormalRemark: string
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

/** 审批状态枚举 */
export type ApprovalStatus = 0 | 1 | 2 | 3 | 4

/** 审批结果枚举 */
export type ApprovalResult = 1 | 2

/** 业务类型枚举 */
export type BizType = 1 | 2 | 3 | 4 | 5

/** 审批人类型枚举 */
export type ApprovalApproverType = 1 | 2 | 3

/** 审批配置查询参数 */
export interface ApprovalConfigQuery {
  /** 当前页码 */
  page: number
  /** 每页条数 */
  size: number
  /** 业务类型 */
  bizType?: BizType
}

/** 审批单查询参数 */
export interface ApprovalOrderQuery {
  /** 当前页码 */
  page: number
  /** 每页条数 */
  size: number
  /** 业务类型 */
  bizType?: BizType
  /** 审批状态 */
  status?: ApprovalStatus
}

/** 审批配置视图对象 */
export interface ApprovalConfigVo {
  /** 配置ID */
  id: number
  /** 配置名称 */
  configName: string
  /** 业务类型 */
  bizType: BizType
  /** 是否启用 */
  enabled: number
  /** 是否免审 */
  autoApprove: number
  /** 备注 */
  remark?: string
  /** 审批超时阈值(小时) */
  timeoutHours: number
  /** 超时处理方式 */
  timeoutAction: number
  /** 审批节点列表 */
  nodes: ApprovalNodeVo[]
  /** 创建时间 */
  createTime: string
}

/** 审批配置新增/编辑DTO */
export interface ApprovalConfigDto {
  /** 配置名称 */
  configName: string
  /** 业务类型 */
  bizType: BizType
  /** 是否启用 */
  enabled: number
  /** 是否免审 */
  autoApprove: number
  /** 备注 */
  remark?: string
  /** 审批超时阈值(小时) */
  timeoutHours?: number
  /** 超时处理方式 */
  timeoutAction?: number
  /** 审批节点列表 */
  nodes: ApprovalNodeDto[]
}

/** 审批节点视图对象 */
export interface ApprovalNodeVo {
  /** 节点ID */
  id: number
  /** 配置ID */
  configId: number
  /** 节点顺序 */
  stepOrder: number
  /** 节点名称 */
  nodeName: string
  /** 审批人类型(1-指定角色 2-指定用户 3-库房管理员) */
  approverType: ApprovalApproverType
  /** 审批人ID */
  approverId?: number
}

/** 审批节点提交对象 */
export interface ApprovalNodeDto {
  /** 节点顺序 */
  stepOrder: number
  /** 节点名称 */
  nodeName: string
  /** 审批人类型(1-指定角色 2-指定用户 3-库房管理员) */
  approverType: ApprovalApproverType
  /** 审批人/角色ID */
  approverId?: number
}

/** 审批单视图对象 */
export interface ApprovalOrderVo {
  /** 审批单ID */
  id: number
  /** 审批单号 */
  approvalNo: string
  /** 业务类型 */
  bizType: BizType
  /** 业务单据ID */
  bizId: number
  /** 业务单号 */
  bizNo: string
  /** 当前节点名称 */
  currentNodeName: string
  /** 审批状态 */
  status: ApprovalStatus
  /** 申请人姓名 */
  applicantName: string
  /** 申请人ID */
  applicantId: number
  /** 当前审批节点 */
  currentStep: number
  /** 总审批节点数 */
  totalSteps: number
  /** 备注 */
  remark?: string
  /** 审批记录列表 */
  records: ApprovalRecordVo[]
  /** 创建时间 */
  createTime: string
}

/** 审批记录视图对象 */
export interface ApprovalRecordVo {
  /** 记录ID */
  id: number
  /** 审批单ID */
  approvalId: number
  /** 节点名称 */
  nodeName: string
  /** 审批人 */
  approverName: string
  /** 审批结果(1-通过 2-驳回) */
  result: ApprovalResult
  /** 审批意见 */
  opinion: string
  /** 审批时间 */
  approveTime: string
}

/** 审批操作DTO */
export interface ApprovalActionDto {
  /** 审批意见 */
  opinion: string
}

/** 机器-备件关联视图对象 */
export interface MachineSpareVo {
  /** ID */
  id: number
  /** 机器名称 */
  machineName: string
  /** 机器编号 */
  machineCode: string
  /** 备件物品ID */
  itemId: number
  /** 备件物品名称 */
  itemName: string
  /** 数量 */
  quantity: number
  /** 备注 */
  remark: string
  /** 创建时间 */
  createTime: string
}

/** 机器-备件关联新增/编辑DTO */
export interface MachineSpareDto {
  /** 机器名称 */
  machineName: string
  /** 机器编号 */
  machineCode: string
  /** 备件物品ID */
  itemId: number
  /** 数量 */
  quantity: number
  /** 备注 */
  remark: string
}
