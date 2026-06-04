import type { EntityId } from './common'
export type { EntityId } from './common'

export interface ElectronicLabelVo {
  id: EntityId
  labelNo: string
  labelType: number
  itemId: EntityId | null
  binId: EntityId | null
  itemName: string | null
  itemCode: string | null
  categoryName: string | null
  subCategoryName: string | null
  binCode: string | null
  locationText: string | null
  stockQuantity: number | null
  batchNo: string | null
  rfidCode: string | null
  qrContent: string | null
  barcodeContent: string | null
  labelStatus: number
  bindType: number
  printStatus: number
  borrowTime: string | null
  borrowerName: string | null
  expectedReturn: string | null
  returnerName: string | null
  returnTime: string | null
  createTime: string
}

export interface LabelGenerateDto {
  itemId?: EntityId | null
  binId: EntityId
  labelPrefix?: string
  count: number
  labelType: number
  bindType: number
}

export interface LabelListParams {
  page?: number
  size?: number
  itemId?: EntityId
  labelType?: number
  labelStatus?: number
  keyword?: string
}

export interface LabelBindDto {
  itemId: EntityId
  bindType: number
}

export interface LabelStatusDto {
  labelStatus: number
}

export interface LabelBatchPrintDto {
  labelIds: EntityId[]
}

export interface LabelScanResultVo extends ElectronicLabelVo {}
