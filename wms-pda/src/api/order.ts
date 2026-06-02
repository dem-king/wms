/**
 * WMS-PDA 单据管理接口
 * 封装五种单据（入库/出库/归还/调拨/报废）的CRUD接口
 * 每种单据：列表(GET)、创建(POST)、详情(GET /{id})、提交(POST /{id}/submit)
 */
import type {
  PageResult,
  OrderPageDto,
  InboundOrderVo,
  InboundOrderCreateDto,
  OutboundOrderVo,
  OutboundOrderCreateDto,
  ReturnOrderVo,
  ReturnOrderCreateDto,
  TransferOrderVo,
  TransferOrderCreateDto,
  ScrapOrderVo,
  ScrapOrderCreateDto
} from '@/utils/constants'
import { get, post } from '@/api/request'

// ==================== 入库单接口 ====================

/**
 * 入库单分页列表
 * GET /api/inbound
 *
 * @param params 分页查询参数
 * @returns 入库单分页结果
 */
export function getInboundListApi(params: OrderPageDto): Promise<PageResult<InboundOrderVo>> {
  return get<PageResult<InboundOrderVo>>('/api/inbound', params as Record<string, unknown>)
}

/**
 * 创建入库单
 * POST /api/inbound
 *
 * @param data 入库单创建请求
 * @returns 创建后的入库单
 */
export function createInboundApi(data: InboundOrderCreateDto): Promise<InboundOrderVo> {
  return post<InboundOrderVo>('/api/inbound', data as Record<string, unknown>)
}

/**
 * 入库单详情
 * GET /api/inbound/{id}
 *
 * @param id 入库单ID
 * @returns 入库单详情
 */
export function getInboundDetailApi(id: number): Promise<InboundOrderVo> {
  return get<InboundOrderVo>(`/api/inbound/${id}`)
}

/**
 * 提交入库单
 * POST /api/inbound/{id}/submit
 *
 * @param id 入库单ID
 * @returns 提交后的入库单
 */
export function submitInboundApi(id: number): Promise<InboundOrderVo> {
  return post<InboundOrderVo>(`/api/inbound/${id}/submit`)
}

// ==================== 出库单接口 ====================

/**
 * 出库单分页列表
 * GET /api/outbound
 *
 * @param params 分页查询参数
 * @returns 出库单分页结果
 */
export function getOutboundListApi(params: OrderPageDto): Promise<PageResult<OutboundOrderVo>> {
  return get<PageResult<OutboundOrderVo>>('/api/outbound', params as Record<string, unknown>)
}

/**
 * 创建出库单
 * POST /api/outbound
 *
 * @param data 出库单创建请求
 * @returns 创建后的出库单
 */
export function createOutboundApi(data: OutboundOrderCreateDto): Promise<OutboundOrderVo> {
  return post<OutboundOrderVo>('/api/outbound', data as Record<string, unknown>)
}

/**
 * 出库单详情
 * GET /api/outbound/{id}
 *
 * @param id 出库单ID
 * @returns 出库单详情
 */
export function getOutboundDetailApi(id: number): Promise<OutboundOrderVo> {
  return get<OutboundOrderVo>(`/api/outbound/${id}`)
}

/**
 * 提交出库单
 * POST /api/outbound/{id}/submit
 *
 * @param id 出库单ID
 * @returns 提交后的出库单
 */
export function submitOutboundApi(id: number): Promise<OutboundOrderVo> {
  return post<OutboundOrderVo>(`/api/outbound/${id}/submit`)
}

// ==================== 归还单接口 ====================

/**
 * 归还单分页列表
 * GET /api/return
 *
 * @param params 分页查询参数
 * @returns 归还单分页结果
 */
export function getReturnListApi(params: OrderPageDto): Promise<PageResult<ReturnOrderVo>> {
  return get<PageResult<ReturnOrderVo>>('/api/return', params as Record<string, unknown>)
}

/**
 * 创建归还单
 * POST /api/return
 *
 * @param data 归还单创建请求
 * @returns 创建后的归还单
 */
export function createReturnApi(data: ReturnOrderCreateDto): Promise<ReturnOrderVo> {
  return post<ReturnOrderVo>('/api/return', data as Record<string, unknown>)
}

/**
 * 归还单详情
 * GET /api/return/{id}
 *
 * @param id 归还单ID
 * @returns 归还单详情
 */
export function getReturnDetailApi(id: number): Promise<ReturnOrderVo> {
  return get<ReturnOrderVo>(`/api/return/${id}`)
}

/**
 * 提交归还单
 * POST /api/return/{id}/submit
 *
 * @param id 归还单ID
 * @returns 提交后的归还单
 */
export function submitReturnApi(id: number): Promise<ReturnOrderVo> {
  return post<ReturnOrderVo>(`/api/return/${id}/submit`)
}

// ==================== 调拨单接口 ====================

/**
 * 调拨单分页列表
 * GET /api/transfer
 *
 * @param params 分页查询参数
 * @returns 调拨单分页结果
 */
export function getTransferListApi(params: OrderPageDto): Promise<PageResult<TransferOrderVo>> {
  return get<PageResult<TransferOrderVo>>('/api/transfer', params as Record<string, unknown>)
}

/**
 * 创建调拨单
 * POST /api/transfer
 *
 * @param data 调拨单创建请求
 * @returns 创建后的调拨单
 */
export function createTransferApi(data: TransferOrderCreateDto): Promise<TransferOrderVo> {
  return post<TransferOrderVo>('/api/transfer', data as Record<string, unknown>)
}

/**
 * 调拨单详情
 * GET /api/transfer/{id}
 *
 * @param id 调拨单ID
 * @returns 调拨单详情
 */
export function getTransferDetailApi(id: number): Promise<TransferOrderVo> {
  return get<TransferOrderVo>(`/api/transfer/${id}`)
}

/**
 * 提交调拨单
 * POST /api/transfer/{id}/submit
 *
 * @param id 调拨单ID
 * @returns 提交后的调拨单
 */
export function submitTransferApi(id: number): Promise<TransferOrderVo> {
  return post<TransferOrderVo>(`/api/transfer/${id}/submit`)
}

// ==================== 报废单接口 ====================

/**
 * 报废单分页列表
 * GET /api/scrap
 *
 * @param params 分页查询参数
 * @returns 报废单分页结果
 */
export function getScrapListApi(params: OrderPageDto): Promise<PageResult<ScrapOrderVo>> {
  return get<PageResult<ScrapOrderVo>>('/api/scrap', params as Record<string, unknown>)
}

/**
 * 创建报废单
 * POST /api/scrap
 *
 * @param data 报废单创建请求
 * @returns 创建后的报废单
 */
export function createScrapApi(data: ScrapOrderCreateDto): Promise<ScrapOrderVo> {
  return post<ScrapOrderVo>('/api/scrap', data as Record<string, unknown>)
}

/**
 * 报废单详情
 * GET /api/scrap/{id}
 *
 * @param id 报废单ID
 * @returns 报废单详情
 */
export function getScrapDetailApi(id: number): Promise<ScrapOrderVo> {
  return get<ScrapOrderVo>(`/api/scrap/${id}`)
}

/**
 * 提交报废单
 * POST /api/scrap/{id}/submit
 *
 * @param id 报废单ID
 * @returns 提交后的报废单
 */
export function submitScrapApi(id: number): Promise<ScrapOrderVo> {
  return post<ScrapOrderVo>(`/api/scrap/${id}/submit`)
}