import { get, post, put, del } from '../request'
import type { WmsTagVo, WmsTagDto } from '@/types/item'

export function getTagList() {
  return get<WmsTagVo[]>('/item/tags')
}

export function addTag(data: WmsTagDto) {
  return post<WmsTagVo>('/item/tags', data)
}

export function updateTag(id: number, data: WmsTagDto) {
  return put<WmsTagVo>(`/item/tags/${id}`, data)
}

export function deleteTag(id: number) {
  return del<void>(`/item/tags/${id}`)
}
