import { get, post, put, del } from '../request'
import type {
  ApprovalOrderVo,
  ApprovalConfigVo,
  ApprovalConfigDto,
  ApprovalActionDto,
} from '@/types/business'
import type { PageResult, PageParams } from '@/types/system'

export function getApprovalOrders(params?: PageParams & { bizType?: string; status?: string }) {
  return get<PageResult<ApprovalOrderVo>>('/approval', params as unknown as Record<string, unknown>)
}

export function getApprovalOrder(id: number) {
  return get<ApprovalOrderVo>(`/approval/${id}`)
}

export function getApprovalByBiz(bizId: number, bizType: string) {
  return get<ApprovalOrderVo>('/approval/biz', { bizId, bizType })
}

export function approveOrder(id: number, data: ApprovalActionDto) {
  return post<void>(`/approval/${id}/approve`, data)
}

export function rejectOrder(id: number, data: ApprovalActionDto) {
  return post<void>(`/approval/${id}/reject`, data)
}

export function revokeOrder(id: number) {
  return post<void>(`/approval/${id}/revoke`)
}

export function getApprovalConfigs(params?: PageParams) {
  return get<PageResult<ApprovalConfigVo>>('/approval/config', params as unknown as Record<string, unknown>)
}

export function getApprovalConfig(id: number) {
  return get<ApprovalConfigVo>(`/approval/config/${id}`)
}

export function addApprovalConfig(data: ApprovalConfigDto) {
  return post<ApprovalConfigVo>('/approval/config', data)
}

export function updateApprovalConfig(id: number, data: ApprovalConfigDto) {
  return put<ApprovalConfigVo>(`/approval/config/${id}`, data)
}

export function deleteApprovalConfig(id: number) {
  return del<void>(`/approval/config/${id}`)
}
