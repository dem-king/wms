import { describe, expect, it, vi } from 'vitest'

const requestMocks = vi.hoisted(() => ({
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
  del: vi.fn(),
}))

vi.mock('../request', () => requestMocks)

import { getWarehouseList, getWarehousePage } from './warehouse'

describe('warehouse api', () => {
  it('requests the warehouse list endpoint', () => {
    getWarehouseList()

    expect(requestMocks.get).toHaveBeenCalledWith('/warehouse/warehouses/list')
  })

  it('requests the warehouse page endpoint with page params', () => {
    getWarehousePage({ page: 1, size: 20 })

    expect(requestMocks.get).toHaveBeenCalledWith('/warehouse/warehouses', { page: 1, size: 20 })
  })
})
