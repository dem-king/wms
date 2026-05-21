import { get, post, put, del } from '../request'
import type {
  OrderScanResult,
  OrderStatus,
  OutboundOrderVo,
  OutboundOrderDto,
  OutboundScanRequest,
  OutboundType,
} from '@/types/business'
import type { PageResult, PageParams } from '@/types/system'

interface OutboundOrderApiDetailVo {
  id: number
  itemId: number
  itemCode: string
  itemName: string
  quantity: number
  binId?: number
}

interface OutboundOrderApiVo {
  id: number
  orderNo: string
  warehouseId: number
  warehouseName: string
  orderType: number
  status: number
  receiver: string
  purpose: string
  expectedReturnDate?: string
  remark: string
  createTime: string
  details?: OutboundOrderApiDetailVo[]
}

const OUTBOUND_TYPE_TO_CODE: Record<OutboundType, number> = {
  BORROW: 1,
  TRANSFER: 2,
  SCRAP: 3,
}

const CODE_TO_OUTBOUND_TYPE: Record<number, OutboundType> = {
  1: 'BORROW',
  2: 'TRANSFER',
  3: 'SCRAP',
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

function normalizeReturnDate(value?: string): string {
  if (!value) {
    return ''
  }

  return value.includes('T') ? value.slice(0, 10) : value
}

function toExpectedReturnDate(value: string): string | undefined {
  return value ? `${value}T00:00:00` : undefined
}

function normalizeOutboundOrder(data?: Partial<OutboundOrderApiVo>): OutboundOrderVo {
  return {
    id: Number(data?.id || 0),
    orderNo: data?.orderNo || '',
    warehouseId: Number(data?.warehouseId || 0),
    warehouseName: data?.warehouseName || '',
    outboundType: CODE_TO_OUTBOUND_TYPE[Number(data?.orderType)] || 'BORROW',
    recipient: data?.receiver || '',
    purpose: data?.purpose || '',
    returnDate: normalizeReturnDate(data?.expectedReturnDate),
    status: CODE_TO_ORDER_STATUS[Number(data?.status)] || 'DRAFT',
    totalAmount: 0,
    remark: data?.remark || '',
    details: (data?.details || []).map((detail) => ({
      id: detail.id,
      orderId: Number(data?.id || 0),
      itemId: detail.itemId,
      itemCode: detail.itemCode || '',
      itemName: detail.itemName || '',
      specModel: '',
      unit: '',
      quantity: Number(detail.quantity || 0),
      unitPrice: 0,
      amount: 0,
    })),
    createTime: data?.createTime || '',
  }
}

function toOutboundPayload(data: OutboundOrderDto) {
  return {
    warehouseId: data.warehouseId,
    orderType: OUTBOUND_TYPE_TO_CODE[data.outboundType],
    receiver: data.recipient,
    purpose: data.purpose,
    expectedReturnDate: toExpectedReturnDate(data.returnDate),
    remark: data.remark,
    details: data.details.map((detail) => ({
      itemId: detail.itemId,
      quantity: detail.quantity,
      binId: detail.binId,
    })),
  }
}

export function getOutboundOrders(params?: PageParams & { orderNo?: string; status?: OrderStatus | string; startTime?: string; endTime?: string }) {
  const query = params
    ? {
        ...params,
        status: params.status ? ORDER_STATUS_TO_CODE[params.status as OrderStatus] : undefined,
      }
    : undefined

  return get<PageResult<OutboundOrderApiVo>>('/outbound', query as unknown as Record<string, unknown>)
    .then((res) => ({
      ...res,
      data: {
        ...res.data,
        records: (res.data?.records || []).map(normalizeOutboundOrder),
      },
    }))
}

export function getOutboundOrder(id: number) {
  return get<OutboundOrderApiVo>(`/outbound/${id}`)
    .then((res) => ({
      ...res,
      data: normalizeOutboundOrder(res.data),
    }))
}

export function addOutboundOrder(data: OutboundOrderDto) {
  return post<OutboundOrderApiVo>('/outbound', toOutboundPayload(data))
    .then((res) => ({
      ...res,
      data: normalizeOutboundOrder(res.data),
    }))
}

export function updateOutboundOrder(id: number, data: OutboundOrderDto) {
  return put<OutboundOrderApiVo>(`/outbound/${id}`, toOutboundPayload(data))
    .then((res) => ({
      ...res,
      data: normalizeOutboundOrder(res.data),
    }))
}

export function submitOutboundOrder(id: number) {
  return post<void>(`/outbound/${id}/submit`)
}

export function deleteOutboundOrder(id: number) {
  return del<void>(`/outbound/${id}`)
}

export function scanOutboundOrder(data: OutboundScanRequest) {
  return post<OrderScanResult>('/outbound/scan', data)
}

