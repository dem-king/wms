import { get, post, put, del } from '../request'
import type { EntityId, OrderStatus, TransferOrderVo, TransferOrderDto } from '@/types/business'
import type { PageResult, PageParams } from '@/types/system'
import { normalizeOrderStatus, orderStatusToCode } from '@/constants/order-status'

function normalizeTransferOrder(data?: Partial<TransferOrderVo>): TransferOrderVo {
  return {
    ...(data as TransferOrderVo),
    status: normalizeOrderStatus(data?.status),
  }
}

export function getTransferOrders(params?: PageParams & { orderNo?: string; status?: OrderStatus | string }) {
  const query = params
    ? {
        ...params,
        status: orderStatusToCode(params.status),
      }
    : undefined

  return get<PageResult<TransferOrderVo>>('/transfer', query as unknown as Record<string, unknown>)
    .then((res) => ({
      ...res,
      data: {
        ...res.data,
        records: (res.data?.records || []).map(normalizeTransferOrder),
      },
    }))
}

export function getTransferOrder(id: EntityId) {
  return get<TransferOrderVo>(`/transfer/${id}`)
    .then((res) => ({
      ...res,
      data: normalizeTransferOrder(res.data),
    }))
}

export function addTransferOrder(data: TransferOrderDto) {
  return post<TransferOrderVo>('/transfer', data)
    .then((res) => ({
      ...res,
      data: normalizeTransferOrder(res.data),
    }))
}

export function updateTransferOrder(id: EntityId, data: TransferOrderDto) {
  return put<TransferOrderVo>(`/transfer/${id}`, data)
    .then((res) => ({
      ...res,
      data: normalizeTransferOrder(res.data),
    }))
}

export function deleteTransferOrder(id: EntityId) {
  return del<void>(`/transfer/${id}`)
}

export function submitTransferOrder(id: EntityId) {
  return post<void>(`/transfer/${id}/submit`)
}
