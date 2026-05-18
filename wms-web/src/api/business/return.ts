import { get, post } from '../request'
import type { ReturnOrderVo, ReturnOrderDto } from '@/types/business'
import type { PageResult, PageParams } from '@/types/system'

export function getReturnOrders(params?: PageParams & { orderNo?: string; status?: string }) {
  return get<PageResult<ReturnOrderVo>>('/return/orders', params as unknown as Record<string, unknown>)
}

export function getReturnOrder(id: number) {
  return get<ReturnOrderVo>(`/return/orders/${id}`)
}

export function addReturnOrder(data: ReturnOrderDto) {
  return post<ReturnOrderVo>('/return/orders', data)
}

