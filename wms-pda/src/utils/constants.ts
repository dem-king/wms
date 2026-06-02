/**
 * WMS-PDA TypeScript类型定义与常量
 * 所有接口DTO/VO类型与后端Java字段名一一对应（小驼峰）
 * 枚举值使用常量对象，禁止魔法数字
 */

// ==================== 统一响应类型 ====================

/** 后端统一响应格式 */
export interface ApiResponse<T> {
  /** 响应码：200=成功, 401=未授权, 其他=业务异常 */
  code: number
  /** 提示信息 */
  msg: string
  /** 业务数据 */
  data: T
}

/** 分页结果 */
export interface PageResult<T> {
  /** 数据列表 */
  records: T[]
  /** 总记录数 */
  total: number
  /** 每页条数 */
  size: number
  /** 当前页码 */
  current: number
}

// ==================== 枚举常量 ====================

/** 标签类型枚举 */
export const LabelType = {
  /** 二维码 */
  QRCODE: 1,
  /** 条形码 */
  BARCODE: 2,
  /** RFID */
  RFID: 3
} as const
export type LabelTypeValue = typeof LabelType[keyof typeof LabelType]

/** 标签状态枚举 */
export const LabelStatus = {
  /** 在库 */
  IN_STOCK: 1,
  /** 正在使用 */
  IN_USE: 2,
  /** 已归还 */
  RETURNED: 3,
  /** 报废 */
  SCRAPPED: 4,
  /** 闲置 */
  IDLE: 5
} as const
export type LabelStatusValue = typeof LabelStatus[keyof typeof LabelStatus]

/** 单据状态枚举 */
export const OrderStatus = {
  /** 草稿 */
  DRAFT: 0,
  /** 待审核 */
  PENDING: 1,
  /** 已审核 */
  APPROVED: 2,
  /** 已完成 */
  COMPLETED: 3,
  /** 已驳回 */
  REJECTED: 4
} as const
export type OrderStatusValue = typeof OrderStatus[keyof typeof OrderStatus]

/** 差异类型枚举（盘盈/盘亏） */
export const DiffType = {
  /** 盘盈：实际有系统无 */
  SURPLUS: 'surplus',
  /** 盘亏：系统有实际无 */
  DEFICIT: 'deficit'
} as const
export type DiffTypeValue = typeof DiffType[keyof typeof DiffType]

/** 绑定类型枚举 */
export const BindType = {
  /** 单品对应 */
  SINGLE: 1,
  /** 批次对应 */
  BATCH: 2
} as const
export type BindTypeValue = typeof BindType[keyof typeof BindType]

/** 打印状态枚举 */
export const PrintStatus = {
  /** 未打印 */
  NOT_PRINTED: 0,
  /** 已打印 */
  PRINTED: 1
} as const
export type PrintStatusValue = typeof PrintStatus[keyof typeof PrintStatus]

/** 扫码作业类型 */
export type ScanJobType = 'inbound' | 'outbound' | 'return' | 'transfer' | 'scrap' | 'query'

/** 离线队列同步状态 */
export const OfflineQueueStatus = {
  /** 待同步 */
  PENDING: 'pending',
  /** 同步成功 */
  SUCCESS: 'success',
  /** 同步失败 */
  FAILED: 'failed'
} as const
export type OfflineQueueStatusValue = typeof OfflineQueueStatus[keyof typeof OfflineQueueStatus]

// ==================== 业务常量 ====================

/** 通用业务常量（与后端BizConstants对应） */
export const BizConstants = {
  /** 启用状态 */
  STATUS_ENABLED: 1,
  /** 禁用状态 */
  STATUS_DISABLED: 0
} as const

/** 离线队列容量上限 */
export const OFFLINE_QUEUE_MAX_SIZE = 100

/** Token过期提前刷新阈值（毫秒）：5分钟 */
export const TOKEN_EXPIRE_THRESHOLD = 5 * 60 * 1000

/** 请求超时配置（毫秒） */
export const RequestTimeout = {
  /** 普通请求超时：10秒 */
  DEFAULT: 10000,
  /** 扫码请求超时：5秒 */
  SCAN: 5000,
  /** 文件上传超时：30秒 */
  FILE: 30000
} as const

/** 操作日志保留天数 */
export const OPERATION_LOG_RETENTION_DAYS = 7

/** 短振动时长（毫秒） */
export const VIBRATE_SHORT_DURATION = 200

/** 长振动时长（毫秒） */
export const VIBRATE_LONG_DURATION = 500

/** RFID默认读取功率 */
export const RFID_DEFAULT_POWER = 30

/** RFID功率最小值 */
export const RFID_POWER_MIN = 5

/** RFID功率最大值 */
export const RFID_POWER_MAX = 30

/** 盘点类型枚举 */
export const CheckType = {
  /** RFID盘点 */
  RFID: 1,
  /** 人工盘点 */
  MANUAL: 2
} as const
export type CheckTypeValue = typeof CheckType[keyof typeof CheckType]

/** PDA硬件扫码键KeyCode常量 */
export const PdaScanKeyCodes = {
  /** Zebra扫码键 */
  ZEBRA: 120,
  /** 优博讯扫码键 */
  UROVO: 293,
  /** 新大陆扫码键 */
  NEWLAND: 280
} as const

/** PDA扫码键KeyCode列表（用于硬件扫码键监听） */
export const PDA_SCAN_KEY_CODES: number[] = [
  PdaScanKeyCodes.ZEBRA,
  PdaScanKeyCodes.UROVO,
  PdaScanKeyCodes.NEWLAND
]

// ==================== 认证相关类型 ====================

/** 登录请求 */
export interface LoginDto {
  /** 用户名 */
  username: string
  /** 密码 */
  password: string
}

/** 登录响应 */
export interface LoginResultVo {
  /** 访问令牌 */
  accessToken: string
  /** 刷新令牌 */
  refreshToken: string
  /** accessToken过期时间（秒） */
  expiresIn: number
}

/** Token刷新请求 */
export interface RefreshTokenDto {
  /** 刷新令牌 */
  refreshToken: string
}

/** Token刷新响应 */
export interface RefreshTokenResultVo {
  /** 新的访问令牌 */
  accessToken: string
  /** accessToken过期时间（秒） */
  expiresIn: number
}

/** 用户信息（与后端UserInfoVo一致） */
export interface UserInfoVo {
  /** 用户ID */
  userId: number
  /** 用户名 */
  username: string
  /** 真实姓名 */
  realName: string
  /** 头像 */
  avatar: string
  /** 部门ID */
  deptId: number
  /** 手机号 */
  phone: string
  /** 邮箱 */
  email: string
}

/** 最近登录信息 */
export interface LastLoginInfoVo {
  /** 最近登录时间 */
  lastLoginTime: string
  /** 最近登录IP */
  lastLoginIp: string
}

/** 用户档案（与后端AuthProfileVo一致） */
export interface AuthProfileVo {
  /** 用户信息 */
  userInfo: UserInfoVo
  /** 最近登录信息 */
  lastLoginInfo: LastLoginInfoVo
  /** 权限列表 */
  permissions: string[]
  /** 角色列表 */
  roles: string[]
}

// ==================== 电子标签类型 ====================

/** 电子标签（与后端ElectronicLabelVo一致） */
export interface ElectronicLabelVo {
  /** 标签ID */
  id: number
  /** 标签编号 */
  labelNo: string
  /** 标签类型 */
  labelType: LabelTypeValue
  /** 物品ID */
  itemId: number
  /** 物品名称 */
  itemName: string
  /** 物品编码 */
  itemCode: string
  /** 批次号 */
  batchNo: string | null
  /** RFID编码 */
  rfidCode: string | null
  /** 二维码内容 */
  qrContent: string | null
  /** 条形码内容 */
  barcodeContent: string | null
  /** 标签状态 */
  labelStatus: LabelStatusValue
  /** 绑定类型 */
  bindType: number
  /** 打印状态 */
  printStatus: number
  /** 借出时间 */
  borrowTime: string | null
  /** 预计归还时间 */
  expectedReturn: string | null
  /** 创建时间 */
  createTime: string
}

// ==================== 扫码相关类型 ====================

/** 入库扫码请求（与后端InboundScanDto一致） */
export interface InboundScanDto {
  /** 扫码内容（二维码/条形码/RFID码） */
  code: string
  /** 当前已添加的标签ID列表（用于重复检测） */
  currentLabelIds: number[]
}

/** 出库扫码请求（与后端OutboundScanDto一致） */
export interface OutboundScanDto {
  /** 扫码内容 */
  code: string
  /** 当前已添加的标签ID列表 */
  currentLabelIds: number[]
}

/** 入库扫码结果（与后端InboundScanResultVo一致） */
export interface InboundScanResultVo {
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
  /** 明细信息 */
  detail: {
    /** 物品ID */
    itemId: number
    /** 数量 */
    quantity: number
  }
}

/** 出库扫码结果（与后端OutboundScanResultVo一致） */
export interface OutboundScanResultVo {
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
  /** 明细信息 */
  detail: {
    /** 物品ID */
    itemId: number
    /** 数量 */
    quantity: number
  }
}

/** 扫码明细项 */
export interface ScanDetailItem {
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
  /** 数量 */
  quantity: number
}

// ==================== 单据相关类型 ====================

/** 单据分页查询参数 */
export interface OrderPageDto {
  /** 页码，默认1 */
  current?: number
  /** 每页条数，默认10 */
  size?: number
  /** 状态筛选 */
  status?: number
  /** 库房筛选 */
  warehouseId?: number
  /** 关键词搜索（单号/物品） */
  keyword?: string
}

/** 入库单创建请求 */
export interface InboundOrderCreateDto {
  /** 库房ID */
  warehouseId: number
  /** 供应商ID */
  supplierId?: number
  /** 单据类型 */
  orderType: number
  /** 备注 */
  remark?: string
  /** 明细列表 */
  details: InboundDetailCreateDto[]
}

/** 入库明细创建请求 */
export interface InboundDetailCreateDto {
  /** 物品ID */
  itemId: number
  /** 数量 */
  quantity: number
  /** 库位ID */
  binId?: number
  /** 单价 */
  unitPrice?: number
}

/** 出库单创建请求 */
export interface OutboundOrderCreateDto {
  /** 库房ID */
  warehouseId: number
  /** 单据类型 */
  orderType: number
  /** 领用人 */
  receiver?: string
  /** 用途 */
  purpose?: string
  /** 预计归还日期 */
  expectedReturnDate?: string
  /** 备注 */
  remark?: string
  /** 明细列表 */
  details: OutboundDetailCreateDto[]
}

/** 出库明细创建请求 */
export interface OutboundDetailCreateDto {
  /** 物品ID */
  itemId: number
  /** 数量 */
  quantity: number
  /** 库位ID */
  binId?: number
}

/** 归还单创建请求 */
export interface ReturnOrderCreateDto {
  /** 关联出库单ID */
  outboundOrderId?: number
  /** 归还人 */
  receiver?: string
  /** 备注 */
  remark?: string
  /** 明细列表 */
  details: ReturnDetailCreateDto[]
}

/** 归还明细创建请求 */
export interface ReturnDetailCreateDto {
  /** 物品ID */
  itemId: number
  /** 数量 */
  quantity: number
  /** 物品状态 */
  conditionStatus?: number
  /** 异常备注 */
  abnormalRemark?: string
}

/** 调拨单创建请求 */
export interface TransferOrderCreateDto {
  /** 源库房ID */
  fromWarehouseId: number
  /** 目标库房ID */
  toWarehouseId: number
  /** 备注 */
  remark?: string
  /** 明细列表 */
  details: TransferDetailCreateDto[]
}

/** 调拨明细创建请求 */
export interface TransferDetailCreateDto {
  /** 物品ID */
  itemId: number
  /** 数量 */
  quantity: number
}

/** 报废单创建请求 */
export interface ScrapOrderCreateDto {
  /** 库房ID */
  warehouseId: number
  /** 报废原因 */
  scrapReason: string
  /** 明细列表 */
  details: ScrapDetailCreateDto[]
}

/** 报废明细创建请求 */
export interface ScrapDetailCreateDto {
  /** 物品ID */
  itemId: number
  /** 数量 */
  quantity: number
}

// ==================== 单据VO类型 ====================

/** 入库单（与后端InboundOrderVo一致） */
export interface InboundOrderVo {
  /** 单据ID */
  id: number
  /** 单据编号 */
  orderNo: string
  /** 库房ID */
  warehouseId: number
  /** 库房名称 */
  warehouseName: string
  /** 供应商ID */
  supplierId: number | null
  /** 供应商名称 */
  supplierName: string | null
  /** 单据类型 */
  orderType: number
  /** 单据状态 */
  status: OrderStatusValue
  /** 总金额 */
  totalAmount: number | null
  /** 备注 */
  remark: string | null
  /** 创建时间 */
  createTime: string
  /** 创建人 */
  createBy: string
  /** 明细列表 */
  details: InboundDetailVo[]
}

/** 入库明细VO */
export interface InboundDetailVo {
  /** 明细ID */
  id: number
  /** 物品ID */
  itemId: number
  /** 物品名称 */
  itemName: string
  /** 物品编码 */
  itemCode: string
  /** 数量 */
  quantity: number
  /** 单价 */
  unitPrice: number | null
  /** 金额 */
  amount: number | null
  /** 库位ID */
  binId: number | null
  /** 库位编码 */
  binCode: string | null
}

/** 出库单（与后端OutboundOrderVo一致） */
export interface OutboundOrderVo {
  /** 单据ID */
  id: number
  /** 单据编号 */
  orderNo: string
  /** 库房ID */
  warehouseId: number
  /** 库房名称 */
  warehouseName: string
  /** 单据类型 */
  orderType: number
  /** 单据状态 */
  status: OrderStatusValue
  /** 领用人 */
  receiver: string | null
  /** 用途 */
  purpose: string | null
  /** 预计归还日期 */
  expectedReturnDate: string | null
  /** 备注 */
  remark: string | null
  /** 创建时间 */
  createTime: string
  /** 创建人 */
  createBy: string
  /** 明细列表 */
  details: OutboundDetailVo[]
}

/** 出库明细VO */
export interface OutboundDetailVo {
  /** 明细ID */
  id: number
  /** 物品ID */
  itemId: number
  /** 物品名称 */
  itemName: string
  /** 物品编码 */
  itemCode: string
  /** 数量 */
  quantity: number
  /** 库位ID */
  binId: number | null
  /** 库位编码 */
  binCode: string | null
}

/** 归还单（与后端ReturnOrderVo一致） */
export interface ReturnOrderVo {
  /** 单据ID */
  id: number
  /** 单据编号 */
  orderNo: string
  /** 关联出库单ID */
  outboundOrderId: number | null
  /** 关联出库单编号 */
  outboundOrderNo: string | null
  /** 单据状态 */
  status: number
  /** 归还人 */
  receiver: string | null
  /** 备注 */
  remark: string | null
  /** 创建时间 */
  createTime: string
  /** 创建人 */
  createBy: string
  /** 明细列表 */
  details: ReturnDetailVo[]
}

/** 归还明细VO */
export interface ReturnDetailVo {
  /** 明细ID */
  id: number
  /** 物品ID */
  itemId: number
  /** 物品名称 */
  itemName: string
  /** 物品编码 */
  itemCode: string
  /** 数量 */
  quantity: number
  /** 物品状态 */
  conditionStatus: number | null
  /** 异常备注 */
  abnormalRemark: string | null
  /** 实际归还数量 */
  actualQuantity: number | null
}

/** 调拨单（与后端TransferOrderVo一致） */
export interface TransferOrderVo {
  /** 单据ID */
  id: number
  /** 单据编号 */
  orderNo: string
  /** 源库房ID */
  fromWarehouseId: number
  /** 源库房名称 */
  fromWarehouseName: string
  /** 目标库房ID */
  toWarehouseId: number
  /** 目标库房名称 */
  toWarehouseName: string
  /** 单据状态 */
  status: OrderStatusValue
  /** 备注 */
  remark: string | null
  /** 创建时间 */
  createTime: string
  /** 创建人 */
  createBy: string
  /** 明细列表 */
  details: TransferDetailVo[]
}

/** 调拨明细VO */
export interface TransferDetailVo {
  /** 明细ID */
  id: number
  /** 物品ID */
  itemId: number
  /** 物品名称 */
  itemName: string
  /** 物品编码 */
  itemCode: string
  /** 数量 */
  quantity: number
}

/** 报废单（与后端ScrapOrderVo一致） */
export interface ScrapOrderVo {
  /** 单据ID */
  id: number
  /** 单据编号 */
  orderNo: string
  /** 库房ID */
  warehouseId: number
  /** 库房名称 */
  warehouseName: string
  /** 单据状态 */
  status: OrderStatusValue
  /** 报废原因 */
  scrapReason: string
  /** 创建时间 */
  createTime: string
  /** 创建人 */
  createBy: string
  /** 明细列表 */
  details: ScrapDetailVo[]
}

/** 报废明细VO */
export interface ScrapDetailVo {
  /** 明细ID */
  id: number
  /** 物品ID */
  itemId: number
  /** 物品名称 */
  itemName: string
  /** 物品编码 */
  itemCode: string
  /** 数量 */
  quantity: number
}

// ==================== 库存相关类型 ====================

/** 库存分页查询参数 */
export interface StockPageDto {
  /** 页码 */
  current?: number
  /** 每页条数 */
  size?: number
  /** 库房ID */
  warehouseId?: number
  /** 关键词搜索（物品名称/编码） */
  keyword?: string
}

/** 库存预警查询参数 */
export interface StockAlertDto {
  /** 页码 */
  current?: number
  /** 每页条数 */
  size?: number
  /** 库房ID */
  warehouseId?: number
}

/** 库存信息（与后端StockVo一致） */
export interface StockVo {
  /** 库存ID */
  id: number
  /** 物品ID */
  itemId: number
  /** 物品编码 */
  itemCode: string
  /** 物品名称 */
  itemName: string
  /** 库位ID */
  binId: number | null
  /** 库房ID */
  warehouseId: number
  /** 区域ID */
  areaId: number | null
  /** 柜子ID */
  cabinetId: number | null
  /** 库存数量 */
  quantity: number
  /** 锁定数量 */
  lockedQuantity: number
  /** 金额 */
  amount: number | null
  /** 最近入库时间 */
  lastInboundTime: string | null
  /** 最近出库时间 */
  lastOutboundTime: string | null
  /** 库存下限 */
  stockLowerLimit: number | null
  /** 库存上限 */
  stockUpperLimit: number | null
  /** 是否预警 */
  alert: boolean
}

/** 库存预警信息 */
export interface StockAlertVo {
  /** 预警ID */
  id: number
  /** 物品ID */
  itemId: number
  /** 物品编码 */
  itemCode: string
  /** 物品名称 */
  itemName: string
  /** 库房ID */
  warehouseId: number
  /** 库房名称 */
  warehouseName: string
  /** 当前库存数量 */
  quantity: number
  /** 库存下限 */
  stockLowerLimit: number
  /** 预警级别 */
  alertLevel: string
}

// ==================== 库房相关类型 ====================

/** 库房信息（与后端WarehouseVo一致） */
export interface WarehouseVo {
  /** 库房ID */
  id: number
  /** 库房名称 */
  warehouseName: string
  /** 库房编码 */
  warehouseCode: string
  /** 地址 */
  address: string | null
  /** 管理人 */
  manager: string | null
  /** 联系电话 */
  phone: string | null
  /** 面积 */
  area: number | null
  /** 状态：1-启用 0-禁用 */
  status: number
  /** 布局宽度 */
  layoutWidth: number | null
  /** 布局高度 */
  layoutHeight: number | null
  /** 布局比例 */
  layoutScale: number | null
  /** 布局背景版本 */
  layoutBackgroundVersion: string | null
  /** 备注 */
  remark: string | null
  /** 创建时间 */
  createTime: string
  /** 更新时间 */
  updateTime: string
}

// ==================== 物品相关类型 ====================

/** 物品分页查询参数 */
export interface ItemPageDto {
  /** 页码 */
  current?: number
  /** 每页条数 */
  size?: number
  /** 分类ID */
  categoryId?: number
  /** 关键词搜索 */
  keyword?: string
}

// ==================== PDA专用类型 ====================

/** RFID批量读取上报请求（与后端RfidBatchReadDto一致） */
export interface RfidBatchReadDto {
  /** 库房ID */
  warehouseId: number
  /** 区域ID */
  areaId?: number
  /** EPC编码列表 */
  epcCodes: string[]
  /** 读取耗时（秒） */
  readDuration?: number
}

/** RFID批量读取上报响应（与后端RfidBatchReadResultVo一致） */
export interface RfidBatchReadResultVo {
  /** 系统在库标签数量 */
  systemCount: number
  /** 实际读取标签数量 */
  actualCount: number
  /** 匹配数量 */
  matchCount: number
  /** 盘盈明细（实际有系统无） */
  surplusDetails: StockCheckDiffDetailVo[]
  /** 盘亏明细（系统有实际无） */
  deficitDetails: StockCheckDiffDetailVo[]
}

/** 盘点差异明细（与后端StockCheckDiffDetailVo一致） */
export interface StockCheckDiffDetailVo {
  /** 标签编号 */
  labelNo: string
  /** 物品ID */
  itemId: number
  /** 物品名称 */
  itemName: string
  /** 物品编码 */
  itemCode: string
  /** 差异类型：surplus-盘盈 / deficit-盘亏 */
  diffType: string
  /** 系统数量 */
  systemQty: number
  /** 实际数量 */
  actualQty: number
}

/** 盘点提交请求（与后端StockCheckDto一致） */
export interface StockCheckDto {
  /** 库房ID */
  warehouseId: number
  /** 区域ID */
  areaId?: number
  /** 盘点类型 */
  checkType: number
  /** 盘点明细列表 */
  items: StockCheckDetailDto[]
  /** 备注 */
  remark?: string
}

/** 盘点明细请求（与后端StockCheckDetailDto一致） */
export interface StockCheckDetailDto {
  /** 物品ID */
  itemId: number
  /** 系统数量 */
  systemQty: number
  /** 实际数量 */
  actualQty: number
  /** 库位ID */
  binId?: number
}

/** 盘点提交响应（与后端StockCheckResultVo一致） */
export interface StockCheckResultVo {
  /** 盘点单ID */
  checkId: number
  /** 盘点单状态 */
  status: number
}

/** PDA待办任务统计（与后端PdaTaskVo一致） */
export interface PdaTaskVo {
  /** 待入库数量 */
  pendingInbound: number
  /** 待出库数量 */
  pendingOutbound: number
  /** 待归还数量 */
  pendingReturn: number
  /** 待审批数量 */
  pendingApproval: number
  /** 库存预警数量 */
  stockAlert: number
  /** 逾期未还数量 */
  overdueReturn: number
}

// ==================== 离线队列类型 ====================

/** 离线队列记录 */
export interface OfflineQueueItem {
  /** 本地自增主键 */
  id: number
  /** 请求URL */
  url: string
  /** HTTP方法 */
  method: 'POST' | 'PUT' | 'PATCH'
  /** 请求体JSON字符串 */
  body: string
  /** 入队时间戳（毫秒） */
  timestamp: number
  /** 重试次数 */
  retryCount: number
  /** 同步状态 */
  status: OfflineQueueStatusValue
  /** 失败原因 */
  errorMsg: string
}

// ==================== RFID原生插件类型 ====================

/** RFID读取参数 */
export interface RfidReadOptions {
  /** 读取功率（5-30），默认30 */
  power: number
  /** 单次读取超时（毫秒），默认0（不限时） */
  timeout: number
  /** 是否重复读取同一标签，默认false */
  repeat: boolean
}

/** RFID原生插件接口 */
export interface WmsRfidPlugin {
  /**
   * 初始化RFID读写模块
   * @param callback 回调：{success: boolean, msg: string}
   */
  init(callback: (result: { success: boolean; msg: string }) => void): void

  /**
   * 检测RFID硬件是否可用
   * @param callback 回调：{available: boolean, msg: string}
   */
  checkHardware(callback: (result: { available: boolean; msg: string }) => void): void

  /**
   * 开始批量读取
   * @param options 读取参数
   * @param onRead 实时读取回调
   * @param onComplete 读取完成回调
   */
  startBatchRead(
    options: RfidReadOptions,
    onRead: (epcCode: string) => void,
    onComplete: (totalCount: number) => void
  ): void

  /** 停止批量读取 */
  stopBatchRead(): void

  /**
   * 设置读取功率
   * @param power 功率值（5-30）
   */
  setPower(power: number): void

  /** 释放RFID资源 */
  release(): void
}