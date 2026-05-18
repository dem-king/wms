import { get, post, put, del } from '../request'
import type { OutboundOrderVo, OutboundOrderDto } from '@/types/business'
import type { PageResult, PageParams } from '@/types/system'

export function getOutboundOrders(params?: PageParams & { orderNo?: string; status?: string; startTime?: string; endTime?: string }) {
  return get<PageResult<OutboundOrderVo>>('/outbound/orders', params as unknown as Record<string, unknown>)
}

export function getOutboundOrder(id: number) {
  return get<OutboundOrderVo>(`/outbound/orders/${id}`)
}

export function addOutboundOrder(data: OutboundOrderDto) {
  return post<OutboundOrderVo>('/outbound/orders', data)
}

export function updateOutboundOrder(id: number, data: OutboundOrderDto) {
  return put<OutboundOrderVo>(`/outbound/orders/${id}`, data)
}

export function submitOutboundOrder(id: number) {
  return put<void>(`/outbound/orders/${id}/submit`)
}

export function deleteOutboundOrder(id: number) {
  return del<void>(`/outbound/orders/${id}`)
}

