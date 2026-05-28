import { get, post, put, del } from '../request'
import type { EntityId, WmsWarehouseVo, WmsWarehouseDto } from '@/types/warehouse'
import type { PageParams, PageResult } from '@/types/system'

export function getWarehouseList() {
  return get<WmsWarehouseVo[]>('/warehouse/warehouses/list')
}

export function getWarehousePage(params: PageParams & { status?: number; keyword?: string }) {
  return get<PageResult<WmsWarehouseVo>>('/warehouse/warehouses', params as unknown as Record<string, unknown>)
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
