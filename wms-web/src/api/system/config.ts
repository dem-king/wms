import { get, post, put, del } from '../request'
import type { EntityId, SysConfigVo, SysConfigDto, PageResult, PageParams } from '@/types/system'

export function getConfigPage(params?: PageParams & { configKey?: string; configGroup?: string }) {
  return get<PageResult<SysConfigVo>>('/system/configs', params as unknown as Record<string, unknown>)
}

export function getConfigByKey(configKey: string) {
  return get<SysConfigVo>(`/system/configs/key/${configKey}`)
}

export function addConfig(data: SysConfigDto) {
  return post<SysConfigVo>('/system/configs', data)
}

export function updateConfig(id: EntityId, data: SysConfigDto) {
  return put<SysConfigVo>(`/system/configs/${id}`, data)
}

export function deleteConfig(id: EntityId) {
  return del<void>(`/system/configs/${id}`)
}
