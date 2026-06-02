import type { OrderStatus } from '@/types/business'

type TagType = 'info' | 'warning' | 'success' | 'danger'

export type SelectableOrderStatus = Exclude<OrderStatus, 'UNKNOWN'>

export const ORDER_STATUS_TO_CODE: Record<SelectableOrderStatus, number> = {
  DRAFT: 0,
  PENDING_REVIEW: 1,
  APPROVING: 2,
  APPROVED: 3,
  REJECTED: 4,
  COMPLETED: 5,
}

export const CODE_TO_ORDER_STATUS: Record<number, SelectableOrderStatus> = {
  0: 'DRAFT',
  1: 'PENDING_REVIEW',
  2: 'APPROVING',
  3: 'APPROVED',
  4: 'REJECTED',
  5: 'COMPLETED',
}

export const orderStatusOptions: Array<{ label: string; value: SelectableOrderStatus }> = [
  { label: '草稿', value: 'DRAFT' },
  { label: '待审批', value: 'PENDING_REVIEW' },
  { label: '审批中', value: 'APPROVING' },
  { label: '已通过', value: 'APPROVED' },
  { label: '已驳回', value: 'REJECTED' },
  { label: '已完成', value: 'COMPLETED' },
]

export function normalizeOrderStatus(status: unknown): OrderStatus {
  if (typeof status === 'string' && status in ORDER_STATUS_TO_CODE) {
    return status as SelectableOrderStatus
  }

  return CODE_TO_ORDER_STATUS[Number(status)] || 'UNKNOWN'
}

export function orderStatusToCode(status?: string | number): number | undefined {
  if (status === undefined || status === null || status === '') {
    return undefined
  }
  if (typeof status === 'number') {
    return status
  }
  return ORDER_STATUS_TO_CODE[status as SelectableOrderStatus]
}

export function orderStatusLabel(status: unknown): string {
  const labels: Record<OrderStatus, string> = {
    DRAFT: '草稿',
    PENDING_REVIEW: '待审批',
    APPROVING: '审批中',
    APPROVED: '已通过',
    REJECTED: '已驳回',
    COMPLETED: '已完成',
    UNKNOWN: '未知',
  }
  return labels[normalizeOrderStatus(status)]
}

export function orderStatusTagType(status: unknown): TagType {
  const map: Record<OrderStatus, TagType> = {
    DRAFT: 'info',
    PENDING_REVIEW: 'warning',
    APPROVING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger',
    COMPLETED: 'success',
    UNKNOWN: 'info',
  }
  return map[normalizeOrderStatus(status)]
}

export function isDraftOrderStatus(status: unknown): boolean {
  return normalizeOrderStatus(status) === 'DRAFT'
}
