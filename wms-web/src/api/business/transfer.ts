import { get, post, put } from '../request'
import type { TransferOrderVo, TransferOrderDto } from '@/types/business'
import type { PageResult, PageParams } from '@/types/system'

export function getTransferOrders(params?: PageParams & { orderNo?: string; status?: string }) {
  return get<PageResult<TransferOrderVo>>('/transfer/orders', params as unknown as Record<string, unknown>)
}

export function getTransferOrder(id: number) {
  return get<TransferOrderVo>(`/transfer/orders/${id}`)
}

export function addTransferOrder(data: TransferOrderDto) {
  return post<TransferOrderVo>('/transfer/orders', data)
}

export function submitTransferOrder(id: number) {
  return put<void>(`/transfer/orders/${id}/submit`)
}

