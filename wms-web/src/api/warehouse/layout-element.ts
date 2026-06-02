/**
 * 库房布局元素API接口
 * 对应后端 LayoutElementController / WarehouseController / AreaController
 */

import { get, post, put, del } from '../request'
import type { EntityId } from '@/types/warehouse'
import type {
  LayoutElementVo,
  LayoutElementDto,

  LayoutElementBatchSaveDto,
  LayoutElementBatchSaveVo,
  WarehouseBackgroundVo,
  AreaLayoutBatchDto,
} from '@/views/warehouse/visual/types/layout-element'

/**
 * 按库房ID查询布局元素列表
 * @param warehouseId 库房ID
 * @returns 布局元素列表
 */
export function getLayoutElementList(warehouseId: EntityId) {
  return get<LayoutElementVo[]>('/warehouse/layout-elements', { warehouseId })
}

/**
 * 新增布局元素
 * @param data 布局元素DTO
 * @returns 新增后的布局元素VO
 */
export function addLayoutElement(data: LayoutElementDto) {
  return post<LayoutElementVo>('/warehouse/layout-elements', data)
}

/**
 * 更新布局元素
 * @param id 元素ID
 * @param data 布局元素DTO
 * @returns 更新后的布局元素VO
 */
export function updateLayoutElement(id: EntityId, data: LayoutElementDto) {
  return put<LayoutElementVo>(`/warehouse/layout-elements/${id}`, data)
}

/**
 * 删除布局元素（逻辑删除）
 * @param id 元素ID
 */
export function deleteLayoutElement(id: EntityId) {
  return del<void>(`/warehouse/layout-elements/${id}`)
}

/**
 * 批量保存布局元素变更
 * @param data 批量保存DTO
 * @returns 批量保存结果VO
 */
export function batchSaveLayoutElements(data: LayoutElementBatchSaveDto) {
  return post<LayoutElementBatchSaveVo>('/warehouse/layout-elements/batch-save', data)
}

/**
 * 上传库房底图文件
 * @param warehouseId 库房ID
 * @param file 底图文件（JPG/PNG/SVG，≤10MB）
 * @returns 底图上传结果VO
 */
export function uploadWarehouseBackground(warehouseId: EntityId, file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return post<WarehouseBackgroundVo>(`/warehouse/warehouses/${warehouseId}/background`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

/**
 * 获取库房底图文件URL（后端代理MinIO）
 * @param warehouseId 库房ID
 * @returns 底图访问URL
 */
export function getWarehouseBackgroundUrl(warehouseId: EntityId, version?: string) {
  const base = `/api/warehouse/warehouses/${warehouseId}/background`
  // 添加版本号参数避免浏览器缓存
  return version ? `${base}?v=${version}` : base
}

/**
 * 删除库房底图引用
 * @param warehouseId 库房ID
 */
export function deleteWarehouseBackground(warehouseId: EntityId) {
  return del<void>(`/warehouse/warehouses/${warehouseId}/background`)
}

/**
 * 批量更新区域坐标
 * @param data 区域布局坐标批量DTO
 * @returns 更新成功的数量
 */
export function updateAreaLayoutCoordinates(data: AreaLayoutBatchDto) {
  return put<number>('/warehouse/areas/layout-coordinates', data)
}