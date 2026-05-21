import { get, post, put, del } from '../request'
import type { TransferOrderVo, TransferOrderDto } from '@/types/business'
import type { PageResult, PageParams } from '@/types/system'

export function getTransferOrders(params?: PageParams & { orderNo?: string; status?: string }) {
  return get<PageResult<TransferOrderVo>>('/transfer', params as unknown as Record<string, unknown>)
}

export function getTransferOrder(id: number) {
  return get<TransferOrderVo>(`/transfer/${id}`)
}

export function addTransferOrder(data: TransferOrderDto) {
  return post<TransferOrderVo>('/transfer', data)
}

export function updateTransferOrder(id: number, data: TransferOrderDto) {
  return put<TransferOrderVo>(`/transfer/${id}`, data)
}

export function deleteTransferOrder(id: number) {
  return del<void>(`/transfer/${id}`)
}

export function submitTransferOrder(id: number) {
  return post<void>(`/transfer/${id}/submit`)
}
