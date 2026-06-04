import { describe, expect, it } from 'vitest'
import { LABEL_CODE_UNSUPPORTED_MESSAGE, resolveLabelCodeDisplay } from './code-display'
import type { ElectronicLabelVo } from '@/types/label'

function createLabel(overrides: Partial<ElectronicLabelVo>): ElectronicLabelVo {
  return {
    id: '1',
    labelNo: 'LBL-2026-001',
    labelType: 1,
    itemId: '10',
    binId: null,
    itemName: '轴承',
    itemCode: 'WB-001',
    categoryName: null,
    subCategoryName: null,
    binCode: null,
    locationText: null,
    stockQuantity: null,
    batchNo: '',
    rfidCode: '',
    qrContent: '',
    barcodeContent: '',
    labelStatus: 1,
    bindType: 1,
    printStatus: 0,
    borrowTime: '',
    borrowerName: null,
    expectedReturn: '',
    returnerName: null,
    returnTime: null,
    createTime: '2026-05-21 12:00:00',
    ...overrides,
  }
}

describe('label code display resolver', () => {
  it('uses qrContent for qr labels', () => {
    expect(resolveLabelCodeDisplay(createLabel({ labelType: 1, qrContent: 'https://wms.local/labels/1' }))).toEqual({
      mode: 'render',
      codeType: 'qr',
      codeValue: 'https://wms.local/labels/1',
      textValue: '',
      errorMessage: '',
    })
  })

  it('returns text-only payload for rfid labels', () => {
    expect(resolveLabelCodeDisplay(createLabel({ labelType: 3, rfidCode: 'RFID-ABC-001' }))).toEqual({
      mode: 'text',
      codeType: null,
      codeValue: '',
      textValue: 'RFID-ABC-001',
      errorMessage: '',
    })
  })

  it('reports unsupported label types explicitly', () => {
    expect(resolveLabelCodeDisplay(createLabel({ labelType: 9, labelNo: 'LBL-UNKNOWN' }))).toEqual({
      mode: 'error',
      codeType: null,
      codeValue: '',
      textValue: '',
      errorMessage: LABEL_CODE_UNSUPPORTED_MESSAGE,
    })
  })
})
