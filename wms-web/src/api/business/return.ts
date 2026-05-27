import { get, post, put, del } from '../request'
import type { EntityId, ReturnOrderVo, ReturnOrderDto } from '@/types/business'
import type { PageResult, PageParams } from '@/types/system'

export function getReturnOrders(params?: PageParams & { orderNo?: string; status?: string }) {
  return get<PageResult<ReturnOrderVo>>('/return', params as unknown as Record<string, unknown>)
}

export function getReturnOrder(id: EntityId) {
  return get<ReturnOrderVo>(`/return/${id}`)
}

export function addReturnOrder(data: ReturnOrderDto) {
  return post<ReturnOrderVo>('/return', data)
}

export function updateReturnOrder(id: EntityId, data: ReturnOrderDto) {
  return put<ReturnOrderVo>(`/return/${id}`, data)
}

export function deleteReturnOrder(id: EntityId) {
  return del<void>(`/return/${id}`)
}

export function submitReturnOrder(id: EntityId) {
  return post<void>(`/return/${id}/submit`)
}
