import { describe, expect, it, vi } from 'vitest'

const requestMocks = vi.hoisted(() => ({
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
  del: vi.fn(),
}))

vi.mock('../request', () => requestMocks)

import { getAreaList, getAreaPage } from './area'

describe('area api', () => {
  it('requests the warehouse area list endpoint', () => {
    getAreaList('1')

    expect(requestMocks.get).toHaveBeenCalledWith('/warehouse/areas/warehouse/1')
  })

  it('requests the area page endpoint with warehouse and page params', () => {
    getAreaPage({ warehouseId: '1', page: 1, size: 20 })

    expect(requestMocks.get).toHaveBeenCalledWith('/warehouse/areas', {
      warehouseId: '1',
      page: 1,
      size: 20,
    })
  })
})
