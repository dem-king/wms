/**
 * WMS-PDA PDA专用接口
 * 封装RFID批量读取上报、盘点结果提交、PDA待办任务等API请求
 */
import type {
  RfidBatchReadDto,
  RfidBatchReadResultVo,
  StockCheckDto,
  StockCheckResultVo,
  PdaTaskVo
} from '@/utils/constants'
import { post, get } from '@/api/request'

/**
 * RFID批量读取上报
 * POST /api/pda/rfid/batch-read
 * 将RFID读取到的EPC码上报后端，后端对比系统在库标签返回差异明细
 *
 * @param data RFID批量读取上报请求参数
 * @returns 对比结果（含盘盈/盘亏明细）
 */
export function rfidBatchReadApi(data: RfidBatchReadDto): Promise<RfidBatchReadResultVo> {
  return post<RfidBatchReadResultVo>('/api/pda/rfid/batch-read', data as Record<string, unknown>)
}

/**
 * 盘点结果提交
 * POST /api/pda/stock/check
 * 确认盘点差异后提交盘点结果，后端创建盘点单
 *
 * @param data 盘点提交请求参数
 * @returns 盘点提交结果（含盘点单ID和状态）
 */
export function stockCheckApi(data: StockCheckDto): Promise<StockCheckResultVo> {
  return post<StockCheckResultVo>('/api/pda/stock/check', data as Record<string, unknown>)
}

/**
 * 获取PDA待办任务统计
 * GET /api/pda/tasks
 * 返回各类型待办任务的数量统计
 *
 * @returns PDA待办任务统计
 */
export function getPdaTasksApi(): Promise<PdaTaskVo> {
  return get<PdaTaskVo>('/api/pda/tasks')
}
