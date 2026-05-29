import { get, post, put, del } from '../request'
import type { EntityId, WmsItemVo, WmsItemDto, ItemImageDto, ItemImageVo } from '@/types/item'
import type { PageResult, PageParams } from '@/types/system'

type ItemEntityId = EntityId | number

export function getItemList(params?: PageParams & { itemCode?: string; itemName?: string; categoryId?: ItemEntityId; status?: number }) {
  return get<PageResult<WmsItemVo>>('/item/items', params as unknown as Record<string, unknown>)
}

export function getItem(id: ItemEntityId) {
  return get<WmsItemVo>(`/item/items/${id}`)
}

export function addItem(data: WmsItemDto) {
  return post<WmsItemVo>('/item/items', data)
}

export function updateItem(id: ItemEntityId, data: WmsItemDto) {
  return put<WmsItemVo>(`/item/items/${id}`, data)
}

export function deleteItem(id: ItemEntityId) {
  return del<void>(`/item/items/${id}`)
}

export function searchItem(keyword: string) {
  return get<WmsItemVo[]>('/item/items/search', { keyword })
}

export function uploadItemImage(itemId: ItemEntityId, file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return post<ItemImageVo>(`/item/items/${itemId}/images`, formData)
}

export function attachItemImage(itemId: ItemEntityId, data: ItemImageDto) {
  return post<ItemImageVo>(`/item/items/${itemId}/images`, data)
}

export function deleteItemImage(itemId: ItemEntityId, imageId: ItemEntityId) {
  return del<void>(`/item/items/${itemId}/images/${imageId}`)
}
