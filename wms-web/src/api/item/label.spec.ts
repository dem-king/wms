import { describe, expect, it, vi } from 'vitest'

const requestMocks = vi.hoisted(() => ({
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
}))

vi.mock('../request', () => requestMocks)

import { batchPrintLabels, scanLabel } from './label'

describe('label api', () => {
  it('sends batch print requests as a label id array', () => {
    const payload = { labelIds: ['1', '2', '3'] }

    batchPrintLabels(payload)

    expect(requestMocks.post).toHaveBeenCalledWith('/labels/print', payload.labelIds)
  })

  it('uses the path-based scan endpoint', () => {
    scanLabel('LBL/2026 001')

    expect(requestMocks.get).toHaveBeenCalledWith('/labels/scan/LBL%2F2026%20001')
  })
})
