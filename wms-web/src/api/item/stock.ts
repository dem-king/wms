import { get, put } from '../request'
import type { WmsStockVo } from '@/types/item'
import type { PageResult, PageParams } from '@/types/system'

export function getStockList(params?: PageParams & { itemName?: string; warehouseId?: number; isAlert?: boolean }) {
  return get<PageResult<WmsStockVo>>('/item/stock', params as unknown as Record<string, unknown>)
}

export function getStockByItemId(itemId: number) {
  return get<WmsStockVo[]>(`/item/stock/${itemId}`)
}

export function getAlertList() {
  return get<WmsStockVo[]>('/item/stock/alert')
}

export function updateThreshold(itemId: number, data: { stockLowerLimit?: number; stockUpperLimit?: number; replenishThreshold?: number }) {
  return put<WmsStockVo>(`/item/stock/${itemId}/threshold`, data)
}
