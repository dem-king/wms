/**
 * WMS-PDA 库存查询接口
 * 封装库存分页列表、库存预警、单个物品库存查询接口
 */
import type { PageResult, StockPageDto, StockVo, StockAlertDto, StockAlertVo } from '@/utils/constants'
import { get } from '@/api/request'

/**
 * 库存分页列表
 * GET /api/item/stock
 *
 * @param params 库存分页查询参数
 * @returns 库存分页结果
 */
export function getStockListApi(params: StockPageDto): Promise<PageResult<StockVo>> {
  return get<PageResult<StockVo>>('/api/item/stock', params as Record<string, unknown>)
}

/**
 * 库存预警列表
 * GET /api/item/stock/alert
 *
 * @param params 库存预警查询参数
 * @returns 库存预警分页结果
 */
export function getStockAlertApi(params: StockAlertDto): Promise<PageResult<StockAlertVo>> {
  return get<PageResult<StockAlertVo>>('/api/item/stock/alert', params as Record<string, unknown>)
}

/**
 * 单个物品库存
 * GET /api/item/stock/{itemId}
 *
 * @param itemId 物品ID
 * @returns 物品库存信息
 */
export function getStockByItemIdApi(itemId: number): Promise<StockVo> {
  return get<StockVo>(`/api/item/stock/${itemId}`)
}