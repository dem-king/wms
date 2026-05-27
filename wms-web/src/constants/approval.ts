import type { ApprovalApproverType, ApprovalResult, ApprovalStatus, BizType } from '@/types/business'

export const APPROVAL_STATUS = {
  PENDING: 0,
  APPROVING: 1,
  APPROVED: 2,
  REJECTED: 3,
  REVOKED: 4,
} as const satisfies Record<string, ApprovalStatus>

export const APPROVAL_RESULT = {
  APPROVED: 1,
  REJECTED: 2,
} as const satisfies Record<string, ApprovalResult>

export const BIZ_TYPE = {
  INBOUND: 1,
  OUTBOUND: 2,
  SCRAP: 3,
  TRANSFER: 4,
  RETURN: 5,
} as const satisfies Record<string, BizType>

export const APPROVER_TYPE = {
  ROLE: 1,
  USER: 2,
  WAREHOUSE_ADMIN: 3,
} as const satisfies Record<string, ApprovalApproverType>

export const SWITCH_STATUS = {
  DISABLED: 0,
  ENABLED: 1,
} as const

export const TIMEOUT_ACTION = {
  REMIND: 1,
  CANCEL: 2,
} as const

export const DEFAULT_TIMEOUT_HOURS = 48

export const BIZ_TYPE_OPTIONS = [
  { label: '入库', value: BIZ_TYPE.INBOUND },
  { label: '出库', value: BIZ_TYPE.OUTBOUND },
  { label: '报废', value: BIZ_TYPE.SCRAP },
  { label: '调拨', value: BIZ_TYPE.TRANSFER },
  { label: '归还', value: BIZ_TYPE.RETURN },
] as const

export const APPROVAL_STATUS_OPTIONS = [
  { label: '待审批', value: APPROVAL_STATUS.PENDING },
  { label: '审批中', value: APPROVAL_STATUS.APPROVING },
  { label: '已通过', value: APPROVAL_STATUS.APPROVED },
  { label: '已驳回', value: APPROVAL_STATUS.REJECTED },
  { label: '已撤回', value: APPROVAL_STATUS.REVOKED },
] as const

export const APPROVER_TYPE_OPTIONS = [
  { label: '指定角色', value: APPROVER_TYPE.ROLE },
  { label: '指定用户', value: APPROVER_TYPE.USER },
  { label: '库房管理员', value: APPROVER_TYPE.WAREHOUSE_ADMIN },
] as const

export const TIMEOUT_ACTION_OPTIONS = [
  { label: '自动提醒', value: TIMEOUT_ACTION.REMIND },
  { label: '自动取消', value: TIMEOUT_ACTION.CANCEL },
] as const

export function getBizTypeLabel(bizType?: BizType | null): string {
  return BIZ_TYPE_OPTIONS.find((item) => item.value === bizType)?.label || '-'
}

export function getApprovalStatusLabel(status?: ApprovalStatus | null): string {
  return APPROVAL_STATUS_OPTIONS.find((item) => item.value === status)?.label || '-'
}

export function getApprovalStatusTagType(status?: ApprovalStatus | null): 'info' | 'warning' | 'success' | 'danger' {
  if (status === APPROVAL_STATUS.APPROVED) {
    return 'success'
  }
  if (status === APPROVAL_STATUS.REJECTED) {
    return 'danger'
  }
  if (status === APPROVAL_STATUS.APPROVING) {
    return 'warning'
  }
  return 'info'
}

export function getApprovalResultLabel(result?: ApprovalResult | null): string {
  if (result === APPROVAL_RESULT.APPROVED) {
    return '通过'
  }
  if (result === APPROVAL_RESULT.REJECTED) {
    return '驳回'
  }
  return '-'
}

export function getApproverTypeLabel(approverType?: ApprovalApproverType | null): string {
  return APPROVER_TYPE_OPTIONS.find((item) => item.value === approverType)?.label || '-'
}
