import { get, post, put, del } from '../request'
import type { SysUserVo, SysUserDto, PageResult, PageParams } from '@/types/system'

export function getUserList(params: PageParams & { username?: string; phone?: string; status?: number }) {
  return get<PageResult<SysUserVo>>('/system/users', params as unknown as Record<string, unknown>)
}

export function getUser(id: number) {
  return get<SysUserVo>(`/system/users/${id}`)
}

export function addUser(data: SysUserDto) {
  return post<SysUserVo>('/system/users', data)
}

export function updateUser(id: number, data: SysUserDto) {
  return put<SysUserVo>(`/system/users/${id}`, data)
}

export function deleteUser(id: number) {
  return del<void>(`/system/users/${id}`)
}

export function resetUserPwd(id: number) {
  return put<void>(`/system/users/${id}/resetPwd`)
}

export function changeUserStatus(id: number, status: number) {
  return put<void>(`/system/users/${id}/status`, { status })
}

