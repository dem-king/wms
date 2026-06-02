import { describe, expect, it } from 'vitest'
import { resolveFileUrl } from './file-url'

describe('resolveFileUrl', () => {
  it('keeps browser-native urls unchanged', () => {
    expect(resolveFileUrl('https://cdn.example.com/a.png')).toBe('https://cdn.example.com/a.png')
    expect(resolveFileUrl('data:image/png;base64,abc')).toBe('data:image/png;base64,abc')
    expect(resolveFileUrl('blob:http://localhost/id')).toBe('blob:http://localhost/id')
  })

  it('keeps api-prefixed storage urls unchanged', () => {
    expect(resolveFileUrl('/api/storage/items/demo.png')).toBe('/api/storage/items/demo.png')
  })

  it('prefixes backend storage urls with the api context', () => {
    expect(resolveFileUrl('/storage/items/demo.png')).toBe('/api/storage/items/demo.png')
  })

  it('prefixes relative storage paths with the api context', () => {
    expect(resolveFileUrl('storage/items/demo.png')).toBe('/api/storage/items/demo.png')
  })
})
