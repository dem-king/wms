/**
 * WMS-PDA 库房接口
 * 封装库房列表查询接口
 */
import type { WarehouseVo } from '@/utils/constants'
import { get } from '@/api/request'

/**
 * 获取库房列表
 * GET /api/warehouses
 *
 * @returns 库房列表
 */
export function getWarehouseListApi(): Promise<WarehouseVo[]> {
  return get<WarehouseVo[]>('/api/warehouses')
}