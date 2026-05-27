import { get, post, put, del } from '../request'
import type { EntityId, SysSupplierVo, SysSupplierDto, PageResult, PageParams } from '@/types/system'

export function getSupplierList(params?: PageParams & { supplierName?: string; supplierCode?: string }) {
  return get<PageResult<SysSupplierVo>>('/system/suppliers', params as unknown as Record<string, unknown>)
}

export function getSupplier(id: EntityId) {
  return get<SysSupplierVo>(`/system/suppliers/${id}`)
}

export function addSupplier(data: SysSupplierDto) {
  return post<SysSupplierVo>('/system/suppliers', data)
}

export function updateSupplier(id: EntityId, data: SysSupplierDto) {
  return put<SysSupplierVo>(`/system/suppliers/${id}`, data)
}

export function deleteSupplier(id: EntityId) {
  return del<void>(`/system/suppliers/${id}`)
}

