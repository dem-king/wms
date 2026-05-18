import { get, post, put } from '../request'
import type { ScrapOrderVo, ScrapOrderDto } from '@/types/business'
import type { PageResult, PageParams } from '@/types/system'

export function getScrapOrders(params?: PageParams & { orderNo?: string; status?: string }) {
  return get<PageResult<ScrapOrderVo>>('/scrap/orders', params as unknown as Record<string, unknown>)
}

export function getScrapOrder(id: number) {
  return get<ScrapOrderVo>(`/scrap/orders/${id}`)
}

export function addScrapOrder(data: ScrapOrderDto) {
  return post<ScrapOrderVo>('/scrap/orders', data)
}

export function submitScrapOrder(id: number) {
  return put<void>(`/scrap/orders/${id}/submit`)
}

