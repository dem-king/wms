import { describe, expect, it, vi } from 'vitest'

const requestMocks = vi.hoisted(() => ({
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
  del: vi.fn(),
}))

vi.mock('../request', () => requestMocks)

import { batchCreateBin, getBinList, getBinListByWarehouse, getBinPage } from './bin'

describe('bin api', () => {
  it('requests the cabinet bin list endpoint', () => {
    getBinList('1')

    expect(requestMocks.get).toHaveBeenCalledWith('/warehouse/bins/cabinet/1')
  })

  it('requests the warehouse bin list endpoint', () => {
    getBinListByWarehouse('10')

    expect(requestMocks.get).toHaveBeenCalledWith('/warehouse/bins/warehouse/10')
  })

  it('requests the bin page endpoint with cabinet and page params', () => {
    getBinPage({ cabinetId: '1', page: 1, size: 20 })

    expect(requestMocks.get).toHaveBeenCalledWith('/warehouse/bins', {
      cabinetId: '1',
      page: 1,
      size: 20,
    })
  })

  it('requests the batch create endpoint with cabinet path and rows cols params', () => {
    batchCreateBin({
      cabinetId: '8',
      startRow: 2,
      endRow: 4,
      startCol: 3,
      endCol: 5,
    })

    expect(requestMocks.post).toHaveBeenCalledWith('/warehouse/bins/cabinet/8/batch', undefined, {
      params: {
        rows: 3,
        cols: 3,
      },
    })
  })
})
