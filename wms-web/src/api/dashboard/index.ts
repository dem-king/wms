import type { DashboardData, DashboardConfig } from '@/types/dashboard'
import type { DashboardLocationWeatherResponse } from '@/types/location-weather'
import { get, post, put } from '@/api/request'

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
  return put<void>(`/monitor/stock-alert/${_id}/resolve`)
}

export const getDashboardLocationWeather = (params: { latitude: number; longitude: number }) => {
  return get<DashboardLocationWeatherResponse>('/report/dashboard/location-weather', params)
}
