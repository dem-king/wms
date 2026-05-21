import { describe, expect, it } from 'vitest'
import { collectScannedLabelIds, mergeScannedDetail } from './order-scan'
import type { OrderScanDetailRow, OrderScanResult } from '@/types/business'

describe('order scan helpers', () => {
  it('collects current label ids from scanned detail rows only', () => {
    const details: OrderScanDetailRow[] = [
      {
        itemId: 1,
        quantity: 1,
        unitPrice: 10,
        scannedLabels: [{ labelId: 11, labelNo: 'LBL-011' }],
      },
      {
        itemId: 2,
        quantity: 2,
        unitPrice: 20,
      },
      {
        itemId: 3,
        quantity: 1,
        unitPrice: 0,
        scannedLabels: [
          { labelId: 12, labelNo: 'LBL-012' },
          { labelId: 13, labelNo: 'LBL-013' },
        ],
      },
    ]

    expect(collectScannedLabelIds(details)).toEqual([11, 12, 13])
  })

  it('merges a scanned result into an existing item row', () => {
    const details: OrderScanDetailRow[] = [
      {
        itemId: 101,
        quantity: 1,
        unitPrice: 12.5,
        itemName: '轴承',
        itemCode: 'WB-101',
        scannedLabels: [{ labelId: 1, labelNo: 'LBL-001' }],
      },
    ]
    const result: OrderScanResult = {
      labelId: 2,
      labelNo: 'LBL-002',
      labelStatus: 1,
      itemId: 101,
      itemName: '轴承',
      itemCode: 'WB-101',
      detail: {
        itemId: 101,
        quantity: 1,
      },
    }

    expect(mergeScannedDetail(details, result)).toEqual([
      {
        itemId: 101,
        quantity: 2,
        unitPrice: 12.5,
        itemName: '轴承',
        itemCode: 'WB-101',
        scannedLabels: [
          { labelId: 1, labelNo: 'LBL-001' },
          { labelId: 2, labelNo: 'LBL-002' },
        ],
      },
    ])
  })

  it('appends a new scanned row when the item does not exist yet', () => {
    const result: OrderScanResult = {
      labelId: 9,
      labelNo: 'LBL-009',
      labelStatus: 1,
      itemId: 202,
      itemName: '密封圈',
      itemCode: 'WB-202',
      detail: {
        itemId: 202,
        quantity: 1,
      },
    }

    expect(mergeScannedDetail([], result)).toEqual([
      {
        itemId: 202,
        quantity: 1,
        unitPrice: 0,
        itemName: '密封圈',
        itemCode: 'WB-202',
        scannedLabels: [{ labelId: 9, labelNo: 'LBL-009' }],
      },
    ])
  })
})
