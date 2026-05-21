import { get, post, put, del } from '../request'
import type { WmsAreaVo, WmsAreaDto } from '@/types/warehouse'

export function getAreaList(warehouseId: number) {
  return get<WmsAreaVo[]>(`/warehouse/areas/warehouse/${warehouseId}`)
}

export function addArea(data: WmsAreaDto) {
  return post<WmsAreaVo>('/warehouse/areas', data)
}

export function updateArea(id: number, data: WmsAreaDto) {
  return put<WmsAreaVo>(`/warehouse/areas/${id}`, data)
}

export function deleteArea(id: number) {
  return del<void>(`/warehouse/areas/${id}`)
}
