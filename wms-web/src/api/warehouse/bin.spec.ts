import { describe, expect, it, vi } from 'vitest'

const requestMocks = vi.hoisted(() => ({
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
  del: vi.fn(),
}))

vi.mock('../request', () => requestMocks)

import { getBinList, getBinListByWarehouse, getBinPage } from './bin'

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
})
