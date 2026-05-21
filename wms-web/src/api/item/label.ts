import { get, post, put } from '../request'
import type { ElectronicLabelVo, LabelGenerateDto, LabelBindDto, LabelStatusDto, LabelBatchPrintDto, LabelScanResultVo } from '@/types/label'
import type { PageResult, PageParams } from '@/types/system'

/** 标签列表(分页) */
export function getLabelList(params?: PageParams & { itemId?: number; labelType?: number; labelStatus?: number }) {
  return get<PageResult<ElectronicLabelVo>>('/labels', params as unknown as Record<string, unknown>)
}

/** 标签详情 */
export function getLabel(id: number) {
  return get<ElectronicLabelVo>(`/labels/${id}`)
}

/** 批量生成标签 */
export function generateLabels(data: LabelGenerateDto) {
  return post<ElectronicLabelVo[]>('/labels/generate', data)
}

/** 标签绑定物品 */
export function bindLabel(id: number, data: LabelBindDto) {
  return put<ElectronicLabelVo>(`/labels/${id}/bind`, data)
}

/** 更新标签状态 */
export function updateLabelStatus(id: number, data: LabelStatusDto) {
  return put<ElectronicLabelVo>(`/labels/${id}/status`, data)
}

/** 批量打印标签 */
export function batchPrintLabels(data: LabelBatchPrintDto) {
  return post<void>('/labels/print', data)
}

/** 扫码查询 */
export function scanLabel(code: string) {
  return get<LabelScanResultVo>('/labels/scan', { code })
}

/** 查询长期闲置标签 */
export function getIdleLabels() {
  return get<ElectronicLabelVo[]>('/labels/idle')
}
