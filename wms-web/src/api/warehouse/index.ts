import { get, post, put, del } from '../request'

export function getWarehouseList(params?: any) {
  return get('/warehouse/warehouses', params)
}

export function addWarehouse(data: any) {
  return post('/warehouse/warehouses', data)
}

export function updateWarehouse(id: number, data: any) {
  return put(`/warehouse/warehouses/${id}`, data)
}

export function deleteWarehouse(id: number) {
  return del(`/warehouse/warehouses/${id}`)
}

export function getAreaList(warehouseId: number) {
  return get('/warehouse/areas', { warehouseId })
}

export function getCabinetList(areaId: number) {
  return get('/warehouse/cabinets', { areaId })
}

export function getCabinetDetail(id: number) {
  return get(`/warehouse/cabinets/${id}`)
}

export function getWarehouseLayout(warehouseId: number) {
  return get(`/warehouse/visual/${warehouseId}/layout`)
}

export function getCabinetItems(warehouseId: number, cabinetId: number) {
  return get(`/warehouse/visual/${warehouseId}/cabinet/${cabinetId}/items`)
}

export function searchInVisual(keyword: string) {
  return get('/warehouse/visual/search', { keyword })
}
