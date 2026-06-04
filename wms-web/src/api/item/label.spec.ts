import { describe, expect, it, vi } from 'vitest'

const requestMocks = vi.hoisted(() => ({
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
}))

vi.mock('../request', () => requestMocks)

import { batchPrintLabels, generateLabels, getLabelList, scanLabel } from './label'

describe('label api', () => {
  it('sends batch print requests as a label id array', () => {
    const payload = { labelIds: ['1', '2', '3'] }

    batchPrintLabels(payload)

    expect(requestMocks.post).toHaveBeenCalledWith('/labels/print', payload.labelIds)
  })

  it('passes keyword through label list params', () => {
    getLabelList({ keyword: 'RFID-001', labelStatus: 2 })

    expect(requestMocks.get).toHaveBeenCalledWith('/labels', { keyword: 'RFID-001', labelStatus: 2 })
  })

  it('sends bin id and custom prefix when generating labels', () => {
    const payload = { itemId: '11', binId: '101', labelPrefix: 'RF', count: 1, labelType: 3, bindType: 1 }

    generateLabels(payload)

    expect(requestMocks.post).toHaveBeenCalledWith('/labels/generate', payload)
  })

  it('uses the path-based scan endpoint', () => {
    scanLabel('LBL/2026 001')

    expect(requestMocks.get).toHaveBeenCalledWith('/labels/scan/LBL%2F2026%20001')
  })
})
