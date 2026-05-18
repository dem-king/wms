import { get, put } from '../request'

export function getPendingList(params?: any) {
  return get('/approval/pending', params)
}

export function getHistoryList(params?: any) {
  return get('/approval/history', params)
}

export function approve(id: number, data?: any) {
  return put(`/approval/${id}/approve`, data)
}

export function reject(id: number, data?: any) {
  return put(`/approval/${id}/reject`, data)
}

export function getApprovalConfig() {
  return get('/approval/config')
}

export function updateApprovalConfig(id: number, data: any) {
  return put(`/approval/config/${id}`, data)
}
