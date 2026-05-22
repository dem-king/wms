import { get, post, put, del } from '../request'
import type { EntityId, WmsWarehouseVo, WmsWarehouseDto } from '@/types/warehouse'

export function getWarehouseList() {
  return get<WmsWarehouseVo[]>('/warehouse/warehouses/list')
}

export function addWarehouse(data: WmsWarehouseDto) {
  return post<WmsWarehouseVo>('/warehouse/warehouses', data)
}

export function updateWarehouse(id: EntityId, data: WmsWarehouseDto) {
  return put<WmsWarehouseVo>(`/warehouse/warehouses/${id}`, data)
}

export function deleteWarehouse(id: EntityId) {
  return del<void>(`/warehouse/warehouses/${id}`)
}
