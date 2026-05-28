import { describe, expect, it, vi } from 'vitest'

const requestMocks = vi.hoisted(() => ({
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
  del: vi.fn(),
}))

vi.mock('../request', () => requestMocks)

import { getCabinetList, getCabinetPage } from './cabinet'

describe('cabinet api', () => {
  it('requests the area cabinet list endpoint', () => {
    getCabinetList('1')

    expect(requestMocks.get).toHaveBeenCalledWith('/warehouse/cabinets/area/1')
  })

  it('requests the cabinet page endpoint with area and page params', () => {
    getCabinetPage({ areaId: '1', page: 1, size: 20 })

    expect(requestMocks.get).toHaveBeenCalledWith('/warehouse/cabinets', {
      areaId: '1',
      page: 1,
      size: 20,
    })
  })
})
