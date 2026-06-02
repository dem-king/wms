/**
 * WMS-PDA 扫码识别接口
 * 封装入库扫码、出库扫码、标签查询扫码接口，超时5秒
 */
import type { InboundScanDto, InboundScanResultVo, OutboundScanDto, OutboundScanResultVo, ElectronicLabelVo } from '@/utils/constants'
import { RequestTimeout } from '@/utils/constants'
import { post, get } from '@/api/request'

/**
 * 入库扫码识别
 * POST /api/inbound/scan
 * 超时5秒
 *
 * @param data 入库扫码请求参数
 * @returns 入库扫码结果
 */
export function inboundScanApi(data: InboundScanDto): Promise<InboundScanResultVo> {
  return post<InboundScanResultVo>('/api/inbound/scan', data, RequestTimeout.SCAN)
}

/**
 * 出库扫码识别
 * POST /api/outbound/scan
 * 超时5秒
 *
 * @param data 出库扫码请求参数
 * @returns 出库扫码结果
 */
export function outboundScanApi(data: OutboundScanDto): Promise<OutboundScanResultVo> {
  return post<OutboundScanResultVo>('/api/outbound/scan', data, RequestTimeout.SCAN)
}

/**
 * 标签查询扫码
 * GET /api/labels/scan/{code}
 * 超时5秒
 *
 * @param code 扫码内容（标签编号/RFID码等）
 * @returns 电子标签信息
 */
export function labelScanApi(code: string): Promise<ElectronicLabelVo> {
  return get<ElectronicLabelVo>(`/api/labels/scan/${encodeURIComponent(code)}`, undefined, RequestTimeout.SCAN)
}