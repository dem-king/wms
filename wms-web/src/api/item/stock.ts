import { get } from '../request'
import type { WmsStockVo } from '@/types/item'
import type { PageResult, PageParams } from '@/types/system'

export function getStockList(params?: PageParams & { itemName?: string; warehouseId?: number; isAlert?: boolean }) {
  return get<PageResult<WmsStockVo>>('/stock', params as unknown as Record<string, unknown>)
}

export function getStockByItemId(itemId: number) {
  return get<WmsStockVo[]>(`/stock/item/${itemId}`)
}

export function getAlertList() {
  return get<WmsStockVo[]>('/stock/alert')
}

