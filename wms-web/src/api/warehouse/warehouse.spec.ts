import { describe, expect, it, vi } from 'vitest'

const requestMocks = vi.hoisted(() => ({
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
  del: vi.fn(),
}))

vi.mock('../request', () => requestMocks)

import { getWarehouseList } from './warehouse'

describe('warehouse api', () => {
  it('requests the warehouse list endpoint', () => {
    getWarehouseList()

    expect(requestMocks.get).toHaveBeenCalledWith('/warehouse/warehouses/list')
  })
})
