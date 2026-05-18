import { get, post, put, del } from '../request'
import type { InboundOrderVo, InboundOrderDto } from '@/types/business'
import type { PageResult, PageParams } from '@/types/system'

export function getInboundOrders(params?: PageParams & { orderNo?: string; status?: string; startTime?: string; endTime?: string }) {
  return get<PageResult<InboundOrderVo>>('/inbound/orders', params as unknown as Record<string, unknown>)
}

export function getInboundOrder(id: number) {
  return get<InboundOrderVo>(`/inbound/orders/${id}`)
}

export function addInboundOrder(data: InboundOrderDto) {
  return post<InboundOrderVo>('/inbound/orders', data)
}

export function updateInboundOrder(id: number, data: InboundOrderDto) {
  return put<InboundOrderVo>(`/inbound/orders/${id}`, data)
}

export function submitInboundOrder(id: number) {
  return put<void>(`/inbound/orders/${id}/submit`)
}

export function deleteInboundOrder(id: number) {
  return del<void>(`/inbound/orders/${id}`)
}

