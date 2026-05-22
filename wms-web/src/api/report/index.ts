import { get, post, put } from '../request'
import type {
  InboundSummaryVo, InboundTrendVo, InboundDistributionVo,
  OutboundSummaryVo, OutboundTrendVo, OutboundDistributionVo,
  StockSummaryVo, StockTrendVo, StockDistributionVo,
  ReturnSummaryVo, ReturnTrendVo, ReturnDistributionVo,
  ScrapSummaryVo, ScrapTrendVo, ScrapDistributionVo,
  TransferSummaryVo, TransferTrendVo, TransferDistributionVo,
  AlertSummaryVo, AlertTrendVo, AlertDistributionVo,
  ChartOptionVo, CostAccountVo,
  ReportQueryDto, TransferReportQueryDto, AlertReportQueryDto,
  ChartQueryDto, ExportQueryDto, CostAccountConfigDto
} from '@/types/report'

export function getInboundSummary(params: ReportQueryDto) { return get<InboundSummaryVo>('/report/inbound/summary', params) }
export function getInboundTrend(params: ReportQueryDto) { return get<InboundTrendVo>('/report/inbound/trend', params) }
export function getInboundCategoryDistribution(params: ReportQueryDto) { return get<InboundDistributionVo>('/report/inbound/category-distribution', params) }

export function getOutboundSummary(params: ReportQueryDto) { return get<OutboundSummaryVo>('/report/outbound/summary', params) }
export function getOutboundTrend(params: ReportQueryDto) { return get<OutboundTrendVo>('/report/outbound/trend', params) }
export function getOutboundCategoryDistribution(params: ReportQueryDto) { return get<OutboundDistributionVo>('/report/outbound/category-distribution', params) }

export function getStockSummary(params: ReportQueryDto) { return get<StockSummaryVo>('/report/stock/summary', params) }
export function getStockTrend(params: ReportQueryDto) { return get<StockTrendVo>('/report/stock/trend', params) }
export function getStockCategoryDistribution(params: ReportQueryDto) { return get<StockDistributionVo>('/report/stock/category-distribution', params) }

export function getReturnSummary(params: ReportQueryDto) { return get<ReturnSummaryVo>('/report/return/summary', params) }
export function getReturnTrend(params: ReportQueryDto) { return get<ReturnTrendVo>('/report/return/trend', params) }
export function getReturnCategoryDistribution(params: ReportQueryDto) { return get<ReturnDistributionVo>('/report/return/category-distribution', params) }

export function getScrapSummary(params: ReportQueryDto) { return get<ScrapSummaryVo>('/report/scrap/summary', params) }
export function getScrapTrend(params: ReportQueryDto) { return get<ScrapTrendVo>('/report/scrap/trend', params) }
export function getScrapCategoryDistribution(params: ReportQueryDto) { return get<ScrapDistributionVo>('/report/scrap/category-distribution', params) }

export function getTransferSummary(params: TransferReportQueryDto) { return get<TransferSummaryVo>('/report/transfer/summary', params) }
export function getTransferTrend(params: TransferReportQueryDto) { return get<TransferTrendVo>('/report/transfer/trend', params) }
export function getTransferCategoryDistribution(params: TransferReportQueryDto) { return get<TransferDistributionVo>('/report/transfer/category-distribution', params) }

export function getAlertSummary(params: AlertReportQueryDto) { return get<AlertSummaryVo>('/report/alert/summary', params) }
export function getAlertTrend(params: AlertReportQueryDto) { return get<AlertTrendVo>('/report/alert/trend', params) }
export function getAlertCategoryDistribution(params: AlertReportQueryDto) { return get<AlertDistributionVo>('/report/alert/category-distribution', params) }

export function getChartData(data: ChartQueryDto) { return post<ChartOptionVo>('/report/chart', data) }

export function getCostSummary(params: { year: number }) { return get<CostAccountVo>('/report/cost/summary', params) }
export function getCostConfig() { return get('/report/cost/config') }
export function updateCostConfig(data: CostAccountConfigDto) { return put('/report/cost/config', data) }

export function exportReportExcel(data: ExportQueryDto) { return post('/report/export/excel', data, { responseType: 'blob' }) }
export function exportReportPdf(data: ExportQueryDto) { return post('/report/export/pdf', data, { responseType: 'blob' }) }
