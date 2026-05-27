import { get, post, put, del } from '../request'
import type { EntityId, TransferOrderVo, TransferOrderDto } from '@/types/business'
import type { PageResult, PageParams } from '@/types/system'

export function getTransferOrders(params?: PageParams & { orderNo?: string; status?: string }) {
  return get<PageResult<TransferOrderVo>>('/transfer', params as unknown as Record<string, unknown>)
}

export function getTransferOrder(id: EntityId) {
  return get<TransferOrderVo>(`/transfer/${id}`)
}

export function addTransferOrder(data: TransferOrderDto) {
  return post<TransferOrderVo>('/transfer', data)
}

export function updateTransferOrder(id: EntityId, data: TransferOrderDto) {
  return put<TransferOrderVo>(`/transfer/${id}`, data)
}

export function deleteTransferOrder(id: EntityId) {
  return del<void>(`/transfer/${id}`)
}

export function submitTransferOrder(id: EntityId) {
  return post<void>(`/transfer/${id}/submit`)
}
