import { get, post, put, del } from '../request'
import type {
  InboundOrderVo,
  InboundOrderDto,
  InboundScanRequest,
  InboundType,
  OrderScanResult,
  OrderStatus,
} from '@/types/business'
import type { PageResult, PageParams } from '@/types/system'

interface InboundOrderApiDetailVo {
  id: number
  itemId: number
  itemCode: string
  itemName: string
  quantity: number
  unitPrice?: number
  amount?: number
  binId?: number
}

interface InboundOrderApiVo {
  id: number
  orderNo: string
  warehouseId: number
  warehouseName: string
  supplierId: number
  supplierName: string
  orderType: number
  status: number
  totalAmount: number
  remark: string
  createTime: string
  details?: InboundOrderApiDetailVo[]
}

const INBOUND_TYPE_TO_CODE: Record<InboundType, number> = {
  PURCHASE: 1,
  RETURN: 2,
  TRANSFER: 3,
}

const CODE_TO_INBOUND_TYPE: Record<number, InboundType> = {
  1: 'PURCHASE',
  2: 'RETURN',
  3: 'TRANSFER',
}

const ORDER_STATUS_TO_CODE: Record<OrderStatus, number> = {
  DRAFT: 0,
  PENDING_REVIEW: 1,
  APPROVED: 2,
  COMPLETED: 3,
  REJECTED: 4,
}

const CODE_TO_ORDER_STATUS: Record<number, OrderStatus> = {
  0: 'DRAFT',
  1: 'PENDING_REVIEW',
  2: 'APPROVED',
  3: 'COMPLETED',
  4: 'REJECTED',
}

function normalizeInboundOrder(data?: Partial<InboundOrderApiVo>): InboundOrderVo {
  return {
    id: Number(data?.id || 0),
    orderNo: data?.orderNo || '',
    warehouseId: Number(data?.warehouseId || 0),
    warehouseName: data?.warehouseName || '',
    supplierId: Number(data?.supplierId || 0),
    supplierName: data?.supplierName || '',
    inboundType: CODE_TO_INBOUND_TYPE[Number(data?.orderType)] || 'PURCHASE',
    status: CODE_TO_ORDER_STATUS[Number(data?.status)] || 'DRAFT',
    totalAmount: Number(data?.totalAmount || 0),
    remark: data?.remark || '',
    createTime: data?.createTime || '',
    details: (data?.details || []).map((detail) => ({
      id: detail.id,
      orderId: Number(data?.id || 0),
      itemId: detail.itemId,
      itemCode: detail.itemCode || '',
      itemName: detail.itemName || '',
      specModel: '',
      unit: '',
      quantity: Number(detail.quantity || 0),
      unitPrice: Number(detail.unitPrice || 0),
      amount: Number(detail.amount || 0),
    })),
  }
}

function toInboundPayload(data: InboundOrderDto) {
  return {
    warehouseId: data.warehouseId,
    supplierId: data.supplierId,
    orderType: INBOUND_TYPE_TO_CODE[data.inboundType],
    remark: data.remark,
    details: data.details.map((detail) => ({
      itemId: detail.itemId,
      quantity: detail.quantity,
      unitPrice: detail.unitPrice,
      binId: detail.binId,
    })),
  }
}

export function getInboundOrders(params?: PageParams & { orderNo?: string; status?: OrderStatus | string; startTime?: string; endTime?: string }) {
  const query = params
    ? {
        ...params,
        status: params.status ? ORDER_STATUS_TO_CODE[params.status as OrderStatus] : undefined,
      }
    : undefined

  return get<PageResult<InboundOrderApiVo>>('/inbound', query as unknown as Record<string, unknown>)
    .then((res) => ({
      ...res,
      data: {
        ...res.data,
        records: (res.data?.records || []).map(normalizeInboundOrder),
      },
    }))
}

export function getInboundOrder(id: number) {
  return get<InboundOrderApiVo>(`/inbound/${id}`)
    .then((res) => ({
      ...res,
      data: normalizeInboundOrder(res.data),
    }))
}

export function addInboundOrder(data: InboundOrderDto) {
  return post<InboundOrderApiVo>('/inbound', toInboundPayload(data))
    .then((res) => ({
      ...res,
      data: normalizeInboundOrder(res.data),
    }))
}

export function updateInboundOrder(id: number, data: InboundOrderDto) {
  return put<InboundOrderApiVo>(`/inbound/${id}`, toInboundPayload(data))
    .then((res) => ({
      ...res,
      data: normalizeInboundOrder(res.data),
    }))
}

export function submitInboundOrder(id: number) {
  return post<void>(`/inbound/${id}/submit`)
}

export function deleteInboundOrder(id: number) {
  return del<void>(`/inbound/${id}`)
}

export function scanInboundOrder(data: InboundScanRequest) {
  return post<OrderScanResult>('/inbound/scan', data)
}

