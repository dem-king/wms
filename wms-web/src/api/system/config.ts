import { get, post, put, del } from '../request'
import type { SysConfigVo, SysConfigDto, PageResult, PageParams } from '@/types/system'

export function getConfigList(params?: PageParams & { configKey?: string; configGroup?: string }) {
  return get<PageResult<SysConfigVo> | SysConfigVo[]>('/system/configs', params as unknown as Record<string, unknown>)
}

export function getConfig(id: number) {
  return get<SysConfigVo>(`/system/configs/${id}`)
}

export function addConfig(data: SysConfigDto) {
  return post<SysConfigVo>('/system/configs', data)
}

export function updateConfig(id: number, data: SysConfigDto) {
  return put<SysConfigVo>(`/system/configs/${id}`, data)
}

export function deleteConfig(id: number) {
  return del<void>(`/system/configs/${id}`)
}

export function getConfigByKey(configKey: string) {
  return get<SysConfigVo>(`/system/configs/key/${configKey}`)
}

