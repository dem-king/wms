import { get, post, put, del } from '../request'
import type { PageParams, PageResult } from '@/types/system'
import type { WmsTagVo, WmsTagDto } from '@/types/item'

export function getTagPage(params: PageParams) {
  return get<PageResult<WmsTagVo>>('/item/tags', params)
}

export function getTagList() {
  return get<WmsTagVo[]>('/item/tags/list')
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
