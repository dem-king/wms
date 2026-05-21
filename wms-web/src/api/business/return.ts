import { get, post, put, del } from '../request'
import type { ReturnOrderVo, ReturnOrderDto } from '@/types/business'
import type { PageResult, PageParams } from '@/types/system'

export function getReturnOrders(params?: PageParams & { orderNo?: string; status?: string }) {
  return get<PageResult<ReturnOrderVo>>('/return', params as unknown as Record<string, unknown>)
}

export function getReturnOrder(id: number) {
  return get<ReturnOrderVo>(`/return/${id}`)
}

export function addReturnOrder(data: ReturnOrderDto) {
  return post<ReturnOrderVo>('/return', data)
}

export function updateReturnOrder(id: number, data: ReturnOrderDto) {
  return put<ReturnOrderVo>(`/return/${id}`, data)
}

export function deleteReturnOrder(id: number) {
  return del<void>(`/return/${id}`)
}

export function submitReturnOrder(id: number) {
  return post<void>(`/return/${id}/submit`)
}
