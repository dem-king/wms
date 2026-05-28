import { describe, expect, it, vi } from 'vitest'

const requestMocks = vi.hoisted(() => ({
  get: vi.fn(() => Promise.resolve({ data: undefined })),
  post: vi.fn(),
}))

vi.mock('@/api/request', () => requestMocks)

import { getDashboardLocationWeather } from './index'

describe('dashboard weather api', () => {
  it('requests dashboard location weather from the backend', async () => {
    await getDashboardLocationWeather({ latitude: 34.2132, longitude: 108.8799 })

    expect(requestMocks.get).toHaveBeenCalledWith('/report/dashboard/location-weather', {
      latitude: 34.2132,
      longitude: 108.8799,
    })
  })
})
