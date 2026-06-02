import { get, post, put, del } from '../request'
import type { EntityId, OrderStatus, ReturnOrderVo, ReturnOrderDto } from '@/types/business'
import type { PageResult, PageParams } from '@/types/system'
import { normalizeOrderStatus, orderStatusToCode } from '@/constants/order-status'

function normalizeReturnOrder(data?: Partial<ReturnOrderVo>): ReturnOrderVo {
  return {
    ...(data as ReturnOrderVo),
    status: normalizeOrderStatus(data?.status),
  }
}

export function getReturnOrders(params?: PageParams & { orderNo?: string; status?: OrderStatus | string }) {
  const query = params
    ? {
        ...params,
        status: orderStatusToCode(params.status),
      }
    : undefined

  return get<PageResult<ReturnOrderVo>>('/return', query as unknown as Record<string, unknown>)
    .then((res) => ({
      ...res,
      data: {
        ...res.data,
        records: (res.data?.records || []).map(normalizeReturnOrder),
      },
    }))
}

export function getReturnOrder(id: EntityId) {
  return get<ReturnOrderVo>(`/return/${id}`)
    .then((res) => ({
      ...res,
      data: normalizeReturnOrder(res.data),
    }))
}

export function addReturnOrder(data: ReturnOrderDto) {
  return post<ReturnOrderVo>('/return', data)
    .then((res) => ({
      ...res,
      data: normalizeReturnOrder(res.data),
    }))
}

export function updateReturnOrder(id: EntityId, data: ReturnOrderDto) {
  return put<ReturnOrderVo>(`/return/${id}`, data)
    .then((res) => ({
      ...res,
      data: normalizeReturnOrder(res.data),
    }))
}

export function deleteReturnOrder(id: EntityId) {
  return del<void>(`/return/${id}`)
}

export function submitReturnOrder(id: EntityId) {
  return post<void>(`/return/${id}/submit`)
}
