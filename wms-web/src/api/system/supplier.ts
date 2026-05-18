import { get, post, put, del } from '../request'
import type { SysSupplierVo, SysSupplierDto, PageResult, PageParams } from '@/types/system'

export function getSupplierList(params?: PageParams & { supplierName?: string; supplierCode?: string }) {
  return get<PageResult<SysSupplierVo>>('/system/suppliers', params as unknown as Record<string, unknown>)
}

export function getSupplier(id: number) {
  return get<SysSupplierVo>(`/system/suppliers/${id}`)
}

export function addSupplier(data: SysSupplierDto) {
  return post<SysSupplierVo>('/system/suppliers', data)
}

export function updateSupplier(id: number, data: SysSupplierDto) {
  return put<SysSupplierVo>(`/system/suppliers/${id}`, data)
}

export function deleteSupplier(id: number) {
  return del<void>(`/system/suppliers/${id}`)
}

