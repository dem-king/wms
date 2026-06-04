import { describe, expect, it } from 'vitest'
import { buildBinSubmitPayload } from './submit-payload'

describe('buildBinSubmitPayload', () => {
  it('maps form fields to backend dto fields', () => {
    const payload = buildBinSubmitPayload({
      cabinetId: '10',
      binCode: 'BIN-01',
      row: 2,
      col: 3,
      status: 1,
    })

    expect(payload).toEqual({
      cabinetId: '10',
      binCode: 'BIN-01',
      row: 2,
      col: 3,
      status: 1,
    })
  })
})
