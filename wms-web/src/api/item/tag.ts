import { get, post, put, del } from '../request'
import type { WmsTagVo, WmsTagDto } from '@/types/item'

export function getTagList() {
  return get<WmsTagVo[]>('/tags')
}

export function addTag(data: WmsTagDto) {
  return post<WmsTagVo>('/tags', data)
}

export function updateTag(id: number, data: WmsTagDto) {
  return put<WmsTagVo>(`/tags/${id}`, data)
}

export function deleteTag(id: number) {
  return del<void>(`/tags/${id}`)
}
