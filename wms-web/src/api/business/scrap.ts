import { get, post, put, del } from '../request'
import type { EntityId, OrderStatus, ScrapOrderVo, ScrapOrderDto } from '@/types/business'
import type { PageResult, PageParams } from '@/types/system'
import { normalizeOrderStatus, orderStatusToCode } from '@/constants/order-status'

function normalizeScrapOrder(data?: Partial<ScrapOrderVo>): ScrapOrderVo {
  return {
    ...(data as ScrapOrderVo),
    status: normalizeOrderStatus(data?.status),
  }
}

export function getScrapOrders(params?: PageParams & { orderNo?: string; status?: OrderStatus | string }) {
  const query = params
    ? {
        ...params,
        status: orderStatusToCode(params.status),
      }
    : undefined

  return get<PageResult<ScrapOrderVo>>('/scrap', query as unknown as Record<string, unknown>)
    .then((res) => ({
      ...res,
      data: {
        ...res.data,
        records: (res.data?.records || []).map(normalizeScrapOrder),
      },
    }))
}

export function getScrapOrder(id: EntityId) {
  return get<ScrapOrderVo>(`/scrap/${id}`)
    .then((res) => ({
      ...res,
      data: normalizeScrapOrder(res.data),
    }))
}

export function addScrapOrder(data: ScrapOrderDto) {
  return post<ScrapOrderVo>('/scrap', data)
    .then((res) => ({
      ...res,
      data: normalizeScrapOrder(res.data),
    }))
}

export function updateScrapOrder(id: EntityId, data: ScrapOrderDto) {
  return put<ScrapOrderVo>(`/scrap/${id}`, data)
    .then((res) => ({
      ...res,
      data: normalizeScrapOrder(res.data),
    }))
}

export function deleteScrapOrder(id: EntityId) {
  return del<void>(`/scrap/${id}`)
}

export function submitScrapOrder(id: EntityId) {
  return post<void>(`/scrap/${id}/submit`)
}
