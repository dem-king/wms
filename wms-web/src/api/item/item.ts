import { get, post, put, del } from '../request'
import type { WmsItemVo, WmsItemDto } from '@/types/item'
import type { PageResult, PageParams } from '@/types/system'

export function getItemList(params?: PageParams & { itemCode?: string; itemName?: string; categoryId?: number; status?: number }) {
  return get<PageResult<WmsItemVo>>('/items', params as unknown as Record<string, unknown>)
}

export function getItem(id: number) {
  return get<WmsItemVo>(`/items/${id}`)
}

export function addItem(data: WmsItemDto) {
  return post<WmsItemVo>('/items', data)
}

export function updateItem(id: number, data: WmsItemDto) {
  return put<WmsItemVo>(`/items/${id}`, data)
}

export function deleteItem(id: number) {
  return del<void>(`/items/${id}`)
}

export function searchItem(keyword: string) {
  return get<WmsItemVo[]>('/items/search', { keyword })
}

