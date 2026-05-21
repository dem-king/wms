import type { OrderScanDetailRow, OrderScanResult } from '@/types/business'

export function collectScannedLabelIds(details: OrderScanDetailRow[]): number[] {
  return details.flatMap((detail) => detail.scannedLabels?.map((label) => label.labelId) || [])
}

export function mergeScannedDetail(
  details: OrderScanDetailRow[],
  result: OrderScanResult,
): OrderScanDetailRow[] {
  const existingIndex = details.findIndex((detail) => detail.itemId === result.detail.itemId)
  const scannedLabel = { labelId: result.labelId, labelNo: result.labelNo }

  if (existingIndex >= 0) {
    return details.map((detail, index) => {
      if (index !== existingIndex) {
        return detail
      }

      return {
        ...detail,
        quantity: detail.quantity + result.detail.quantity,
        itemName: detail.itemName || result.itemName,
        itemCode: detail.itemCode || result.itemCode,
        scannedLabels: [...(detail.scannedLabels || []), scannedLabel],
      }
    })
  }

  return [
    ...details,
    {
      itemId: result.detail.itemId,
      quantity: result.detail.quantity,
      unitPrice: 0,
      itemName: result.itemName,
      itemCode: result.itemCode,
      scannedLabels: [scannedLabel],
    },
  ]
}
