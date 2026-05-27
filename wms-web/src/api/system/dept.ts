import { get, post, put, del } from '../request'
import type { EntityId, SysDeptVo, SysDeptDto } from '@/types/system'

export function getDeptList() {
  return get<SysDeptVo[]>('/system/depts')
}

export function getDept(id: EntityId) {
  return get<SysDeptVo>(`/system/depts/${id}`)
}

export function addDept(data: SysDeptDto) {
  return post<SysDeptVo>('/system/depts', data)
}

export function updateDept(id: EntityId, data: SysDeptDto) {
  return put<SysDeptVo>(`/system/depts/${id}`, data)
}

export function deleteDept(id: EntityId) {
  return del<void>(`/system/depts/${id}`)
}

export function getDeptTree() {
  return get<SysDeptVo[]>('/system/depts/tree')
}
