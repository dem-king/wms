import type { DashboardData, DashboardConfig } from '@/types/dashboard'
import { get, post } from '@/api/request'

export const getDashboardData = (_role: string = 'admin') => {
  return get<DashboardData>('/report/dashboard/data')
}

export const getDashboardConfig = () => {
  return get<DashboardConfig>('/report/dashboard/config')
}

export const saveDashboardConfig = (config: DashboardConfig) => {
  return post<void>('/report/dashboard/config', config)
}

export const completeTask = (_id: string) => {
  // 暂时保留，根据需要对接到具体的审批/出库等接口
  return post<void>(`/business/task/${_id}/complete`)
}

export const dismissAlert = (_id: string) => {
  // 暂时保留，对接到预警处理接口
  return post<void>(`/monitor/alert/${_id}/dismiss`)
}