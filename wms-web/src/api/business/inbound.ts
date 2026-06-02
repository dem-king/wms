import { get, post, put, del } from '../request'
import type {
  EntityId,
  InboundOrderVo,
  InboundOrderDto,
  InboundScanRequest,
  InboundType,
  OrderScanResult,
  OrderStatus,
} from '@/types/business'
import type { PageResult, PageParams } from '@/types/system'
import { normalizeOrderStatus, orderStatusToCode } from '@/constants/order-status'

interface InboundOrderApiDetailVo {
  id: EntityId
  itemId: EntityId
  itemCode: string
  itemName: string
  quantity: number
  unitPrice?: number
  amount?: number
  binId?: EntityId
}

interface InboundOrderApiVo {
  id: EntityId
  orderNo: string
  warehouseId: EntityId
  warehouseName: string
  supplierId: EntityId
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

function normalizeInboundOrder(data?: Partial<InboundOrderApiVo>): InboundOrderVo {
  return {
    id: data?.id || '',
    orderNo: data?.orderNo || '',
    warehouseId: data?.warehouseId || '',
    warehouseName: data?.warehouseName || '',
    supplierId: data?.supplierId || '',
    supplierName: data?.supplierName || '',
    inboundType: CODE_TO_INBOUND_TYPE[Number(data?.orderType)] || 'PURCHASE',
    status: normalizeOrderStatus(data?.status),
    totalAmount: Number(data?.totalAmount || 0),
    remark: data?.remark || '',
    createTime: data?.createTime || '',
    details: (data?.details || []).map((detail) => ({
      id: detail.id || '',
      orderId: data?.id || '',
      itemId: detail.itemId || '',
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
      itemId: detail.itemId || '',
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
        status: orderStatusToCode(params.status),
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

export function getInboundOrder(id: EntityId) {
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

export function updateInboundOrder(id: EntityId, data: InboundOrderDto) {
  return put<InboundOrderApiVo>(`/inbound/${id}`, toInboundPayload(data))
    .then((res) => ({
      ...res,
      data: normalizeInboundOrder(res.data),
    }))
}

export function submitInboundOrder(id: EntityId) {
  return post<void>(`/inbound/${id}/submit`)
}

export function deleteInboundOrder(id: EntityId) {
  return del<void>(`/inbound/${id}`)
}

export function scanInboundOrder(data: InboundScanRequest) {
  return post<OrderScanResult>('/inbound/scan', data)
}

