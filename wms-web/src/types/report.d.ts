/** 报表通用类型 */

/** 分类汇总项 */
export interface CategorySummaryItem {
  /** 分类ID */
  categoryId: number
  /** 分类名称 */
  categoryName: string
  /** 数量 */
  quantity: number
  /** 金额 */
  amount: number
}

/** 趋势项 */
export interface TrendItem {
  /** 日期标识 */
  date: string
  /** 数量 */
  quantity: number
  /** 金额 */
  amount: number
}

/** 分布项 */
export interface DistributionItem {
  /** 分类ID */
  categoryId: number
  /** 分类名称 */
  categoryName: string
  /** 数量 */
  quantity: number
  /** 金额 */
  amount: number
  /** 占比(%) */
  percentage: number
}

/** 入库汇总统计 */
export interface InboundSummaryVo {
  totalQuantity: number
  totalAmount: number
  orderCount: number
  categorySummaryList: CategorySummaryItem[]
}

/** 入库趋势统计 */
export interface InboundTrendVo {
  trendType: string
  trendList: TrendItem[]
}

/** 入库分类分布统计 */
export interface InboundDistributionVo {
  distributionList: DistributionItem[]
}

/** 出库汇总统计 */
export interface OutboundSummaryVo {
  totalQuantity: number
  totalAmount: number
  orderCount: number
  categorySummaryList: CategorySummaryItem[]
}

/** 出库趋势统计 */
export interface OutboundTrendVo {
  trendType: string
  trendList: TrendItem[]
}

/** 出库分类分布统计 */
export interface OutboundDistributionVo {
  distributionList: DistributionItem[]
}

/** 库存汇总统计 */
export interface StockSummaryVo {
  totalQuantity: number
  totalAmount: number
  categorySummaryList: CategorySummaryItem[]
}

/** 库存趋势统计 */
export interface StockTrendVo {
  trendType: string
  trendList: TrendItem[]
}

/** 库存分类分布统计 */
export interface StockDistributionVo {
  distributionList: DistributionItem[]
}

/** 借还汇总统计 */
export interface ReturnSummaryVo {
  totalQuantity: number
  normalQuantity: number
  damagedQuantity: number
  normalRate: number
  orderCount: number
  categorySummaryList: CategorySummaryItem[]
}

/** 借还趋势统计 */
export interface ReturnTrendVo {
  trendType: string
  trendList: TrendItem[]
}

/** 借还分类分布统计 */
export interface ReturnDistributionVo {
  distributionList: DistributionItem[]
}

/** 报废汇总统计 */
export interface ScrapSummaryVo {
  totalQuantity: number
  totalAmount: number
  orderCount: number
  categorySummaryList: CategorySummaryItem[]
}

/** 报废趋势统计 */
export interface ScrapTrendVo {
  trendType: string
  trendList: TrendItem[]
}

/** 报废分类分布统计 */
export interface ScrapDistributionVo {
  distributionList: DistributionItem[]
}

/** 调拨分类汇总项 */
export interface TransferCategorySummaryItem {
  fromWarehouseId: number
  fromWarehouseName: string
  toWarehouseId: number
  toWarehouseName: string
  categoryId: number
  categoryName: string
  quantity: number
  amount: number
}

/** 调拨汇总统计 */
export interface TransferSummaryVo {
  totalQuantity: number
  totalAmount: number
  orderCount: number
  categorySummaryList: TransferCategorySummaryItem[]
}

/** 调拨趋势统计 */
export interface TransferTrendVo {
  trendType: string
  trendList: TrendItem[]
}

/** 调拨分类分布统计 */
export interface TransferDistributionVo {
  distributionList: DistributionItem[]
}

/** 预警汇总项 */
export interface AlertSummaryItem {
  alertType: string
  alertTypeName: string
  triggerCount: number
  affectedItemCount: number
  resolvedCount: number
}

/** 预警汇总统计 */
export interface AlertSummaryVo {
  totalTriggerCount: number
  totalAffectedItemCount: number
  totalResolvedCount: number
  alertSummaryList: AlertSummaryItem[]
}

/** 预警趋势项 */
export interface AlertTrendItem {
  date: string
  triggerCount: number
  affectedItemCount: number
}

/** 预警趋势统计 */
export interface AlertTrendVo {
  trendType: string
  trendList: AlertTrendItem[]
}

/** 预警类型分布项 */
export interface AlertDistributionItem {
  alertType: string
  alertTypeName: string
  triggerCount: number
  percentage: number
}

/** 预警类型分布统计 */
export interface AlertDistributionVo {
  distributionList: AlertDistributionItem[]
}

/** 图表选项数据 */
export interface ChartOptionVo {
  reportType: string
  chartType: string
  option: Record<string, any>
}

/** 周期费用明细项 */
export interface CostPeriodItem {
  periodLabel: string
  purchaseCost: number
  consumeCost: number
  scrapCost: number
  transferCost: number
  periodTotal: number
}

/** 年度费用核算结果 */
export interface CostAccountVo {
  enabled: boolean
  year: number
  period: string
  periodList: CostPeriodItem[]
  totalCost: number
}

/** 报表查询参数 */
export interface ReportQueryDto {
  startDate: string
  endDate: string
  warehouseId?: number
  categoryId?: number
  trendType?: string
}

/** 调拨报表查询参数 */
export interface TransferReportQueryDto {
  startDate: string
  endDate: string
  fromWarehouseId?: number
  toWarehouseId?: number
  categoryId?: number
  trendType?: string
}

/** 预警报表查询参数 */
export interface AlertReportQueryDto {
  startDate: string
  endDate: string
  warehouseId?: number
  alertType?: string
  trendType?: string
}

/** 图表查询参数 */
export interface ChartQueryDto {
  reportType: string
  chartType: string
  trendType?: string
  startDate: string
  endDate: string
  warehouseId?: number
  categoryId?: number
}

/** 报表导出参数 */
export interface ExportQueryDto {
  reportType: string
  exportType: string
  startDate: string
  endDate: string
  warehouseId?: number
  categoryId?: number
}

/** 费用核算配置参数 */
export interface CostAccountConfigDto {
  enabled: boolean
  year: number
  period: string
}
