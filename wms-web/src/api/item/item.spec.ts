import { describe, expect, it, vi } from 'vitest'

const requestMocks = vi.hoisted(() => ({
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
  del: vi.fn(),
}))

vi.mock('../request', () => requestMocks)

import { attachItemImage } from './item'

describe('item api', () => {
  it('attaches uploaded image metadata to an item', () => {
    const payload = {
      imageUrl: '/api/storage/items/1/demo.png',
      objectName: '1/demo.png',
      imageName: 'demo.png',
    }

    attachItemImage(1, payload)

    expect(requestMocks.post).toHaveBeenCalledWith('/item/items/1/images', payload)
  })
})
