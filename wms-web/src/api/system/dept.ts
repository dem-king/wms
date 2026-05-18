import { get, post, put, del } from '../request'
import type { SysDeptVo, SysDeptDto } from '@/types/system'

export function getDeptList() {
  return get<SysDeptVo[]>('/system/depts')
}

export function getDept(id: number) {
  return get<SysDeptVo>(`/system/depts/${id}`)
}

export function addDept(data: SysDeptDto) {
  return post<SysDeptVo>('/system/depts', data)
}

export function updateDept(id: number, data: SysDeptDto) {
  return put<SysDeptVo>(`/system/depts/${id}`, data)
}

export function deleteDept(id: number) {
  return del<void>(`/system/depts/${id}`)
}

export function getDeptTree() {
  return get<SysDeptVo[]>('/system/depts/tree')
}
