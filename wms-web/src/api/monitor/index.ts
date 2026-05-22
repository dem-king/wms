import { get, put } from '../request'
import type { StockAlertVo, OverdueReturnVo } from '@/types/monitor'

export function getStockAlertPage(params: any) { return get<StockAlertVo>('/monitor/stock-alert', params) }
export function resolveStockAlert(id: number) { return put(`/monitor/stock-alert/${id}/resolve`) }

export function getOverdueReturnPage(params: any) { return get<OverdueReturnVo>('/monitor/overdue-return', params) }
