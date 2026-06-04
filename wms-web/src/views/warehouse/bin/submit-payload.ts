import type { EntityId, WmsBinDto } from '@/types/warehouse'

export interface BinFormState {
  cabinetId: EntityId
  binCode: string
  row: number
  col: number
  status: number
}

export function buildBinSubmitPayload(form: BinFormState): WmsBinDto {
  return {
    cabinetId: form.cabinetId,
    binCode: form.binCode,
    row: form.row,
    col: form.col,
    status: form.status,
  }
}
