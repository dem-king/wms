import { get, post, put, del } from '../request'
import type { WmsItemVo, WmsItemDto } from '@/types/item'
import type { PageResult, PageParams } from '@/types/system'

export function getItemList(params?: PageParams & { itemCode?: string; itemName?: string; categoryId?: number; status?: number }) {
  return get<PageResult<WmsItemVo>>('/item/items', params as unknown as Record<string, unknown>)
}

export function getItem(id: number) {
  return get<WmsItemVo>(`/item/items/${id}`)
}

export function addItem(data: WmsItemDto) {
  return post<WmsItemVo>('/item/items', data)
}

export function updateItem(id: number, data: WmsItemDto) {
  return put<WmsItemVo>(`/item/items/${id}`, data)
}

export function deleteItem(id: number) {
  return del<void>(`/item/items/${id}`)
}

export function searchItem(keyword: string) {
  return get<WmsItemVo[]>('/item/items/search', { keyword })
}

export function uploadItemImage(itemId: number, file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return post<any>(`/item/items/${itemId}/images`, formData)
}

export function deleteItemImage(itemId: number, imageId: number) {
  return del<void>(`/item/items/${itemId}/images/${imageId}`)
}
