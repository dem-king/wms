import { get, post, put, del } from '../request'
import type { EntityId, WmsItemVo, WmsItemDto } from '@/types/item'
import type { PageResult, PageParams } from '@/types/system'

export function getItemList(params?: PageParams & { itemCode?: string; itemName?: string; categoryId?: EntityId; status?: number }) {
  return get<PageResult<WmsItemVo>>('/item/items', params as unknown as Record<string, unknown>)
}

export function getItem(id: EntityId) {
  return get<WmsItemVo>(`/item/items/${id}`)
}

export function addItem(data: WmsItemDto) {
  return post<WmsItemVo>('/item/items', data)
}

export function updateItem(id: EntityId, data: WmsItemDto) {
  return put<WmsItemVo>(`/item/items/${id}`, data)
}

export function deleteItem(id: EntityId) {
  return del<void>(`/item/items/${id}`)
}

export function searchItem(keyword: string) {
  return get<WmsItemVo[]>('/item/items/search', { keyword })
}

export function uploadItemImage(itemId: EntityId, file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return post<any>(`/item/items/${itemId}/images`, formData)
}

export function deleteItemImage(itemId: EntityId, imageId: EntityId) {
  return del<void>(`/item/items/${itemId}/images/${imageId}`)
}
