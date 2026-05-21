import { get, post, put, del } from '../request'
import type { SysRoleVo, SysRoleDto, PageResult, PageParams } from '@/types/system'
import type { MenuTreeNode } from '@/types/auth'

export function getRoleList(params?: PageParams & { roleName?: string; roleCode?: string; status?: number }) {
  return get<PageResult<SysRoleVo> | SysRoleVo[]>('/system/roles', params as unknown as Record<string, unknown>)
}

export function addRole(data: SysRoleDto) {
  return post<SysRoleVo>('/system/roles', data)
}

export function updateRole(id: number, data: SysRoleDto) {
  return put<SysRoleVo>(`/system/roles/${id}`, data)
}

export function deleteRole(id: number) {
  return del<void>(`/system/roles/${id}`)
}

export function getRoleMenus(roleId: number) {
  return get<number[]>(`/system/roles/${roleId}/menus`)
}

export function assignRoleMenus(roleId: number, menuIds: number[]) {
  return put<void>(`/system/roles/${roleId}/menus`, { menuIds })
}

export function getAllRoles() {
  return get<SysRoleVo[]>('/system/roles/all')
}

export function getMenuTreeForRole() {
  return get<MenuTreeNode[]>('/system/menus/tree')
}

