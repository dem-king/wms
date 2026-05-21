import { get, post, put, del } from '../request'
import type { WmsBinVo, WmsBinDto, WmsBinBatchDto } from '@/types/warehouse'

export function getBinList(cabinetId: number) {
  return get<WmsBinVo[]>(`/warehouse/bins/cabinet/${cabinetId}`)
}

export function addBin(data: WmsBinDto) {
  return post<WmsBinVo>('/warehouse/bins', data)
}

export function updateBin(id: number, data: WmsBinDto) {
  return put<WmsBinVo>(`/warehouse/bins/${id}`, data)
}

export function deleteBin(id: number) {
  return del<void>(`/warehouse/bins/${id}`)
}

export function batchCreateBin(data: WmsBinBatchDto) {
  return post<WmsBinVo[]>('/warehouse/bins/batch', data)
}
