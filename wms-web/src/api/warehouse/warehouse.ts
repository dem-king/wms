import { get, post, put, del } from '../request'
import type { WmsWarehouseVo, WmsWarehouseDto } from '@/types/warehouse'

export function getWarehouseList() {
  return get<WmsWarehouseVo[]>('/warehouse/warehouses')
}

export function addWarehouse(data: WmsWarehouseDto) {
  return post<WmsWarehouseVo>('/warehouse/warehouses', data)
}

export function updateWarehouse(id: number, data: WmsWarehouseDto) {
  return put<WmsWarehouseVo>(`/warehouse/warehouses/${id}`, data)
}

export function deleteWarehouse(id: number) {
  return del<void>(`/warehouse/warehouses/${id}`)
}
