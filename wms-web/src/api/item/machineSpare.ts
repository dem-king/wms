import { get, post, put, del } from '../request'
import type { EntityId, MachineSpareVo, MachineSpareDto } from '@/types/business'
import type { PageResult, PageParams } from '@/types/system'

export function getMachineSpareList(params?: PageParams & { machineCode?: string; machineName?: string }) {
  return get<PageResult<MachineSpareVo>>('/machine-spare', params as unknown as Record<string, unknown>)
}

export function getMachineSpare(id: EntityId) {
  return get<MachineSpareVo>(`/machine-spare/${id}`)
}

export function getMachineSpareByMachine(machineCode: string) {
  return get<MachineSpareVo[]>(`/machine-spare/machine/${machineCode}`)
}

export function addMachineSpare(data: MachineSpareDto) {
  return post<MachineSpareVo>('/machine-spare', data)
}

export function updateMachineSpare(id: EntityId, data: MachineSpareDto) {
  return put<MachineSpareVo>(`/machine-spare/${id}`, data)
}

export function deleteMachineSpare(id: EntityId) {
  return del<void>(`/machine-spare/${id}`)
}
