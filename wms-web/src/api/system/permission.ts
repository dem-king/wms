import { get, post, put, del } from '../request'
import type { EntityId, SysPermissionVo, SysPermissionDto, PageResult, PageParams } from '@/types/system'

export function getPermissionList(params?: PageParams & { permName?: string; permCode?: string }) {
  return get<PageResult<SysPermissionVo> | SysPermissionVo[]>('/system/permissions', params as unknown as Record<string, unknown>)
}

export function getPermission(id: EntityId) {
  return get<SysPermissionVo>(`/system/permissions/${id}`)
}

export function addPermission(data: SysPermissionDto) {
  return post<SysPermissionVo>('/system/permissions', data)
}

export function updatePermission(id: EntityId, data: SysPermissionDto) {
  return put<SysPermissionVo>(`/system/permissions/${id}`, data)
}

export function deletePermission(id: EntityId) {
  return del<void>(`/system/permissions/${id}`)
}

