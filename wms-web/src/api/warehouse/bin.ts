import { get, post, put, del } from '../request'
import type { EntityId, WmsBinVo, WmsBinDto, WmsBinBatchDto } from '@/types/warehouse'
import type { PageParams, PageResult } from '@/types/system'

export function getBinList(cabinetId: EntityId) {
  return get<WmsBinVo[]>(`/warehouse/bins/cabinet/${cabinetId}`)
}

export function getBinPage(params: PageParams & { cabinetId: EntityId }) {
  return get<PageResult<WmsBinVo>>('/warehouse/bins', params as unknown as Record<string, unknown>)
}

export function addBin(data: WmsBinDto) {
  return post<WmsBinVo>('/warehouse/bins', data)
}

export function updateBin(id: EntityId, data: WmsBinDto) {
  return put<WmsBinVo>(`/warehouse/bins/${id}`, data)
}

export function deleteBin(id: EntityId) {
  return del<void>(`/warehouse/bins/${id}`)
}

export function batchCreateBin(data: WmsBinBatchDto) {
  return post<WmsBinVo[]>('/warehouse/bins/batch', data)
}
