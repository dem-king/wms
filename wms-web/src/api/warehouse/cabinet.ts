import { get, post, put, del } from '../request'
import type {
  WmsCabinetVo,
  WmsCabinetDto,
  WmsCabinetLayoutBatchSaveDto,
  WmsCabinetLayoutSaveVo,
} from '@/types/warehouse'

export function getCabinetList(areaId: number) {
  return get<WmsCabinetVo[]>(`/warehouse/cabinets/area/${areaId}`)
}

export function getCabinet(id: number) {
  return get<WmsCabinetVo>(`/warehouse/cabinets/${id}`)
}

export function addCabinet(data: WmsCabinetDto) {
  return post<WmsCabinetVo>('/warehouse/cabinets', data)
}

export function updateCabinet(id: number, data: WmsCabinetDto) {
  return put<WmsCabinetVo>(`/warehouse/cabinets/${id}`, data)
}

export function deleteCabinet(id: number) {
  return del<void>(`/warehouse/cabinets/${id}`)
}

export function updateCabinetPosition(id: number, data: { rows: number; cols: number }) {
  return put<WmsCabinetVo>(`/warehouse/cabinets/${id}/position`, data)
}

export function saveCabinetLayout(data: WmsCabinetLayoutBatchSaveDto) {
  return post<WmsCabinetLayoutSaveVo>('/warehouse/cabinets/layout', data)
}
