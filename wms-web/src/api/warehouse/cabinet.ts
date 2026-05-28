import { get, post, put, del } from '../request'
import type {
  EntityId,
  WmsCabinetVo,
  WmsCabinetDto,
  WmsCabinetLayoutBatchSaveDto,
  WmsCabinetLayoutSaveVo,
} from '@/types/warehouse'
import type { PageParams, PageResult } from '@/types/system'

export function getCabinetList(areaId: EntityId) {
  return get<WmsCabinetVo[]>(`/warehouse/cabinets/area/${areaId}`)
}

export function getCabinetPage(params: PageParams & { areaId: EntityId }) {
  return get<PageResult<WmsCabinetVo>>('/warehouse/cabinets', params as unknown as Record<string, unknown>)
}

export function getCabinet(id: EntityId) {
  return get<WmsCabinetVo>(`/warehouse/cabinets/${id}`)
}

export function addCabinet(data: WmsCabinetDto) {
  return post<WmsCabinetVo>('/warehouse/cabinets', data)
}

export function updateCabinet(id: EntityId, data: WmsCabinetDto) {
  return put<WmsCabinetVo>(`/warehouse/cabinets/${id}`, data)
}

export function deleteCabinet(id: EntityId) {
  return del<void>(`/warehouse/cabinets/${id}`)
}

export function updateCabinetPosition(id: EntityId, data: { rows: number; cols: number }) {
  return put<WmsCabinetVo>(`/warehouse/cabinets/${id}/position`, data)
}

export function saveCabinetLayout(data: WmsCabinetLayoutBatchSaveDto) {
  return post<WmsCabinetLayoutSaveVo>('/warehouse/cabinets/layout', data)
}
