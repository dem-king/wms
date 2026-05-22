import { get, post, put, del } from '../request'
import type { EntityId, WmsAreaVo, WmsAreaDto } from '@/types/warehouse'

export function getAreaList(warehouseId: EntityId) {
  return get<WmsAreaVo[]>(`/warehouse/areas/warehouse/${warehouseId}`)
}

export function addArea(data: WmsAreaDto) {
  return post<WmsAreaVo>('/warehouse/areas', data)
}

export function updateArea(id: EntityId, data: WmsAreaDto) {
  return put<WmsAreaVo>(`/warehouse/areas/${id}`, data)
}

export function deleteArea(id: EntityId) {
  return del<void>(`/warehouse/areas/${id}`)
}
