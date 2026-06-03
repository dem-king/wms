import { describe, expect, it, vi } from 'vitest'

const requestMocks = vi.hoisted(() => ({
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
}))

vi.mock('../request', () => requestMocks)

import { getProfile, getSliderCaptcha, updateProfile, uploadAvatar } from './auth'

describe('auth api', () => {
  it('requests the current user profile endpoint', () => {
    getProfile()

    expect(requestMocks.get).toHaveBeenCalledWith('/auth/profile')
  })

  it('fetches the slider puzzle captcha from the new endpoint', () => {
    getSliderCaptcha()

    expect(requestMocks.get).toHaveBeenCalledWith('/auth/captcha/slider')
  })

  it('updates the current user profile', () => {
    const payload = {
      realName: '张三',
      phone: '13800138000',
      email: 'zhangsan@example.com',
      avatar: '/wms/avatar/avatar.png',
    }

    updateProfile(payload)

    expect(requestMocks.put).toHaveBeenCalledWith('/auth/profile', payload)
  })

  it('uploads avatar files for the current user profile', () => {
    const formData = new FormData()
    formData.append('file', new Blob(['avatar'], { type: 'image/png' }), 'avatar.png')

    uploadAvatar(formData)

    expect(requestMocks.post).toHaveBeenCalledWith('/auth/profile/avatar', formData)
  })
})
