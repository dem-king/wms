import { get, post, put, del } from '../request'
import type { EntityId, ScrapOrderVo, ScrapOrderDto } from '@/types/business'
import type { PageResult, PageParams } from '@/types/system'

export function getScrapOrders(params?: PageParams & { orderNo?: string; status?: string }) {
  return get<PageResult<ScrapOrderVo>>('/scrap', params as unknown as Record<string, unknown>)
}

export function getScrapOrder(id: EntityId) {
  return get<ScrapOrderVo>(`/scrap/${id}`)
}

export function addScrapOrder(data: ScrapOrderDto) {
  return post<ScrapOrderVo>('/scrap', data)
}

export function updateScrapOrder(id: EntityId, data: ScrapOrderDto) {
  return put<ScrapOrderVo>(`/scrap/${id}`, data)
}

export function deleteScrapOrder(id: EntityId) {
  return del<void>(`/scrap/${id}`)
}

export function submitScrapOrder(id: EntityId) {
  return post<void>(`/scrap/${id}/submit`)
}
